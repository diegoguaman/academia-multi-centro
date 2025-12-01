# Repositorios Spring Data JPA: Abstracción y Optimización del Acceso a Datos

## Introducción

Spring Data JPA proporciona una abstracción poderosa sobre JPA, reduciendo significativamente el código necesario para implementar repositorios. En lugar de escribir implementaciones manuales, defines interfaces y Spring genera la implementación automáticamente.

## ¿Por qué Spring Data JPA?

### Comparación: JPA Tradicional vs Spring Data JPA

**JPA Tradicional (Sin Spring Data):**
```java
@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Usuario findById(Long id) {
        return entityManager.find(Usuario.class, id);
    }
    
    @Override
    public List<Usuario> findAll() {
        TypedQuery<Usuario> query = entityManager.createQuery(
            "SELECT u FROM Usuario u", Usuario.class);
        return query.getResultList();
    }
    
    @Override
    public Usuario save(Usuario usuario) {
        if (usuario.getIdUsuario() == null) {
            entityManager.persist(usuario);
        } else {
            usuario = entityManager.merge(usuario);
        }
        return usuario;
    }
    
    @Override
    public void deleteById(Long id) {
        Usuario usuario = findById(id);
        if (usuario != null) {
            entityManager.remove(usuario);
        }
    }
    
    @Override
    public Optional<Usuario> findByEmail(String email) {
        TypedQuery<Usuario> query = entityManager.createQuery(
            "SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class);
        query.setParameter("email", email);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    // ... más métodos
}
```

**Spring Data JPA:**
```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

**Reducción:** De ~100 líneas a ~5 líneas (95% menos código)

## Fundamentos de Spring Data JPA

### Interfaz Base: JpaRepository

```java
public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {
    // Métodos CRUD básicos
    <S extends T> S save(S entity);
    <S extends T> List<S> saveAll(Iterable<S> entities);
    Optional<T> findById(ID id);
    boolean existsById(ID id);
    List<T> findAll();
    long count();
    void deleteById(ID id);
    void delete(T entity);
    void deleteAll();
    void flush();
    <S extends T> S saveAndFlush(S entity);
    // ... más métodos
}
```

**Ventajas:**
- **CRUD Completo**: Todos los métodos básicos ya implementados
- **Type Safety**: Tipado genérico previene errores
- **Transaccional**: Métodos automáticamente transaccionales
- **Optimización**: Spring optimiza queries automáticamente

### Query Methods: Convención sobre Configuración

Spring Data JPA genera queries automáticamente basándose en el nombre del método.

**Patrón:**
```
[find|read|get|query|stream][Distinct][By...][OrderBy...]
```

**Ejemplos:**

```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Buscar por campo simple
    Optional<Usuario> findByEmail(String email);
    
    // Buscar múltiples
    List<Usuario> findByRol(Usuario.Rol rol);
    
    // Verificar existencia (más eficiente que findById().isPresent())
    boolean existsByEmail(String email);
    
    // Contar
    long countByRol(Usuario.Rol rol);
    
    // Eliminar
    void deleteByEmail(String email);
    
    // Buscar por relación
    List<Usuario> findByDatosPersonalesNombre(String nombre);
    
    // Múltiples condiciones (AND)
    List<Usuario> findByEmailAndRol(String email, Usuario.Rol rol);
    
    // Múltiples condiciones (OR)
    List<Usuario> findByEmailOrRol(String email, Usuario.Rol rol);
    
    // Negación
    List<Usuario> findByRolNot(Usuario.Rol rol);
    
    // Comparaciones
    List<Usuario> findByFechaCreacionAfter(LocalDateTime fecha);
    List<Usuario> findByFechaCreacionBefore(LocalDateTime fecha);
    List<Usuario> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Like / Containing / StartingWith / EndingWith
    List<Usuario> findByEmailContaining(String substring);
    List<Usuario> findByEmailStartingWith(String prefix);
    List<Usuario> findByEmailEndingWith(String suffix);
    List<Usuario> findByEmailLike(String pattern);
    
    // Null checks
    List<Usuario> findByEmailIsNull();
    List<Usuario> findByEmailIsNotNull();
    
    // In
    List<Usuario> findByRolIn(List<Usuario.Rol> roles);
    
    // IgnoreCase
    List<Usuario> findByEmailIgnoreCase(String email);
    
    // Ordenamiento
    List<Usuario> findByRolOrderByFechaCreacionDesc(Usuario.Rol rol);
    
    // Limitando resultados
    Optional<Usuario> findFirstByRolOrderByFechaCreacionDesc(Usuario.Rol rol);
    List<Usuario> findTop10ByRolOrderByFechaCreacionDesc(Usuario.Rol rol);
    
    // Distinct
    List<Usuario> findDistinctByRol(Usuario.Rol rol);
}
```

## ¿Por qué Crear Métodos Personalizados?

### 1. Performance: existsBy vs findById

**Ineficiente:**
```java
public boolean usuarioExists(String email) {
    Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
    return usuario.isPresent();  // Ejecuta SELECT completo
}
```

**Eficiente:**
```java
public boolean usuarioExists(String email) {
    return usuarioRepository.existsByEmail(email);  // Ejecuta SELECT COUNT(1) o EXISTS
}
```

**SQL Generado:**

**findByEmail:**
```sql
SELECT u.id_usuario, u.email, u.password_hash, u.rol, u.fecha_creacion, u.activo
FROM usuario u
WHERE u.email = ?
```

**existsByEmail:**
```sql
SELECT COUNT(1) > 0
FROM usuario u
WHERE u.email = ?
-- O más eficiente:
SELECT EXISTS(SELECT 1 FROM usuario WHERE email = ?)
```

### 2. Legibilidad y Expresividad

**Menos Claro:**
```java
List<Matricula> matriculas = matriculaRepository.findAll()
    .stream()
    .filter(m -> m.getAlumno().getIdUsuario().equals(idAlumno))
    .filter(m -> m.getEstadoPago() == Matricula.EstadoPago.PAGADO)
    .collect(Collectors.toList());
