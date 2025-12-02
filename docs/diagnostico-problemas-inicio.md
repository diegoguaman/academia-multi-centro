# 🔍 Diagnóstico: Problemas al Iniciar la Aplicación

## Fecha: 2025-12-02
## Problema: La aplicación no inicia - Error de conexión a base de datos

---

## ❌ ERROR PRINCIPAL

```
Driver org.postgresql.Driver claims to not accept jdbcUrl, ${DB_SUPABASE}
```

**Causa raíz:** Spring Boot está leyendo literalmente `${DB_SUPABASE}` en lugar de resolver la variable de entorno.

---

## 📋 DIAGNÓSTICO COMPLETO

### 1. Problema: Variables de Entorno No Resueltas

**Síntoma:**
- Spring Boot no puede resolver `${DB_SUPABASE}`, `${DB_USERNAME}`, `${DB_PASSWORD}`
- El driver PostgreSQL recibe el string literal `"${DB_SUPABASE}"` en lugar de la URL real

**Causa:**
- Spring Boot **NO lee archivos `.env` automáticamente**
- Las variables deben estar en el sistema operativo o usar una librería adicional
- En Windows PowerShell, las variables se configuran diferente que en bash

**Ubicación del problema:**
```properties
# src/main/resources/application.properties
spring.datasource.url=${DB_SUPABASE}  ← Variable no resuelta
spring.datasource.username=${DB_USERNAME}  ← Variable no resuelta
spring.datasource.password=${DB_PASSWORD}  ← Variable no resuelta
```

---

### 2. Problema: IDE Classpath Error

**Síntoma:**
```
The file AcademymanagerApplication.java isn't on the classpath
```

**Causa:**
- El IDE (VS Code con Java Extension) no detecta correctamente el proyecto Maven
- El classpath no está configurado correctamente

**Impacto:**
- ⚠️ **NO crítico** - La aplicación puede ejecutarse con `mvn spring-boot:run`
- ⚠️ Puede afectar debugging y autocompletado en el IDE

---

### 3. Problema: Errores en `/target`

**Síntoma:**
- Muchos errores reportados en la carpeta `target/`

**Causa:**
- `target/` contiene archivos generados (compilados, mappers de MapStruct, etc.)
- El IDE puede mostrar errores en archivos generados que son normales

**Impacto:**
- ⚠️ **NO crítico** - Los archivos en `target/` son generados automáticamente
- ✅ Es normal tener "errores" en archivos generados si el IDE no los reconoce

**Solución:**
- Excluir `target/` del análisis del IDE (ya está en `.gitignore`)

---

## 🎯 SOLUCIONES (Por Prioridad)

### SOLUCIÓN 1: Configurar Variables de Entorno en Windows PowerShell ⭐ (RECOMENDADA)

**Opción A: Variables de sesión (temporal)**

```powershell
# En PowerShell, antes de ejecutar mvn spring-boot:run
$env:DB_SUPABASE = "jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "tu_password_aqui"
$env:JWT_SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
$env:JWT_EXPIRATION_TIME = "86400000"

# Luego ejecutar
mvn spring-boot:run
```

**Opción B: Script de inicio (recomendado)**

Crear archivo `start-dev.ps1`:
```powershell
# start-dev.ps1
$env:DB_SUPABASE = "jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "tu_password_aqui"
$env:JWT_SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
$env:JWT_EXPIRATION_TIME = "86400000"

Write-Host "Variables de entorno configuradas" -ForegroundColor Green
mvn spring-boot:run
```

**Uso:**
```powershell
.\start-dev.ps1
```

---

### SOLUCIÓN 2: Usar Librería para Leer .env ⭐⭐ (ALTERNATIVA)

**Agregar dependencia al `pom.xml`:**

```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>4.0.0</version>
</dependency>
```

**Crear archivo `.env` en la raíz:**
```env
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=tu_password_aqui
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_TIME=86400000
```

**Ventajas:**
- ✅ No necesitas exportar variables manualmente
- ✅ Funciona igual en Windows, Linux, macOS
- ✅ El archivo `.env` está en `.gitignore` (seguro)

**Desventajas:**
- ❌ Dependencia adicional
- ❌ Requiere rebuild del proyecto

---

### SOLUCIÓN 3: Valores por Defecto en application.properties ⭐⭐⭐ (MÁS SIMPLE)

**Modificar `application.properties`:**

```properties
# Database Configuration (Supabase)
spring.datasource.url=${DB_SUPABASE:jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:tu_password_default}
```

**Sintaxis:** `${VARIABLE:valor_por_defecto}`

**Ventajas:**
- ✅ Funciona inmediatamente sin configuración adicional
- ✅ Si existe la variable de entorno, la usa
- ✅ Si no existe, usa el valor por defecto

