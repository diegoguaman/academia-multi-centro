# DTOs: Request y Response - Separación y Optimización

## Introducción

Los DTOs (Data Transfer Objects) son objetos que transportan datos entre capas de la aplicación sin exponer la estructura interna de las entidades. En aplicaciones Spring Boot empresariales, los DTOs son esenciales para mantener la separación de concerns, seguridad y performance.

## ¿Por qué DTOs?

### Problemas sin DTOs

**Exponer Entidades Directamente:**

```java
@RestController
public class UsuarioController {
    @GetMapping("/usuarios/{id}")
    public Usuario getUsuario(@PathVariable Long id) {
        return usuarioService.findById(id);  // ❌ Retorna entidad JPA
    }
}
```

**Problemas:**
1. **Lazy Loading Exceptions**: Si accedes a relaciones lazy fuera de la transacción
2. **Exposición de Estructura Interna**: Expone campos que no deberían ser públicos
3. **Serialización Circular**: Relaciones bidireccionales causan loops infinitos
4. **Performance**: Serializa más datos de los necesarios
5. **Seguridad**: Expone campos sensibles (passwordHash, etc.)
6. **Acoplamiento**: Cambios en entidades afectan la API

**Ejemplo de Problema:**
```java
// En el servicio (transacción activa)
Usuario usuario = usuarioRepository.findById(1L).get();
// usuario.getDatosPersonales() funciona aquí

// En el controller (transacción cerrada)
return usuario;  // ❌ LazyInitializationException al serializar
```

## DTOs: La Solución

### Request DTOs vs Response DTOs

**Request DTOs (Entrada):**
- Datos que el cliente envía al servidor
- Validaciones de entrada
- Solo campos necesarios para crear/actualizar

**Response DTOs (Salida):**
- Datos que el servidor envía al cliente
- Solo campos que el cliente necesita
- Puede incluir datos calculados o agregados

## Request DTOs

### Características

1. **Validaciones de Entrada**: Jakarta Validation annotations
2. **Solo Campos Necesarios**: No incluir campos calculados o generados
3. **Inmutabilidad Opcional**: Pueden ser mutables para facilitar binding

### Ejemplo: UsuarioRequestDto

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotNull(message = "Role is required")
    private Usuario.Rol rol;
    
    private Boolean activo;  // Opcional, tiene valor por defecto en entidad
}
```

### Validaciones Comunes

```java
public class CursoRequestDto {
    // NotNull - Campo requerido
    @NotNull(message = "Materia ID is required")
    private Long idMateria;
    
    // NotBlank - String no vacío
    @NotBlank(message = "Name is required")
    private String nombre;
    
    // Positive - Número positivo
    @NotNull
    @Positive(message = "Base price must be positive")
    private BigDecimal precioBase;
    
    // DecimalMin/Max - Rango decimal
    @DecimalMin(value = "0.0", message = "Grade must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Grade must be at most 10.0")
    private BigDecimal nota;
    
    // Size - Tamaño de colección o string
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String nombre;
    
    // Past/Future - Fechas
    @Past(message = "Start date must be in the past")
    private LocalDate fechaInicio;
    
    @Future(message = "End date must be in the future")
    private LocalDate fechaFin;
    
    // Pattern - Expresión regular
    @Pattern(regexp = "^[A-Z]{2}\\d{8}$", message = "CIF must match pattern")
    private String cif;
}
```

### ¿Qué Lógica va en Request DTOs?

**✅ Va en Request DTOs:**
- Validaciones de formato (email, pattern, size)
- Validaciones de tipo (NotNull, Positive)
- Campos de entrada del usuario

**❌ NO va en Request DTOs:**
- Lógica de negocio (va en servicios)
- Cálculos (va en servicios)
- Validaciones que requieren acceso a base de datos (va en servicios)
- Campos calculados o generados

**Ejemplo de Separación:**

```java
// Request DTO - Solo validaciones de formato
public class MatriculaRequestDto {
    @NotNull
    private Long idConvocatoria;
    
    @NotNull
    private Long idAlumno;
    
    // No valida si la convocatoria existe - eso va en el servicio
}