```

**Más Claro:**
```java
List<Matricula> matriculas = matriculaRepository
    .findByAlumnoIdUsuarioAndEstadoPago(idAlumno, Matricula.EstadoPago.PAGADO);
```

**Ventajas:**
- Query ejecutada en base de datos (más eficiente)
- Código más legible
- Type-safe (errores en compile-time)

### 3. Reutilización

**Sin Método Personalizado:**
```java
// En UsuarioService
public boolean validateEmail(String email) {
    return usuarioRepository.findByEmail(email).isPresent();
}

// En RegistrationService
public boolean isEmailTaken(String email) {
    return usuarioRepository.findByEmail(email).isPresent();
}

// Duplicación de lógica
```

**Con Método Personalizado:**
```java
// En UsuarioRepository
boolean existsByEmail(String email);

// Reutilizado en múltiples servicios
public boolean validateEmail(String email) {
    return usuarioRepository.existsByEmail(email);
}
```

### 4. Type Safety y Validación en Compile-Time

Spring Data JPA valida los nombres de métodos en tiempo de compilación:

```java
// ✅ Válido - compila
Optional<Usuario> findByEmail(String email);

// ❌ Error de compilación - campo no existe
Optional<Usuario> findByEmailAddress(String emailAddress);

// ❌ Error de compilación - tipo incorrecto
Optional<Usuario> findByEmail(Integer email);
```

## Queries Personalizadas con @Query

Cuando los query methods no son suficientes, puedes usar `@Query`:

### JPQL (Java Persistence Query Language)

```java
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    // JPQL - trabaja con entidades
    @Query("SELECT m FROM Matricula m WHERE m.alumno.idUsuario = :idAlumno AND m.estadoPago = :estado")
    List<Matricula> findMatriculasByAlumnoAndEstado(
        @Param("idAlumno") Long idAlumno,
        @Param("estado") Matricula.EstadoPago estado
    );
    
    // Proyección - solo campos específicos
    @Query("SELECT m.idMatricula, m.precioFinal FROM Matricula m WHERE m.alumno.idUsuario = :idAlumno")
    List<Object[]> findMatriculaSummaryByAlumno(@Param("idAlumno") Long idAlumno);
    
    // Agregaciones
    @Query("SELECT SUM(m.precioFinal) FROM Matricula m WHERE m.alumno.idUsuario = :idAlumno")
    BigDecimal calculateTotalByAlumno(@Param("idAlumno") Long idAlumno);
    
    // Subconsultas
    @Query("SELECT m FROM Matricula m WHERE m.precioFinal > (SELECT AVG(m2.precioFinal) FROM Matricula m2)")
    List<Matricula> findMatriculasAboveAverage();
}
```

### Native Queries (SQL Nativo)

```java
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    // SQL nativo - más control, pero menos portable
    @Query(value = "SELECT * FROM matricula WHERE id_alumno = :idAlumno AND estado_pago = :estado", 
           nativeQuery = true)
    List<Matricula> findMatriculasNative(
        @Param("idAlumno") Long idAlumno,
        @Param("estado") String estado
    );
    
    // Con paginación
    @Query(value = "SELECT * FROM matricula WHERE id_alumno = :idAlumno",
           countQuery = "SELECT COUNT(*) FROM matricula WHERE id_alumno = :idAlumno",
           nativeQuery = true)
    Page<Matricula> findMatriculasByAlumnoPaged(
        @Param("idAlumno") Long idAlumno,
        Pageable pageable
    );
}
```

**Cuándo usar Native Queries:**
- Funciones específicas de base de datos (PostgreSQL, MySQL, etc.)
- Queries complejas con optimizaciones específicas
- Migración de código SQL existente

**Desventajas:**
- Menos portable entre bases de datos
- No type-safe (errores en runtime)
- Más difícil de mantener

## Modificando Datos: @Modifying

Para queries que modifican datos (UPDATE, DELETE), necesitas `@Modifying`:

```java
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    @Modifying
    @Query("UPDATE Matricula m SET m.estadoPago = :estado WHERE m.idMatricula = :id")
    int updateEstadoPago(@Param("id") Long id, @Param("estado") Matricula.EstadoPago estado);
    
    @Modifying
    @Query("DELETE FROM Matricula m WHERE m.estadoPago = :estado AND m.fechaMatricula < :fecha")
    int deleteOldMatriculas(@Param("estado") Matricula.EstadoPago estado, @Param("fecha") LocalDateTime fecha);
    
    @Modifying(clearAutomatically = true)  // Limpia EntityManager después
    @Query("UPDATE Matricula m SET m.descuentoAplicado = :descuento WHERE m.idMatricula = :id")
    int updateDescuento(@Param("id") Long id, @Param("descuento") BigDecimal descuento);
}
```

**Importante:**
- `@Modifying` requiere `@Transactional` en el método que lo llama
- `clearAutomatically = true` limpia el EntityManager para evitar datos obsoletos

## Paginación y Ordenamiento

### Paginación

```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Paginación automática
    Page<Usuario> findByRol(Usuario.Rol rol, Pageable pageable);
    
    // Slice (más eficiente, no cuenta total)
    Slice<Usuario> findByRol(Usuario.Rol rol, Pageable pageable);
    
    // Lista paginada
    List<Usuario> findByRol(Usuario.Rol rol, Pageable pageable);
}

