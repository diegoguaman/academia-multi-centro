# Testing: Alcanzando ~95% de Cobertura en Aplicaciones Spring Boot

## Introducción

El testing es fundamental en aplicaciones empresariales. Una cobertura de ~95% no es solo un número, es una garantía de que el código está probado, es confiable y puede evolucionar con confianza. En este documento, exploramos cómo crear tests efectivos y alcanzar alta cobertura.

## ¿Por qué ~95% de Cobertura?

### Niveles de Cobertura

- **0-50%**: Código crítico sin probar, alto riesgo
- **50-80%**: Cobertura básica, algunos casos edge sin cubrir
- **80-95%**: Cobertura sólida, casos edge cubiertos
- **95-100%**: Cobertura exhaustiva (puede ser excesivo para algunos proyectos)

**~95% es el punto óptimo** porque:
- Cubre código crítico y casos edge
- Permite excluir código generado (getters/setters, builders)
- Balance entre esfuerzo y beneficio
- Aceptable para estándares empresariales

## Tipos de Tests

### 1. Unit Tests

**Definición:** Tests que verifican una unidad de código (método, clase) de forma aislada.

**Características:**
- Rápidos (milisegundos)
- Aislados (no dependen de BD, red, etc.)
- Usan mocks/stubs para dependencias
- Ejecutan sin contexto Spring completo

**Cuándo usar:**
- Lógica de negocio en servicios
- Métodos de utilidad
- Validaciones
- Transformaciones de datos

### 2. Integration Tests

**Definición:** Tests que verifican la integración entre componentes (repositorios, servicios).

**Características:**
- Más lentos (segundos)
- Usan contexto Spring (parcial o completo)
- Pueden usar base de datos en memoria (H2) o Testcontainers
- Verifican interacción entre componentes

**Cuándo usar:**
- Repositorios JPA
- Integración servicio-repositorio
- Transacciones
- Validaciones de esquema JPA

### 3. End-to-End Tests

**Definición:** Tests que verifican el flujo completo desde el controller hasta la BD.

**Características:**
- Más lentos (segundos a minutos)
- Usan contexto Spring completo
- Usan base de datos real o Testcontainers
- Verifican flujo completo

**Cuándo usar:**
- Flujos críticos de negocio
- Validación de API completa
- Tests de aceptación

## Unit Tests: Estructura y Ejemplos

### Estructura: Arrange-Act-Assert (AAA)

```java
@Test
void createShouldReturnUsuarioResponseDtoWhenValidInput() {
    // Arrange - Preparar datos de prueba
    UsuarioRequestDto inputDto = UsuarioRequestDto.builder()
            .email("test@example.com")
            .password("password123")
            .rol(Usuario.Rol.ALUMNO)
            .build();
    Usuario mockUsuario = Usuario.builder()
            .idUsuario(1L)
            .email("test@example.com")
            .build();
    UsuarioResponseDto expectedDto = UsuarioResponseDto.builder()
            .idUsuario(1L)
            .email("test@example.com")
            .build();
    
    // Configurar mocks
    when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
    when(usuarioMapper.toEntity(any())).thenReturn(mockUsuario);
    when(usuarioRepository.save(any())).thenReturn(mockUsuario);
    when(usuarioMapper.toResponseDto(any())).thenReturn(expectedDto);
    
    // Act - Ejecutar método bajo prueba
    UsuarioResponseDto actualResult = usuarioService.create(inputDto);
    
    // Assert - Verificar resultado
    assertNotNull(actualResult);
    assertEquals(expectedDto.getEmail(), actualResult.getEmail());
    verify(usuarioRepository).existsByEmail(inputDto.getEmail());
    verify(usuarioRepository).save(any(Usuario.class));
}
```

### Naming Conventions

```java
// Patrón: methodName_condition_expectedResult
@Test
void create_WhenEmailExists_ShouldThrowException() { }

@Test
void findById_WhenExists_ShouldReturnUsuario() { }

@Test
void findById_WhenNotExists_ShouldThrowException() { }

@Test
void update_WhenValidInput_ShouldReturnUpdatedUsuario() { }
```

