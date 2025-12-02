# Día 3: Seguridad Avanzada con Spring Security y JWT

## Índice
1. [Introducción a la Seguridad en Aplicaciones Enterprise](#introducción)
2. [Por qué JWT es la Mejor Opción](#por-qué-jwt)
3. [Arquitectura de Spring Security](#arquitectura-spring-security)
4. [Implementación Técnica Detallada](#implementación)
5. [CI/CD: De Junior a Senior](#cicd-profesional)
6. [Best Practices y Consideraciones de Producción](#best-practices)

---

## Introducción a la Seguridad en Aplicaciones Enterprise

### El Problema: Autenticación vs Autorización

**Autenticación**: ¿Quién eres? (Login - verificar identidad)
**Autorización**: ¿Qué puedes hacer? (Permisos - verificar acceso)

En aplicaciones modernas enterprise, necesitamos:
- ✅ Stateless authentication (no almacenar sesión en servidor)
- ✅ Escalabilidad horizontal (múltiples instancias)
- ✅ Soporte multi-plataforma (web, mobile, APIs)
- ✅ Seguridad robusta contra ataques comunes (CSRF, XSS, injection)

---

## Por qué JWT es la Mejor Opción

### 1. Comparación con Session-Based Authentication

#### Session-Based (Enfoque Tradicional)
```
Cliente → Login → Servidor crea session → SessionID en cookie → 
→ Cada request: Cookie enviada → Servidor busca session en memoria/Redis/DB
```

**Problemas:**
- ❌ **No escalable**: Necesitas sticky sessions o Redis compartido entre instancias
- ❌ **Stateful**: Servidor debe mantener estado (memoria/storage)
- ❌ **No mobile-friendly**: Cookies no funcionan bien en apps nativas
- ❌ **CSRF vulnerable**: Cookies se envían automáticamente

#### JWT-Based (Enfoque Moderno)
```
Cliente → Login → Servidor genera JWT → Token devuelto al cliente →
→ Cada request: Authorization: Bearer <token> → Servidor valida firma →
→ Sin búsqueda en DB (self-contained)
```

**Ventajas:**
- ✅ **Stateless**: No almacenamiento de sesión en servidor
- ✅ **Escalable**: Cualquier instancia puede validar el token
- ✅ **Cross-platform**: Funciona en web, mobile, IoT
- ✅ **Microservices-ready**: Token compartible entre servicios
- ✅ **CSRF-resistant**: No usa cookies (Authorization header)

### 2. Estructura Técnica de JWT

Un JWT tiene tres partes separadas por puntos:
```
Header.Payload.Signature
```

#### Header (Base64URL encoded)
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```
- `alg`: Algoritmo de firma (HS256 = HMAC-SHA256)
- `typ`: Tipo de token

#### Payload (Base64URL encoded) - Claims
```json
{
  "sub": "user@example.com",
  "roles": ["ROLE_ADMIN"],
  "iat": 1701518400,
  "exp": 1701604800
}
```
- `sub` (subject): Identificador del usuario
- `roles`: Autoridades/permisos
- `iat` (issued at): Timestamp de creación
- `exp` (expiration): Timestamp de expiración

**Claims estándar (RFC 7519):**
- `iss` (issuer): Quien emitió el token
- `aud` (audience): Para quién es el token
- `nbf` (not before): Token no válido antes de este tiempo
- `jti` (JWT ID): Identificador único del token

#### Signature (Firma criptográfica)
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

**¿Por qué es seguro?**
- La firma se genera con una clave secreta (mínimo 256 bits para HS256)
- Si alguien modifica el payload, la firma no coincidirá
- Sin la clave secreta, no se puede generar una firma válida
- Validación: Recalcular firma y comparar con la recibida

### 3. Algoritmos de Firma: HMAC vs RSA

#### HMAC (Symmetric - Clave simétrica)
- **HS256, HS384, HS512**: HMAC with SHA-256/384/512
- Misma clave para firmar y verificar
- **Pros**: Rápido, simple
- **Contras**: Todos los servicios necesitan la misma clave secreta
- **Uso**: Aplicaciones monolíticas, microservicios confiables

#### RSA (Asymmetric - Par de claves)
- **RS256, RS384, RS512**: RSA with SHA-256/384/512
- Clave privada para firmar, clave pública para verificar
- **Pros**: Clave pública puede distribuirse sin riesgo
- **Contras**: Más lento (operaciones criptográficas pesadas)
- **Uso**: Microservicios públicos, federación de identidad

**Para tu proyecto**: HS256 es suficiente (academia interna, no servicios públicos)

### 4. JWT Trade-offs y Mitigaciones

#### Problema 1: Revocación de Tokens
**Issue**: JWT es stateless, no puedes "cancelar" un token válido.

**Soluciones:**
1. **Short expiration time**: Tokens de 15-60 minutos
2. **Refresh tokens**: Token de larga duración almacenado en DB
3. **Token blacklist**: Redis con tokens revocados (pierde stateless)
4. **Token versioning**: Campo `version` en user, incrementar al cambiar password

**Implementación con Refresh Token:**
```java
// Access token: 15 minutos
String accessToken = jwtService.generateToken(userDetails, 900000);

// Refresh token: 7 días, almacenado en DB
RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

return AuthResponse.builder()
    .accessToken(accessToken)
    .refreshToken(refreshToken.getToken())
    .expiresIn(900000L)
    .build();
```

#### Problema 2: Tamaño del Token
**Issue**: JWT puede ser grande (200-400 bytes vs SessionID 32 bytes)

**Mitigaciones:**
- No incluir datos innecesarios en claims
- Usar HS256 en vez de RS256 (firma más corta)
- Comprimir payload (raro, aumenta complejidad)

#### Problema 3: Información Sensible en Token
**Issue**: JWT es Base64, no encriptado (cualquiera puede decodificar)

**Reglas:**
- ❌ NUNCA incluir: passwords, tarjetas de crédito, datos médicos
- ✅ OK incluir: user ID, email, roles (ya autenticado)
- Para datos sensibles: JWE (JSON Web Encryption)

---

## Arquitectura de Spring Security

### Filter Chain: Cómo Funciona Internamente

Spring Security es una cadena de filtros que interceptan cada request:

```
HTTP Request
    ↓
SecurityContextPersistenceFilter (restaura contexto)
    ↓
LogoutFilter (maneja logout)
    ↓
UsernamePasswordAuthenticationFilter (login form)
    ↓
JwtAuthenticationFilter (CUSTOM - valida JWT)  ← TU FILTRO AQUÍ
    ↓
ExceptionTranslationFilter (maneja excepciones de seguridad)
    ↓
FilterSecurityInterceptor (verifica autorización)
    ↓
Controller
```

### Tu JwtAuthenticationFilter: Paso a Paso

```java
@Override
protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
) throws ServletException, IOException {
    
    // PASO 1: Extraer header Authorization
    String authHeader = request.getHeader("Authorization");
    
    // PASO 2: Validar formato "Bearer <token>"
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response); // Continuar sin autenticar
        return;
    }
    
    try {
        // PASO 3: Extraer token (quitar "Bearer " prefix)
        String token = authHeader.substring(7);
        
        // PASO 4: Extraer username del token (validando firma)
        String username = jwtService.extractUsername(token);
        
        // PASO 5: Si username válido y no hay autenticación previa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // PASO 6: Cargar usuario desde DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // PASO 7: Validar token (firma + expiración)
            if (jwtService.isTokenValid(token, userDetails)) {
                
                // PASO 8: Crear objeto de autenticación
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // credentials (no necesarias después de validar)
                        userDetails.getAuthorities() // roles
                    );
                
                // PASO 9: Agregar detalles de request
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // PASO 10: Establecer autenticación en SecurityContext (ThreadLocal)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    } catch (JwtException e) {
        logger.error("JWT validation failed: " + e.getMessage());
        // No establecer autenticación → request será rechazado
    }
    
    // PASO 11: Continuar cadena de filtros
    filterChain.doFilter(request, response);
}
```

**Puntos Críticos:**

1. **OncePerRequestFilter**: Garantiza ejecución única (evita procesar dos veces en forwards/includes)

2. **SecurityContextHolder**: ThreadLocal que almacena autenticación por request
   ```java
   // ThreadLocal: cada thread (request) tiene su propia copia
   SecurityContextHolder.setContext(authentication);
   ```

3. **Try-catch**: No propagar excepciones JWT → simplemente no autenticar (401 automático)

4. **Order importante**: JwtFilter ANTES de UsernamePasswordAuthenticationFilter
   ```java
   .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
   ```

### SecurityConfig: Configuración Profesional

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // 1. Desactivar CSRF (no necesario con JWT stateless)
        .csrf(AbstractHttpConfigurer::disable)
        
        // 2. Configurar CORS (permitir frontend en diferente dominio)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        
        // 3. Autorización granular por endpoint
        .authorizeHttpRequests(auth -> auth
            // Public endpoints
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/actuator/health").permitAll()
            
            // Role-based access
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/cursos/**").hasAnyRole("ADMIN", "PROFESOR")
            .requestMatchers(HttpMethod.GET, "/api/cursos/**").authenticated()
            
            // Default: todo lo demás requiere autenticación
            .anyRequest().authenticated()
        )
        
        // 4. Sesión STATELESS (no crear HttpSession)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        
        // 5. Deshabilitar login form (no necesitamos página HTML)
        .formLogin(AbstractHttpConfigurer::disable)
        
        // 6. Deshabilitar HTTP Basic (usamos JWT)
        .httpBasic(AbstractHttpConfigurer::disable)
        
        // 7. Agregar filtro JWT
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        
        // 8. Exception handling personalizado
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint()) // 401 custom
            .accessDeniedHandler(accessDeniedHandler()) // 403 custom
        );
    
    return http.build();
}
```

**Por qué desactivar CSRF con JWT:**
- CSRF (Cross-Site Request Forgery) explota cookies enviadas automáticamente
- JWT se envía en header `Authorization` (manual, no automático)
- Frontend debe explícitamente incluir header → CSRF no aplica

**Por qué SessionCreationPolicy.STATELESS:**
```java
// CON SESIÓN (stateful):
HttpSession session = request.getSession(); // Crea sesión en memoria
session.setAttribute("user", user); // Estado en servidor

// SIN SESIÓN (stateless):
// Cada request es independiente, autenticación via token
// No estado en servidor → escalabilidad horizontal
```

---

## Implementación Técnica Detallada

### 1. JwtService: Generación y Validación

#### Generación de Token
```java
public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    
    // Agregar roles al payload
    claims.put("roles", userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList()));
    
    // Agregar claims custom (ej: ID de centro para multi-tenant)
    claims.put("centroId", getCentroId(userDetails));
    
    return Jwts.builder()
        .setClaims(claims) // Custom claims
        .setSubject(userDetails.getUsername()) // Standard claim: sub
        .setIssuedAt(new Date()) // Standard claim: iat
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // exp
        .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Firma HMAC-SHA256
        .compact(); // Serializar a string
}
```

**Internamente, JJWT hace:**
```java
// 1. Crear header JSON
String header = base64UrlEncode('{"alg":"HS256","typ":"JWT"}');

// 2. Crear payload JSON
String payload = base64UrlEncode(claims.toJson());

// 3. Crear firma
String signature = HMACSHA256(header + "." + payload, secretKey);

// 4. Combinar
return header + "." + payload + "." + signature;
```

#### Validación de Token
```java
public boolean isTokenValid(String token, UserDetails userDetails) {
    try {
        // 1. Extraer username (valida firma automáticamente)
        String username = extractUsername(token);
        
        // 2. Verificar username coincide
        boolean usernameMatches = username.equals(userDetails.getUsername());
        
        // 3. Verificar no expirado
        boolean notExpired = !isTokenExpired(token);
        
        // 4. (Opcional) Verificar versión de token
        // String tokenVersion = extractClaim(token, claims -> claims.get("version"));
        // boolean versionValid = tokenVersion.equals(user.getTokenVersion());
        
        return usernameMatches && notExpired;
        
    } catch (ExpiredJwtException e) {
        // Token expirado → loggear y retornar false
        log.warn("Token expired for user: {}", userDetails.getUsername());
        return false;
    } catch (SignatureException e) {
        // Firma inválida → posible tampering
        log.error("Invalid JWT signature for user: {}", userDetails.getUsername());
        return false;
    } catch (MalformedJwtException e) {
        // Token malformado → no es JWT válido
        log.error("Malformed JWT token");
        return false;
    }
}
```

**Parsing interno:**
```java
private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey()) // Clave para verificar firma
        .build()
        .parseClaimsJws(token) // Parse y verifica firma
        .getBody(); // Retorna payload (claims)
}
```

**¿Cómo verifica la firma?**
```java
// JJWT internamente:
String[] parts = token.split("\\.");
String headerBase64 = parts[0];
String payloadBase64 = parts[1];
String signatureBase64 = parts[2];

// Recalcular firma
String calculatedSignature = HMACSHA256(
    headerBase64 + "." + payloadBase64,
    secretKey
);

// Comparar
if (!calculatedSignature.equals(signatureBase64)) {
    throw new SignatureException("Invalid signature");
}
```

### 2. BCrypt: Por qué es el Estándar

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10); // 10 rounds (default)
}
```

#### ¿Cómo funciona BCrypt?

1. **Salting automático**: Cada hash incluye sal única
   ```
   Entrada: "MyPassword123"
   Hash 1: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhW
   Hash 2: $2a$10$X5NdFKt7lIxp7K8RLhf7jOXdUOz1CsYXK8Lmz6v3w9N5Q2w3Rt4Ka
   (diferente sal → diferente hash, ambos válidos)
   ```

2. **Estructura del hash:**
   ```
   $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhW
   │ │ │  │                      │
   │ │ │  └─ Salt (22 chars)    └─ Hash (31 chars)
   │ │ └─ Cost factor (2^10 = 1024 rounds)
   │ └─ Minor version
   └─ Algoritmo (2a = bcrypt)
   ```

3. **Cost factor**: Número de rounds = 2^cost
   - 10 → 1024 rounds (~0.1 segundos en hardware moderno)
   - 12 → 4096 rounds (~0.4 segundos)
   - 14 → 16384 rounds (~1.6 segundos)

**Por qué BCrypt sobre SHA-256:**
```java
// SHA-256 (MAL - muy rápido, vulnerable a rainbow tables)
String hash = SHA256("MyPassword123");
// Hash siempre igual → rainbow table lookup → crackeado instantáneo

// BCrypt (BIEN - lento por diseño, sal integrada)
String hash = BCrypt.hashpw("MyPassword123", BCrypt.gensalt(10));
// Lento → brute force inviable (1024 rounds por intento)
// Sal única → rainbow tables inútiles
```

**Comparación de velocidad (feature, no bug):**
- SHA-256: ~1,000,000,000 hashes/segundo (GPU)
- BCrypt (10 rounds): ~10,000 hashes/segundo

**Para crackear password 8 caracteres (alfanumérico):**
- SHA-256: ~1 hora
- BCrypt (10 rounds): ~2,000 años

### 3. UserDetailsService: Integración con DB

```java
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // 1. Query usuario desde DB
    Usuario usuario = usuarioRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    
    // 2. Verificar cuenta activa
    if (!usuario.getActivo()) {
        throw new DisabledException("User account is disabled");
    }
    
    // 3. Convertir a Spring Security UserDetails
    return User.builder()
        .username(usuario.getEmail())
        .password(usuario.getPasswordHash()) // BCrypt hash desde DB
        .authorities(getAuthorities(usuario)) // Roles
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(!usuario.getActivo())
        .build();
}

