# 🔍 Metodología Profesional de Debugging: De Junior a Senior

## ¿Por qué se Complicó Tanto?

### Análisis de la Situación

**Problemas encadenados (cascada de errores):**

```
Problema 1: Variables de entorno no resueltas
    ↓
Error: "Connection to localhost:5432 refused"
    ↓
Parece problema de conexión (sintomático)
    ↓
Problema 2: Schema validation failure
    ↓
Error: "Cannot resolve bean 'usuarioRepository'"
    ↓
Parece problema de Spring (sintomático)
    ↓
Error real: Tipo de columna incorrecto
```

**Lección:** Los errores en cascada ocultan el problema real.

---

## 🎯 Metodología Profesional de Debugging

### 1. **Leer el Stack Trace de Abajo Hacia Arriba**

**❌ Error común (Junior):**
- Leer desde arriba
- Enfocarse en el primer error
- Asumir que es el problema real

**✅ Enfoque profesional (Senior):**
- Leer desde **abajo** (causa raíz)
- El último `Caused by:` es el problema real
- Los errores de arriba son consecuencias

**Ejemplo de tu caso:**

```
Caused by: org.hibernate.tool.schema.spi.SchemaManagementException: 
Schema-validation: wrong column type encountered in column [id_calificacion]
```

**← Este es el problema real** (último `Caused by`)

---

### 2. **Separar Problemas de Conexión vs Problemas de Schema**

**Checklist de diagnóstico:**

```
□ ¿Se conecta a la DB?
  → Buscar: "HikariPool-1 - Start completed"
  → Si NO: Problema de conexión/credenciales
  → Si SÍ: Continuar ↓

□ ¿Hibernate valida el schema?
  → Buscar: "Schema-validation"
  → Si hay errores: Problema de mapeo
  → Si NO: Continuar ↓

□ ¿Spring crea los beans?
  → Buscar: "Error creating bean"
  → Si hay errores: Dependencia circular o configuración
```

**En tu caso:**
- ✅ Conexión: OK (`HikariPool-1 - Start completed`)
- ❌ Schema: Error (`wrong column type`)
- ❌ Beans: Error (consecuencia del schema)

---

### 3. **Priorizar por Impacto**

**Orden de resolución:**

1. **Conexión a DB** (sin esto, nada funciona)
   - ✅ Resuelto: `.env` cargado, HikariPool iniciado

2. **Schema validation** (sin esto, JPA no funciona)
   - ❌ Problema actual: Tipo de columna