### Ejemplo Completo: UsuarioServiceTest

```java
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioMapper usuarioMapper;
    @InjectMocks
    private UsuarioService usuarioService;
    
    private UsuarioRequestDto inputUsuarioRequestDto;
    private Usuario mockUsuario;
    private UsuarioResponseDto expectedUsuarioResponseDto;
    
    @BeforeEach
    void setUp() {
        // Preparar datos de prueba comunes
        inputUsuarioRequestDto = UsuarioRequestDto.builder()
                .email("test@example.com")
                .password("password123")
                .rol(Usuario.Rol.ALUMNO)
                .activo(true)
                .build();
        mockUsuario = Usuario.builder()
                .idUsuario(1L)
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .rol(Usuario.Rol.ALUMNO)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
        expectedUsuarioResponseDto = UsuarioResponseDto.builder()
                .idUsuario(1L)
                .email("test@example.com")
                .rol(Usuario.Rol.ALUMNO)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }
    
    @Test
    void createShouldReturnUsuarioResponseDtoWhenValidInput() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioMapper.toEntity(any(UsuarioRequestDto.class))).thenReturn(mockUsuario);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(mockUsuario);
        when(usuarioMapper.toResponseDto(any(Usuario.class))).thenReturn(expectedUsuarioResponseDto);
        
        // Act
        UsuarioResponseDto actualResult = usuarioService.create(inputUsuarioRequestDto);
        
        // Assert
        assertNotNull(actualResult);
        assertEquals(expectedUsuarioResponseDto.getEmail(), actualResult.getEmail());
        verify(usuarioRepository).existsByEmail(inputUsuarioRequestDto.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
    }
    
    @Test
    void createShouldThrowExceptionWhenEmailExists() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> usuarioService.create(inputUsuarioRequestDto));
        assertEquals("Email already exists: " + inputUsuarioRequestDto.getEmail(), exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    
    @Test
    void findByIdShouldReturnUsuarioResponseDtoWhenExists() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mockUsuario));
        when(usuarioMapper.toResponseDto(any(Usuario.class))).thenReturn(expectedUsuarioResponseDto);
        
        // Act
        UsuarioResponseDto actualResult = usuarioService.findById(1L);
        
        // Assert
        assertNotNull(actualResult);
        assertEquals(expectedUsuarioResponseDto.getIdUsuario(), actualResult.getIdUsuario());
        verify(usuarioRepository).findById(1L);
    }
    
    @Test
    void findByIdShouldThrowExceptionWhenNotFound() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> usuarioService.findById(1L));
        assertEquals("Usuario not found with id: 1", exception.getMessage());
    }
    
    @Test
    void findAllShouldReturnListOfUsuarioResponseDto() {
        // Arrange
        List<Usuario> mockUsuarios = List.of(mockUsuario);
        when(usuarioRepository.findAll()).thenReturn(mockUsuarios);
        when(usuarioMapper.toResponseDto(any(Usuario.class))).thenReturn(expectedUsuarioResponseDto);
        
        // Act
        List<UsuarioResponseDto> actualResult = usuarioService.findAll();
        
        // Assert
        assertNotNull(actualResult);
        assertEquals(1, actualResult.size());
        verify(usuarioRepository).findAll();
    }
    
    @Test
    void updateShouldReturnUpdatedUsuarioResponseDtoWhenValidInput() {
        // Arrange
        UsuarioRequestDto updateRequestDto = UsuarioRequestDto.builder()
                .email("updated@example.com")
                .rol(Usuario.Rol.PROFESOR)
                .activo(false)
                .build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mockUsuario));
        when(usuarioRepository.existsByEmailAndIdUsuarioNot(anyString(), anyLong())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(mockUsuario);
        when(usuarioMapper.toResponseDto(any(Usuario.class))).thenReturn(expectedUsuarioResponseDto);
        
        // Act
        UsuarioResponseDto actualResult = usuarioService.update(1L, updateRequestDto);
        
        // Assert
        assertNotNull(actualResult);
        verify(usuarioRepository).findById(1L);
        verify(usuarioMapper).updateEntityFromDto(updateRequestDto, mockUsuario);
        verify(usuarioRepository).save(mockUsuario);
    }
    
    @Test
    void deleteShouldDeleteUsuarioWhenExists() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        
        // Act
        usuarioService.delete(1L);
        
        // Assert
        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository).deleteById(1L);
    }
    
    @Test
    void deleteShouldThrowExceptionWhenNotFound() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(false);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> usuarioService.delete(1L));
        assertEquals("Usuario not found with id: 1", exception.getMessage());
        verify(usuarioRepository, never()).deleteById(anyLong());
    }
}
```

