# Quick Start: Día 3 - Spring Security + JWT

## Setup Rápido (5 minutos)

### 1. Configurar Variables de Entorno

```bash
# Copiar archivo de ejemplo
cp .env.example .env

# Editar .env con tus valores
nano .env

# O exportar directamente:
export DB_SUPABASE="jdbc:postgresql://..."
export DB_USERNAME="postgres"
export DB_PASSWORD="your_password"
export JWT_SECRET_KEY=$(openssl rand -base64 32)
export JWT_EXPIRATION_TIME=86400000
```

### 2. Compilar y Ejecutar

```bash
# Compilar proyecto
mvn clean package -DskipTests

# Ejecutar
java -jar target/academymanager-0.0.1-SNAPSHOT.jar

# O con Maven:
mvn spring-boot:run
```

### 3. Probar Autenticación

#### Opción A: Con cURL

```bash
# 1. Registrar usuario
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@test.com",
    "password": "Admin123!",
    "rol": "ADMIN",
    "nombre": "Admin",
    "apellidos": "Usuario"
  }'

# 2. Login y guardar token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@test.com",
    "password": "Admin123!"
  }' | jq -r '.token')

echo "Token: $TOKEN"

# 3. Acceder a endpoint protegido
curl -X GET http://localhost:8080/api/cursos \
  -H "Authorization: Bearer $TOKEN"

# 4. Probar acceso sin token (debe fallar con 401)
curl -X GET http://localhost:8080/api/cursos

# 5. Probar con token inválido (debe fallar con 401)
curl -X GET http://localhost:8080/api/cursos \
  -H "Authorization: Bearer invalid_token_here"
```

#### Opción B: Con Postman

1. **Importar Collection:**
   - Create new collection: "Academia API"
   
2. **Request 1: Register**
   ```
   POST http://localhost:8080/api/auth/register
   
   Body (JSON):
   {
     "email": "admin@test.com",
     "password": "Admin123!",
     "rol": "ADMIN",
     "nombre": "Admin",
     "apellidos": "Usuario"
   }
   ```

3. **Request 2: Login**
   ```
   POST http://localhost:8080/api/auth/login
   
   Body (JSON):
   {
     "email": "admin@test.com",
     "password": "Admin123!"
   }
   
   Tests (JavaScript):
   pm.environment.set("jwt_token", pm.response.json().token);
   ```

4. **Request 3: Get Cursos (Protected)**
   ```
   GET http://localhost:8080/api/cursos
   
   Headers:
   Authorization: Bearer {{jwt_token}}
   ```

#### Opción C: Script Automático

```bash
# Usar script incluido
./test-auth.sh
```

### 4. Verificar CI/CD

```bash
# 1. Commit cambios
git add .
git commit -m "feat: Add JWT authentication"

# 2. Push a GitHub
git push origin main

# 3. Ver workflow en:
# https://github.com/YOUR_USERNAME/YOUR_REPO/actions

# 4. Pipeline ejecutará automáticamente
```

---

## Troubleshooting

### Error: "JWT secret key must be at least 256 bits"

**Solución:**
```bash
# Generar nuevo secret de 256+ bits
openssl rand -base64 32

# Actualizar en .env o application.properties
JWT_SECRET_KEY=<nuevo_secret_aqui>
```

### Error: "User not found" en login

**Causa:** Usuario no existe en DB

**Solución:**
```bash
# Registrar usuario primero
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{ "email": "test@test.com", "password": "Test123!", "rol": "ALUMNO", "nombre": "Test", "apellidos": "User" }'
```

### Error: "Bad credentials" con password correcto

**Causa:** Password no hasheado correctamente

**Solución:** Verificar que AuthService usa `passwordEncoder.encode()` al registrar:
```java
usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
```

### Error: 403 Forbidden en endpoint protegido

**Causa:** Usuario no tiene rol requerido

**Solución:** Verificar configuración en SecurityConfig:
```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
// Usuario debe tener rol ADMIN para acceder a /api/admin/**
```

