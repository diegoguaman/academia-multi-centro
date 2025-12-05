# 🐳 Proceso Completo de Dockerización - Guía Paso a Paso

## 📋 Resumen

Este documento detalla el proceso completo para dockerizar la aplicación Spring Boot Academia Multi-Centro, incluyendo los cambios realizados, configuración necesaria y comandos en el orden correcto.

---

## ✅ Cambios Realizados

### 1. Agregar Spring Boot Actuator al `pom.xml`

**Razón**: El Dockerfile usa `/actuator/health` para health checks, pero Actuator no estaba en las dependencias.

**Cambio realizado**:
```xml
<!-- Agregado en pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**Ubicación**: `pom.xml` (después de `spring-boot-starter-validation`)

**Verificación**: Actuator ya estaba configurado en `application.properties`:
```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
management.health.livenessState.enabled=true
management.health.readinessState.enabled=true
```

---

### 2. Correcciones en el `Dockerfile`

#### 2.1. Instalar `wget` para Health Checks

**Problema**: Alpine Linux no incluye `wget` por defecto, pero el health check lo necesita.

**Cambio realizado**:
```dockerfile
# Install wget for health checks
RUN apk add --no-cache wget
```

**Ubicación**: Después de `FROM eclipse-temurin:21-jre-alpine`, antes de crear el usuario.

---

#### 2.2. Documentación de Variables de Entorno

**Razón**: Hacer explícitas las variables de entorno requeridas para facilitar el uso.

**Cambio realizado**:
```dockerfile
# Required Environment Variables (must be set at runtime):
# - DB_SUPABASE: JDBC URL to Supabase (e.g., jdbc:postgresql://host:6543/postgres?sslmode=require)
# - DB_USERNAME: Database username (e.g., postgres)
# - DB_PASSWORD: Database password (SECRET - use Kubernetes Secrets or Docker secrets)
# - JWT_SECRET_KEY: 256-bit secret key for JWT signing (SECRET)
# - JWT_EXPIRATION_TIME: JWT expiration time in milliseconds (default: 86400000)
# - SPRING_PROFILES_ACTIVE: Spring profile (recommended: prod for production)
#
# Example:
# docker run -e DB_SUPABASE="..." -e DB_PASSWORD="..." -e JWT_SECRET_KEY="..." ...
```

**Ubicación**: Después del `HEALTHCHECK`, antes de `ENV JAVA_OPTS`.

---

## 📦 Estado Final del Dockerfile

El Dockerfile ahora incluye:

✅ **Multi-stage build** (optimizado para tamaño)  
✅ **Usuario no-root** (seguridad)  
✅ **wget instalado** (para health checks)  
✅ **Health check configurado** (`/actuator/health`)  
✅ **JVM optimizado** para contenedores  
✅ **Variables de entorno documentadas**  
✅ **Alpine Linux** (imagen ligera ~200MB)

---

## 🚀 Proceso Completo: Comandos en Orden

### Paso 1: Verificar Prerequisitos

**Verificar que Maven está disponible:**
```powershell
.\mvnw --version
```

**Verificar que Docker está corriendo:**
```powershell
docker --version
docker ps
```

**Verificar que el proyecto compila:**
```powershell
.\mvnw clean compile
```

---

### Paso 2: Compilar el Proyecto (Generar JAR)

**Comando:**
```powershell
.\mvnw clean package -DskipTests
```

**Qué hace:**
- Limpia compilaciones anteriores (`clean`)
- Compila el código fuente
- Ejecuta tests (opcional, se puede omitir con `-DskipTests`)
- Genera el JAR en `target/academymanager-0.0.1-SNAPSHOT.jar`

**Verificación:**
```powershell
# Verificar que el JAR se generó
Test-Path target\academymanager-0.0.1-SNAPSHOT.jar
# Debe retornar: True
```

**Tiempo estimado**: 2-5 minutos (depende de si las dependencias están cacheadas)

---

### Paso 3: Construir la Imagen Docker

**Comando:**
```powershell
docker build -t academia-multi-centro:latest .
```

**Qué hace:**
- Lee el `Dockerfile` en el directorio actual (`.`)
- Construye la imagen con el tag `academia-multi-centro:latest`
- Ejecuta el multi-stage build:
  - **Stage 1 (builder)**: Compila la aplicación (si se usa)
  - **Stage 2 (runtime)**: Crea la imagen final con solo el JAR

**Verificación:**
```powershell
# Ver la imagen creada
docker images academia-multi-centro

