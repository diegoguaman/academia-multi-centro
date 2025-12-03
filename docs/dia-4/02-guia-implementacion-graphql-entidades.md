# Guía de Implementación GraphQL para Entidades Restantes

## Tabla de Contenidos
1. [Estructura del Proyecto](#estructura-del-proyecto)
2. [Paso a Paso: Implementar una Nueva Entidad](#paso-a-paso-implementar-una-nueva-entidad)
3. [Ejemplos Completos](#ejemplos-completos)
4. [Resolvers de Relaciones (Batch Mapping)](#resolvers-de-relaciones-batch-mapping)
5. [Validación y Manejo de Errores](#validación-y-manejo-de-errores)
6. [Testing de Resolvers](#testing-de-resolvers)

---

## Estructura del Proyecto

```
src/main/java/com/academy/academymanager/
├── graphql/
│   ├── scalar/
│   │   ├── DateTimeScalar.java        # ✅ Ya implementado
│   │   └── BigDecimalScalar.java      # ✅ Ya implementado
│   └── resolver/
│       ├── MatriculaResolver.java     # ✅ Ejemplo implementado
│       ├── CursoResolver.java         # ✅ Ejemplo implementado
│       ├── ConvocatoriaResolver.java  # ⏳ Pendiente
│       ├── UsuarioResolver.java       # ⏳ Pendiente
│       └── ... (resto de resolvers)
├── config/
│   └── GraphQLConfig.java             # ✅ Ya configurado
└── ...

src/main/resources/
└── graphql/
    └── schema.graphqls                # ✅ Schema base creado
```

---

## Paso a Paso: Implementar una Nueva Entidad

### Ejemplo: Implementar ConvocatoriaResolver

#### Paso 1: Verificar el Schema GraphQL

El schema ya debería tener definida la entidad. Verifica en `schema.graphqls`:

```graphql
type Convocatoria {
    idConvocatoria: ID!
    codigo: String!
    fechaInicio: String!
    fechaFin: String!
    activo: Boolean!
    fechaCreacion: DateTime!
    fechaModificacion: DateTime
    # Relations
    curso: Curso!
    profesor: Usuario!
    centro: Centro!
    matriculas: [Matricula!]!
}

type Query {
    convocatoria(id: ID!): Convocatoria
    convocatorias(activo: Boolean, idCentro: ID): [Convocatoria!]!
}

type Mutation {
    createConvocatoria(input: ConvocatoriaInput!): Convocatoria!
    updateConvocatoria(id: ID!, input: ConvocatoriaInput!): Convocatoria!
    deleteConvocatoria(id: ID!): Boolean!
}

input ConvocatoriaInput {
    codigo: String
    idCurso: ID!
    idProfesor: ID!
    idCentro: ID!
    fechaInicio: String!
    fechaFin: String!
    activo: Boolean
}
```

**Nota:** Si no está en el schema, agrégala siguiendo el patrón de `Matricula` o `Curso`.

#### Paso 2: Crear el Resolver

Crea `ConvocatoriaResolver.java` en `src/main/java/com/academy/academymanager/graphql/resolver/`:

```java
package com.academy.academymanager.graphql.resolver;

import com.academy.academymanager.dto.request.ConvocatoriaRequestDto;
import com.academy.academymanager.dto.response.ConvocatoriaResponseDto;
import com.academy.academymanager.service.ConvocatoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ConvocatoriaResolver {
    private final ConvocatoriaService convocatoriaService;

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ALUMNO', 'ADMINISTRATIVO')")
    public ConvocatoriaResponseDto convocatoria(@Argument final Long id) {
        return convocatoriaService.findById(id);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ALUMNO', 'ADMINISTRATIVO')")
    public List<ConvocatoriaResponseDto> convocatorias(
            @Argument final Boolean activo,
            @Argument final Long idCentro
    ) {
        if (idCentro != null) {
            // Implementar método en service: findByCentroId(idCentro)
            return convocatoriaService.findByCentroId(idCentro);
        }
        if (activo != null && activo) {
            return convocatoriaService.findActive();
        }
        return convocatoriaService.findAll();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public ConvocatoriaResponseDto createConvocatoria(
            @Argument final ConvocatoriaInput input
    ) {
        ConvocatoriaRequestDto requestDto = ConvocatoriaRequestDto.builder()
                .codigo(input.getCodigo())
                .idCurso(input.getIdCurso())
                .idProfesor(input.getIdProfesor())
                .idCentro(input.getIdCentro())
                .fechaInicio(input.getFechaInicio())
                .fechaFin(input.getFechaFin())
                .activo(input.getActivo() != null ? input.getActivo() : true)
                .build();
        return convocatoriaService.create(requestDto);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public ConvocatoriaResponseDto updateConvocatoria(
            @Argument final Long id,
            @Argument final ConvocatoriaInput input
    ) {
        ConvocatoriaRequestDto requestDto = ConvocatoriaRequestDto.builder()
                .codigo(input.getCodigo())
                .idCurso(input.getIdCurso())
                .idProfesor(input.getIdProfesor())
                .idCentro(input.getIdCentro())
                .fechaInicio(input.getFechaInicio())
                .fechaFin(input.getFechaFin())
                .activo(input.getActivo())
                .build();
        return convocatoriaService.update(id, requestDto);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteConvocatoria(@Argument final Long id) {
        convocatoriaService.delete(id);
        return true;
    }

    // Input type inner class
    public static class ConvocatoriaInput {
        private String codigo;
        private Long idCurso;
        private Long idProfesor;
        private Long idCentro;
        private String fechaInicio;
        private String fechaFin;
        private Boolean activo;

        // Getters and setters (usar Lombok @Data si prefieres)
        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        // ... resto de getters/setters
    }
}
```

#### Paso 3: Mapear DTOs a Input Types

**Importante:** Los Input Types en GraphQL son diferentes a los DTOs. Tienes dos opciones:

**Opción A: Usar clases internas (como en los ejemplos)**
- Ventaja: Todo en un archivo
- Desventaja: Más verboso

**Opción B: Crear clases separadas en `graphql/input/`**
```java
package com.academy.academymanager.graphql.input;

import lombok.Data;

@Data
public class ConvocatoriaInput {
    private String codigo;
    private Long idCurso;
    private Long idProfesor;
    private Long idCentro;
    private String fechaInicio;
    private String fechaFin;
    private Boolean activo;
}
```

**Recomendación:** Usa la opción que prefieras, pero sé consistente en todo el proyecto.

#### Paso 4: Verificar que el Service tiene los métodos necesarios

Si tu `ConvocatoriaService` no tiene `findByCentroId()`, agrégalo:

```java
@Transactional(readOnly = true)
public List<ConvocatoriaResponseDto> findByCentroId(Long idCentro) {
    return convocatoriaRepository.findByCentroIdCentro(idCentro).stream()
            .map(convocatoriaMapper::toResponseDto)
            .collect(Collectors.toList());
}
```

Y en el Repository:

```java
List<Convocatoria> findByCentroIdCentro(Long idCentro);
```

#### Paso 5: Probar el Resolver

1. Inicia la aplicación
2. Accede a GraphiQL (si está habilitado): `http://localhost:8080/graphiql`
3. Ejecuta una query de prueba:

```graphql
query {
  convocatorias(activo: true) {
    codigo
    fechaInicio
    curso {
      nombre
    }
    centro {
      nombre
    }
  }
}
```

---

## Ejemplos Completos

### Ejemplo 1: Resolver Simple (Usuario)

```java
@Controller
@RequiredArgsConstructor
public class UsuarioResolver {
    private final UsuarioService usuarioService;

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ADMINISTRATIVO')")
    public UsuarioResponseDto usuario(@Argument final Long id) {
        return usuarioService.findById(id);
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioResponseDto> usuarios(@Argument final Rol rol) {
        if (rol != null) {
            return usuarioService.findByRol(rol);
        }
        return usuarioService.findAll();
    }
}
```

### Ejemplo 2: Resolver con Filtros Complejos (Centro)

```java
@Controller
@RequiredArgsConstructor
public class CentroResolver {
    private final CentroService centroService;

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public CentroResponseDto centro(@Argument final Long id) {
        return centroService.findById(id);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    public List<CentroResponseDto> centros(
            @Argument final Boolean activo,
            @Argument final Long idEmpresa,
            @Argument final Long idComunidad
    ) {
        // Implementar lógica de filtrado combinado
        List<CentroResponseDto> all = centroService.findAll();
        
        if (idEmpresa != null) {
            all = all.stream()
                    .filter(c -> c.getIdEmpresa().equals(idEmpresa))
                    .toList();
        }
        if (idComunidad != null) {
            all = all.stream()
                    .filter(c -> c.getIdComunidad().equals(idComunidad))
                    .toList();
        }
        if (activo != null) {
            all = all.stream()
                    .filter(c -> c.getActivo().equals(activo))
                    .toList();
        }
        return all;
    }
}
```

**Nota:** Para filtros complejos, es mejor implementarlos en el Service/Repository para mejor performance.

---

## Resolvers de Relaciones (Batch Mapping)

### Problema N+1

Cuando resuelves relaciones, puedes encontrarte con el problema N+1:

```graphql
query {
  convocatorias {
    codigo
    curso {           # <- Esto puede causar N+1
      nombre
    }
    profesor {        # <- Esto también
      email
    }
  }
}
```

**Sin optimización:** 
- 1 query para convocatorias
- N queries para cursos (una por convocatoria)
- N queries para profesores (una por convocatoria)
- Total: 1 + 2N queries

### Solución: Batch Mapping

```java
@Controller
@RequiredArgsConstructor
public class ConvocatoriaResolver {
    private final ConvocatoriaService convocatoriaService;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;

    // Queries normales...
    
    /**
     * Batch resolver para la relación curso.
     * GraphQL automáticamente agrupa todas las convocatorias
     * y ejecuta este método una sola vez.
     */
    @BatchMapping
    public Map<ConvocatoriaResponseDto, CursoResponseDto> curso(
            List<ConvocatoriaResponseDto> convocatorias
    ) {
        // Extraer IDs únicos
        List<Long> cursoIds = convocatorias.stream()
                .map(ConvocatoriaResponseDto::getIdCurso)
                .distinct()
                .toList();
        
        // 1 sola query para todos los cursos
        Map<Long, CursoResponseDto> cursosMap = cursoRepository
                .findByIdIn(cursoIds)
                .stream()
                .map(cursoMapper::toResponseDto)
                .collect(Collectors.toMap(
                    CursoResponseDto::getIdCurso,
                    curso -> curso
                ));
        
        // Mapear cada convocatoria a su curso
        return convocatorias.stream()
                .collect(Collectors.toMap(
                    convocatoria -> convocatoria,
                    convocatoria -> cursosMap.get(convocatoria.getIdCurso())
                ));
    }

    /**
     * Batch resolver para la relación profesor.
     */
    @BatchMapping
    public Map<ConvocatoriaResponseDto, UsuarioResponseDto> profesor(
            List<ConvocatoriaResponseDto> convocatorias
    ) {
        List<Long> profesorIds = convocatorias.stream()
                .map(ConvocatoriaResponseDto::getIdProfesor)
                .distinct()
                .toList();
        
        Map<Long, UsuarioResponseDto> profesoresMap = usuarioRepository
                .findByIdIn(profesorIds)
                .stream()
                .map(usuarioMapper::toResponseDto)
                .collect(Collectors.toMap(
                    UsuarioResponseDto::getIdUsuario,
                    usuario -> usuario
                ));
        
        return convocatorias.stream()
                .collect(Collectors.toMap(
                    convocatoria -> convocatoria,
                    convocatoria -> profesoresMap.get(convocatoria.getIdProfesor())
                ));
    }
}
```

**Con optimización:**
- 1 query para convocatorias
- 1 query para todos los cursos (batch)
- 1 query para todos los profesores (batch)
- Total: 3 queries (independiente del número de convocatorias)

---

## Validación y Manejo de Errores

### Validación de Inputs

Puedes usar Bean Validation en los Input Types:

```java
public static class ConvocatoriaInput {
    @NotBlank(message = "El código es obligatorio")
    private String codigo;
    
    @NotNull(message = "El curso es obligatorio")
    private Long idCurso;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDate fechaInicio;
    
    // ...
}
```

Y en el resolver, validar:

```java
@MutationMapping
public ConvocatoriaResponseDto createConvocatoria(
        @Valid @Argument final ConvocatoriaInput input
) {
    // Spring automáticamente valida si usas @Valid
    // Si hay errores, lanza MethodArgumentNotValidException
    // ...
}
```

### Manejo de Errores Personalizado

Crea un `GraphQLErrorHandler`:

```java
@Component
public class GraphQLExceptionHandler implements GraphQLErrorHandler {
    
    @Override
    public List<GraphQLError> processErrors(List<GraphQLError> errors) {
        return errors.stream()
                .map(this::handleError)
                .collect(Collectors.toList());
    }
    
    private GraphQLError handleError(GraphQLError error) {
        if (error instanceof ExceptionWhileDataFetching) {
            ExceptionWhileDataFetching exError = (ExceptionWhileDataFetching) error;
            Throwable cause = exError.getException();
            
            if (cause instanceof IllegalArgumentException) {
                return GraphqlErrorBuilder.newError()
                        .message(cause.getMessage())
                        .errorType(ErrorType.BAD_REQUEST)
                        .build();
            }
            
            if (cause instanceof EntityNotFoundException) {
                return GraphqlErrorBuilder.newError()
                        .message(cause.getMessage())
                        .errorType(ErrorType.NOT_FOUND)
                        .build();
            }
        }
        return error;
    }
}
```

---

## Testing de Resolvers

### Test Unitario de Resolver

```java
@ExtendWith(MockitoExtension.class)
class ConvocatoriaResolverTest {
    
    @Mock
    private ConvocatoriaService convocatoriaService;
    
    @InjectMocks
    private ConvocatoriaResolver resolver;
    
    @Test
    void shouldReturnConvocatoriaWhenIdExists() {
        Long id = 1L;
        ConvocatoriaResponseDto expected = ConvocatoriaResponseDto.builder()
                .idConvocatoria(id)
                .codigo("CONV-2024-001")
                .build();
        
        when(convocatoriaService.findById(id)).thenReturn(expected);
        
        ConvocatoriaResponseDto actual = resolver.convocatoria(id);
        
        assertThat(actual).isEqualTo(expected);
        verify(convocatoriaService).findById(id);
    }
}
```

### Test de Integración GraphQL

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureGraphQlTester
class ConvocatoriaResolverIntegrationTest {
    
    @Autowired
    private GraphQlTester graphQlTester;
    
    @Test
    void shouldReturnConvocatorias() {
        String query = """
            query {
              convocatorias(activo: true) {
                codigo
                fechaInicio
                curso {
                  nombre
                }
              }
            }
            """;
        
        graphQlTester.document(query)
                .execute()
                .path("convocatorias[*].codigo")
                .entityList(String.class)
                .hasSizeGreaterThan(0);
    }
}
```

---

## Checklist para Implementar una Nueva Entidad

- [ ] Verificar/agregar tipo en `schema.graphqls`
- [ ] Crear Resolver en `graphql/resolver/`
- [ ] Implementar queries (`@QueryMapping`)
- [ ] Implementar mutations (`@MutationMapping`)
- [ ] Agregar autorización (`@PreAuthorize`)
- [ ] Crear Input Types (clases internas o separadas)
- [ ] Implementar batch mappings para relaciones (si aplica)
- [ ] Agregar validación de inputs (si aplica)
- [ ] Verificar que Services tienen métodos necesarios
- [ ] Escribir tests unitarios
- [ ] Escribir tests de integración
- [ ] Probar en GraphiQL o Postman

---

## Entidades Pendientes de Implementar

1. **Convocatoria** - Resolver básico + batch mappings para curso, profesor, centro
2. **Usuario** - Resolver básico, cuidado con datos sensibles (password)
3. **Centro** - Resolver con filtros múltiples
4. **Empresa** - Resolver simple
5. **Comunidad** - Resolver simple
6. **Materia** - Resolver simple
7. **Formato** - Resolver simple
8. **Calificacion** - Resolver con relación a matrícula
9. **Factura** - Resolver con relación a matrícula
10. **EntidadSubvencionadora** - Resolver simple
11. **DatosPersonales** - Resolver con relación a usuario (one-to-one)

---

**Última actualización**: Día 4 - Academia Multi-Centro
**Próximos pasos**: Implementar resolvers restantes siguiendo esta guía

