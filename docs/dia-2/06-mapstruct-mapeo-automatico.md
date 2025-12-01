# MapStruct: Mapeo Automático y Eficiente entre DTOs y Entidades

## Introducción

MapStruct es un generador de código que crea implementaciones de mappers entre objetos Java en tiempo de compilación. A diferencia de soluciones basadas en reflection (como ModelMapper), MapStruct genera código Java puro, resultando en mapeo type-safe y de alto rendimiento.

## ¿Por qué MapStruct?

### Comparación: Reflection vs Code Generation

**ModelMapper (Reflection):**
```java
ModelMapper mapper = new ModelMapper();
UsuarioResponseDto dto = mapper.map(usuario, UsuarioResponseDto.class);
```

**Problemas:**
- ❌ Errores en runtime, no en compile-time
- ❌ Performance: reflection es lento
- ❌ Difícil de debuggear
- ❌ No type-safe

**MapStruct (Code Generation):**
```java
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioResponseDto toResponseDto(Usuario usuario);
}

// MapStruct genera en compile-time:
@Component
public class UsuarioMapperImpl implements UsuarioMapper {
    @Override
    public UsuarioResponseDto toResponseDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setEmail(usuario.getEmail());
        // ... código Java puro
        return dto;
    }
}
```

**Ventajas:**
- ✅ Errores en compile-time
- ✅ Performance: código Java puro (sin reflection)
- ✅ Fácil de debuggear (código generado visible)
- ✅ Type-safe

### Performance Comparison

```
Reflection-based (ModelMapper):  ~1000 ops/sec
MapStruct (generated code):      ~100,000 ops/sec
```

**MapStruct es ~100x más rápido** porque no usa reflection.

## Configuración de MapStruct

### pom.xml

```xml
<properties>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>${mapstruct.version}</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**Importante:** Lombok y MapStruct deben procesarse en el orden correcto. Lombok primero genera getters/setters, luego MapStruct los usa.

## Mappers Básicos

### Mapper Simple

```java
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);
    
    // Mapeo automático por nombre de campo
    UsuarioResponseDto toResponseDto(Usuario usuario);
    
    // Mapeo inverso
    Usuario toEntity(UsuarioRequestDto dto);
}
```

**Código Generado:**
```java
@Component
public class UsuarioMapperImpl implements UsuarioMapper {
    @Override
    public UsuarioResponseDto toResponseDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        return dto;
    }
}
```

### componentModel = "spring"

```java
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    // Genera @Component, puede ser inyectado con @Autowired
}
```

**Opciones:**
- `"spring"`: Genera `@Component` (para Spring)
- `"cdi"`: Genera `@ApplicationScoped` (para Jakarta CDI)
- `"default"`: No genera anotación (usa INSTANCE)

## Mapeo Avanzado

### Ignorar Campos

```java
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(target = "passwordHash", ignore = true)  // No mapear passwordHash
    @Mapping(target = "datosPersonales", ignore = true)  // No mapear relación
    @Mapping(target = "fechaCreacion", ignore = true)  // Se establece en @PrePersist
    Usuario toEntity(UsuarioRequestDto dto);
}
```

**Cuándo ignorar:**
- Campos sensibles (passwordHash)
- Relaciones lazy (evitar LazyInitializationException)
- Campos calculados o generados automáticamente
- Campos que se establecen en callbacks JPA

### Mapeo de Campos con Nombres Diferentes

```java
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(source = "idUsuario", target = "id")  // idUsuario → id
    @Mapping(source = "password", target = "passwordHash")  // password → passwordHash
    Usuario toEntity(UsuarioRequestDto dto);
}
```

### Mapeo de Campos Anidados

```java
@Mapper(componentModel = "spring")
public interface CursoMapper {
    // Mapeo automático de campos anidados
    @Mapping(target = "nombreMateria", source = "materia.nombre")
    @Mapping(target = "nombreFormato", source = "formato.nombre")
    CursoResponseDto toResponseDto(Curso curso);
}
```

**Código Generado:**
```java
@Override
public CursoResponseDto toResponseDto(Curso curso) {
    if (curso == null) {
        return null;
    }
    CursoResponseDto dto = new CursoResponseDto();
    dto.setNombreMateria(curso.getMateria() != null ? curso.getMateria().getNombre() : null);
    dto.setNombreFormato(curso.getFormato() != null ? curso.getFormato().getNombre() : null);
    // ...
    return dto;
}
```

### Expresiones Personalizadas

```java
@Mapper(componentModel = "spring")
public interface MatriculaMapper {
    @Mapping(target = "nombreAlumno", 
             expression = "java(entity.getAlumno().getDatosPersonales() != null ? " +
                        "entity.getAlumno().getDatosPersonales().getNombre() + \" \" + " +
                        "entity.getAlumno().getDatosPersonales().getApellidos() : null)")
    MatriculaResponseDto toResponseDto(Matricula entity);
}
```

**Cuándo usar expresiones:**
- Cálculos simples (concatenación, formateo)
- Transformaciones de datos
- Validaciones condicionales

**⚠️ Limitación:** Expresiones complejas hacen el código menos legible. Para lógica compleja, usa métodos `@AfterMapping`.

### Métodos @AfterMapping

```java
@Mapper(componentModel = "spring")
public interface MatriculaMapper {
    MatriculaResponseDto toResponseDto(Matricula entity);
    