private List<GrantedAuthority> getAuthorities(Usuario usuario) {
    // Convertir rol de Usuario a GrantedAuthority
    // IMPORTANTE: Prefijo "ROLE_" requerido por Spring Security
    return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
}
```

**¿Por qué "ROLE_" prefix?**
```java
// En SecurityConfig:
.hasRole("ADMIN") // Spring busca "ROLE_ADMIN" internamente

// Equivalente a:
.hasAuthority("ROLE_ADMIN")

// Sin prefijo:
.hasAuthority("ADMIN") // Busca exactamente "ADMIN"
```

### 4. Login Flow Completo

```java
@PostMapping("/login")
public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    
    // PASO 1: AuthenticationManager valida credenciales
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword() // Plain text
        )
    );
    // Internamente:
    // 1. Llama a UserDetailsService.loadUserByUsername(email)
    // 2. Obtiene password hash desde DB
    // 3. Compara con BCrypt: passwordEncoder.matches(plainPassword, hashFromDB)
    // 4. Si coincide → retorna Authentication
    // 5. Si no → lanza BadCredentialsException
    
    // PASO 2: Cargar UserDetails (ya validado)
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    
    // PASO 3: Generar JWT
    String token = jwtService.generateToken(userDetails);
    
    // PASO 4: Retornar respuesta
    return ResponseEntity.ok(LoginResponse.builder()
        .token(token)
        .tokenType("Bearer")
        .expiresIn(jwtExpiration)
        .build());
}
```

**Flujo visual:**
```
Cliente: POST /api/auth/login
  ↓
  { "email": "admin@example.com", "password": "Admin123!" }
  ↓
