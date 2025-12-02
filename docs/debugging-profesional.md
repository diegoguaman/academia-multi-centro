# 🔍 Metodología Profesional de Debugging

## Análisis de los Logs: Paso a Paso

### ✅ Lo que SÍ funciona:

```
✓ .env file loaded successfully (development mode)
HikariPool-1 - Starting...
HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@212332ff
HikariPool-1 - Start completed.
```

**Conclusión:** ✅ **La conexión a Supabase funciona perfectamente**

### ❌ El Problema Real:

```
Schema-validation: wrong column type encountered in column [id_calificacion] 
in table [calificacion]; 
found [serial (Types#INTEGER)], but expecting [bigint (Types#BIGINT)]
```

**Análisis:**
- Hibernate está validando el schema (modo `validate`)
- La entidad Java tiene: `Long idCalificacion` → mapea a `BIGINT`
- La base de datos tiene: `serial` → es `INTEGER` en PostgreSQL
- Hibernate detecta el desajuste y falla

---

## 🎯 Metodología Profesional de Debugging

### 1. **Identificar el Error Real (No el Sintomático)**

**Error sintomático (lo que parece):**
- "Connection refused" → Parece problema de conexión

**Error real (lo que es):**
- Schema validation failure → Problema de mapeo de tipos

**Técnica:**
- Leer el **último error** en el stack trace (causa raíz)
- Ignorar errores intermedios (cascada de fallos)

### 2. **Priorizar Problemas**

**Orden de prioridad:**
1. ✅ Conexión a DB → **RESUELTO** (HikariPool started)
2. ❌ Schema validation → **PROBLEMA ACTUAL**
3. ⚠️ Otros errores → Dependen del #2

### 3. **Buscar en Logs: Patrones Clave**

**Señales de éxito:**
- `HikariPool-1 - Start completed` → Conexión OK
- `✓ .env file loaded` → Configuración OK

**Señales de error:**
- `Schema-validation: wrong column type` → Mapeo incorrecto
- `Connection refused` → Problema de red/credenciales

### 4. **Aislar el Problema**

**Preguntas clave:**
- ¿Dónde falla? → Schema validation
- ¿Por qué falla? → Tipo de columna incorrecto
- ¿Qué entidad? → `Calificacion`
- ¿Qué columna? → `id_calificacion`

### 5. **Solución Incremental**

**No arreglar todo de golpe:**
1. Primero: Arreglar schema validation
2. Luego: Verificar otras entidades
3. Finalmente: Probar endpoints

---

## 🔧 Solución al Problema Actual

### Opción 1: Cambiar ddl-auto (Rápido - Desarrollo)

```properties
# Cambiar de validate a update
spring.jpa.hibernate.ddl-auto=update
```

**Ventajas:**
- ✅ Funciona inmediatamente
- ✅ Hibernate sincroniza schema automáticamente

**Desventajas:**
- ⚠️ Puede modificar la DB (no ideal para producción)
- ⚠️ No detecta problemas de schema

### Opción 2: Cambiar a none (Temporal - Debugging)

```properties
# Desactivar validación temporalmente
spring.jpa.hibernate.ddl-auto=none
```

**Ventajas:**
- ✅ No valida schema
- ✅ Permite iniciar la app para debugging

**Desventajas:**
- ⚠️ No detecta problemas de schema
- ⚠️ Solo para debugging

### Opción 3: Arreglar Tipo en DB (Profesional - Recomendado)

**En Supabase, ejecutar:**

```sql
-- Cambiar tipo de INTEGER a BIGINT
ALTER TABLE calificacion 
ALTER COLUMN id_calificacion TYPE BIGINT;
```

**Ventajas:**
- ✅ Arregla el problema de raíz
- ✅ Mantiene validación activa
- ✅ Consistente con otras entidades

**Desventajas:**
- ⚠️ Requiere acceso a DB
- ⚠️ Puede afectar datos existentes

### Opción 4: Mapear Tipo en Entidad (Si no puedes cambiar DB)

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_calificacion", columnDefinition = "INTEGER")
private Integer idCalificacion;  // Cambiar de Long a Integer
```

**Ventajas:**
- ✅ No requiere cambios en DB
- ✅ Funciona con schema existente

**Desventajas:**
- ⚠️ Limita rango de IDs (2 mil millones vs 9 quintillones)
- ⚠️ Inconsistente con otras entidades (usar Long)

---

## 📊 Comparación de Opciones

| Opción | Velocidad | Seguridad | Profesional | Recomendado |
|--------|-----------|-----------|-------------|-------------|
| `ddl-auto=update` | ⭐⭐⭐ | ⭐⭐ | ⭐⭐ | Desarrollo |
| `ddl-auto=none` | ⭐⭐⭐ | ⭐ | ⭐ | Debugging |
| Cambiar DB | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | **Producción** |
| Cambiar entidad | ⭐⭐ | ⭐⭐ | ⭐⭐ | Si no puedes cambiar DB |

---

## 🎯 Recomendación Profesional

**Para desarrollo inmediato:**
```properties
spring.jpa.hibernate.ddl-auto=update
```

**Para producción:**
1. Cambiar tipo en DB a BIGINT
2. Mantener `ddl-auto=validate`
3. Verificar todas las entidades

---

## 🔍 Verificar Otras Entidades

Después de arreglar `Calificacion`, verificar que otras entidades no tengan el mismo problema:

```sql
-- Verificar tipos de IDs
SELECT 
    table_name,
    column_name,
    data_type
FROM information_schema.columns
WHERE column_name LIKE 'id_%'
ORDER BY table_name, column_name;
```

---

## ✅ Checklist de Debugging Profesional

- [x] Identificar error real (no sintomático)
- [x] Verificar conexión a DB (✅ funciona)
- [x] Aislar problema específico (schema validation)
- [x] Identificar entidad afectada (Calificacion)
- [x] Elegir solución apropiada
- [ ] Aplicar solución
- [ ] Verificar que funciona
- [ ] Documentar solución

---

**Próximo paso:** Aplicar solución y verificar que la app inicia correctamente.

