# Servicios: Lógica de Negocio Completa y Robusta

## Introducción

La capa de servicio es el corazón de la aplicación empresarial. Aquí reside toda la lógica de negocio, orquestación de operaciones, validaciones y coordinación entre repositorios. Un servicio bien diseñado es la diferencia entre código mantenible y código que se vuelve un problema.

## ¿Qué es un Servicio?

Un servicio es una clase que:
1. **Contiene lógica de negocio**: Reglas que definen cómo funciona el dominio
2. **Orquesta operaciones**: Coordina múltiples repositorios y operaciones
3. **Valida reglas de negocio**: Asegura que las operaciones cumplan con las reglas
4. **Maneja transacciones**: Garantiza consistencia de datos
5. **Transforma datos**: Convierte entre entidades y DTOs

## Estructura de un Servicio Completo

### Ejemplo: MatriculaService

```java
@Service
@RequiredArgsConstructor
@Transactional
public class MatriculaService {
    // Dependencias inyectadas (inmutables)
    private final MatriculaRepository matriculaRepository;
    private final ConvocatoriaRepository convocatoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EntidadSubvencionadoraRepository entidadSubvencionadoraRepository;
    private final DatosPersonalesRepository datosPersonalesRepository;
    private final MatriculaMapper matriculaMapper;
    
    // Constantes de negocio (no magic numbers)
    private static final BigDecimal DISCAPACIDAD_THRESHOLD = new BigDecimal("33.0");
    private static final BigDecimal DISCAPACIDAD_DISCOUNT_PERCENTAGE = new BigDecimal("0.20");
    
    /**
     * Crea una nueva matrícula aplicando todas las reglas de negocio.
     */
    public MatriculaResponseDto create(MatriculaRequestDto requestDto) {
        // 1. Validar y obtener dependencias
        Convocatoria convocatoria = validateAndGetConvocatoria(requestDto.getIdConvocatoria());
        Usuario alumno = validateAndGetAlumno(requestDto.getIdAlumno());
        
        // 2. Crear entidad desde DTO
        Matricula matricula = matriculaMapper.toEntity(requestDto);
        matricula.setConvocatoria(convocatoria);
        matricula.setAlumno(alumno);
        
        // 3. Configurar relaciones opcionales
        if (requestDto.getIdEntidadSubvencionadora() != null) {
            EntidadSubvencionadora entidad = validateAndGetEntidad(requestDto.getIdEntidadSubvencionadora());
            matricula.setEntidadSubvencionadora(entidad);
        }
        
        // 4. Generar código único si no existe
        if (matricula.getCodigo() == null || matricula.getCodigo().isEmpty()) {
            matricula.setCodigo(generateCodigoMatricula());
        }
        
        // 5. Aplicar lógica de negocio (cálculo de precios)
        calculatePricing(matricula);
        
        // 6. Persistir
        Matricula saved = matriculaRepository.save(matricula);
        
        // 7. Retornar DTO
        return matriculaMapper.toResponseDto(saved);
    }
    
    /**
     * Obtiene una matrícula por ID.
     */
    @Transactional(readOnly = true)
    public MatriculaResponseDto findById(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matricula not found with id: " + id));
        return matriculaMapper.toResponseDto(matricula);
    }
    
    /**
     * Obtiene todas las matrículas de un alumno.
     */
    @Transactional(readOnly = true)
    public List<MatriculaResponseDto> findByAlumno(Long idAlumno) {
        return matriculaRepository.findByAlumnoIdUsuario(idAlumno).stream()
                .map(matriculaMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Actualiza una matrícula existente.
     */
    public MatriculaResponseDto update(Long id, MatriculaRequestDto requestDto) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matricula not found with id: " + id));
        
        // Actualizar relaciones si se proporcionan
        if (requestDto.getIdConvocatoria() != null) {
            Convocatoria convocatoria = validateAndGetConvocatoria(requestDto.getIdConvocatoria());
            matricula.setConvocatoria(convocatoria);
        }
        
        if (requestDto.getIdEntidadSubvencionadora() != null) {
            EntidadSubvencionadora entidad = validateAndGetEntidad(requestDto.getIdEntidadSubvencionadora());
            matricula.setEntidadSubvencionadora(entidad);
        }
        
        // Actualizar campos del DTO
        matriculaMapper.updateEntityFromDto(requestDto, matricula);
        
        // Recalcular precios (pueden haber cambiado)
        calculatePricing(matricula);
        
        Matricula updated = matriculaRepository.save(matricula);
        return matriculaMapper.toResponseDto(updated);
    }
    
    /**
     * Elimina una matrícula.
     */
    public void delete(Long id) {
        if (!matriculaRepository.existsById(id)) {
            throw new IllegalArgumentException("Matricula not found with id: " + id);
        }
        matriculaRepository.deleteById(id);
    }
    
    // ========== Métodos Privados de Lógica de Negocio ==========
    
    /**
     * Calcula el pricing de una matrícula aplicando descuentos y subvenciones.
     */
    private void calculatePricing(Matricula matricula) {
        // 1. Obtener precio base del curso
        BigDecimal precioBase = matricula.getConvocatoria().getCurso().getPrecioBase();
        matricula.setPrecioBruto(precioBase);
        
        // 2. Aplicar descuento por discapacidad
        datosPersonalesRepository.findById(matricula.getAlumno().getIdUsuario())
                .ifPresent(datosPersonales -> {
                    BigDecimal discapacidadPorcentaje = datosPersonales.getDiscapacidadPorcentaje();
                    if (discapacidadPorcentaje != null && 
                        discapacidadPorcentaje.compareTo(DISCAPACIDAD_THRESHOLD) >= 0) {
                        BigDecimal descuento = precioBase.multiply(DISCAPACIDAD_DISCOUNT_PERCENTAGE);
                        matricula.setDescuentoAplicado(descuento);
                        matricula.setMotivoDescuento("Descuento Discapacidad (+33%)");
                    } else {
                        matricula.setDescuentoAplicado(BigDecimal.ZERO);
                        matricula.setMotivoDescuento("Sin descuento");
                    }
                });
        
        // 3. Asegurar importe de subvención
        if (matricula.getImporteSubvencionado() == null) {
            matricula.setImporteSubvencionado(BigDecimal.ZERO);
        }
        
        // Nota: precioFinal se calcula automáticamente en la BD (GENERATED ALWAYS AS)
    }
    
    /**
     * Genera un código único para la matrícula.
     */
    private String generateCodigoMatricula() {
        return "MAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // ========== Métodos de Validación ==========
    
    private Convocatoria validateAndGetConvocatoria(Long id) {
        return convocatoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Convocatoria not found with id: " + id));
    }
    
    private Usuario validateAndGetAlumno(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alumno not found with id: " + id));
        if (!usuario.getRol().equals(Usuario.Rol.ALUMNO)) {
            throw new IllegalArgumentException("User is not an ALUMNO: " + id);
        }
        return usuario;
    }
    
    private EntidadSubvencionadora validateAndGetEntidad(Long id) {
        return entidadSubvencionadoraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("EntidadSubvencionadora not found with id: " + id));
    }
}
```