AuthController
  ↓
AuthenticationManager.authenticate()
  ↓
DaoAuthenticationProvider
  ↓
UserDetailsServiceImpl.loadUserByUsername("admin@example.com")
  ↓
UsuarioRepository.findByEmail("admin@example.com")
  ↓
DB: SELECT * FROM usuario WHERE email = 'admin@example.com'
  ↓
Usuario { passwordHash: "$2a$10$...", rol: ADMIN }
  ↓
BCryptPasswordEncoder.matches("Admin123!", "$2a$10$...")
  ↓
Match ✓ → Authentication object
  ↓
JwtService.generateToken(userDetails)
  ↓
Token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  ↓
LoginResponse { token: "...", expiresIn: 86400000 }
  ↓
Cliente: Almacenar token (localStorage/secure storage)
```

---

## CI/CD: De Junior a Senior

### ¿Qué es CI/CD y Por Qué es Crítico?

**CI (Continuous Integration)**: Integrar código frecuentemente (múltiples veces al día)
- Cada push → build automático + tests
- Detectar errores inmediatamente
- Feedback rápido a desarrolladores

**CD (Continuous Delivery/Deployment)**: Desplegar automáticamente
- Continuous Delivery: Deploy manual a producción (aprobación humana)
- Continuous Deployment: Deploy automático a producción (totalmente automatizado)

### Pipeline Profesional: Etapas Explicadas

#### Stage 1: Build and Test
```yaml
build-and-test:
  runs-on: ubuntu-latest
  steps:
    - name: Checkout code
      uses: actions/checkout@v4
      with:
        fetch-depth: 0  # Historial completo (para análisis de cambios)
    
    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'  # Eclipse Temurin (OpenJDK)
        cache: 'maven'  # Cache de dependencias Maven
    
    - name: Build with Maven
      run: mvn clean compile -B -V
      # -B: Batch mode (no output interactivo)
      # -V: Show version
    
    - name: Run tests
      run: mvn test -B
    
    - name: Check code coverage
      run: mvn jacoco:check
      # Falla build si coverage < 95% (configurado en pom.xml)
