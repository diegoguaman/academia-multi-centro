# 🔧 Solución: Error de Conexión a Base de Datos en Docker

## 📋 Análisis del Error

### Error Observado
```
Caused by: java.net.ConnectException: Connection refused
at org.postgresql.core.PGStream.createSocket(PGStream.java:261)
```

### Tipo de Error
**❌ NO es un error de código**  
**✅ Es un error de CONFIGURACIÓN/DESPLIEGUE**

---

## 🔍 ¿Por qué Ocurre?

### 1. **Variables de Entorno No Configuradas**

Cuando ejecutas el contenedor Docker **sin pasar las variables de entorno**, la aplicación usa los valores por defecto de `application.properties`:

```properties
# Valores por defecto (cuando no hay variables de entorno)
spring.datasource.url=${DB_SUPABASE:jdbc:postgresql://localhost:5432/postgres}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:}
```

**Problema**: Intenta conectarse a `localhost:5432`, pero:
- Dentro del contenedor Docker, `localhost` es el propio contenedor (no tu máquina)
- No hay PostgreSQL corriendo dentro del contenedor
- Necesita conectarse a Supabase (base de datos externa)

### 2. **Flujo de Configuración**

```
┌─────────────────────────────────────────────────────────┐
│ 1. Docker ejecuta contenedor                             │
│    → Sin variables de entorno                            │
│                                                           │
│ 2. Spring Boot lee application.properties                │
│    → DB_SUPABASE no está definida                        │
│    → Usa default: localhost:5432                         │
│                                                           │
│ 3. HikariCP intenta conectar                             │
│    → localhost:5432 dentro del contenedor                │
│    → Connection refused (no hay DB ahí)                 │
│                                                           │
│ 4. Aplicación falla al iniciar                          │
└─────────────────────────────────────────────────────────┘
```

---

## ✅ Solución: Pasar Variables de Entorno

### Opción 1: Usar el Script PowerShell (Recomendado)

```powershell
# Ejecutar el script que ya tienes configurado
.\docker-run.ps1
```

Este script:
- ✅ Detiene contenedores existentes
- ✅ Pasa todas las variables de entorno necesarias
- ✅ Muestra los logs automáticamente

### Opción 2: Comando Docker Manual

```powershell
docker run -d `
  --name academia-app `
  -p 8080:8080 `
  -e DB_SUPABASE="jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres?sslmode=require" `
  -e DB_USERNAME="postgres.wjbbuiiskercelchtaqg" `
  -e DB_PASSWORD="Ac4d3m1a_1994!" `
  -e JWT_SECRET_KEY="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970" `
  -e JWT_EXPIRATION_TIME="86400000" `
  -e SPRING_PROFILES_ACTIVE="prod" `
  academia-multi-centro:latest
```

**⚠️ IMPORTANTE**: Reemplaza los valores con tus credenciales reales de Supabase.

### Opción 3: Usar Archivo .env (Solo Desarrollo)

```powershell
# Crear archivo .env (si no existe)
# Luego usar docker-compose o pasar con --env-file
docker run -d --env-file .env academia-multi-centro:latest
```

**⚠️ NO recomendado para producción**: Usa variables de entorno del sistema.

---

## 🔍 Verificación: ¿Funciona Correctamente?

### 1. **Verificar que el Contenedor Está Corriendo**

```powershell
docker ps
```

Deberías ver:
```
CONTAINER ID   IMAGE                        STATUS
abc123def456   academia-multi-centro:latest Up 2 minutes
```

### 2. **Verificar Logs (Sin Errores de Conexión)**

```powershell
docker logs academia-app
```

**✅ Logs Correctos**:
```
Production profile detected - skipping .env file loading
HikariPool-1 - Start completed.
Started AcademymanagerApplication in 6.968 seconds
```

**❌ Logs con Error**:
```
Connection refused
HikariPool-1 - Connection is not available
```

### 3. **Probar Health Check**

```powershell
curl http://localhost:8080/actuator/health
```

**Respuesta Esperada**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL"
      }
    }
  }
}
```

### 4. **Verificar Variables de Entorno Dentro del Contenedor**

