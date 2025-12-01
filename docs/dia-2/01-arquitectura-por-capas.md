# Arquitectura por Capas: Fundamentos y Aplicación en Spring Boot

## Introducción

La arquitectura por capas (Layered Architecture) es un patrón arquitectónico que organiza el código en capas horizontales, cada una con responsabilidades específicas. En el contexto de Spring Boot y aplicaciones empresariales, esta arquitectura proporciona separación de concerns, mantenibilidad y escalabilidad.

## ¿Por qué Arquitectura por Capas?

### Ventajas Clave

1. **Separación de Responsabilidades (SoC)**
   - Cada capa tiene una responsabilidad única y bien definida
   - Facilita el mantenimiento y la evolución del código
   - Reduce el acoplamiento entre componentes

2. **Testabilidad**
   - Cada capa puede ser testeada de forma independiente
   - Facilita el uso de mocks y stubs
   - Permite tests unitarios e integración más claros

3. **Reutilización**
   - La lógica de negocio puede ser reutilizada desde diferentes puntos de entrada
   - Los repositorios pueden ser compartidos entre servicios

4. **Escalabilidad**
   - Fácil agregar nuevas funcionalidades sin afectar capas existentes
   - Permite optimizaciones por capa (caché, transacciones, etc.)

5. **Onboarding de Desarrolladores**
   - Estructura predecible facilita el aprendizaje del código
   - Nuevos desarrolladores entienden rápidamente dónde buscar/modificar código

## Capas en Nuestra Aplicación

### 1. Capa de Dominio (Domain Layer)

**Ubicación:** `com.academy.academymanager.domain.entity`

**Responsabilidades:**
- Representar las entidades del negocio
- Contener la estructura de datos
- Definir relaciones entre entidades
- Validaciones básicas a nivel de entidad

**Ejemplo:**
```java
@Entity
@Table(name = "matricula")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Matricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_matricula")
    private Long idMatricula;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_convocatoria", nullable = false)
    private Convocatoria convocatoria;
    
    // Lógica de negocio básica en callbacks
    @PrePersist
    protected void onCreate() {
        if (fechaMatricula == null) {
            fechaMatricula = LocalDateTime.now();
        }
    }
}
```

**Principios:**
- **Anemic Domain Model Anti-pattern**: Evitar entidades que solo son "getters/setters"
- **Rich Domain Model**: Incluir lógica de negocio básica cuando tiene sentido
- **JPA Annotations**: Mapeo claro a la base de datos

### 2. Capa de Repositorio (Repository Layer)

**Ubicación:** `com.academy.academymanager.repository`

**Responsabilidades:**
- Abstracción del acceso a datos
- Métodos de consulta personalizados
- Operaciones CRUD estándar
- Optimización de queries

**Ejemplo:**
```java
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    // Query methods automáticos por convención de nombres
    Optional<Matricula> findByCodigo(String codigo);
    
    // Métodos que verifican existencia (más eficientes que findById)
    boolean existsByCodigo(String codigo);
    
    // Queries por relaciones
    List<Matricula> findByAlumnoIdUsuario(Long idAlumno);
    
    // Queries con condiciones complejas
    List<Matricula> findByEstadoPago(Matricula.EstadoPago estadoPago);
}
```

**¿Por qué crear métodos personalizados?**

1. **Performance**: `existsByCodigo()` es más eficiente que `findByCodigo().isPresent()`
2. **Legibilidad**: Nombres descriptivos mejoran la comprensión del código
3. **Reutilización**: Evita duplicar lógica de consulta en servicios
4. **Type Safety**: Spring Data JPA valida métodos en compile-time

**Ventajas de Spring Data JPA:**
- **Query Methods**: Generación automática de queries por convención
- **@Query**: Queries JPQL o nativas personalizadas
- **Specifications**: Queries dinámicas complejas
- **Paging and Sorting**: Soporte nativo para paginación

### 3. Capa de Servicio (Service Layer)

**Ubicación:** `com.academy.academymanager.service`

