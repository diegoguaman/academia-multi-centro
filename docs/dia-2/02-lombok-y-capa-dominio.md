# Lombok y la Capa de Dominio: Reduciendo Boilerplate, Mejorando Legibilidad

## Introducción a Lombok

**Project Lombok** es una librería de Java que reduce el código boilerplate mediante anotaciones procesadas en tiempo de compilación. En lugar de escribir getters, setters, constructores, etc., Lombok los genera automáticamente.

## ¿Por qué Lombok?

### Problema: Código Boilerplate

**Sin Lombok (Java tradicional):**
```java
public class Usuario {
    private Long idUsuario;
    private String email;
    private String passwordHash;
    
    // Constructor sin argumentos
    public Usuario() {
    }
    
    // Constructor con todos los argumentos
    public Usuario(Long idUsuario, String email, String passwordHash) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    
    // Getters
    public Long getIdUsuario() {
        return idUsuario;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    // Setters
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(idUsuario, usuario.idUsuario) &&
               Objects.equals(email, usuario.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, email);
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
               "idUsuario=" + idUsuario +
               ", email='" + email + '\'' +
               '}';
    }
}
```

**Con Lombok:**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    private Long idUsuario;
    private String email;
    private String passwordHash;
}
```

**Reducción:** De ~80 líneas a ~8 líneas (90% menos código)

## Anotaciones de Lombok en Detalle

### @Data

**¿Qué hace?**
- Genera getters para todos los campos
- Genera setters para campos no-final
- Genera `toString()`
- Genera `equals()` y `hashCode()`
- Genera constructor con campos `final` y `@NonNull`

**Ejemplo:**
```java
@Data
public class Usuario {
    private Long idUsuario;
    private String email;
    private final String passwordHash; // final = solo getter, no setter
}

// Genera automáticamente:
// - getIdUsuario(), setIdUsuario()
// - getEmail(), setEmail()
// - getPasswordHash() (sin setter porque es final)
// - toString(), equals(), hashCode()
```

**Cuándo usar:**
- Entidades JPA
- DTOs
- Clases de dominio que necesitan mutabilidad

**Cuándo NO usar:**
- Clases inmutables (usa `@Value` en su lugar)
- Clases con lógica compleja de equals/hashCode

### @NoArgsConstructor

**¿Qué hace?**
- Genera constructor sin argumentos

**Ejemplo:**
```java
@NoArgsConstructor
public class Usuario {
    private Long idUsuario;
    private String email;
}

// Genera:
// public Usuario() {
// }
```

**Por qué es necesario:**
- JPA requiere constructor sin argumentos para instanciar entidades
- Frameworks de serialización (Jackson) lo necesitan

**Opciones:**
- `@NoArgsConstructor(force = true)`: Inicializa campos finales con valores por defecto
- `@NoArgsConstructor(access = AccessLevel.PRIVATE)`: Constructor privado

### @AllArgsConstructor

**¿Qué hace?**
- Genera constructor con todos los argumentos

**Ejemplo:**
```java
@AllArgsConstructor
public class Usuario {
    private Long idUsuario;
    private String email;
    private String passwordHash;
}

// Genera:
// public Usuario(Long idUsuario, String email, String passwordHash) {
//     this.idUsuario = idUsuario;
//     this.email = email;
//     this.passwordHash = passwordHash;
// }
```

**Cuándo usar:**
- Tests unitarios
- Factories
- Cuando necesitas crear objetos con todos los campos

### @Builder

**¿Qué hace?**
- Implementa el patrón Builder
- Permite construcción fluida de objetos

**Ejemplo:**
```java
@Builder
public class Usuario {
    private Long idUsuario;
    private String email;
    private String passwordHash;
    @Builder.Default
    private Boolean activo = true;
}

// Uso:
Usuario usuario = Usuario.builder()
    .email("test@example.com")
    .passwordHash("hash")
    .activo(true)  // Opcional, tiene valor por defecto
    .build();
```

**Ventajas del Builder:**
1. **Legibilidad**: Código más claro que múltiples setters
2. **Inmutabilidad**: Puedes hacer el objeto final después de construirlo
3. **Valores por defecto**: `@Builder.Default` permite valores por defecto
4. **Validación**: Puedes validar en el método `build()`

**Builder Personalizado:**
```java
@Builder
public class Matricula {
    private Long idMatricula;
    private BigDecimal precioBruto;
    
    @Builder.Default
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;
    