**Desventajas:**
- ⚠️ Password en texto plano en el código (solo para desarrollo)
- ⚠️ No usar en producción

---

### SOLUCIÓN 4: application-dev.properties (RECOMENDADA PARA DESARROLLO)

**Crear `src/main/resources/application-dev.properties`:**

```properties
# Database Configuration (Supabase) - Development
spring.datasource.url=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=tu_password_aqui

# JWT Configuration
jwt.secret.key=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration.time=86400000
```

**Modificar `application.properties` para usar valores por defecto:**

```properties
spring.datasource.url=${DB_SUPABASE:jdbc:postgresql://localhost:5432/postgres}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:}
```

**Ejecutar con profile:**
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Ventajas:**
- ✅ Separación de configuraciones (dev vs prod)
- ✅ No expone passwords en código principal
- ✅ Fácil de cambiar entre entornos

---

## 🔧 SOLUCIÓN PARA IDE CLASSPATH ERROR

### VS Code / Cursor

1. **Abrir Command Palette:** `Ctrl+Shift+P`
2. **Ejecutar:** `Java: Clean Java Language Server Workspace`
3. **Reiniciar VS Code**

### IntelliJ IDEA

1. **File → Invalidate Caches / Restart**
2. **Maven → Reload Project**

### Verificar Maven

```powershell
# Verificar que Maven detecta el proyecto
mvn validate

# Limpiar y recompilar
mvn clean compile
```

---

## 🧹 SOLUCIÓN PARA ERRORES EN /target

### Excluir target/ del análisis del IDE

**VS Code / Cursor:**
1. Abrir `.vscode/settings.json`
2. Agregar:
```json
{
  "files.exclude": {
    "**/target": true
  },
  "java.project.sourcePaths": [
    "src/main/java"
  ],
  "java.project.outputPath": "target/classes"
}
```

**IntelliJ IDEA:**
- `target/` ya está excluido por defecto
- Si no, `File → Settings → Build → Excluded` → Agregar `target/`

---

## ✅ CHECKLIST DE VERIFICACIÓN

### Antes de Ejecutar:

- [ ] Variables de entorno configuradas O
- [ ] `application-dev.properties` creado O
- [ ] Valores por defecto en `application.properties`
- [ ] Password de Supabase correcta
- [ ] URL de Supabase correcta (verificar en dashboard)

### Después de Ejecutar:

- [ ] Aplicación inicia sin errores
- [ ] Log muestra: `Tomcat started on port(s): 8080`
- [ ] No hay errores de conexión a DB
- [ ] Health check responde: `GET http://localhost:8080/actuator/health`

---

## 🚨 VERIFICAR CONEXIÓN A SUPABASE

### Test Manual de Conexión:

```powershell
# Instalar psql (si no lo tienes)
# O usar PgAdmin para verificar conexión

# Verificar que la URL es correcta:
# Debe ser: jdbc:postgresql://db.XXXXX.supabase.co:5432/postgres
# NO debe tener: ?sslmode=require (Spring Boot lo maneja automáticamente)
```

### Verificar en Supabase Dashboard:

1. Ir a: https://supabase.com/dashboard
2. Seleccionar tu proyecto
3. Settings → Database
4. Verificar:
   - **Host:** `db.XXXXX.supabase.co`
   - **Port:** `5432`
   - **Database:** `postgres`
   - **Password:** (la que configuraste)

---

## 📊 RESUMEN DE PROBLEMAS Y SOLUCIONES

| Problema | Severidad | Solución | Prioridad |
|----------|-----------|----------|-----------|
| Variables de entorno no resueltas | 🔴 CRÍTICO | Script PowerShell o application-dev.properties | 1 |
| IDE classpath error | 🟡 MEDIO | Clean workspace / Reload Maven | 2 |
| Errores en /target | 🟢 BAJO | Excluir del IDE | 3 |

---

## 🎯 RECOMENDACIÓN FINAL

**Para desarrollo local (Windows):**

1. ✅ **Crear `application-dev.properties`** con valores hardcodeados (solo dev)
2. ✅ **Ejecutar con:** `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
3. ✅ **Para producción:** Usar variables de entorno reales

**Ventajas:**
- ✅ Funciona inmediatamente
- ✅ No necesitas configurar variables cada vez
- ✅ Separación clara entre dev y prod
- ✅ Seguro (`.env` y `application-dev.properties` en `.gitignore`)

---

## 📝 PRÓXIMOS PASOS

1. **Implementar SOLUCIÓN 4** (application-dev.properties)
2. **Verificar conexión a Supabase**
3. **Ejecutar aplicación**
4. **Probar endpoints de autenticación**

---

**¿Necesitas ayuda con alguna solución específica?** 🚀