```

**Por qué cache de Maven:**
```yaml
# Sin cache: Descarga ~100MB dependencias cada build (2-5 min)
# Con cache: Reutiliza dependencias (skip descarga, ~30 seg)

# GitHub Actions cachea ~/.m2/repository basado en hash de pom.xml
# Si pom.xml no cambia → reutilizar cache
# Si pom.xml cambia → rebuild cache
```

#### Stage 2: Code Quality
```yaml
code-quality:
  needs: build-and-test  # Ejecuta DESPUÉS de build exitoso
  steps:
    - name: SonarCloud analysis
      env:
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      run: |
        mvn sonar:sonar \
          -Dsonar.projectKey=academia-multi-centro \
          -Dsonar.organization=tu-org \
          -Dsonar.host.url=https://sonarcloud.io
```

**Qué analiza SonarQube/SonarCloud:**
- Code smells: Código mal escrito (no bugs, pero difícil de mantener)
- Bugs: Errores detectables estáticamente
- Security vulnerabilities: Patrones inseguros (SQL injection, XSS)
- Code coverage: % de código testeado
- Duplicación: Código repetido (DRY violations)
- Complejidad ciclomática: Complejidad de funciones (McCabe)

**Umbrales de calidad (Quality Gate):**
```properties
# Fallar build si:
sonar.qualitygate.wait=true
sonar.coverage.minimum=80%
sonar.duplicated_lines_density.maximum=3%
sonar.bugs.new=0
sonar.vulnerabilities.new=0
```

#### Stage 3: Security Scan
```yaml
security-scan:
  steps:
    - name: OWASP Dependency Check
      run: |
        mvn org.owasp:dependency-check-maven:check \
          -DfailBuildOnCVSS=7
      # CVSS: Common Vulnerability Scoring System (0-10)
      # 7+ = High/Critical severity → fallar build