    // Builder personalizado con validación
    public static class MatriculaBuilder {
        public Matricula build() {
            if (precioBruto.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Precio bruto no puede ser negativo");
            }
            return new Matricula(idMatricula, precioBruto, descuentoAplicado);
        }
    }
}
```

### @RequiredArgsConstructor

**¿Qué hace?**
- Genera constructor con campos `final` y `@NonNull`

**Ejemplo:**
```java
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;  // final = incluido en constructor
    private final UsuarioMapper usuarioMapper;           // final = incluido en constructor
    private String someField;                            // no final = no incluido
}

// Genera:
// public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
//     this.usuarioRepository = usuarioRepository;
//     this.usuarioMapper = usuarioMapper;
// }
```

**Ventajas:**
- **Dependency Injection**: Reemplaza `@Autowired` con inmutabilidad
- **Inmutabilidad**: Campos `final` no pueden ser reasignados
- **Testabilidad**: Fácil de mockear en tests

**Comparación:**

**Tradicional (mutable):**
```java
@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;  // Puede ser null, puede ser reasignado
    
    public void someMethod() {
        usuarioRepository = null;  // ⚠️ Posible, pero peligroso
    }
}
```

**Con Lombok (inmutable):**
```java
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;  // No puede ser null, no puede ser reasignado
    
    public void someMethod() {
        // usuarioRepository = null;  // ❌ Error de compilación
    }
}
```

### @Value

**¿Qué hace?**
- Crea clases inmutables
- Todos los campos son `final`
- Genera getters, `equals()`, `hashCode()`, `toString()`
- NO genera setters

**Ejemplo:**
```java
@Value
@Builder
public class UsuarioInfo {
    String email;
    String nombre;
    Boolean activo;
}

// Uso:
UsuarioInfo info = UsuarioInfo.builder()
    .email("test@example.com")
    .nombre("Test User")
    .activo(true)
    .build();

// info.setEmail("new@example.com");  // ❌ No existe setter
```

**Cuándo usar:**
- DTOs inmutables
- Value Objects
- Clases de configuración

### @Slf4j

**¿Qué hace?**
- Genera un logger estático `log`

**Ejemplo:**
```java
@Slf4j
@Service
public class UsuarioService {
    public void createUsuario() {
        log.debug("Creating usuario");  // No necesitas declarar Logger
        log.info("Usuario created successfully");
        log.error("Error creating usuario", exception);
    }
}

// Sin Lombok:
@Service
public class UsuarioService {
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    // ...
}
```

## Lombok en la Capa de Dominio

### Entidades JPA con Lombok

**Ejemplo Completo:**
```java
@Entity
@Table(name = "matricula")
@Data                    // Getters, setters, equals, hashCode, toString
@NoArgsConstructor       // Requerido por JPA
@AllArgsConstructor     // Útil para tests
@Builder                // Patrón Builder para construcción fluida
public class Matricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_matricula")
    private Long idMatricula;
    
    @Column(name = "precio_bruto", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBruto;
    
    @Column(name = "descuento_aplicado", precision = 10, scale = 2)
    @Builder.Default     // Valor por defecto en Builder
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;
    
    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
    
    // Callbacks JPA
    @PrePersist
    protected void onCreate() {
        if (fechaMatricula == null) {
            fechaMatricula = LocalDateTime.now();
        }
    }
}
```

### Consideraciones Especiales

#### 1. Relaciones JPA y Lombok

**Problema:** `@Data` genera `equals()` y `hashCode()` que incluyen relaciones lazy, causando problemas.

**Solución:**
```java
@Data
@EqualsAndHashCode(exclude = {"convocatoria", "alumno"})  // Excluir relaciones
public class Matricula {
    @ManyToOne(fetch = FetchType.LAZY)
    private Convocatoria convocatoria;  // Excluido de equals/hashCode
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario alumno;  // Excluido de equals/hashCode
}
```

**Mejor Solución:**
```java
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)  // Solo incluir campos específicos
public class Matricula {
    @Id
    @EqualsAndHashCode.Include  // Solo ID en equals/hashCode
    private Long idMatricula;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Convocatoria convocatoria;  // No incluido
}
```

#### 2. Stack Overflow con Relaciones Bidireccionales

**Problema:**
```java
@Data  // Genera toString() que incluye todas las relaciones
public class Matricula {
    @ManyToOne
    private Convocatoria convocatoria;
}