// Uso:
Pageable pageable = PageRequest.of(0, 10, Sort.by("fechaCreacion").descending());
Page<Usuario> usuarios = usuarioRepository.findByRol(Usuario.Rol.ALUMNO, pageable);

// Información de paginación
usuarios.getTotalElements();  // Total de elementos
usuarios.getTotalPages();    // Total de páginas
usuarios.hasNext();          // ¿Tiene siguiente página?
usuarios.hasPrevious();      // ¿Tiene página anterior?
```

### Ordenamiento

```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Ordenamiento en nombre del método
    List<Usuario> findByRolOrderByFechaCreacionDesc(Usuario.Rol rol);
    
    // Múltiples campos
    List<Usuario> findByRolOrderByFechaCreacionDescEmailAsc(Usuario.Rol rol);
    
    // Con Sort parameter
    List<Usuario> findByRol(Usuario.Rol rol, Sort sort);
}

// Uso:
Sort sort = Sort.by("fechaCreacion").descending().and(Sort.by("email").ascending());
List<Usuario> usuarios = usuarioRepository.findByRol(Usuario.Rol.ALUMNO, sort);
```

## Especificaciones (Specifications)

Para queries dinámicas complejas:

```java
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long>, JpaSpecificationExecutor<Matricula> {
    // Métodos de Specification disponibles automáticamente
}

// Uso:
public class MatriculaSpecifications {
    public static Specification<Matricula> hasAlumno(Long idAlumno) {
        return (root, query, cb) -> cb.equal(root.get("alumno").get("idUsuario"), idAlumno);
    }
    