### Cobertura de Casos Edge

```java
// Test de lógica de negocio compleja
@Test
void createShouldApplyDiscountWhenDiscapacidadGreaterThan33() {
    // Arrange
    DatosPersonales datosPersonales = DatosPersonales.builder()
            .idUsuario(1L)
            .discapacidadPorcentaje(new BigDecimal("40.00"))
            .build();
    when(convocatoriaRepository.findById(1L)).thenReturn(Optional.of(mockConvocatoria));
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mockAlumno));
    when(matriculaMapper.toEntity(any())).thenReturn(mockMatricula);
    when(datosPersonalesRepository.findById(1L)).thenReturn(Optional.of(datosPersonales));
    when(matriculaRepository.save(any(Matricula.class))).thenReturn(mockMatricula);
    when(matriculaMapper.toResponseDto(any())).thenReturn(expectedMatriculaResponseDto);
    
    // Act
    matriculaService.create(inputMatriculaRequestDto);
    
    // Assert
    verify(matriculaRepository).save(argThat(matricula -> 
            matricula.getDescuentoAplicado().compareTo(BigDecimal.ZERO) > 0 &&
            matricula.getMotivoDescuento().contains("Discapacidad")
    ));
}

// Test de validación de reglas de negocio
@Test
void createShouldThrowExceptionWhenUserIsNotAlumno() {
    // Arrange
    Usuario profesor = Usuario.builder()
            .idUsuario(2L)
            .rol(Usuario.Rol.PROFESOR)
            .build();
    when(convocatoriaRepository.findById(1L)).thenReturn(Optional.of(mockConvocatoria));
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(profesor));
    
    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> matriculaService.create(inputMatriculaRequestDto));
    assertEquals("User is not an ALUMNO: 1", exception.getMessage());
    verify(matriculaRepository, never()).save(any(Matricula.class));
}
```

## Integration Tests: Repositorios

### Estructura con @DataJpaTest

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    private Usuario testUsuario;
    
    @BeforeEach
    void setUp() {
        testUsuario = Usuario.builder()
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .rol(Usuario.Rol.ALUMNO)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(testUsuario);
    }
    
    @Test
    void findByEmailShouldReturnUsuarioWhenExists() {
        // Act
        Optional<Usuario> found = usuarioRepository.findByEmail("test@example.com");
        
        // Assert
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }
    
    @Test
    void findByEmailShouldReturnEmptyWhenNotExists() {
        // Act
        Optional<Usuario> found = usuarioRepository.findByEmail("nonexistent@example.com");
        
        // Assert
        assertFalse(found.isPresent());
    }
    
    @Test
    void existsByEmailShouldReturnTrueWhenExists() {
        // Act
        boolean exists = usuarioRepository.existsByEmail("test@example.com");
        
        // Assert
        assertTrue(exists);
    }
    
    @Test
    void saveShouldPersistUsuario() {
        // Arrange
        Usuario newUsuario = Usuario.builder()
                .email("new@example.com")
                .passwordHash("hash")
                .rol(Usuario.Rol.PROFESOR)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
        
        // Act
        Usuario saved = usuarioRepository.save(newUsuario);
        
        // Assert
        assertNotNull(saved.getIdUsuario());
        assertTrue(usuarioRepository.existsById(saved.getIdUsuario()));
    }
}
```

### Testcontainers para Tests de Integración Real

```java
@SpringBootTest
@Testcontainers
class MatriculaRepositoryIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private MatriculaRepository matriculaRepository;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    void shouldPersistAndRetrieveMatricula() {
        // Test con base de datos real
    }
}
```

## Configuración de JaCoCo para Cobertura

### pom.xml

```xml
<properties>
    <jacoco.version>0.8.11</jacoco.version>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>${jacoco.version}</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                <execution>
                    <id>check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>PACKAGE</element>
                                <limits>
                                    <limit>
                                        <counter>LINE</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.95</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Ejecutar Tests con Cobertura