# Debe mostrar algo como:
# REPOSITORY              TAG       IMAGE ID       CREATED         SIZE
# academia-multi-centro   latest    abc123def456   2 minutes ago   250MB
```

**Tiempo estimado**: 3-8 minutos (primera vez más lento por descargar layers)

**Nota**: Si ya construiste antes, Docker usará cache y será más rápido.

---

### Paso 4: Verificar la Imagen

**Ver detalles de la imagen:**
```powershell
docker inspect academia-multi-centro:latest
```

**Ver tamaño de la imagen:**
```powershell
docker images academia-multi-centro --format "{{.Size}}"
```

**Tamaño esperado**: ~200-250MB (con Alpine Linux)

---

### Paso 5: Ejecutar el Contenedor

#### Opción A: Comando Manual (Una Línea)

```powershell
docker run -d --name academia-app -p 8080:8080 -e DB_SUPABASE="jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require" -e DB_USERNAME="postgres" -e DB_PASSWORD="Ac4d3m1a_1994!" -e JWT_SECRET_KEY="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970" -e JWT_EXPIRATION_TIME="86400000" -e SPRING_PROFILES_ACTIVE="prod" academia-multi-centro:latest
```

#### Opción B: Usando Script PowerShell

```powershell
.\docker-run.ps1
```

**Qué hace el comando:**
- `-d`: Ejecuta en modo detached (background)
- `--name academia-app`: Nombre del contenedor
- `-p 8080:8080`: Mapea puerto 8080 del host al puerto 8080 del contenedor
- `-e`: Define variables de entorno (ver sección siguiente)
- `academia-multi-centro:latest`: Imagen a ejecutar

**Variables de entorno configuradas:**
- `DB_SUPABASE`: URL JDBC a Supabase (Session Pooler, puerto 6543)
- `DB_USERNAME`: Usuario de base de datos
- `DB_PASSWORD`: Password de base de datos (⚠️ SECRET)
- `JWT_SECRET_KEY`: Clave secreta para JWT (⚠️ SECRET)
- `JWT_EXPIRATION_TIME`: Tiempo de expiración del JWT (24 horas)
- `SPRING_PROFILES_ACTIVE`: Perfil Spring (prod para producción)

---

### Paso 6: Verificar que el Contenedor Está Corriendo

**Ver contenedores activos:**
```powershell
docker ps
```

**Deberías ver:**
```
CONTAINER ID   IMAGE                        STATUS          PORTS                    NAMES
abc123def456   academia-multi-centro:latest Up 30 seconds   0.0.0.0:8080->8080/tcp   academia-app
```

**Ver logs del contenedor:**
```powershell
docker logs academia-app
```

**Logs en tiempo real:**
```powershell
docker logs -f academia-app
```

**Presiona `Ctrl+C` para salir de los logs en tiempo real.**

---

### Paso 7: Verificar Health Check

**Verificar endpoint de health:**
```powershell
# Opción 1: Usando curl (si está instalado)
curl http://localhost:8080/actuator/health

# Opción 2: Usando Invoke-WebRequest (PowerShell nativo)
Invoke-WebRequest -Uri http://localhost:8080/actuator/health | Select-Object -ExpandProperty Content

# Opción 3: Abrir en navegador
# http://localhost:8080/actuator/health
```

**Respuesta esperada:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

**Verificar health check de Docker:**
```powershell
docker inspect academia-app --format='{{json .State.Health}}' | ConvertFrom-Json
```

**Debería mostrar:**
```json
{
  "Status": "healthy",
  "FailingStreak": 0,
  "Log": [...]
}
```

---

### Paso 8: Verificar Conexión a Supabase

**Buscar en logs indicadores de conexión exitosa:**
```powershell
docker logs academia-app | Select-String -Pattern "HikariPool|Started AcademymanagerApplication|Connected"
```

**Indicadores de éxito:**
- `HikariPool-1 - Start completed.`
- `Started AcademymanagerApplication in X seconds`
- `Tomcat started on port(s): 8080`

**Indicadores de error:**
- `Connection refused`
- `Authentication failed`
- `UnknownHostException`

---

## 🛠️ Comandos de Mantenimiento

### Detener el Contenedor

```powershell
docker stop academia-app
```

### Iniciar el Contenedor (si está detenido)

```powershell
docker start academia-app
```

### Eliminar el Contenedor

```powershell
# Primero detener
docker stop academia-app

# Luego eliminar
docker rm academia-app
```

### Eliminar la Imagen

```powershell
# Primero eliminar contenedores que la usan
docker stop academia-app
docker rm academia-app

