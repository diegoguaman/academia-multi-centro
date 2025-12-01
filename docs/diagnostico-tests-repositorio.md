# Diagnóstico y Solución: Errores en Tests de Repositorio

## 🔍 Problema Identificado

Al ejecutar `mvn clean test`, los tests de repositorio (`UsuarioRepositoryTest` y `CursoRepositoryTest`) fallan con el error:

```
Failed to load ApplicationContext
Schema-validation: missing table [calificacion]
```

## 📋 Análisis del Error

### 1. **Error Raíz**
```
Caused by: org.hibernate.tool.schema.spi.SchemaManagementException: 
Schema-validation: missing table [calificacion]
```

### 2. **Causa Principal**

El problema tiene **3 causas interrelacionadas**:

#### A. Configuración de Hibernate en Producción
En `application.properties` principal:
```properties
spring.jpa.hibernate.ddl-auto=validate
```
- `validate` requiere que **todas las tablas existan** en la base de datos
- Valida el esquema contra la base de datos real (PostgreSQL en Supabase)

#### B. Comportamiento de `@DataJpaTest`
- `@DataJpaTest` intenta cargar el **ApplicationContext completo**
- Lee primero `application.properties` (producción)
- Luego intenta aplicar configuraciones de test
- **Problema**: Hibernate valida el esquema antes de que se configure H2

#### C. Base de Datos en Memoria Vacía
- Los tests usan H2 en memoria (`application-test.properties`)
- H2 está **vacía** al inicio
- Hibernate intenta **validar** un esquema que no existe
- Falla porque no encuentra las tablas

## 🎯 Solución Implementada

### Cambios Realizados

#### 1. **Anotaciones en Tests de Repositorio**

Agregamos dos anotaciones clave:

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
class UsuarioRepositoryTest {
    // ...
}
```

**Explicación de cada anotación:**

- **`@DataJpaTest`**: Configura un contexto de test ligero solo para JPA
- **`@AutoConfigureTestDatabase(replace = Replace.ANY)`**: 
  - Reemplaza la base de datos de producción con una embebida (H2)
  - `ANY` = reemplaza cualquier base de datos (PostgreSQL, MySQL, etc.)
  - Alternativa: `NONE` = no reemplaza (mantiene la original)
- **`@TestPropertySource(locations = "classpath:application-test.properties")`**:
  - **Sobrescribe** las propiedades del `application.properties` principal
  - Asegura que los tests usen H2 y `ddl-auto=create-drop`

#### 2. **Configuración en `application-test.properties`**

```properties
# Base de datos H2 en memoria
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver

# IMPORTANTE: create-drop en lugar de validate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

**Diferencia clave:**
- `validate`: Valida esquema existente → ❌ Falla si no hay tablas
- `create-drop`: Crea esquema al inicio, lo elimina al final → ✅ Perfecto para tests

## 📚 Conceptos Clave para Entrevistas

### 1. **Estrategias de DDL en Hibernate**

| Estrategia | Uso | Comportamiento |
|------------|-----|---------------|
| `validate` | Producción | Valida que el esquema coincida con las entidades |
| `update` | Desarrollo | Actualiza el esquema si hay cambios |
| `create` | Desarrollo/Test | Crea el esquema, no lo elimina |
| `create-drop` | **Test** | Crea al inicio, elimina al final |
| `none` | Producción | No hace nada (usa Flyway/Liquibase) |

### 2. **Anotaciones de Testing en Spring Boot**

#### `@DataJpaTest`
- **Propósito**: Test de integración para capa de persistencia
- **Incluye**: 
  - Repositorios JPA
  - `TestEntityManager`
  - Transacciones (rollback automático)
- **Excluye**: 
  - `@Component`, `@Service`, `@Controller`
  - Auto-configuración completa de Spring Boot

