# 🔄 Solución: Actuator No Funciona en Docker (403 Forbidden)

## 🔍 Problema

**Síntoma**: 
- ✅ La aplicación funciona en local y puedes acceder a `/actuator/health`
- ❌ La misma aplicación en Docker devuelve **403 Forbidden** para `/actuator/health`

**Causa**: 
La imagen Docker fue construida **ANTES** de hacer los cambios en `SecurityConfig.java` y `JwtAuthenticationFilter.java`.

---

## ✅ Solución: Reconstruir la Imagen

### Paso 1: Recompilar el JAR

```powershell
# Limpiar y recompilar con los cambios
.\mvnw clean package -DskipTests
```

**¿Por qué?**: Los cambios en `SecurityConfig.java` y `JwtAuthenticationFilter.java` necesitan estar compilados en el JAR.

### Paso 2: Reconstruir la Imagen Docker

```powershell
# Reconstruir la imagen con el nuevo JAR
docker build -t academia-multi-centro:latest .
```

**¿Por qué?**: El Dockerfile copia el JAR desde `target/*.jar`. Si el JAR no tiene los cambios, la imagen tampoco los tendrá.

### Paso 3: Detener y Eliminar el Contenedor Anterior

```powershell
# Detener contenedor existente
docker stop academia-app
docker rm academia-app
```

### Paso 4: Ejecutar el Contenedor Nuevo

```powershell
# Ejecutar con el script (que ya tiene las variables de entorno)
.\docker-run.ps1
```

O manualmente:
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

### Paso 5: Verificar que Funciona

```powershell
# Probar health check
curl http://localhost:8080/actuator/health
```

O abrir en el navegador: `http://localhost:8080/actuator/health`

**Respuesta Esperada**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

---

## 🔍 ¿Por qué Funciona en Local pero No en Docker?

### ✅ Local (Funciona)

```powershell
# Ejecutas directamente el JAR
java -jar target/academymanager-0.0.1-SNAPSHOT.jar
```

**Flujo**:
1. Maven compila el código con los cambios
2. Genera JAR con `SecurityConfig` actualizado
3. Ejecutas el JAR directamente
4. ✅ Actuator funciona porque el código tiene los cambios

### ❌ Docker (No Funciona - Antes de Rebuild)

```powershell
# Imagen construida ANTES de los cambios
docker build -t academia-multi-centro:latest .
docker run academia-multi-centro:latest
```

**Flujo**:
1. Dockerfile copia `target/*.jar` (JAR viejo, sin cambios)
2. Construye imagen con JAR viejo
3. Ejecutas contenedor con código viejo
4. ❌ Actuator no funciona porque el código NO tiene los cambios

### ✅ Docker (Funciona - Después de Rebuild)

```powershell
# Recompilar JAR con cambios
.\mvnw clean package -DskipTests

# Reconstruir imagen con JAR nuevo
docker build -t academia-multi-centro:latest .

# Ejecutar contenedor
docker run academia-multi-centro:latest
```

**Flujo**:
1. Maven compila código con cambios → JAR nuevo
2. Dockerfile copia JAR nuevo
3. Construye imagen con código nuevo
4. ✅ Actuator funciona porque el código tiene los cambios

---

## 📋 Checklist de Verificación

Después de reconstruir, verifica:

- [ ] JAR se compiló correctamente (`target/*.jar` existe y es reciente)
- [ ] Imagen Docker se reconstruyó (verifica timestamp)
- [ ] Contenedor se ejecuta sin errores
- [ ] Logs muestran "Started AcademymanagerApplication"
- [ ] `curl http://localhost:8080/actuator/health` devuelve `{"status":"UP"}`
- [ ] Navegador puede acceder a `http://localhost:8080/actuator/health` sin 403

---

## 🚨 Troubleshooting

### Problema: Sigue dando 403 después de rebuild

**Causa**: Cache de Docker o JAR no se actualizó.

**Solución**:
```powershell
# 1. Limpiar completamente
docker stop academia-app
docker rm academia-app
docker rmi academia-multi-centro:latest

# 2. Recompilar sin cache
.\mvnw clean package -DskipTests

# 3. Reconstruir sin cache
docker build --no-cache -t academia-multi-centro:latest .

# 4. Ejecutar
.\docker-run.ps1
```

### Problema: JAR no se genera

**Causa**: Errores de compilación.

**Solución**:
```powershell
# Ver errores de compilación
.\mvnw clean compile

# Si hay errores, corregirlos primero
# Luego compilar JAR
.\mvnw package -DskipTests
```

---

## 📝 Comandos Rápidos (Todo en Uno)

```powershell
# Script completo para rebuild
.\mvnw clean package -DskipTests
docker build -t academia-multi-centro:latest .
docker stop academia-app 2>$null
docker rm academia-app 2>$null
.\docker-run.ps1
```

---

## 🎯 Resumen para Entrevistas

### ¿Por qué Actuator No Funcionaba en Docker?

"La imagen Docker fue construida antes de hacer los cambios en `SecurityConfig` para permitir acceso a Actuator. El JAR dentro de la imagen no tenía los cambios, por lo que Spring Security seguía bloqueando los endpoints."

### ¿Cómo lo Solucionaste?

"Recompilé el JAR con `mvn clean package`, reconstruí la imagen Docker, y volví a ejecutar el contenedor. Esto aseguró que el código actualizado (con permisos para Actuator) estuviera en la imagen."

### Lección Aprendida

"Siempre que hago cambios en el código, necesito recompilar el JAR y reconstruir la imagen Docker para que los cambios estén disponibles en el contenedor."

---

**Referencias**:
- [Docker Build Process](https://docs.docker.com/engine/reference/commandline/build/)
- [Maven Package Goal](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)