```bash
# Ejecutar tests y generar reporte
mvn clean test

# Ver reporte
open target/site/jacoco/index.html

# Verificar cobertura (falla si < 95%)
mvn verify
```

### Excluir Código de Cobertura

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <excludes>
            <!-- Excluir DTOs (solo getters/setters) -->
            <exclude>**/dto/**</exclude>
            <!-- Excluir entidades (generadas por Lombok) -->
            <exclude>**/domain/entity/**</exclude>
            <!-- Excluir mappers generados -->
            <exclude>**/mapper/*Impl</exclude>
            <!-- Excluir configuración -->
            <exclude>**/config/**</exclude>
        </excludes>
    </configuration>
</plugin>
```

## Estrategias para Alcanzar ~95% de Cobertura

### 1. Testear Todos los Caminos (Branches)

```java
// Método con múltiples caminos
public MatriculaResponseDto create(MatriculaRequestDto dto) {
    if (dto.getIdConvocatoria() == null) {
        throw new IllegalArgumentException("Convocatoria ID required");
    }
    Convocatoria convocatoria = convocatoriaRepository.findById(dto.getIdConvocatoria())
            .orElseThrow(() -> new IllegalArgumentException("Convocatoria not found"));
    if (convocatoria.getFechaInicio().isBefore(LocalDate.now())) {
        throw new IllegalArgumentException("Convocatoria has started");
    }
    // ...
}

// Tests necesarios:
@Test void create_WhenConvocatoriaIdNull_ShouldThrowException() { }
@Test void create_WhenConvocatoriaNotFound_ShouldThrowException() { }
@Test void create_WhenConvocatoriaStarted_ShouldThrowException() { }
@Test void create_WhenValidInput_ShouldReturnMatricula() { }
```

### 2. Testear Casos Edge

```java
// Casos edge comunes:
@Test void create_WhenEmailIsEmpty_ShouldThrowException() { }
@Test void create_WhenEmailIsNull_ShouldThrowException() { }
@Test void create_WhenEmailIsInvalid_ShouldThrowException() { }
@Test void create_WhenPriceIsZero_ShouldThrowException() { }
@Test void create_WhenPriceIsNegative_ShouldThrowException() { }
@Test void create_WhenDateIsInPast_ShouldThrowException() { }
```

### 3. Testear Validaciones de Negocio

```java
// Validaciones específicas del dominio:
@Test void createMatricula_WhenAlumnoIsNotAlumno_ShouldThrowException() { }
@Test void createMatricula_WhenConvocatoriaIsFull_ShouldThrowException() { }
@Test void createMatricula_WhenAlumnoAlreadyEnrolled_ShouldThrowException() { }
```

### 4. Testear Métodos Privados Indirectamente

```java
// ❌ NO testear métodos privados directamente
// private void calculatePricing(Matricula matricula) { }

// ✅ Testear a través de métodos públicos
@Test
void createShouldApplyDiscountWhenDiscapacidadGreaterThan33() {
    // Este test ejercita calculatePricing indirectamente
    // Verifica que el descuento se aplica correctamente
}
```

### 5. Usar Parameterized Tests

```java
@ParameterizedTest
@ValueSource(strings = {"", " ", "invalid-email", "@example.com"})
void createShouldThrowExceptionWhenEmailInvalid(String email) {
    UsuarioRequestDto dto = UsuarioRequestDto.builder()
            .email(email)
            .password("password")
            .rol(Usuario.Rol.ALUMNO)
            .build();
    
    assertThrows(IllegalArgumentException.class, 
            () -> usuarioService.create(dto));
}
```

## Mejores Prácticas

### 1. Un Test, Una Aserción (cuando sea posible)

```java
// ✅ Bueno - Una aserción principal
@Test
void createShouldReturnUsuarioWithCorrectEmail() {
    UsuarioResponseDto result = usuarioService.create(dto);
    assertEquals("test@example.com", result.getEmail());
}

// ✅ También bueno - Múltiples aserciones relacionadas
@Test
void createShouldReturnCompleteUsuario() {
    UsuarioResponseDto result = usuarioService.create(dto);
    assertNotNull(result.getIdUsuario());
    assertEquals("test@example.com", result.getEmail());
    assertEquals(Usuario.Rol.ALUMNO, result.getRol());
}
```

### 2. Tests Independientes

```java
// ✅ Bueno - Cada test es independiente
@Test
void test1() {
    // No depende de test2
}

@Test
void test2() {
    // No depende de test1
}

// ❌ Evitar - Tests dependientes
@Test
void test1() {
    // Crea datos
}

@Test
void test2() {
    // Depende de datos de test1
}
```

### 3. Usar @BeforeEach para Setup Común

```java
@BeforeEach
void setUp() {
    // Datos comunes a todos los tests
    inputDto = UsuarioRequestDto.builder()...
    mockUsuario = Usuario.builder()...
}
```

### 4. Verificar Interacciones con Mocks

```java
// ✅ Verificar que se llamó
verify(usuarioRepository).save(any(Usuario.class));

// ✅ Verificar que NO se llamó
verify(usuarioRepository, never()).delete(any());

// ✅ Verificar número de llamadas
verify(usuarioRepository, times(2)).findById(anyLong());

// ✅ Verificar argumentos
verify(usuarioRepository).save(argThat(usuario -> 
    usuario.getEmail().equals("test@example.com")
));
```

## Preguntas de Entrevista

### ¿Cómo alcanzas ~95% de cobertura?

**Respuesta:**
"1. **Tests Unitarios Completos**: Testeo todos los métodos públicos de servicios con casos happy path y edge cases
2. **Tests de Integración**: Verifico repositorios con @DataJpaTest
3. **Cobertura de Branches**: Testeo todos los caminos (if/else, excepciones)
4. **Exclusión Inteligente**: Excluyo código generado (Lombok, MapStruct) y DTOs simples
5. **JaCoCo Configuration**: Configuro reglas para verificar mínimo 95% en cada paquete
6. **CI/CD Integration**: Los tests se ejecutan automáticamente y fallan si cobertura < 95%"

### ¿Qué excluyes de la cobertura y por qué?

**Respuesta:**
"Excluyo:
1. **DTOs**: Solo contienen getters/setters, no lógica de negocio
2. **Entidades con Lombok**: Código generado (getters/setters, builders)
3. **Mappers Generados**: MapStruct genera implementaciones, no necesitan tests
4. **Configuración**: Clases de configuración de Spring
5. **Excepciones Personalizadas**: Constructores simples

La clave es enfocar la cobertura en código con lógica de negocio: servicios, validaciones, cálculos."

### ¿Cómo testeas lógica compleja en servicios?

**Respuesta:**
"1. **Extracción de Métodos**: Divido lógica compleja en métodos privados pequeños
2. **Testeo Indirecto**: Testeo métodos privados a través de métodos públicos
3. **Mocks Específicos**: Uso mocks para simular diferentes escenarios
4. **Verificación de Estado**: Verifico que el estado final es correcto
5. **Verificación de Interacciones**: Verifico que se llamaron los métodos correctos con los argumentos correctos
6. **Casos Edge**: Testeo casos límite (null, valores extremos, condiciones de borde)"

## Conclusión

Alcanzar ~95% de cobertura requiere disciplina, pero los beneficios son enormes: código más confiable, refactoring seguro y detección temprana de bugs. La clave está en testear sistemáticamente, excluir código generado inteligentemente y mantener los tests mantenibles.