```powershell
docker exec academia-app env | grep DB_
```

Deberías ver:
```
DB_SUPABASE=jdbc:postgresql://...
DB_USERNAME=postgres.wjbbuiiskercelchtaqg
DB_PASSWORD=Ac4d3m1a_1994!
```

---

## 🚨 Troubleshooting

### Problema 1: "Connection refused" Persiste

**Causa**: Variables de entorno no se están pasando correctamente.

**Solución**:
```powershell
# 1. Detener contenedor
docker stop academia-app
docker rm academia-app

# 2. Verificar que las variables están correctas
# (revisa docker-run.ps1 o el comando que usas)

# 3. Ejecutar de nuevo con variables explícitas
.\docker-run.ps1
```

### Problema 2: "Invalid username or password"

**Causa**: Credenciales de Supabase incorrectas.

**Solución**:
1. Verifica tus credenciales en Supabase Dashboard
2. Asegúrate de usar el **usuario completo**: `postgres.wjbbuiiskercelchtaqg`
3. Verifica que la URL de conexión sea correcta

### Problema 3: "Connection timeout"

**Causa**: Firewall o red bloqueando conexión a Supabase.

**Solución**:
1. Verifica que puedes conectarte a Supabase desde tu máquina
2. Verifica que el puerto 5432 (o 6543) no esté bloqueado
3. Prueba con `sslmode=require` en la URL

---

## 📊 Comparación: Local vs Docker

### ✅ Funciona Localmente

```powershell
# Localmente (tu máquina)
java -jar target/academymanager-0.0.1-SNAPSHOT.jar
```

**Por qué funciona**:
- Lee `.env` file (si existe)
- O usa variables de entorno del sistema
- O usa `application.properties` con `localhost:5432` (si tienes PostgreSQL local)

### ❌ Falla en Docker

```powershell
# Docker (sin variables de entorno)
docker run academia-multi-centro:latest
```

**Por qué falla**:
- No hay `.env` dentro del contenedor
- No hay variables de entorno del sistema
- Usa default `localhost:5432` (que no existe en el contenedor)

### ✅ Funciona en Docker (Con Variables)

```powershell
# Docker (con variables de entorno)
docker run -e DB_SUPABASE="..." academia-multi-centro:latest
```

**Por qué funciona**:
- Variables de entorno se pasan al contenedor
- Spring Boot las lee y las usa
- Se conecta a Supabase correctamente

---

## 🎯 Resumen para Entrevistas

### ¿Qué Tipo de Error Era?

"Era un error de **configuración/despliegue**, no de código. La aplicación funcionaba localmente porque leía variables de entorno o un archivo `.env`, pero en Docker necesitaba que las variables se pasaran explícitamente al contenedor."

### ¿Cómo lo Solucionaste?

"Implementé un script PowerShell (`docker-run.ps1`) que pasa todas las variables de entorno necesarias al contenedor Docker. Esto asegura que la aplicación tenga acceso a las credenciales de Supabase y otras configuraciones críticas."

### ¿Por qué es Importante?

"En producción (Kubernetes), las variables de entorno se gestionan mediante Secrets y ConfigMaps, siguiendo el mismo patrón: la aplicación lee variables de entorno, no archivos locales."

---

## ✅ Checklist de Verificación

- [ ] Contenedor se ejecuta sin errores
- [ ] Logs muestran "HikariPool-1 - Start completed"
- [ ] Health check devuelve `"status": "UP"`
- [ ] Health check muestra `"db": { "status": "UP" }`
- [ ] Variables de entorno están presentes en el contenedor
- [ ] No hay errores de "Connection refused"
- [ ] Aplicación responde en `http://localhost:8080/actuator/health`

---

## 📝 Comandos Rápidos

```powershell
# Ejecutar con script (recomendado)
.\docker-run.ps1

# Ver logs
docker logs -f academia-app

# Verificar health
curl http://localhost:8080/actuator/health

# Detener
docker stop academia-app

# Eliminar
docker rm academia-app
```

---

**Referencias**:
- [Docker Environment Variables](https://docs.docker.com/engine/reference/commandline/run/#env)
- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