```

**Qué hace OWASP Dependency Check:**
1. Escanea todas las dependencias en `pom.xml`
2. Consulta National Vulnerability Database (NVD)
3. Identifica CVEs (Common Vulnerabilities and Exposures)
4. Genera reporte HTML con vulnerabilidades

**Ejemplo output:**
```
Dependency: spring-core-5.3.10.jar
CVE-2022-22965: Spring4Shell RCE
CVSS Score: 9.8 (CRITICAL)
Fix: Upgrade to spring-core-5.3.18+
```

**Suppressions file** (falsos positivos):
```xml
<!-- .owasp-suppressions.xml -->
<suppressions>
  <suppress>
    <cve>CVE-2021-12345</cve>
    <reason>False positive - feature not used</reason>
  </suppress>
</suppressions>
```

#### Stage 4: Docker Build
```yaml
docker-build:
  needs: [build-and-test, code-quality]  # AND lógico
  if: github.ref == 'refs/heads/main'  # Solo en branch main
  steps:
    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v3
      # Buildx: Builder avanzado con cache, multi-platform, etc.
    
    - name: Build and push
      uses: docker/build-push-action@v5
      with:
        context: .
        push: true
        tags: |
          myrepo/academia:latest
          myrepo/academia:${{ github.sha }}
        cache-from: type=gha  # GitHub Actions cache
        cache-to: type=gha,mode=max
```

**Multi-stage build optimization:**
```dockerfile
# Stage 1: Build (maven + JDK)
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline  # Cache layer
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime (solo JRE)
FROM eclipse-temurin:21-jre-alpine
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# Resultado:
# Builder stage: ~600MB (descartado)
# Runtime image: ~200MB (solo JRE + JAR)
```

**Layer caching:**
```dockerfile
# MALO (rebuild todo si cambia código):
COPY . .
RUN mvn package

