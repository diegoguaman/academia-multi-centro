# ✅ Solución: Conectar con Supabase

## 🔍 Problema Detectado

El error muestra:
```
Connection to localhost:5432 refused
```

**Causa:** La aplicación está intentando conectarse a `localhost:5432` en lugar de Supabase.

**Razón:** La configuración de Supabase no se está cargando correctamente.

---

## ✅ Solución Implementada

He corregido `DotenvConfig.java` para que cargue `.env` **ANTES** de que Spring Boot lea `application.properties`.

### Opción 1: Usar archivo `.env` (Recomendado)

**Paso 1:** Crear archivo `.env` en la raíz del proyecto:

```env
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=Ac4d3m1a_1994!
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_TIME=86400000
```

**Paso 2:** Ejecutar aplicación:

```powershell
mvn spring-boot:run
```

**Ahora debería:**
- ✅ Cargar `.env` automáticamente
- ✅ Conectar a Supabase correctamente
- ✅ Mostrar: `✓ .env file loaded successfully`

---

### Opción 2: Usar `application-dev.properties` (Alternativa)

**Ya está configurado** en `src/main/resources/application-dev.properties`:

```properties
spring.datasource.url=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=Ac4d3m1a_1994!
```

**Ejecutar con perfil dev:**

```powershell
# Opción A: Script
.\start-dev.ps1

# Opción B: Manual
$env:SPRING_PROFILES_ACTIVE="dev"
mvn spring-boot:run
```

---

## 🔍 Verificar Conexión

### 1. Verificar que se carga `.env`

Al iniciar, deberías ver:
```
✓ .env file loaded successfully (development mode)
```

### 2. Verificar URL de conexión

En los logs, busca:
```
HikariPool-1 - Starting...
```

Si conecta correctamente, verás:
```
HikariPool-1 - Start completed.
```

Si hay error, verás:
```
Connection to db.wjbbuiiskercelchtaqg.supabase.co:5432 refused
```

### 3. Test de conexión

```powershell
# Verificar que la aplicación inicia
mvn spring-boot:run

# En otra terminal, probar health endpoint
curl http://localhost:8080/actuator/health
```

---

## ⚠️ Si Aún Hay Problemas

### Error: "Connection refused" a Supabase

**Posibles causas:**

1. **Password incorrecto**
   - Verificar password en Supabase Dashboard
   - Resetear si es necesario

2. **Problema IPv4**
   - Usar Session Pooler (puerto 6543):
   ```env
   DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
   ```

3. **Firewall bloqueando**
   - Verificar que puerto 5432 o 6543 esté abierto
   - Probar desde otra red

4. **Proyecto pausado en Supabase**
   - Verificar en Supabase Dashboard que el proyecto esté activo

### Error: "Still connecting to localhost"

**Solución:**
1. Verificar que `.env` existe en la raíz del proyecto
2. Verificar formato del archivo (sin espacios extra)
3. Reiniciar aplicación completamente

---

## 📊 Prioridad de Configuración

```
1. Variables de entorno del sistema (más alta)
   ↓
2. .env file (cargado por DotenvConfig)
   ↓
3. application-dev.properties (si profile=dev)
   ↓
4. application.properties defaults (más baja)
```

---

## ✅ Checklist

- [ ] Archivo `.env` creado en raíz del proyecto
- [ ] URL correcta: `jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres`
- [ ] Password correcto configurado
- [ ] Aplicación reiniciada completamente
- [ ] Log muestra: `✓ .env file loaded successfully`
- [ ] Log muestra: `HikariPool-1 - Start completed`

---

## 🎯 Próximos Pasos

1. **Crear `.env`** con la configuración de arriba
2. **Ejecutar:** `mvn spring-boot:run`
3. **Verificar logs** para confirmar conexión
4. **Probar endpoint:** `curl http://localhost:8080/actuator/health`

**¡Debería funcionar ahora!** 🚀