    public static Specification<Matricula> hasEstado(Matricula.EstadoPago estado) {
        return (root, query, cb) -> cb.equal(root.get("estadoPago"), estado);
    }
    
    public static Specification<Matricula> precioGreaterThan(BigDecimal precio) {
        return (root, query, cb) -> cb.greaterThan(root.get("precioFinal"), precio);
    }
}

// Combinar especificaciones:
Specification<Matricula> spec = MatriculaSpecifications.hasAlumno(1L)
    .and(MatriculaSpecifications.hasEstado(Matricula.EstadoPago.PAGADO))
    .and(MatriculaSpecifications.precioGreaterThan(new BigDecimal("100")));

List<Matricula> matriculas = matriculaRepository.findAll(spec);
```

## Mejores Prácticas

### 1. Naming Conventions

```java
// ✅ Bueno - claro y descriptivo
Optional<Usuario> findByEmail(String email);
List<Matricula> findByAlumnoIdUsuarioAndEstadoPago(Long idAlumno, EstadoPago estado);

// ❌ Evitar - ambiguo
Optional<Usuario> find(String email);
List<Matricula> find(Long id, EstadoPago estado);
```

### 2. Performance: existsBy vs findById

```java
// ✅ Eficiente
boolean exists = usuarioRepository.existsByEmail(email);

// ❌ Ineficiente
boolean exists = usuarioRepository.findByEmail(email).isPresent();
```

### 3. Evitar N+1 Queries

```java
// ❌ N+1 Query Problem
List<Matricula> matriculas = matriculaRepository.findAll();
for (Matricula m : matriculas) {
    System.out.println(m.getConvocatoria().getCurso().getNombre());  // Query por cada matricula
}

// ✅ Solución: Fetch Join
@Query("SELECT m FROM Matricula m JOIN FETCH m.convocatoria c JOIN FETCH c.curso")
List<Matricula> findAllWithRelations();
```

### 4. Usar Proyecciones para Optimizar

```java
// ❌ Trae toda la entidad
List<Matricula> matriculas = matriculaRepository.findAll();

// ✅ Solo campos necesarios
@Query("SELECT m.idMatricula, m.precioFinal FROM Matricula m")
List<Object[]> findMatriculaSummaries();

// ✅ O mejor: Interface Projection
public interface MatriculaSummary {
    Long getIdMatricula();
    BigDecimal getPrecioFinal();
}

@Query("SELECT m.idMatricula as idMatricula, m.precioFinal as precioFinal FROM Matricula m")
List<MatriculaSummary> findMatriculaSummaries();
```

## Preguntas de Entrevista

### ¿Por qué usar Spring Data JPA en lugar de JPA tradicional?

**Respuesta:**
"Spring Data JPA reduce significativamente el código boilerplate (95% menos código). Los query methods se generan automáticamente basándose en convenciones de nombres, proporcionando type-safety en compile-time. Además, Spring optimiza automáticamente las queries y maneja transacciones. Para queries complejas, puedo usar @Query con JPQL o SQL nativo, manteniendo la flexibilidad cuando la necesito."

### ¿Cómo evitas el problema N+1?

**Respuesta:**
"Uso fetch joins en @Query para cargar relaciones en una sola query. También configuro fetch strategies en las entidades (@ManyToOne(fetch = FetchType.EAGER) para relaciones críticas, aunque prefiero LAZY por defecto y usar fetch joins explícitos cuando necesito las relaciones. Para casos complejos, uso EntityGraph para definir qué relaciones cargar."

### ¿Cuándo usarías @Query en lugar de query methods?

**Respuesta:**
"Uso @Query cuando:
1. La query es compleja y no se puede expresar con convenciones de nombres
2. Necesito optimizaciones específicas (fetch joins, proyecciones)
3. Necesito agregaciones o subconsultas
4. Necesito queries nativas para funciones específicas de la base de datos
5. Necesito modificar datos con @Modifying"

## Conclusión

Spring Data JPA es una herramienta poderosa que simplifica significativamente el acceso a datos mientras mantiene la flexibilidad para casos complejos. La clave está en entender cuándo usar query methods automáticos vs queries personalizadas, y aplicar mejores prácticas para optimizar performance.