# BIEN (cache de dependencias):
COPY pom.xml .
RUN mvn dependency:go-offline  ← Cache layer (reutilizado si pom.xml no cambia)
COPY src ./src
RUN mvn package  ← Solo rebuild si código cambia
```

#### Stage 5: Deploy to Production
```yaml
deploy-production:
  needs: docker-build
  environment:
    name: production  # GitHub environment protection rules
    url: https://academia.example.com
  steps:
    - name: Deploy to Kubernetes
      run: |
        kubectl set image deployment/academia-app \
          academia-app=myrepo/academia:${{ github.sha }} \
          --namespace=production
        
        kubectl rollout status deployment/academia-app -n production
        # Espera hasta que deployment esté ready
```

**Deployment strategies:**

1. **Rolling update** (default Kubernetes):
   ```
   Pods: [v1] [v1] [v1]
          ↓
   Pods: [v2] [v1] [v1] (nuevo pod v2 ready)
          ↓
   Pods: [v2] [v2] [v1] (segundo pod v2 ready)
          ↓
   Pods: [v2] [v2] [v2] (todos v2, v1 terminados)
   ```
   - ✅ Zero downtime
   - ✅ Gradual rollout
   - ❌ Dos versiones simultáneas (compatibilidad necesaria)

2. **Blue-Green deployment:**
   ```
   Production → Blue (v1)
   
   Deploy Green (v2) → Test
   
   Switch traffic → Green (v2)
   
   Keep Blue (v1) para rollback rápido
   ```
   - ✅ Rollback instantáneo
   - ✅ Testing en ambiente idéntico a prod
   - ❌ Doble infraestructura (costoso)

3. **Canary deployment:**
   ```
   90% traffic → v1
   10% traffic → v2 (canary)
   
   Monitor metrics (errores, latencia)
   
   Si OK: 50% → v1, 50% → v2
   Si OK: 100% → v2
   
   Si errores: Rollback automático
   ```
   - ✅ Bajo riesgo (solo 10% usuarios afectados)
   - ✅ Detección temprana de issues
   - ❌ Complejo de implementar

### Secrets Management: Seguridad en CI/CD

```yaml
# NUNCA en código:
jwt.secret.key=mysecretkey123

# GitHub Secrets:
steps:
  - name: Deploy
    env:
      DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
      JWT_SECRET: ${{ secrets.JWT_SECRET }}
    run: |
      kubectl create secret generic app-secrets \
        --from-literal=db-password=$DB_PASSWORD \
        --from-literal=jwt-secret=$JWT_SECRET
```

**Jerarquía de secrets:**
1. **Development**: `.env` local (gitignored)
2. **CI/CD**: GitHub Secrets / GitLab CI Variables
3. **Kubernetes**: Secrets / ConfigMaps
4. **Production**: HashiCorp Vault / AWS Secrets Manager

**Rotación de secrets:**
```bash
# Manual rotation script
NEW_JWT_SECRET=$(openssl rand -base64 32)

# Update in Vault
vault kv put secret/app jwt-secret=$NEW_JWT_SECRET

# Update Kubernetes secret
kubectl create secret generic jwt-secret \
  --from-literal=key=$NEW_JWT_SECRET \
  --dry-run=client -o yaml | kubectl apply -f -

# Restart pods to pick up new secret
kubectl rollout restart deployment/academia-app
```

### Monitoring y Observability: Post-Deployment

#### 1. Health Checks
```java
// Spring Boot Actuator
// GET /actuator/health
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": { "database": "PostgreSQL", "validationQuery": "isValid()" }
    },
    "diskSpace": {
      "status": "UP",
      "details": { "total": 500GB, "free": 250GB }
    }
  }
}
```

**Kubernetes liveness/readiness probes:**
```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
  failureThreshold: 3  # 3 fallos consecutivos → restart pod

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 20
  periodSeconds: 5
  # Fallo → remover pod de load balancer (no restart)
```

#### 2. Metrics (Prometheus + Grafana)
```java
// Spring Boot Actuator + Micrometer
@Timed(value = "login.requests", description = "Login endpoint latency")
@PostMapping("/login")
public ResponseEntity<LoginResponse> login(...) {
    // ...
}

// Métricas expuestas:
// GET /actuator/prometheus
# HELP login_requests_seconds Login endpoint latency
# TYPE login_requests_seconds summary
login_requests_seconds_count 1250
login_requests_seconds_sum 156.3
login_requests_seconds_max 2.4
```

**Dashboard Grafana:**
- Request rate (req/s)
- Latency (p50, p95, p99)
- Error rate (4xx, 5xx)
- JVM metrics (heap, GC pauses)
- DB connection pool

#### 3. Logging (ELK Stack)
```java
// Structured logging con Logback + Logstash encoder
@Slf4j
@RestController
public class AuthController {
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        log.info("Login attempt", kv("email", request.getEmail()));
        