### CI/CD no ejecuta

**Causa:** Workflow no activado o branch incorrecto

**Solución:**
```yaml
# Verificar en .github/workflows/ci-cd.yml
on:
  push:
    branches: [ main, develop ]  # Debe incluir tu branch
```

---

## Testing de Roles

```bash
# 1. Crear usuarios con diferentes roles
# ADMIN
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@test.com", "password": "Admin123!", "rol": "ADMIN", "nombre": "Admin", "apellidos": "User"}'

# PROFESOR
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "profesor@test.com", "password": "Prof123!", "rol": "PROFESOR", "nombre": "Profesor", "apellidos": "User"}'

# ALUMNO
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "alumno@test.com", "password": "Alum123!", "rol": "ALUMNO", "nombre": "Alumno", "apellidos": "User"}'

# 2. Login con cada usuario y probar accesos
# ADMIN puede acceder a todo
ADMIN_TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@test.com", "password": "Admin123!"}' | jq -r '.token')

curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer $ADMIN_TOKEN"
# ✅ Debe funcionar (200 OK)

# ALUMNO NO puede acceder a admin endpoints
ALUMNO_TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "alumno@test.com", "password": "Alum123!"}' | jq -r '.token')

curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer $ALUMNO_TOKEN"
# ❌ Debe fallar (403 Forbidden)
```

---

## Docker Testing

```bash
# 1. Build imagen
docker build -t academia-app:latest .

# 2. Run container
docker run -d \
  -p 8080:8080 \
  -e DB_SUPABASE="jdbc:postgresql://..." \
  -e DB_USERNAME="postgres" \
  -e DB_PASSWORD="password" \
  -e JWT_SECRET_KEY="$(openssl rand -base64 32)" \
  --name academia-app \
  academia-app:latest

# 3. Ver logs
docker logs -f academia-app

# 4. Test health
curl http://localhost:8080/actuator/health

# 5. Test authentication
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@test.com", "password": "Test123!"}'

# 6. Stop y remove
docker stop academia-app
docker rm academia-app
```

---

## Métricas de Éxito

✅ **Authentication funciona:**
- Register retorna 201 Created con token
- Login retorna 200 OK con token
- Token válido permite acceso a endpoints protegidos

✅ **Authorization funciona:**
- ADMIN puede acceder a /api/admin/**
- PROFESOR NO puede acceder a /api/admin/**
- Requests sin token reciben 401 Unauthorized

✅ **CI/CD funciona:**
- Build completa sin errores
- Tests pasan (95%+ coverage)
- Security scan no encuentra vulnerabilidades críticas
- Docker imagen construida correctamente

---

## Próximos Pasos

1. **Agregar Refresh Tokens** (opcional)
2. **Implementar Rate Limiting** (prevenir brute force)
3. **Agregar Audit Logging** (registrar eventos de seguridad)
4. **Deploy a staging** (Heroku/Railway/GKE)
5. **Continuar con Día 4:** GraphQL + APIs modernas

---

## Comandos Útiles

```bash
# Ver token decodificado (sin verificar firma)
echo "$TOKEN" | cut -d'.' -f2 | base64 -d | jq

# Generar múltiples usuarios de prueba
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/auth/register \
    -H "Content-Type: application/json" \
    -d "{\"email\": \"user$i@test.com\", \"password\": \"User${i}123!\", \"rol\": \"ALUMNO\", \"nombre\": \"User\", \"apellidos\": \"$i\"}"
done

# Ver todos los workflows de GitHub Actions
gh run list

# Ver logs de último workflow
gh run view --log

# Rebuild Docker image sin cache
docker build --no-cache -t academia-app:latest .

# Push imagen a Docker Hub
docker tag academia-app:latest YOUR_USERNAME/academia-app:latest
docker push YOUR_USERNAME/academia-app:latest
```

---

¡Listo para producción! 🚀