## Principios para Servicios Completos

### 1. Single Responsibility Principle (SRP)

**Cada servicio debe tener una responsabilidad única:**

```java
// ✅ Bueno - Un servicio por entidad principal
@Service
public class UsuarioService {
    // Solo lógica relacionada con usuarios
}

@Service
public class MatriculaService {
    // Solo lógica relacionada con matrículas
}

// ❌ Evitar - Servicio que hace demasiado
@Service
public class AcademyService {
    // Maneja usuarios, cursos, matrículas, facturas... demasiado
}
```

### 2. Dependency Injection Inmutable

**Usar `@RequiredArgsConstructor` con campos `final`:**

```java
// ✅ Bueno - Inmutable, type-safe
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
}

// ❌ Evitar - Mutable, puede ser null
@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;  // Puede ser reasignado
}
```

**Ventajas:**
- **Inmutabilidad**: Campos no pueden ser reasignados
- **Thread-safe**: No hay riesgo de race conditions
- **Testabilidad**: Fácil de mockear en tests
- **Null-safety**: Spring garantiza que no sean null

### 3. Gestión de Transacciones

**Usar `@Transactional` apropiadamente:**

```java
@Service
@RequiredArgsConstructor
@Transactional  // Por defecto, todos los métodos son transaccionales
public class MatriculaService {
    
    // Método que modifica datos - transaccional
    public MatriculaResponseDto create(MatriculaRequestDto dto) {
        // Si algo falla, todo se revierte (rollback)
    }
    
    // Método de solo lectura - optimizado
    @Transactional(readOnly = true)
    public MatriculaResponseDto findById(Long id) {
        // Hibernate optimiza para solo lectura
        // No necesita lock de escritura
    }
}
```

**Cuándo usar `readOnly = true`:**
- Métodos que solo leen datos
- Mejora performance (Hibernate no necesita locks)
- Previene modificaciones accidentales

**Propagación de Transacciones:**

```java
@Transactional(propagation = Propagation.REQUIRED)  // Por defecto
public void method1() {
    // Si ya hay transacción, la usa; si no, crea una nueva
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public void method2() {
    // Siempre crea una nueva transacción (útil para logging)
}

@Transactional(propagation = Propagation.NEVER)
public void method3() {
    // Nunca debe ejecutarse dentro de una transacción
}
```

### 4. Validación de Reglas de Negocio