**Responsabilidades:**
- Lógica de negocio
- Orquestación de operaciones
- Validaciones de negocio
- Manejo de transacciones
- Coordinación entre repositorios

**Ejemplo Completo:**
```java
@Service
@RequiredArgsConstructor
@Transactional
public class MatriculaService {
    private final MatriculaRepository matriculaRepository;
    private final ConvocatoriaRepository convocatoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MatriculaMapper matriculaMapper;
    
    // Constantes de negocio
    private static final BigDecimal DISCAPACIDAD_THRESHOLD = new BigDecimal("33.0");
    private static final BigDecimal DISCAPACIDAD_DISCOUNT_PERCENTAGE = new BigDecimal("0.20");
    
    public MatriculaResponseDto create(MatriculaRequestDto requestDto) {
        // 1. Validar dependencias
        Convocatoria convocatoria = convocatoriaRepository.findById(requestDto.getIdConvocatoria())
                .orElseThrow(() -> new IllegalArgumentException("Convocatoria not found"));
        
        // 2. Validar reglas de negocio
        Usuario alumno = usuarioRepository.findById(requestDto.getIdAlumno())
                .orElseThrow(() -> new IllegalArgumentException("Alumno not found"));
        if (!alumno.getRol().equals(Usuario.Rol.ALUMNO)) {
            throw new IllegalArgumentException("User is not an ALUMNO");
        }
        
        // 3. Mapear DTO a Entity
        Matricula matricula = matriculaMapper.toEntity(requestDto);
        matricula.setConvocatoria(convocatoria);
        matricula.setAlumno(alumno);
        
        // 4. Aplicar lógica de negocio
        calculatePricing(matricula);
        
        // 5. Persistir
        Matricula saved = matriculaRepository.save(matricula);
        
        // 6. Retornar DTO
        return matriculaMapper.toResponseDto(saved);
    }
    
    private void calculatePricing(Matricula matricula) {
        // Lógica compleja de cálculo de precios
        BigDecimal precioBase = matricula.getConvocatoria().getCurso().getPrecioBase();
        matricula.setPrecioBruto(precioBase);
        
        // Aplicar descuentos según reglas de negocio
        // ...
    }
}
```

**Principios de Servicios Completos:**

1. **Single Responsibility**: Un servicio por entidad principal
2. **Transaction Management**: `@Transactional` para operaciones que modifican datos
3. **Error Handling**: Excepciones descriptivas con mensajes claros
4. **Business Logic**: Toda la lógica de negocio debe estar aquí, no en controllers
5. **Dependency Injection**: Usar `@RequiredArgsConstructor` con `final` fields (inmutabilidad)

**Anotaciones Clave:**
- `@Service`: Marca la clase como servicio de Spring
- `@Transactional`: Gestiona transacciones automáticamente
- `@Transactional(readOnly = true)`: Optimiza queries de solo lectura

### 4. Capa de DTOs (Data Transfer Objects)

**Ubicación:** 
- Request: `com.academy.academymanager.dto.request`
- Response: `com.academy.academymanager.dto.response`

**Responsabilidades:**
- Transferir datos entre capas
- Validación de entrada
- Protección de la capa de dominio
- Optimización de transferencia de datos

**¿Por qué usar DTOs?**

1. **Seguridad**: No exponer entidades JPA directamente (evita lazy loading exceptions)
2. **Performance**: Transferir solo los datos necesarios
3. **Versionado**: Cambiar DTOs sin afectar entidades
4. **Validación**: Validar datos de entrada antes de procesarlos
5. **Desacoplamiento**: Cambios en entidades no afectan la API

### 5. Capa de Mappers

**Ubicación:** `com.academy.academymanager.mapper`

**Responsabilidades:**
- Convertir entre DTOs y Entidades
- Mapeo de campos anidados
- Transformación de datos

## Flujo de Datos Completo

```
Controller (Futuro)
    ↓
    Recibe: RequestDto
    ↓
Service Layer
    ↓
    Valida negocio
    ↓
    Usa: Repository
    ↓
    Mapea: Entity ↔ DTO
    ↓
    Retorna: ResponseDto
    ↓
Controller
    ↓
    Retorna: HTTP Response
```