        try {
            LoginResponse response = authService.login(request);
            log.info("Login successful", 
                kv("email", request.getEmail()),
                kv("rol", response.getRol()));
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            log.warn("Login failed: invalid credentials",
                kv("email", request.getEmail()));
            throw e;
        }
    }
}

// JSON output:
{
  "timestamp": "2024-12-02T10:30:00.123Z",
  "level": "INFO",
  "message": "Login successful",
  "email": "admin@example.com",
  "rol": "ADMIN",
  "traceId": "abc123",  // Para correlación de requests
  "spanId": "def456"
}
```

**Centralized logging flow:**
```
Application → Logback → Logstash → Elasticsearch → Kibana
                        (parse JSON)  (indexar)     (visualizar)
```

#### 4. Distributed Tracing (Jaeger/Zipkin)
```java
// Spring Cloud Sleuth + Zipkin
// Trace ID propagado automáticamente entre microservicios

// Service A (API Gateway):
log.info("Request received"); // traceId=abc123, spanId=span1

// Service B (Auth Service):
log.info("Validating credentials"); // traceId=abc123, spanId=span2

// Service C (User Service):
log.info("Loading user from DB"); // traceId=abc123, spanId=span3

// En Jaeger UI:
// Ver toda la traza abc123 con timeline de cada span
```

### De Junior a Senior: Checklist

#### Junior Developer:
- ✅ Escribe código que funciona
- ❌ No considera edge cases
- ❌ No escribe tests
- ❌ No piensa en seguridad
- ❌ Commits directos a main
- ❌ No documenta

#### Mid-Level Developer:
- ✅ Código limpio y testeado
- ✅ Maneja errores correctamente
- ✅ Usa feature branches + PRs
- ⚠️ Seguridad básica (no secrets en código)
- ⚠️ Deployment manual

#### Senior Developer:
- ✅ Arquitectura escalable (stateless, horizontal scaling)
- ✅ Security-first (JWT, BCrypt, OWASP Top 10)
- ✅ CI/CD completo (build, test, scan, deploy automático)
- ✅ Observability (logs, metrics, tracing)
- ✅ IaC (Infrastructure as Code - Terraform, Helm)
- ✅ Disaster recovery (backups, rollback strategies)
- ✅ Performance optimization (caching, connection pooling)
- ✅ Documentación técnica completa
- ✅ Mentorship y code reviews

---

## Best Practices y Consideraciones de Producción

### 1. Configuración de Secretos

```properties
# application.properties (defaults para desarrollo)
jwt.secret.key=default-dev-key-NOT-FOR-PRODUCTION
jwt.expiration.time=86400000

# application-prod.properties (NO commitear)
jwt.secret.key=${JWT_SECRET_KEY}
jwt.expiration.time=${JWT_EXPIRATION_TIME}

# Kubernetes Secret
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
stringData:
  JWT_SECRET_KEY: <generado con: openssl rand -base64 32>
  DB_PASSWORD: <desde password manager>
```

**Generación de secret seguro:**
```bash
# 256 bits (32 bytes) para HS256
openssl rand -base64 32
# Output: 4NV7q...8xY= (44 caracteres Base64)
```

### 2. Rate Limiting: Prevenir Brute Force

```java
@Configuration
public class RateLimitConfig {
    
    @Bean
    public RateLimiter loginRateLimiter() {
        return RateLimiter.create(5.0); // 5 requests/segundo
    }
}

@RestController
public class AuthController {
    
    @Autowired
    private RateLimiter loginRateLimiter;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(LoginRequest request) {
        if (!loginRateLimiter.tryAcquire()) {
            return ResponseEntity.status(429).body("Too many requests");
        }
        // ...
    }
}
```

**Rate limiting avanzado (Redis):**
```java
// Bucket per IP address
String key = "rate_limit:" + request.getRemoteAddr();
Long requests = redisTemplate.opsForValue().increment(key);

if (requests == 1) {
    redisTemplate.expire(key, 1, TimeUnit.MINUTES);
}

if (requests > 10) { // 10 requests/minuto
    throw new TooManyRequestsException();
}
```

### 3. Logging de Eventos de Seguridad

```java
@Aspect
@Component
@Slf4j
public class SecurityAuditAspect {
    