**Validar antes de procesar:**

```java
public MatriculaResponseDto create(MatriculaRequestDto requestDto) {
    // 1. Validar que la convocatoria existe
    Convocatoria convocatoria = convocatoriaRepository.findById(requestDto.getIdConvocatoria())
            .orElseThrow(() -> new IllegalArgumentException("Convocatoria not found"));
    
    // 2. Validar que el usuario existe y es alumno
    Usuario alumno = usuarioRepository.findById(requestDto.getIdAlumno())
            .orElseThrow(() -> new IllegalArgumentException("Alumno not found"));
    if (!alumno.getRol().equals(Usuario.Rol.ALUMNO)) {
        throw new IllegalArgumentException("User is not an ALUMNO");
    }
    
    // 3. Validar reglas de negocio específicas
    if (convocatoria.getFechaInicio().isBefore(LocalDate.now())) {
        throw new IllegalArgumentException("Convocatoria has already started");
    }
    
    // 4. Continuar con la lógica...
}
```

**Tipos de Validaciones:**

1. **Validaciones de Existencia**: ¿Existe la entidad?
2. **Validaciones de Estado**: ¿Está en el estado correcto?
3. **Validaciones de Reglas de Negocio**: ¿Cumple las reglas?
4. **Validaciones de Permisos**: ¿Tiene permisos para la operación?

### 5. Manejo de Errores Descriptivo

**Excepciones con mensajes claros:**

```java
// ✅ Bueno - Mensaje descriptivo
public UsuarioResponseDto findById(Long id) {
    return usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Usuario not found with id: " + id));
}

// ❌ Evitar - Mensaje genérico
public UsuarioResponseDto findById(Long id) {
    return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Error"));
}
```

**Jerarquía de Excepciones:**

```java
// Excepciones de negocio (checked o unchecked según necesidad)
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

// Uso:
throw new BusinessException("Cannot delete usuario with active matriculas");
```

### 6. Extracción de Métodos Privados

**Métodos pequeños y con propósito único:**

```java
// ✅ Bueno - Métodos pequeños y claros
private void calculatePricing(Matricula matricula) {
    BigDecimal precioBase = getPrecioBase(matricula);
    matricula.setPrecioBruto(precioBase);
    applyDiscount(matricula);
    applySubsidy(matricula);
}

private BigDecimal getPrecioBase(Matricula matricula) {
    return matricula.getConvocatoria().getCurso().getPrecioBase();
}

private void applyDiscount(Matricula matricula) {
    // Lógica de descuentos
}

private void applySubsidy(Matricula matricula) {
    // Lógica de subvenciones
}

// ❌ Evitar - Método largo y difícil de entender
private void calculatePricing(Matricula matricula) {
    // 100 líneas de código mezclando todo
}
```

### 7. Constantes de Negocio

**No usar "magic numbers" o strings mágicos:**

```java
// ✅ Bueno - Constantes claras
private static final BigDecimal DISCAPACIDAD_THRESHOLD = new BigDecimal("33.0");
private static final BigDecimal DISCAPACIDAD_DISCOUNT_PERCENTAGE = new BigDecimal("0.20");
private static final String DESCUENTO_MOTIVO = "Descuento Discapacidad (+33%)";

if (discapacidadPorcentaje.compareTo(DISCAPACIDAD_THRESHOLD) >= 0) {
    BigDecimal descuento = precioBase.multiply(DISCAPACIDAD_DISCOUNT_PERCENTAGE);
    matricula.setMotivoDescuento(DESCUENTO_MOTIVO);
}

// ❌ Evitar - Magic numbers
if (discapacidadPorcentaje.compareTo(new BigDecimal("33.0")) >= 0) {
    BigDecimal descuento = precioBase.multiply(new BigDecimal("0.20"));
    matricula.setMotivoDescuento("Descuento Discapacidad (+33%)");
}
```

## Patrones Comunes en Servicios

### 1. Patrón Find-or-Create

```java
public ConvocatoriaResponseDto findOrCreate(ConvocatoriaRequestDto requestDto) {
    // Intentar encontrar existente
    Optional<Convocatoria> existing = convocatoriaRepository.findByCodigo(requestDto.getCodigo());
    
    if (existing.isPresent()) {
        return convocatoriaMapper.toResponseDto(existing.get());
    }
    
    // Crear nuevo si no existe
    return create(requestDto);
}
```

### 2. Patrón Validar-y-Obtener

```java
private Convocatoria validateAndGetConvocatoria(Long id) {
    return convocatoriaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Convocatoria not found with id: " + id));
}

// Reutilizable en múltiples métodos
public MatriculaResponseDto create(MatriculaRequestDto dto) {
    Convocatoria convocatoria = validateAndGetConvocatoria(dto.getIdConvocatoria());
    // ...
}
```

