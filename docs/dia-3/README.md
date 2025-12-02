# Día 3: Seguridad Avanzada y CI/CD

## Resumen Ejecutivo

En este día implementamos **autenticación y autorización profesional** con Spring Security y JWT, junto con un **pipeline CI/CD completo** con GitHub Actions. Este módulo te eleva de desarrollador junior/mid a **nivel senior** en seguridad y DevOps.

## ¿Qué se implementó?

### 1. Spring Security + JWT (Autenticación Stateless)

#### Clases creadas:
```
src/main/java/com/academy/academymanager/
├── security/
│   ├── JwtService.java                    # Generación y validación de tokens
│   ├── JwtException.java                  # Excepciones JWT custom
│   ├── UserDetailsServiceImpl.java        # Integración con DB
│   ├── JwtAuthenticationFilter.java       # Filtro para validar tokens
│   └── SecurityConfig.java                # Configuración de seguridad
├── controller/
│   └── AuthController.java                # Endpoints login/register
├── service/
│   └── AuthService.java                   # Lógica de autenticación
├── dto/request/
│   ├── LoginRequestDto.java
│   └── RegisterRequestDto.java
├── dto/response/
│   └── LoginResponseDto.java
└── exception/
    └── GlobalExceptionHandler.java        # Manejo centralizado de errores
```

#### Características implementadas:
- ✅ **JWT tokens** con firma HMAC-SHA256
- ✅ **BCrypt** para hashing de passwords (10 rounds)
- ✅ **Role-based access control** (ADMIN, PROFESOR, ALUMNO, ADMINISTRATIVO)
- ✅ **Stateless authentication** (sin HttpSession)
- ✅ **Exception handling** global con mensajes claros
- ✅ **Validación** automática de DTOs con Jakarta Validation

#### Endpoints disponibles:
```bash
# Registro de usuario
POST /api/auth/register
Content-Type: application/json

{
  "email": "admin@academia.com",
  "password": "Admin123!",
  "rol": "ADMIN",
  "nombre": "Juan",
  "apellidos": "Pérez"
}

# Login
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@academia.com",
  "password": "Admin123!"
}

# Respuesta:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "email": "admin@academia.com",
  "rol": "ADMIN",
  "nombre": "Juan Pérez"
}

# Usar token en requests protegidos:
GET /api/cursos
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 2. CI/CD con GitHub Actions

#### Archivos creados:
```
.github/workflows/
└── ci-cd.yml                # Pipeline completo con 8 jobs

Dockerfile                   # Multi-stage build optimizado
.dockerignore               # Excluir archivos innecesarios
```

#### Pipeline stages:
1. **Build and Test**: Compilar, ejecutar tests, verificar coverage
2. **Code Quality**: Análisis con SonarCloud
3. **Security Scan**: OWASP Dependency Check
4. **Docker Build**: Construir y pushear imagen
5. **Deploy Staging**: Deployment automático a staging
6. **Deploy Production**: Deployment con aprobación manual
7. **Smoke Tests**: Tests post-deployment
8. **Performance Tests**: Load testing (opcional)

#### Características del pipeline:
- ✅ **Parallel execution** para acelerar builds
- ✅ **Caching** de Maven dependencies (ahorra 2-5 min)
- ✅ **Artifacts** compartidos entre jobs
- ✅ **Security scanning** automático
- ✅ **Docker optimization** con multi-stage builds
- ✅ **Deployment strategies** documentadas (rolling, blue-green, canary)

### 3. Configuración

#### application.properties actualizado:
```properties
# JWT Configuration
jwt.secret.key=${JWT_SECRET_KEY:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
jwt.expiration.time=${JWT_EXPIRATION_TIME:86400000}
```

#### Variables de entorno necesarias:
```bash
# Production
export JWT_SECRET_KEY="<generar con: openssl rand -base64 32>"
export JWT_EXPIRATION_TIME=86400000  # 24 horas en milisegundos

# Database (ya existentes)
export DB_SUPABASE="jdbc:postgresql://..."
export DB_USERNAME="postgres"
export DB_PASSWORD="..."
```

#### GitHub Secrets a configurar:
- `DOCKER_USERNAME`: Usuario de Docker Hub
- `DOCKER_PASSWORD`: Token de Docker Hub
- `SONAR_TOKEN`: Token de SonarCloud (opcional)

## Documentación Técnica

### 📖 [01-seguridad-jwt-spring-security.md](./01-seguridad-jwt-spring-security.md)
**Contenido:**
- Por qué JWT es mejor que sessions
- Estructura técnica de JWT (header, payload, signature)
- Algoritmos de firma (HMAC vs RSA)
- Trade-offs y mitigaciones (revocación, tamaño, seguridad)
- Arquitectura de Spring Security (filter chain)
- Implementación paso a paso con código comentado
- BCrypt: por qué es el estándar para passwords
- Testing de seguridad

**Para entrevistas:** Explica JWT, SecurityConfig, BCrypt con profundidad técnica.

### 📖 [02-cicd-github-actions-profesional.md](./02-cicd-github-actions-profesional.md)
**Contenido:**
- Fundamentos de CI/CD (por qué es crítico)
- GitHub Actions: arquitectura completa
- Pipeline explicado stage por stage
- Docker optimization (multi-stage, caching)
- Deployment strategies (rolling, blue-green, canary)
- Security en CI/CD (secrets, least privilege)
- Monitoring y rollback automático
- Roadmap de junior a senior

**Para entrevistas:** Explica tu pipeline completo, deployment strategies, observability.

## Testing

### Probar autenticación localmente:

```bash
# 1. Compilar proyecto
mvn clean package -DskipTests

# 2. Ejecutar aplicación
java -jar target/academymanager-0.0.1-SNAPSHOT.jar

# 3. Registrar usuario
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@academia.com",
    "password": "Test123!",
    "rol": "ALUMNO",
    "nombre": "Test",
    "apellidos": "Usuario"
  }'