#### `@AutoConfigureTestDatabase`
- **Propósito**: Controlar qué base de datos usar en tests
- **Opciones**:
  - `Replace.ANY`: Reemplaza cualquier BD con embebida
  - `Replace.NONE`: Mantiene la BD configurada
  - `Replace.AUTO_CONFIGURED`: Solo si hay BD embebida disponible

#### `@TestPropertySource`
- **Propósito**: Sobrescribir propiedades para tests específicos
- **Prioridad**: Más alta que `application.properties`
- **Uso común**: Configurar BD de test, deshabilitar seguridad, etc.

### 3. **Orden de Carga de Propiedades**

Spring Boot carga propiedades en este orden (mayor a menor prioridad):

1. `@TestPropertySource` (en tests)
2. `application-{profile}.properties`
3. `application.properties`
4. Variables de entorno
5. Propiedades del sistema

### 4. **Estrategias de Testing de Repositorios**

#### Opción A: H2 en Memoria (Actual)
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
```
- ✅ Rápido
- ✅ Aislado
- ⚠️ Diferencias de sintaxis SQL con PostgreSQL

#### Opción B: Testcontainers (Recomendado para integración real)
```java
@SpringBootTest
@Testcontainers
class RepositoryTest {
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:15");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
    }
}
```
- ✅ Base de datos real (PostgreSQL)
- ✅ Mismo comportamiento que producción
- ⚠️ Más lento (requiere Docker)

## 🛠️ Proceso de Diagnóstico (Para Entrevistas)

### Paso 1: Leer el Error Completo
```
Schema-validation: missing table [calificacion]
```
→ Indica que Hibernate está en modo `validate`

### Paso 2: Identificar la Causa
- ¿Qué archivo de propiedades se está usando?
- ¿Qué valor tiene `ddl-auto`?
- ¿Qué anotaciones tiene el test?

### Paso 3: Verificar Configuración
- `application.properties` → `ddl-auto=validate` ❌
- `application-test.properties` → `ddl-auto=create-drop` ✅
- Test sin `@TestPropertySource` → No usa propiedades de test ❌

### Paso 4: Aplicar Solución
1. Agregar `@TestPropertySource` al test
2. Agregar `@AutoConfigureTestDatabase` si es necesario
3. Verificar que `application-test.properties` tenga `create-drop`

## ✅ Checklist de Verificación

Antes de ejecutar tests de repositorio, verificar:

- [ ] `application-test.properties` existe y tiene `ddl-auto=create-drop`
- [ ] Tests tienen `@TestPropertySource(locations = "classpath:application-test.properties")`
- [ ] Tests tienen `@AutoConfigureTestDatabase(replace = Replace.ANY)` si usan H2
- [ ] H2 está en el classpath (`pom.xml` con scope `test`)
- [ ] No hay dependencias de PostgreSQL en tiempo de test

## 🎓 Preguntas Frecuentes en Entrevistas

### "¿Por qué `@DataJpaTest` no usa automáticamente H2?"

**Respuesta**: `@DataJpaTest` **sí** reemplaza automáticamente la BD con H2 si está disponible, PERO solo si no hay configuración explícita de DataSource. Si `application.properties` tiene configuración de PostgreSQL, Spring Boot intenta usarla. Por eso necesitamos `@TestPropertySource` para sobrescribir.

### "¿Cuál es la diferencia entre `@SpringBootTest` y `@DataJpaTest`?"

**Respuesta**:
- `@SpringBootTest`: Carga el contexto completo (como producción)
- `@DataJpaTest`: Carga solo componentes JPA (más rápido, más aislado)

### "¿Por qué usar `create-drop` en tests y no `update`?"

**Respuesta**: 
- `create-drop`: Garantiza un estado limpio en cada test (crea y elimina)
- `update`: Modifica esquema existente (puede dejar datos residuales entre tests)

## 📖 Referencias

- [Spring Boot Testing - DataJpaTest](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing.spring-boot-applications.spring-data-jpa-tests)
- [Hibernate DDL Auto](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#configurations-hbm-ddl)
- [Testcontainers](https://www.testcontainers.org/)