    @AfterMapping
    default void calculateNombreAlumno(Matricula entity, @MappingTarget MatriculaResponseDto dto) {
        if (entity.getAlumno() != null && entity.getAlumno().getDatosPersonales() != null) {
            DatosPersonales datos = entity.getAlumno().getDatosPersonales();
            dto.setNombreAlumno(datos.getNombre() + " " + datos.getApellidos());
        }
    }
    
    @AfterMapping
    default void setDefaultValues(@MappingTarget MatriculaResponseDto dto) {
        if (dto.getDescuentoAplicado() == null) {
            dto.setDescuentoAplicado(BigDecimal.ZERO);
        }
    }
}
```

**Ventajas de @AfterMapping:**
- Código más legible que expresiones complejas
- Reutilizable en múltiples métodos de mapeo
- Fácil de testear

### Mapeo de Colecciones

```java
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    // Mapeo automático de listas
    List<UsuarioResponseDto> toResponseDtoList(List<Usuario> usuarios);
    
    // Mapeo de sets
    Set<UsuarioResponseDto> toResponseDtoSet(Set<Usuario> usuarios);
    
    // Mapeo de maps
    Map<String, UsuarioResponseDto> toResponseDtoMap(Map<String, Usuario> usuarios);
}
```

**Código Generado:**
```java
@Override
public List<UsuarioResponseDto> toResponseDtoList(List<Usuario> usuarios) {
    if (usuarios == null) {
        return null;
    }
    List<UsuarioResponseDto> list = new ArrayList<>(usuarios.size());
    for (Usuario usuario : usuarios) {
        list.add(toResponseDto(usuario));
    }
    return list;
}
```

## Mapeo de Update (Parcial)

### updateEntityFromDto

```java
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(target = "idUsuario", ignore = true)  // No actualizar ID
    @Mapping(target = "passwordHash", ignore = true)  // Se maneja en servicio
    @Mapping(target = "datosPersonales", ignore = true)  // Relación separada
    @Mapping(target = "fechaCreacion", ignore = true)  // No modificable
    void updateEntityFromDto(UsuarioRequestDto dto, @MappingTarget Usuario entity);
}
```

**Uso:**
```java
@Service
public class UsuarioService {
    public UsuarioResponseDto update(Long id, UsuarioRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(...);
        usuarioMapper.updateEntityFromDto(dto, usuario);  // Actualiza campos
        // usuario.setPasswordHash(...) si es necesario
        return usuarioMapper.toResponseDto(usuarioRepository.save(usuario));
    }
}
```

**Código Generado:**
```java
@Override
public void updateEntityFromDto(UsuarioRequestDto dto, Usuario entity) {
    if (dto == null) {
        return;
    }
    entity.setEmail(dto.getEmail());
    entity.setRol(dto.getRol());
    entity.setActivo(dto.getActivo());
    // idUsuario, passwordHash, etc. se ignoran
}
```

## Mappers Complejos: Ejemplo Real

### MatriculaMapper Completo

```java
@Mapper(componentModel = "spring", uses = {ConvocatoriaMapper.class})
public interface MatriculaMapper {
    // Mapeo de creación (Request → Entity)
    @Mapping(target = "idMatricula", ignore = true)
    @Mapping(target = "convocatoria", ignore = true)  // Se establece en servicio
    @Mapping(target = "alumno", ignore = true)  // Se establece en servicio
    @Mapping(target = "entidadSubvencionadora", ignore = true)  // Opcional
    @Mapping(target = "facturas", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    @Mapping(target = "fechaMatricula", ignore = true)  // Se establece en @PrePersist
    @Mapping(target = "precioBruto", ignore = true)  // Se calcula en servicio
    @Mapping(target = "descuentoAplicado", ignore = true)  // Se calcula en servicio
    @Mapping(target = "motivoDescuento", ignore = true)  // Se calcula en servicio
    @Mapping(target = "precioFinal", ignore = true)  // Calculado en BD
    @Mapping(target = "fechaCreacion", ignore = true)  // Se establece en @PrePersist
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Matricula toEntity(MatriculaRequestDto dto);
    
    // Mapeo de respuesta (Entity → Response)
    @Mapping(target = "codigoConvocatoria", source = "convocatoria.codigo")
    @Mapping(target = "nombreAlumno", expression = "java(calculateNombreAlumno(entity))")
    MatriculaResponseDto toResponseDto(Matricula entity);
    
    // Mapeo de actualización
    @Mapping(target = "idMatricula", ignore = true)
    @Mapping(target = "convocatoria", ignore = true)
    @Mapping(target = "alumno", ignore = true)
    @Mapping(target = "entidadSubvencionadora", ignore = true)
    @Mapping(target = "facturas", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    @Mapping(target = "fechaMatricula", ignore = true)
    @Mapping(target = "precioBruto", ignore = true)
    @Mapping(target = "descuentoAplicado", ignore = true)
    @Mapping(target = "motivoDescuento", ignore = true)
    @Mapping(target = "precioFinal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(MatriculaRequestDto dto, @MappingTarget Matricula entity);
    
    // Método helper para cálculo
    default String calculateNombreAlumno(Matricula entity) {
        if (entity.getAlumno() != null && 
            entity.getAlumno().getDatosPersonales() != null) {
            DatosPersonales datos = entity.getAlumno().getDatosPersonales();
            return datos.getNombre() + " " + datos.getApellidos();
        }
        return null;
    }
}
```

## Mejores Prácticas

### 1. Usar componentModel = "spring"

```java
// ✅ Bueno - Inyectable en Spring
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioResponseDto toResponseDto(Usuario usuario);
}

// Uso:
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioMapper usuarioMapper;  // Inyectado automáticamente
}
```

### 2. Ignorar Campos que se Manejan en Servicios

```java
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(target = "passwordHash", ignore = true)  // Se hashea en servicio
    @Mapping(target = "fechaCreacion", ignore = true)  // Se establece en @PrePersist
    Usuario toEntity(UsuarioRequestDto dto);
}
```

### 3. Usar @AfterMapping para Lógica Compleja

```java
// ✅ Bueno - Legible y mantenible
@AfterMapping
default void calculateNombreAlumno(Matricula entity, @MappingTarget MatriculaResponseDto dto) {
    // Lógica compleja aquí
}