# Luego eliminar la imagen
docker rmi academia-multi-centro:latest
```

### Ver Uso de Recursos

```powershell
docker stats academia-app
```

---

## 🔍 Troubleshooting

### Problema: "Cannot connect to Docker daemon"

**Solución:**
```powershell
# Verificar que Docker Desktop está corriendo
# En Windows: Abrir Docker Desktop desde el menú de inicio
```

---

### Problema: "Port 8080 is already in use"

**Solución:**
```powershell
# Opción 1: Usar otro puerto
docker run -d --name academia-app -p 8081:8080 ... academia-multi-centro:latest

# Opción 2: Detener el proceso que usa el puerto 8080
# Ver qué proceso usa el puerto:
netstat -ano | findstr :8080
# Matar el proceso (reemplazar PID con el número encontrado):
taskkill /PID <PID> /F
```

---

### Problema: "Container exits immediately"

**Solución:**
```powershell
# Ver logs para identificar el error
docker logs academia-app

# Ejecutar en modo interactivo para ver errores
docker run -it --rm -p 8080:8080 -e DB_SUPABASE="..." ... academia-multi-centro:latest
```

**Errores comunes:**
- Variables de entorno faltantes → Verificar que todas las `-e` están presentes
- Error de conexión a DB → Verificar URL y credenciales de Supabase
- JAR no encontrado → Recompilar con `mvnw clean package`

---

### Problema: "Health check failing"

**Solución:**
```powershell
# Verificar que Actuator está habilitado
docker exec academia-app wget -qO- http://localhost:8080/actuator/health

# Verificar que la app está corriendo
docker exec academia-app ps aux | grep java
```

---

## 📊 Checklist de Verificación

Antes de considerar el proceso completo, verifica:

- [ ] JAR compilado exitosamente (`target/*.jar` existe)
- [ ] Imagen Docker construida (`docker images` muestra la imagen)
- [ ] Contenedor corriendo (`docker ps` muestra el contenedor)
- [ ] Health check responde (`/actuator/health` retorna `{"status":"UP"}`)
- [ ] Conexión a Supabase exitosa (logs muestran `HikariPool-1 - Start completed`)
- [ ] Aplicación accesible (puerto 8080 responde)
- [ ] Logs sin errores críticos

---

## 🎯 Resumen de Comandos (Orden de Ejecución)

```powershell
# 1. Compilar proyecto
.\mvnw clean package -DskipTests

# 2. Construir imagen Docker
docker build -t academia-multi-centro:latest .

# 3. Ejecutar contenedor
docker run -d --name academia-app -p 8080:8080 `
  -e DB_SUPABASE="jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require" `
  -e DB_USERNAME="postgres" `
  -e DB_PASSWORD="Ac4d3m1a_1994!" `
  -e JWT_SECRET_KEY="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970" `
  -e JWT_EXPIRATION_TIME="86400000" `
  -e SPRING_PROFILES_ACTIVE="prod" `
  academia-multi-centro:latest

# 4. Verificar logs
docker logs -f academia-app

# 5. Verificar health
Invoke-WebRequest -Uri http://localhost:8080/actuator/health
```

---

## 📝 Notas Importantes

1. **Variables de Entorno**: Nunca commitear secrets en Git. En producción, usar Kubernetes Secrets o External Secrets Operator.

2. **Puerto 6543**: Se usa el Session Pooler de Supabase (compatible con IPv4). Si tu red soporta IPv6, puedes usar el puerto 5432.

3. **Perfil `prod`**: Con este perfil, la aplicación NO carga archivos `.env` (ver `DotenvConfig.java`). Solo usa variables de entorno del sistema.

4. **Health Check**: Se ejecuta cada 30 segundos. Si falla 3 veces consecutivas, Docker marca el contenedor como unhealthy.

5. **Tamaño de Imagen**: Con Alpine Linux, la imagen final debería ser ~200-250MB. Si es mayor, verificar que no se están incluyendo archivos innecesarios.

---

## 🚀 Próximos Pasos

Una vez que la imagen funciona correctamente:

1. **Push a Registry**: Subir la imagen a Docker Hub, GCR, o ECR
2. **Kubernetes**: Crear manifests (Deployment, Service, Secrets)
3. **CI/CD**: Automatizar build y push en GitHub Actions
4. **Producción**: Configurar External Secrets Operator para gestión de secrets

---

**✅ Proceso completado cuando:**
- La imagen se construye sin errores
- El contenedor inicia correctamente
- Health check responde `UP`
- La aplicación se conecta a Supabase
- Los logs no muestran errores críticos

---

**Tiempo total estimado**: 10-15 minutos (primera vez, con descargas)