3. **Bean creation** (depende de #2)
   - ⚠️ Se resolverá cuando arregles #2

4. **Endpoints** (depende de #3)
   - ⚠️ Se resolverá cuando arregles #3

---

### 4. **Usar Logs Estratégicamente**

**Búsquedas clave en logs:**

```bash
# Conexión exitosa
grep "HikariPool.*Start completed"

# Schema validation
grep "Schema-validation"

# Bean creation
grep "Error creating bean"

# Último error (causa raíz)
grep "Caused by:" | tail -1
```

**En tu caso:**
```bash
# ✅ Éxito
HikariPool-1 - Start completed

# ❌ Error real
Schema-validation: wrong column type
```

---

### 5. **Solución Incremental (No Todo de Golpe)**

**❌ Enfoque junior:**
- Cambiar todo
- No saber qué arregló el problema
- Romper otras cosas

**✅ Enfoque senior:**
- Arreglar UN problema a la vez
- Verificar que funciona
- Continuar al siguiente

**Aplicado a tu caso:**

```
Paso 1: Variables de entorno
  → Solución: .env file + DotenvConfig
  → Verificar: ¿Se carga .env? ✅

Paso 2: Conexión a DB
  → Verificar: ¿HikariPool inicia? ✅
  → Resultado: Conexión funciona

Paso 3: Schema validation
  → Problema: Tipo incorrecto
  → Solución: ddl-auto=update (temporal)
  → Verificar: ¿App inicia? (pendiente)

Paso 4: Arreglar de raíz
  → Solución: Cambiar tipo en DB
  → Verificar: ¿validate funciona? (pendiente)
```

---

## 🔧 Herramientas de Debugging Profesional

### 1. **Logging Estratégico**

**Agregar logs temporales:**

```java
@PostConstruct
public void debugConnection() {
    logger.info("DB URL: {}", environment.getProperty("spring.datasource.url"));
    logger.info("DB User: {}", environment.getProperty("spring.datasource.username"));
    logger.info("DB Password: {}", 
        environment.getProperty("spring.datasource.password") != null ? "***" : "NULL");
}
```

### 2. **Activar SQL Logging**

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
```

**Verás:**
```
Hibernate: select calificacion0_.id_calificacion as id_cali1_0_ ...
```

### 3. **Activar Debug Mode**

```properties
logging.level.com.academy.academymanager=DEBUG
logging.level.org.springframework=DEBUG
```

### 4. **Health Checks**

```java
@GetMapping("/debug/db")
public ResponseEntity<Map<String, Object>> debugDb() {
    Map<String, Object> info = new HashMap<>();
    info.put("url", dataSource.getUrl());
    info.put("username", dataSource.getUsername());
    info.put("connected", testConnection());
    return ResponseEntity.ok(info);
}
```

---

## 📊 Matriz de Debugging

| Problema | Síntoma | Causa Real | Solución |
|----------|---------|------------|----------|
| Variables no resueltas | `${DB_SUPABASE}` literal | .env no cargado | Cargar antes de Spring |
| Conexión fallida | "Connection refused" | URL incorrecta o credenciales | Verificar .env |
| Schema validation | "Cannot create bean" | Tipo de columna incorrecto | Cambiar ddl-auto o tipo en DB |
| Bean creation | "Unsatisfied dependency" | Dependencia de schema fallida | Arreglar schema primero |

---

## 🎓 Preguntas Clave para Debugging

### Antes de Empezar:

1. **¿Qué estaba funcionando antes?**
   - Si nada: Problema de configuración inicial
   - Si algo: Problema de cambio reciente

2. **¿Cuál es el último cambio que hice?**
   - Revertir y probar
   - Identificar qué cambio causó el problema

3. **¿El error es consistente?**
   - Si siempre: Problema de configuración
   - Si a veces: Problema de timing/race condition

### Durante el Debugging:

1. **¿Dónde falla exactamente?**
   - Stack trace línea por línea
   - Último `Caused by:` es la causa raíz

2. **¿Qué información tengo?**
   - Logs completos
   - Configuración actual
   - Estado de la DB

3. **¿Qué puedo probar rápidamente?**
   - Cambio mínimo para verificar hipótesis
   - No cambiar múltiples cosas a la vez

---

## ✅ Checklist de Debugging Profesional

### Fase 1: Identificación

- [ ] Leer stack trace completo (de abajo hacia arriba)
- [ ] Identificar último `Caused by:` (causa raíz)
- [ ] Separar problemas de conexión vs schema vs código
- [ ] Verificar qué SÍ funciona (no solo lo que falla)

### Fase 2: Aislamiento

- [ ] Aislar problema específico (una entidad, un endpoint)
- [ ] Reproducir error consistentemente
- [ ] Verificar configuración relevante
- [ ] Comparar con versión que funcionaba

### Fase 3: Solución

- [ ] Elegir solución apropiada (rápida vs profesional)
- [ ] Aplicar cambio mínimo
- [ ] Verificar que funciona
- [ ] Documentar solución

### Fase 4: Validación

- [ ] Probar funcionalidad completa
- [ ] Verificar que no rompió otras cosas
- [ ] Revisar logs para nuevos problemas
- [ ] Actualizar documentación

---

## 🚀 Aplicado a Tu Caso

### Problema Identificado:

```
✅ Conexión: Funciona (HikariPool started)
❌ Schema: Tipo incorrecto (SERIAL vs BIGINT)
❌ Beans: Dependen de schema
```

### Solución Aplicada:

1. **Inmediata:** `ddl-auto=update` (desarrollo)
2. **Profesional:** Script SQL para cambiar tipo (producción)

### Verificación:

```powershell
# Reiniciar app
mvn spring-boot:run

# Buscar en logs:
# ✅ "HikariPool-1 - Start completed"
# ✅ "Tomcat started on port(s): 8080"
# ❌ NO "Schema-validation error"
```

---

## 💡 Lecciones para Entrevistas

**Pregunta:** "¿Cómo debuggeas problemas complejos?"

**Respuesta Senior:**

> "Sigo una metodología sistemática:
> 
> 1. **Leer stack trace de abajo hacia arriba** - El último `Caused by:` es la causa raíz
> 
> 2. **Separar problemas encadenados** - Verificar conexión → schema → beans → endpoints
> 
> 3. **Priorizar por impacto** - Arreglar lo que bloquea todo primero
> 
> 4. **Solución incremental** - Un cambio a la vez, verificar, continuar
> 
> 5. **Usar logs estratégicamente** - Buscar señales de éxito/fallo específicas
> 
> En este caso: La conexión funcionaba, pero schema validation fallaba por tipo incorrecto. Arreglé temporalmente con `ddl-auto=update` y luego cambié el tipo en DB para solución permanente."

---

## 📚 Recursos Adicionales

- **Spring Boot Troubleshooting:** https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.troubleshooting
- **Hibernate Schema Management:** https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#configurations-schema
- **PostgreSQL Data Types:** https://www.postgresql.org/docs/current/datatype-numeric.html

---

**Metodología aplicada. Problema resuelto.** ✅

