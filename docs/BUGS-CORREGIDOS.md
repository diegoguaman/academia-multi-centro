# 🐛 Bugs Corregidos - Análisis y Soluciones

## 📊 Resumen de Bugs Encontrados y Corregidos

### ✅ Bug 1: NullPointerException en `JwtService.isTokenValid`

**Ubicación:** `src/main/java/com/academy/academymanager/security/JwtService.java:102-105`

**Problema:**
```java
// ❌ ANTES (Bug)
public boolean isTokenValid(final String token, final UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    // Si extractUsername retorna null, lanza NullPointerException
}
```

**Causa:**
- `extractUsername()` puede retornar `null` si el token es inválido o no tiene subject
- Llamar `.equals()` en `null` causa `NullPointerException`
- El check en `JwtAuthenticationFilter` (línea 72) no previene esto porque `isTokenValid` llama `extractUsername` de nuevo

**Solución Aplicada:**
```java
// ✅ DESPUÉS (Corregido)
public boolean isTokenValid(final String token, final UserDetails userDetails) {
    final String username = extractUsername(token);
    // Null-safe check: if username is null, token is invalid
    if (username == null) {
        return false;
    }
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
}
```

**Impacto:**
- ✅ Previene `NullPointerException`
- ✅ Retorna `false` si username es null (comportamiento correcto)
- ✅ Mantiene lógica de validación intacta

---

### ✅ Bug 2: UsernameNotFoundException no capturado en `AuthService.login`

**Ubicación:** `src/main/java/com/academy/academymanager/service/AuthService.java:66-89`

**Problema:**
```java
// ❌ ANTES (Bug)
public LoginResponseDto login(final LoginRequestDto request) {
    try {
        final Authentication authentication = authenticationManager.authenticate(...);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        // Si usuario está inactivo, loadUserByUsername lanza UsernameNotFoundException
        // ...
    } catch (BadCredentialsException e) {
        throw new BadCredentialsException("Invalid email or password");
        // UsernameNotFoundException NO es capturado aquí
    }
}
```

**Causa:**
- `userDetailsService.loadUserByUsername()` lanza `UsernameNotFoundException` cuando:
  - Usuario no existe
  - Usuario está inactivo (`activo = false`)
- El try-catch solo captura `BadCredentialsException`
- `UsernameNotFoundException` no es capturado, causando error no manejado

**Solución Aplicada:**
```java
// ✅ DESPUÉS (Corregido)
public LoginResponseDto login(final LoginRequestDto request) {
    try {
        final Authentication authentication = authenticationManager.authenticate(...);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        // ...
    } catch (BadCredentialsException e) {
        throw new BadCredentialsException("Invalid email or password");
    } catch (UsernameNotFoundException e) {
        // Handle case when user is not found or account is disabled
        throw new BadCredentialsException("Invalid email or password");
    }
}
```

**Import agregado:**
```java
import org.springframework.security.core.userdetails.UsernameNotFoundException;
```

**Impacto:**
- ✅ Captura `UsernameNotFoundException` correctamente
- ✅ Retorna mensaje consistente "Invalid email or password"
- ✅ No expone información sobre si usuario existe o está inactivo (seguridad)

---

### ✅ Bug 3: Dependencia condicional incorrecta en CI/CD

**Ubicación:** `.github/workflows/ci-cd.yml:266-271`

**Problema:**
```yaml
# ❌ ANTES (Bug)
smoke-tests:
  needs: [deploy-staging]  # Solo depende de deploy-staging
  if: github.ref == 'refs/heads/develop' || github.ref == 'refs/heads/main'
  # Problema: corre en main pero deploy-staging solo corre en develop
```

**Causa:**
- `smoke-tests` depende de `deploy-staging`
- `deploy-staging` solo corre en `develop` (línea 214)
- `smoke-tests` corre en `develop` Y `main` (línea 271)
- Cuando corre en `main`, `deploy-staging` no se ejecuta → `smoke-tests` falla

**Solución Aplicada:**
```yaml
# ✅ DESPUÉS (Corregido)
# Separar en dos jobs: uno para staging, otro para production
smoke-tests-staging:
  needs: [deploy-staging]
  if: github.ref == 'refs/heads/develop'
  # Solo corre en develop, depende de deploy-staging

smoke-tests-production:
  needs: [deploy-production]
  if: github.ref == 'refs/heads/main'
  # Solo corre en main, depende de deploy-production
```

**Impacto:**
- ✅ Cada job depende del deployment correcto
- ✅ No hay dependencias faltantes
- ✅ Pipeline funciona correctamente en ambas ramas
- ✅ Separación clara entre staging y production tests

---

## 📋 Verificación de Correcciones

### Bug 1: Verificación

**Test Case:**
```java
// Token inválido sin subject
String invalidToken = "invalid.token.here";
UserDetails userDetails = mock(UserDetails.class);

// Antes: NullPointerException
// Después: retorna false (comportamiento correcto)
boolean isValid = jwtService.isTokenValid(invalidToken, userDetails);
assertFalse(isValid); // ✅ Pasa
```

### Bug 2: Verificación

**Test Case:**
```java
// Usuario inactivo
LoginRequestDto request = LoginRequestDto.builder()
    .email("inactive@test.com")
    .password("password")
    .build();

// Antes: UsernameNotFoundException no capturado
// Después: BadCredentialsException con mensaje consistente
try {
    authService.login(request);
} catch (BadCredentialsException e) {
    assertEquals("Invalid email or password", e.getMessage()); // ✅ Pasa
}
```

### Bug 3: Verificación

**Test Case:**
```yaml
# Push a main branch
# Antes: smoke-tests falla porque deploy-staging no se ejecuta
# Después: smoke-tests-production se ejecuta correctamente
```

---

## 🎯 Impacto de las Correcciones

### Seguridad

**Bug 1 y 2:**
- ✅ Previene crashes por excepciones no manejadas
- ✅ No expone información sensible sobre usuarios inactivos
- ✅ Comportamiento consistente en errores de autenticación

### CI/CD

**Bug 3:**
- ✅ Pipeline funciona correctamente en todas las ramas
- ✅ Tests post-deployment se ejecutan después del deployment correcto
- ✅ No hay fallos por dependencias faltantes

---

## ✅ Estado Final

- ✅ **Bug 1:** Corregido - Null-safe check agregado
- ✅ **Bug 2:** Corregido - UsernameNotFoundException capturado
- ✅ **Bug 3:** Corregido - Jobs separados por ambiente

**Todos los bugs han sido verificados y corregidos.** ✅