// ❌ Evitar - Expresión muy larga
@Mapping(target = "nombreAlumno", expression = "java(/* 10 líneas de código */)")
```

### 4. Documentar Mappers Complejos

```java
/**
 * Mapper para convertir entre Matricula entity y DTOs.
 * 
 * Notas importantes:
 * - precioFinal se ignora en toEntity porque se calcula en BD
 * - relaciones lazy se ignoran para evitar LazyInitializationException
 * - campos calculados se generan en @AfterMapping
 */
@Mapper(componentModel = "spring")
public interface MatriculaMapper {
    // ...
}
```

## Debugging MapStruct

### Ver Código Generado

**Ubicación:** `target/generated-sources/annotations/com/academy/academymanager/mapper/`

**Ejemplo:**
```
target/
  generated-sources/
    annotations/
      com/
        academy/
          academymanager/
            mapper/
              UsuarioMapperImpl.java  // Código generado
```

**IntelliJ IDEA:**
1. Build → Rebuild Project
2. Navegar a `target/generated-sources/annotations/`
3. Revisar código generado

### Errores Comunes

**1. Campo no encontrado:**
```
error: Unknown property "nombreCompleto" in result type UsuarioResponseDto.
```

**Solución:** Verificar que el campo existe en el DTO o usar `@Mapping` para mapear a otro campo.

**2. Tipo incompatible:**
```
error: Can't map property "BigDecimal precioBase" to "String precioBase".
```

**Solución:** Usar `@Mapping` con conversión o método helper.

**3. Lombok no procesado:**
```
error: Cannot find getter for property "email" in Usuario.
```

**Solución:** Asegurar que Lombok se procesa antes que MapStruct en `annotationProcessorPaths`.

## Preguntas de Entrevista

### ¿Por qué MapStruct en lugar de ModelMapper o BeanUtils?

**Respuesta:**
"MapStruct genera código Java puro en compile-time, resultando en:
1. **Performance**: ~100x más rápido que reflection-based mappers
2. **Type Safety**: Errores detectados en compile-time, no runtime
3. **Debugging**: Código generado visible y debuggable
4. **Sin Dependencias Runtime**: No necesita librerías en runtime
5. **IDE Support**: Autocompletado y refactoring funcionan correctamente

ModelMapper y BeanUtils usan reflection, que es lento y propenso a errores en runtime."

### ¿Cómo manejas mapeos complejos con lógica de negocio?

**Respuesta:**
"Para lógica simple, uso expresiones en `@Mapping`. Para lógica compleja, uso métodos `@AfterMapping` que son más legibles y testables. Si la lógica es muy compleja o requiere acceso a repositorios, la muevo al servicio y el mapper solo hace el mapeo básico. La clave es mantener los mappers enfocados en transformación de datos, no en lógica de negocio."

### ¿Cómo evitas LazyInitializationException en mappers?

**Respuesta:**
"Uso `@Mapping(target = "relacion", ignore = true)` para ignorar relaciones lazy en el mapeo. Luego, si necesito datos de la relación, uso `@Mapping(source = "relacion.campo", target = "campo")` para mapear campos específicos, lo cual accede a la relación dentro de la transacción. Alternativamente, uso fetch joins en repositorios para cargar relaciones necesarias antes del mapeo."

## Conclusión

MapStruct es la solución ideal para mapeo entre DTOs y entidades en aplicaciones Spring Boot. Su enfoque de code generation proporciona performance, type safety y mantenibilidad superiores a soluciones basadas en reflection.