# 4. Login
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@academia.com",
    "password": "Test123!"
  }' | jq -r '.token')

# 5. Acceder a endpoint protegido
curl -X GET http://localhost:8080/api/cursos \
  -H "Authorization: Bearer $TOKEN"
```

### Probar CI/CD:

```bash
# 1. Commit y push código
git add .
git commit -m "feat: Implement JWT authentication"
git push origin main

# 2. Ver workflow en GitHub Actions:
# https://github.com/tu-usuario/tu-repo/actions

# 3. Pipeline ejecutará automáticamente:
# - Build (1-2 min)
# - Tests (1-2 min)
# - Security scan (2-3 min)
# - Docker build (3-5 min si sin cache)
# - Deploy (según configuración)
```

## Qué aprendiste (Nivel Senior)

### Conceptos Técnicos:

1. **JWT Profundo:**
   - Estructura (header.payload.signature)
   - Algoritmos de firma (HMAC-SHA vs RSA)
   - Validación (verificación de firma + expiración)
   - Trade-offs (revocación, tamaño)
   - Mitigaciones (refresh tokens, blacklist)

2. **Spring Security Internals:**
   - Filter chain (cómo funciona)
   - SecurityContext (ThreadLocal)
   - UserDetailsService (integración con DB)
   - AuthenticationManager (validación de credenciales)
   - BCrypt (salting, cost factor)

3. **CI/CD Profesional:**
   - Pipeline multi-stage
   - Parallel execution
   - Artifact management
   - Security scanning (OWASP)
   - Docker optimization (multi-stage, cache)
   - Deployment strategies (rolling, blue-green, canary)
   - Observability (logs, metrics, health checks)

### Habilidades para Entrevistas:

#### Pregunta: "¿Cómo funciona JWT?"
**Respuesta senior:**
> JWT es un token self-contained con tres partes: header (algoritmo), payload (claims), y signature (verificación de integridad). Uso HMAC-SHA256 para firmar: signature = HMACSHA256(base64(header) + "." + base64(payload), secret). El servidor valida recalculando la firma y verificando que coincida. Para revocación uso refresh tokens con expiration corta (15 min) + token de larga duración en DB.

#### Pregunta: "¿Por qué BCrypt y no SHA-256?"
**Respuesta senior:**
> BCrypt es intencionalmente lento (cost factor 10 = 2^10 rounds) para prevenir brute force. Incluye salting automático (salt único en cada hash), previniendo rainbow tables. SHA-256 es rápido (millones de hashes/seg en GPU), vulnerable a ataques. BCrypt puede ejecutar ~10k hashes/seg, haciendo brute force inviable (años vs horas).

#### Pregunta: "¿Cómo optimizas tu CI/CD?"
**Respuesta senior:**
> Implemento parallel execution (tests unit + integration + E2E en paralelo), caching de Maven dependencies (ahorra 2-5 min), y Docker layer caching (solo rebuild layers que cambiaron). Uso fail-fast para cancelar jobs si build falla temprano. Para deployment, prefiero canary con automated rollback basado en error rate y latency metrics (Prometheus + Flagger).

## Próximos Pasos

### Día 4: GraphQL y APIs Modernas
- Implementar GraphQL junto a REST
- Resolver N+1 problem con DataLoader
- Subscriptions para real-time
- Comparar GraphQL vs REST

### Mejoras Opcionales (Día 3):

1. **Refresh Tokens:**
   ```java
   @Entity
   public class RefreshToken {
       @Id private UUID token;
       @ManyToOne private Usuario usuario;
       private Instant expiryDate;
   }
   ```

2. **Rate Limiting:**
   ```java
   @Configuration
   public class RateLimitConfig {
       @Bean
       public RateLimiter loginRateLimiter() {
           return RateLimiter.create(5.0); // 5 req/s
       }
   }
   ```

3. **Audit Logging:**
   ```java
   @Aspect
   @Component
   public class SecurityAuditAspect {
       @AfterReturning("@annotation(PostMapping)")
       public void auditLogin(JoinPoint joinPoint) {
           log.info("SECURITY_AUDIT: Login successful");
       }
   }
   ```

## Recursos Adicionales

### Documentación Oficial:
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JWT.io - JWT Debugger](https://jwt.io/)
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)

### Tools:
- [Postman](https://www.postman.com/) - Testing APIs
- [SonarCloud](https://sonarcloud.io/) - Code quality
- [Docker Hub](https://hub.docker.com/) - Container registry
- [Trivy](https://github.com/aquasecurity/trivy) - Security scanner

## Conclusión

Has completado la implementación de **seguridad profesional** y **CI/CD enterprise-grade**. Estos son skills de **nivel senior** que demuestran:

✅ Dominio de autenticación stateless (JWT)  
✅ Conocimiento profundo de Spring Security  
✅ Implementación de pipelines automatizados  
✅ Security-first mindset (OWASP, scanning)  
✅ DevOps practices (Docker, deployment strategies)  
✅ Observability (monitoring, health checks)  

**Estás listo para entrevistas en INNOQA** y otras consultoras de nivel enterprise.

¡Continúa con Día 4: GraphQL y APIs Modernas!