### 3. Patrón Builder para Operaciones Complejas

```java
public MatriculaResponseDto createWithOptions(MatriculaRequestDto dto, CreateOptions options) {
    Matricula matricula = Matricula.builder()
            .convocatoria(validateAndGetConvocatoria(dto.getIdConvocatoria()))
            .alumno(validateAndGetAlumno(dto.getIdAlumno()))
            .codigo(options.shouldGenerateCode() ? generateCodigo() : dto.getCodigo())
            .build();
    
    if (options.shouldCalculatePricing()) {
        calculatePricing(matricula);
    }
    
    return matriculaMapper.toResponseDto(matriculaRepository.save(matricula));
}
```

## Mejores Prácticas

### 1. Naming Conventions

```java
// ✅ Bueno - Verbos claros
public MatriculaResponseDto create(MatriculaRequestDto dto);
public MatriculaResponseDto update(Long id, MatriculaRequestDto dto);
public void delete(Long id);
public MatriculaResponseDto findById(Long id);
public List<MatriculaResponseDto> findAll();

// ❌ Evitar - Nombres ambiguos
public MatriculaResponseDto process(MatriculaRequestDto dto);
public MatriculaResponseDto handle(Long id, MatriculaRequestDto dto);
```

### 2. Retornar DTOs, No Entidades

```java
// ✅ Bueno - Retorna DTO
public MatriculaResponseDto findById(Long id) {
    Matricula matricula = matriculaRepository.findById(id)...
    return matriculaMapper.toResponseDto(matricula);
}

// ❌ Evitar - Expone entidad
public Matricula findById(Long id) {
    return matriculaRepository.findById(id)...
    // Problemas: lazy loading, exposición de estructura interna
}
```

### 3. Métodos Públicos vs Privados

```java
// Públicos - API del servicio
public MatriculaResponseDto create(MatriculaRequestDto dto) { ... }
public MatriculaResponseDto findById(Long id) { ... }

// Privados - Lógica interna
private void calculatePricing(Matricula matricula) { ... }
private String generateCodigo() { ... }
private Convocatoria validateAndGetConvocatoria(Long id) { ... }
```

### 4. Documentación con JSDoc

```java
/**
 * Crea una nueva matrícula aplicando todas las reglas de negocio.
 * 
 * @param requestDto DTO con los datos de la matrícula a crear
 * @return DTO de la matrícula creada
 * @throws IllegalArgumentException si la convocatoria o alumno no existen,
 *         o si el usuario no es un ALUMNO
 */
public MatriculaResponseDto create(MatriculaRequestDto requestDto) {
    // ...
}
```

## Preguntas de Entrevista

### ¿Cómo estructuras un servicio para que sea mantenible?

**Respuesta:**
"1. **Single Responsibility**: Un servicio por entidad principal
2. **Dependency Injection Inmutable**: `@RequiredArgsConstructor` con campos `final`
3. **Transacciones Apropiadas**: `@Transactional` para escritura, `readOnly = true` para lectura
4. **Validación Temprana**: Validar dependencias y reglas antes de procesar
5. **Métodos Privados**: Extraer lógica compleja en métodos pequeños y claros
6. **Constantes de Negocio**: No usar magic numbers
7. **Manejo de Errores**: Excepciones descriptivas con mensajes claros
8. **Documentación**: JSDoc para métodos públicos"

### ¿Cómo manejas transacciones en servicios?

**Respuesta:**
"Uso `@Transactional` a nivel de clase para métodos que modifican datos, y `@Transactional(readOnly = true)` para métodos de solo lectura. Esto optimiza performance (Hibernate no necesita locks de escritura) y previene modificaciones accidentales. Para operaciones complejas que requieren múltiples transacciones, uso `Propagation.REQUIRES_NEW` para crear transacciones independientes (útil para logging que debe persistir incluso si la transacción principal falla)."

### ¿Dónde va la lógica de negocio: en servicios o en entidades?

**Respuesta:**
"La lógica de negocio va principalmente en servicios porque:
1. **Orquestación**: Los servicios coordinan múltiples entidades y repositorios
2. **Transacciones**: Los servicios manejan transacciones que pueden abarcar múltiples entidades
3. **Validaciones Complejas**: Validaciones que requieren acceso a repositorios
4. **Lógica de Aplicación**: Reglas que no pertenecen a una entidad específica

Sin embargo, lógica básica puede ir en entidades usando callbacks JPA (@PrePersist, @PreUpdate) para operaciones simples como establecer fechas automáticamente."

## Conclusión

Un servicio bien diseñado es la base de una aplicación mantenible y escalable. Siguiendo estos principios y patrones, creas servicios que son fáciles de entender, testear y mantener, lo cual es esencial en proyectos empresariales.