    @AfterReturning(pointcut = "@annotation(PostMapping)", returning = "result")
    public void auditSuccessfulLogin(JoinPoint joinPoint, Object result) {
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> response = (ResponseEntity<?>) result;
            if (response.getBody() instanceof LoginResponse) {
                LoginResponse loginResponse = (LoginResponse) response.getBody();
                log.info("SECURITY_AUDIT: Successful login",
                    kv("email", loginResponse.getEmail()),
                    kv("timestamp", Instant.now()),
                    kv("ip", getCurrentRequest().getRemoteAddr()));
            }
        }
    }
    
    @AfterThrowing(pointcut = "@annotation(PostMapping)", throwing = "exception")
    public void auditFailedLogin(JoinPoint joinPoint, Exception exception) {
        if (exception instanceof BadCredentialsException) {
            log.warn("SECURITY_AUDIT: Failed login attempt",
                kv("exception", exception.getMessage()),
                kv("timestamp", Instant.now()),
                kv("ip", getCurrentRequest().getRemoteAddr()));
        }
    }
}
```

### 4. CORS Configuration Correcta

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ❌ MAL (development only):
        // configuration.setAllowedOrigins(List.of("*"));
        
        // ✅ BIEN (production):
        configuration.setAllowedOrigins(List.of(
            "https://academia.example.com",
            "https://admin.academia.example.com"
        ));
        
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache preflight 1 hora
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

### 5. Exception Handling Completo

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex) {
        log.error("JWT error", ex);
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(401)
                .error("Unauthorized")
                .message("Invalid or expired token")
                .path(getCurrentRequest().getRequestURI())
                .build());
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied", ex);
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(403)
                .error("Forbidden")
                .message("Insufficient permissions")
                .path(getCurrentRequest().getRequestURI())
                .build());
    }
    
    // No exponer detalles internos en producción
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        
        String message = isProduction() 
            ? "An unexpected error occurred" 
            : ex.getMessage();
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(500)
                .error("Internal Server Error")
                .message(message)
                .build());
    }
}
```

### 6. Testing de Seguridad

```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private JwtService jwtService;
    
    @Test
    void accessProtectedEndpointWithoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/cursos"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    void accessProtectedEndpointWithValidToken_shouldReturn200() throws Exception {
        UserDetails user = User.builder()
            .username("admin@test.com")
            .password("password")
            .authorities("ROLE_ADMIN")
            .build();
        
        String token = jwtService.generateToken(user);
        
        mockMvc.perform(get("/api/cursos")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
    
    @Test
    void accessAdminEndpointWithUserRole_shouldReturn403() throws Exception {
        UserDetails user = User.builder()
            .username("user@test.com")
            .password("password")
            .authorities("ROLE_ALUMNO")
            .build();
        
        String token = jwtService.generateToken(user);
        
        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }
    
    @Test
    void accessEndpointWithExpiredToken_shouldReturn401() throws Exception {
        // Token con expiración en el pasado
        String expiredToken = Jwts.builder()
            .setSubject("user@test.com")
            .setExpiration(new Date(System.currentTimeMillis() - 10000))
            .signWith(getSignInKey())
            .compact();
        
        mockMvc.perform(get("/api/cursos")
                .header("Authorization", "Bearer " + expiredToken))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    void accessEndpointWithTamperedToken_shouldReturn401() throws Exception {
        String validToken = jwtService.generateToken(createUserDetails());
        
        // Modificar payload (tampering)
        String[] parts = validToken.split("\\.");
        String tamperedToken = parts[0] + ".eyJzdWIiOiJhdHRhY2tlciJ9." + parts[2];
        
        mockMvc.perform(get("/api/cursos")
                .header("Authorization", "Bearer " + tamperedToken))
            .andExpect(status().isUnauthorized());
    }
}
```

---

## Conclusión: Resumen Ejecutivo

### ¿Por qué JWT?
1. **Stateless**: Escalabilidad horizontal sin sticky sessions
2. **Cross-platform**: Web, mobile, APIs unificadas
3. **Microservices**: Token compartible entre servicios
4. **Performance**: No lookup en DB por request (después de login)

### ¿Por qué Spring Security?
1. **Industry standard**: Usado por 70%+ proyectos Spring
2. **Battle-tested**: Años de parches de seguridad
3. **Flexible**: Configuración granular de autenticación/autorización
4. **Integrado**: Funciona nativamente con Spring ecosystem

### CI/CD Profesional:
1. **Automatización**: Build → Test → Deploy sin intervención manual
2. **Quality gates**: Cobertura, seguridad, code smells verificados automáticamente
3. **Feedback rápido**: Errores detectados en minutos (no días)
4. **Deployment seguro**: Rollback automático si health checks fallan

### Checklist para Producción:
- ✅ JWT con secret fuerte (256+ bits)
- ✅ BCrypt para passwords (10+ rounds)
- ✅ HTTPS obligatorio (no HTTP en producción)
- ✅ Rate limiting en endpoints públicos
- ✅ CORS configurado restrictivamente
- ✅ Secrets en variables de entorno (nunca en código)
- ✅ Logging de eventos de seguridad
- ✅ Monitoring y alertas (uptime, errores, latencia)
- ✅ Backups automáticos de DB
- ✅ Disaster recovery plan

**Próximos pasos:** Día 4 - GraphQL y APIs modernas.