## Comparación con Otras Arquitecturas

### Arquitectura por Capas vs Hexagonal

**Por Capas (Nuestra elección):**
- ✅ Más simple de entender
- ✅ Adecuada para aplicaciones monolíticas
- ✅ Menos overhead
- ❌ Acoplamiento a frameworks

**Hexagonal (Ports & Adapters):**
- ✅ Desacoplamiento total
- ✅ Testabilidad extrema
- ✅ Flexibilidad de infraestructura
- ❌ Más complejidad inicial
- ❌ Overhead para proyectos pequeños

**Decisión:** Para un proyecto empresarial que será containerizado, la arquitectura por capas es suficiente y más pragmática.

## Mejores Prácticas

### 1. Naming Conventions
- **Entities**: Sustantivos en singular (`Usuario`, `Matricula`)
- **Repositories**: `[Entity]Repository` (`UsuarioRepository`)
- **Services**: `[Entity]Service` (`UsuarioService`)
- **DTOs**: `[Entity]RequestDto` / `[Entity]ResponseDto`

### 2. Package Structure
```
com.academy.academymanager
├── domain
│   └── entity          # Entidades JPA
├── repository          # Repositorios
├── service             # Servicios
├── dto
│   ├── request         # DTOs de entrada
│   └── response        # DTOs de salida
└── mapper              # Mappers MapStruct
```

### 3. Principios SOLID Aplicados

**S - Single Responsibility:**
- Cada clase tiene una única responsabilidad
- Ejemplo: `UsuarioService` solo maneja lógica de usuarios

**O - Open/Closed:**
- Abierto para extensión, cerrado para modificación
- Ejemplo: Agregar nuevos métodos a repositorios sin modificar existentes

**L - Liskov Substitution:**
- Las implementaciones pueden sustituirse
- Ejemplo: Cualquier implementación de `JpaRepository` funciona igual

**I - Interface Segregation:**
- Interfaces específicas, no genéricas
- Ejemplo: `UsuarioRepository` específico, no un `GenericRepository`

**D - Dependency Inversion:**
- Depender de abstracciones, no de implementaciones
- Ejemplo: Servicios dependen de interfaces de repositorios

## Preguntas de Entrevista Técnica

### ¿Por qué elegiste arquitectura por capas?

**Respuesta:**
"Para este proyecto, elegí arquitectura por capas porque:
1. Es adecuada para aplicaciones monolíticas que serán containerizadas
2. Facilita el mantenimiento con separación clara de responsabilidades
3. Permite escalar agregando nuevas funcionalidades sin afectar capas existentes
4. Es más pragmática que arquitecturas más complejas para el scope del proyecto
5. Facilita el onboarding de nuevos desarrolladores con estructura predecible"

### ¿Cuándo usarías Hexagonal Architecture?

**Respuesta:**
"Usaría Hexagonal cuando:
1. Necesito cambiar de base de datos frecuentemente
2. Tengo múltiples puntos de entrada (REST, GraphQL, gRPC)
3. El proyecto es de larga duración con alta complejidad
4. Necesito máxima testabilidad y desacoplamiento
5. El equipo tiene experiencia con arquitecturas avanzadas"

### ¿Cómo evitas el acoplamiento entre capas?

**Respuesta:**
"1. Uso DTOs para transferir datos, no entidades directamente
2. Los servicios dependen de interfaces de repositorios, no implementaciones
3. Uso mappers (MapStruct) para conversión, evitando lógica de mapeo en servicios
4. Las entidades no conocen DTOs ni servicios
5. Cada capa solo conoce la capa inmediatamente inferior"

## Conclusión

La arquitectura por capas proporciona una base sólida para aplicaciones empresariales en Spring Boot. Su simplicidad y claridad la hacen ideal para proyectos que necesitan mantenibilidad y escalabilidad sin la complejidad de arquitecturas más avanzadas.