// Servicio - Validaciones de negocio
public MatriculaResponseDto create(MatriculaRequestDto dto) {
    // Validar que la convocatoria existe (requiere acceso a BD)
    Convocatoria convocatoria = convocatoriaRepository.findById(dto.getIdConvocatoria())
            .orElseThrow(() -> new IllegalArgumentException("Convocatoria not found"));
    
    // Validar reglas de negocio
    if (convocatoria.getFechaInicio().isBefore(LocalDate.now())) {
        throw new IllegalArgumentException("Convocatoria has already started");
    }
    
    // ...
}
```

## Response DTOs

### Características

1. **Solo Datos Necesarios**: Exponer solo lo que el cliente necesita
2. **Datos Calculados**: Puede incluir campos calculados o agregados
3. **Relaciones Simplificadas**: En lugar de objetos anidados, IDs o nombres
4. **Inmutabilidad Recomendada**: Una vez creado, no debería cambiar

### Ejemplo: MatriculaResponseDto

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatriculaResponseDto {
    // ID de la entidad
    private Long idMatricula;
    
    // Código único
    private String codigo;
    
    // IDs de relaciones (en lugar de objetos completos)
    private Long idConvocatoria;
    private String codigoConvocatoria;  // Campo útil para el cliente
    
    private Long idAlumno;
    private String nombreAlumno;  // Nombre completo calculado
    
    // Fechas
    private LocalDateTime fechaMatricula;
    
    // Datos financieros
    private BigDecimal precioBruto;
    private BigDecimal descuentoAplicado;
    private String motivoDescuento;
    private BigDecimal importeSubvencionado;
    private BigDecimal precioFinal;  // Campo calculado en BD
    
    // Estado
    private Matricula.EstadoPago estadoPago;
    
    // Auditoría (solo lectura)
    private LocalDateTime fechaCreacion;
    
    // NO incluir:
    // - passwordHash (seguridad)
    // - Objetos completos de relaciones (performance)
    // - Campos internos de la entidad
}
```

### ¿Qué Lógica va en Response DTOs?

**✅ Va en Response DTOs:**
- Campos calculados que mejoran la UX (nombreAlumno, codigoConvocatoria)
- Agregaciones simples (total, promedio)
- Formateo de datos para presentación

**❌ NO va en Response DTOs:**
- Lógica de negocio compleja (va en servicios)
- Validaciones (ya se validaron en Request)
- Acceso a base de datos (va en servicios/mappers)

**Ejemplo de Campo Calculado en Response:**

```java
// Mapper - Calcula nombre completo
@Mapping(target = "nombreAlumno", 
         expression = "java(entity.getAlumno().getDatosPersonales() != null ? " +
                      "entity.getAlumno().getDatosPersonales().getNombre() + \" \" + " +
                      "entity.getAlumno().getDatosPersonales().getApellidos() : null)")
MatriculaResponseDto toResponseDto(Matricula entity);
```

## Comparación: Request vs Response

### MatriculaRequestDto (Request)

```java
public class MatriculaRequestDto {
    // Solo campos necesarios para CREAR/ACTUALIZAR
    private String codigo;  // Opcional, se genera si no se proporciona
    private Long idConvocatoria;  // Requerido
    private Long idAlumno;  // Requerido
    private Long idEntidadSubvencionadora;  // Opcional
    private BigDecimal importeSubvencionado;  // Opcional
    
    // NO incluir:
    // - idMatricula (se genera)
    // - precioBruto (se calcula)
    // - descuentoAplicado (se calcula)
    // - precioFinal (se calcula)
    // - fechaMatricula (se establece automáticamente)
    // - estadoPago (tiene valor por defecto)
}
```

### MatriculaResponseDto (Response)

```java
public class MatriculaResponseDto {
    // Todos los campos que el cliente necesita VER
    private Long idMatricula;  // ID generado
    private String codigo;  // Código único
    private Long idConvocatoria;
    private String codigoConvocatoria;  // Útil para el cliente
    private Long idAlumno;
    private String nombreAlumno;  // Calculado para UX
    private LocalDateTime fechaMatricula;
    private BigDecimal precioBruto;
    private BigDecimal descuentoAplicado;
    private String motivoDescuento;
    private BigDecimal importeSubvencionado;
    private BigDecimal precioFinal;  // Calculado en BD
    private Matricula.EstadoPago estadoPago;
    private LocalDateTime fechaCreacion;
}
```

## Ventajas de Usar DTOs

### 1. Seguridad

**Sin DTOs:**
```java
// Expone passwordHash
public Usuario getUsuario(Long id) {
    return usuarioRepository.findById(id).get();
    // Serializa: { idUsuario: 1, email: "...", passwordHash: "$2a$10$..." }
}
```

**Con DTOs:**
```java
// No expone passwordHash
public UsuarioResponseDto getUsuario(Long id) {
    Usuario usuario = usuarioRepository.findById(id).get();
    return usuarioMapper.toResponseDto(usuario);
    // Serializa: { idUsuario: 1, email: "..." }  // Sin passwordHash
}
```

### 2. Performance

**Sin DTOs:**
```java
// Serializa toda la entidad con relaciones
public Usuario getUsuario(Long id) {
    return usuarioRepository.findById(id).get();
    // Serializa: usuario + datosPersonales + todas las relaciones
}
```

**Con DTOs:**
```java
// Serializa solo lo necesario
public UsuarioResponseDto getUsuario(Long id) {
    Usuario usuario = usuarioRepository.findById(id).get();
    return usuarioMapper.toResponseDto(usuario);
    // Serializa: solo campos del DTO, sin relaciones pesadas
}
```