@Data
public class Convocatoria {
    @OneToMany(mappedBy = "convocatoria")
    private List<Matricula> matriculas;
}

// matricula.toString() → incluye convocatoria
// convocatoria.toString() → incluye matriculas
// Resultado: StackOverflowError
```

**Solución:**
```java
@Data
@ToString(exclude = {"convocatoria"})  // Excluir de toString
public class Matricula {
    @ManyToOne
    private Convocatoria convocatoria;
}
```

#### 3. Inmutabilidad Parcial

**Problema:** `@Data` genera setters para todos los campos, pero algunos no deberían ser modificables.

**Solución:**
```java
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)  // No generar setter
    private Long idUsuario;
    
    @Column(updatable = false)  // JPA también lo previene
    @Setter(AccessLevel.NONE)
    private LocalDateTime fechaCreacion;
}
```

## Configuración de Lombok

### pom.xml

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

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
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

### IntelliJ IDEA

1. Instalar plugin "Lombok"
2. Habilitar: Settings → Build → Compiler → Annotation Processors → Enable annotation processing

## Ventajas y Desventajas

### Ventajas

1. **Menos Código**: 70-90% menos código boilerplate
2. **Legibilidad**: Código más limpio y enfocado en lógica de negocio
3. **Mantenibilidad**: Menos código = menos bugs
4. **Productividad**: Desarrollo más rápido
5. **Refactoring**: Cambios en campos se propagan automáticamente

### Desventajas

1. **Dependencia Externa**: Requiere plugin en IDE
2. **Debugging**: Código generado puede ser confuso al debuggear
3. **Curva de Aprendizaje**: Desarrolladores deben conocer anotaciones
4. **Compatibilidad**: Algunas herramientas pueden no entender código generado

## Mejores Prácticas

### 1. Usar @RequiredArgsConstructor en Servicios

```java
// ✅ Bueno
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
}

// ❌ Evitar
@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private UsuarioMapper mapper;
}
```

### 2. Excluir Relaciones de equals/hashCode

```java
// ✅ Bueno
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Matricula {
    @Id
    @EqualsAndHashCode.Include
    private Long idMatricula;
}

// ❌ Evitar
@Data  // Incluye todas las relaciones en equals/hashCode
public class Matricula {
    @ManyToOne(fetch = FetchType.LAZY)
    private Convocatoria convocatoria;  // Puede causar problemas
}
```

### 3. Usar @Builder.Default para Valores por Defecto

```java
// ✅ Bueno
@Builder
public class Matricula {
    @Builder.Default
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;
    
    @Builder.Default
    private Boolean activo = true;
}
```

## Preguntas de Entrevista

### ¿Por qué usar Lombok en lugar de código tradicional?

**Respuesta:**
"Lombok reduce significativamente el código boilerplate (70-90%), mejorando la legibilidad y mantenibilidad. En entidades JPA, reduce de ~80 líneas a ~8 líneas para una clase simple. Además, reduce errores comunes como olvidar actualizar equals/hashCode cuando se agregan campos. El código generado es validado en compile-time, no en runtime."

### ¿Cuáles son los riesgos de usar Lombok?

**Respuesta:**
"Los principales riesgos son:
1. **Relaciones JPA**: `@Data` genera equals/hashCode que pueden incluir relaciones lazy, causando problemas. Solución: usar `@EqualsAndHashCode(onlyExplicitlyIncluded = true)`
2. **Stack Overflow**: Relaciones bidireccionales en toString(). Solución: `@ToString(exclude = {...})`
3. **Dependencia de IDE**: Requiere plugin. Mitigación: código compila sin plugin, solo ayuda en desarrollo
4. **Debugging**: Código generado puede ser confuso. Mitigación: usar Delombok para ver código generado"

### ¿Cómo manejas la inmutabilidad con Lombok?

**Respuesta:**
"Para inmutabilidad total, uso `@Value` que hace todos los campos final y no genera setters. Para inmutabilidad parcial, uso `@Data` con `@Setter(AccessLevel.NONE)` en campos específicos. En servicios, uso `@RequiredArgsConstructor` con campos `final` para dependency injection inmutable, evitando `@Autowired`."

## Conclusión

Lombok es una herramienta poderosa que, cuando se usa correctamente, mejora significativamente la productividad y legibilidad del código. La clave está en entender sus limitaciones, especialmente con JPA, y aplicar las mejores prácticas para evitar problemas comunes.