### 3. Versionado de API

**Sin DTOs:**
```java
// Cambio en entidad afecta API inmediatamente
@Entity
public class Usuario {
    private String email;
    private String nuevoCampo;  // ❌ Se expone automáticamente
}
```

**Con DTOs:**
```java
// Cambio en entidad no afecta API
@Entity
public class Usuario {
    private String email;
    private String nuevoCampo;  // ✅ No se expone hasta actualizar DTO
}

// DTO puede evolucionar independientemente
public class UsuarioResponseDto {
    private String email;
    // nuevoCampo se agrega cuando esté listo
}
```

### 4. Desacoplamiento

**Sin DTOs:**
```java
// Cambios en entidad rompen la API
@Entity
public class Usuario {
    private String email;
    // Cambiar a: private Email email;  // ❌ Rompe serialización
}
```

**Con DTOs:**
```java
// Cambios en entidad no afectan DTO
@Entity
public class Usuario {
    private Email email;  // ✅ Cambio interno
}

// DTO mantiene compatibilidad
public class UsuarioResponseDto {
    private String email;  // ✅ Mantiene formato string para API
}
```

## Mejores Prácticas

### 1. Validaciones en Request, No en Response

```java
// ✅ Request - Validaciones
public class UsuarioRequestDto {
    @NotBlank
    @Email
    private String email;
}

// ❌ Response - No validaciones (solo lectura)
public class UsuarioResponseDto {
    private String email;  // Ya validado, solo se lee
}
```

### 2. Separar Request de Update

```java
// Para crear - algunos campos opcionales
public class CreateUsuarioRequestDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private Boolean activo;  // Opcional, tiene default
}

// Para actualizar - ID requerido, otros opcionales
public class UpdateUsuarioRequestDto {
    @NotNull
    private Long idUsuario;
    private String email;  // Opcional
    private String password;  // Opcional
    private Boolean activo;  // Opcional
}
```

### 3. Usar Builder para Construcción Flexible

```java
@Builder
public class MatriculaResponseDto {
    private Long idMatricula;
    private String codigo;
    
    @Builder.Default
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;
}

// Uso en mapper o servicio
MatriculaResponseDto dto = MatriculaResponseDto.builder()
    .idMatricula(matricula.getIdMatricula())
    .codigo(matricula.getCodigo())
    .descuentoAplicado(matricula.getDescuentoAplicado())
    .build();
```

### 4. Documentar Campos Importantes

```java
/**
 * DTO para respuesta de matrícula.
 * 
 * precioFinal: Campo calculado automáticamente por la base de datos
 * como precioBruto - descuentoAplicado - importeSubvencionado
 */
public class MatriculaResponseDto {
    /**
     * Precio final calculado. Este campo es de solo lectura y se calcula
     * automáticamente en la base de datos.
     */
    private BigDecimal precioFinal;
}
```

## Preguntas de Entrevista

### ¿Por qué usar DTOs en lugar de exponer entidades directamente?

**Respuesta:**
"Los DTOs proporcionan múltiples ventajas:
1. **Seguridad**: No exponen campos sensibles como passwordHash
2. **Performance**: Serializan solo los datos necesarios, no relaciones completas
3. **Desacoplamiento**: Cambios en entidades no afectan la API
4. **Versionado**: La API puede evolucionar independientemente de las entidades
5. **Lazy Loading**: Evitan LazyInitializationException al no exponer relaciones lazy
6. **Validación**: Request DTOs validan entrada antes de procesar"

### ¿Qué validaciones van en Request DTOs vs Servicios?

**Respuesta:**
"**Request DTOs**: Validaciones de formato y tipo que no requieren acceso a datos:
- @NotNull, @NotBlank, @Email, @Size, @Pattern, @Positive, etc.
- Validaciones que se pueden hacer sin consultar la base de datos

**Servicios**: Validaciones de negocio que requieren acceso a datos:
- Verificar que una entidad existe
- Validar reglas de negocio complejas
- Verificar permisos o estados
- Validaciones que requieren consultar repositorios"

### ¿Cómo manejas campos calculados en Response DTOs?

**Respuesta:**
"Los campos calculados se generan en el mapper usando expresiones de MapStruct. Por ejemplo, `nombreAlumno` se calcula concatenando nombre y apellidos de datosPersonales. Para campos calculados en la base de datos (como precioFinal con GENERATED ALWAYS AS), simplemente se mapean desde la entidad. La clave es que el cálculo se hace una vez (en mapper o BD) y el resultado se incluye en el DTO, no se calcula en cada acceso."

## Conclusión

Los DTOs son esenciales para crear APIs seguras, performantes y mantenibles. La separación clara entre Request (entrada validada) y Response (salida optimizada) es fundamental en aplicaciones empresariales.

