# 🔐 Guía Completa: Autenticación en GraphQL

## 🎯 Preguntas Frecuentes

### ¿Cómo hacer que GraphQL sepa que tengo rol ADMIN?

Necesitas enviar un **token JWT** en el header `Authorization` de cada request a GraphQL.

### ¿Se puede hacer en producción o solo con frontend?

**Sí, se puede hacer en desarrollo y producción.** No necesitas frontend para probar. Puedes usar:
- **GraphiQL** (interfaz gráfica) con headers
- **Postman** o cualquier cliente HTTP
- **cURL** desde terminal

### ¿Se usa GraphiQL en producción?

**NO, generalmente NO se usa GraphiQL en producción** porque:
- Expone tu API públicamente
- Permite ejecutar cualquier query
- Puede ser usado para hacer ataques

En producción normalmente:
- Se usa **solo el endpoint `/graphql`**
- El frontend hace queries desde código
- Se usa Postman/Insomnia para pruebas manuales

---

## 🚀 Paso a Paso: Autenticación en GraphQL

### Paso 1: Obtener Token JWT

**Endpoint:** `POST http://localhost:8080/api/auth/login`

**Body (JSON):**
```json
{
  "email": "diengo@diego.com",
  "password": "tu_password_aqui"
}
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"diengo@diego.com","password":"tu_password"}'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJkaWVuZ29AZGllZ28uY29tIiwiZXhwIjoxNzM1MjY5NjAwfQ...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "email": "diengo@diego.com",
  "rol": "ADMIN",
  "nombre": "Diego"
}
```

**⚠️ IMPORTANTE:** Copia el valor de `token` (sin comillas).

---

### Paso 2: Usar Token en GraphiQL

#### Opción A: Agregar Header en GraphiQL

1. **Abre GraphiQL:** `http://localhost:8080/graphiql?path=/graphql`

2. **Busca la sección de HTTP Headers** (normalmente abajo o en el panel lateral)

3. **Agrega el header:**
   ```json
   {
     "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   }
   ```
   
   **Reemplaza** `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...` con tu token real.

4. **Ejecuta tu query:**
   ```graphql
   query {
     cursos(activo: true) {
       nombre
     }
   }
   ```

#### Opción B: Si GraphiQL no Tiene Campo de Headers

Algunas versiones de GraphiQL no tienen interfaz para headers. En ese caso:

1. **Abre DevTools del navegador** (F12)
2. **Ve a la pestaña Network**
3. **Ejecuta tu query**
4. **Busca la request a `/graphql`**
5. **Haz clic derecho → Copy → Copy as cURL**
6. **Agrega el header manualmente:**
   ```bash
   curl -X POST http://localhost:8080/graphql \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer tu_token_aqui" \
     -d '{"query":"{ cursos(activo: true) { nombre } }"}'
   ```

---

### Paso 3: Usar Token en Postman

1. **Crea una nueva request:**
   - Method: `POST`
   - URL: `http://localhost:8080/graphql`

2. **Headers:**
   - `Content-Type`: `application/json`
   - `Authorization`: `Bearer tu_token_aqui`

3. **Body (raw JSON):**
   ```json
   {
     "query": "query { cursos(activo: true) { nombre } }"
   }
   ```

4. **Send**

---

## 🔍 Verificar que Funciona

### Query de Prueba (sin roles)

```graphql
query {
  usuario(id: 1) {
    idUsuario
    email
    rol
  }
}
```

**Sin token:** ❌ Error "Unauthorized"  
**Con token:** ✅ Funciona

### Query con Rol Específico

```graphql
query {
  usuarios {
    idUsuario
    email
    rol
  }
}
```

**Solo funciona con token de usuario ADMIN.**

---

## 🏭 Desarrollo vs Producción

### Desarrollo (Local)

**Configuración actual:**
```java
.requestMatchers("/graphql/**", "/graphiql/**").permitAll()
```

**Esto significa:**
- ✅ Puedes acceder a GraphiQL sin token
- ✅ Puedes hacer queries sin token (pero `@PreAuthorize` bloqueará)
- ✅ Para queries con roles → necesitas token

**Recomendación:** Dejar así para desarrollo.

---

### Producción

**Debes cambiar a:**
```java
.requestMatchers("/graphql/**").authenticated()  // Requiere token
.requestMatchers("/graphiql/**").denyAll()       // Deshabilitar GraphiQL
```

**O mejor aún, usar variables de entorno:**
```java
@Value("${graphql.public.enabled:false}")
private boolean graphqlPublicEnabled;

.requestMatchers("/graphql/**")
    .access(graphqlPublicEnabled ? "permitAll()" : "authenticated()")
.requestMatchers("/graphiql/**")
    .access(graphqlPublicEnabled ? "permitAll()" : "denyAll()")
```

**En `application.properties` de producción:**
```properties
graphql.public.enabled=false
```

---

## 🛠️ Cómo Funciona la Autenticación

### Flujo Completo

```
1. Cliente → POST /api/auth/login
   ↓
2. AuthController valida credenciales
   ↓
3. AuthService genera JWT token
   ↓
4. Cliente recibe token
   ↓
5. Cliente → POST /graphql con header "Authorization: Bearer <token>"
   ↓
6. JwtAuthenticationFilter extrae token del header
   ↓
7. JwtService valida token (firma + expiración)
   ↓
8. UserDetailsService carga usuario desde DB
   ↓
9. SecurityContext establece autenticación con roles
   ↓
10. @PreAuthorize verifica roles
   ↓
11. Resolver ejecuta query
```

### Código Relevante

**JwtAuthenticationFilter** (ya está en tu proyecto):
```java
// Extrae token del header
String jwtToken = authorizationHeader.substring(7); // Quita "Bearer "

// Valida y establece autenticación
if (jwtService.isTokenValid(jwtToken, userDetails)) {
    SecurityContextHolder.getContext().setAuthentication(authToken);
}
```

**Los roles vienen del token JWT** y se establecen automáticamente en el SecurityContext.

---

## 📝 Ejemplos Completos

### Ejemplo 1: Query Simple (requiere autenticación)

**GraphQL Query:**
```graphql
query {
  cursos(activo: true) {
    idCurso
    nombre
    precioBase
  }
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{"query":"{ cursos(activo: true) { idCurso nombre precioBase } }"}'
```

---

### Ejemplo 2: Query con Rol ADMIN

**GraphQL Query:**
```graphql
query {
  usuarios {
    idUsuario
    email
    rol
  }
}
```

**Solo funciona si el token tiene rol ADMIN.**

---

### Ejemplo 3: Mutation (requiere autenticación)

**GraphQL Mutation:**
```graphql
mutation {
  createCurso(input: {
    nombre: "Nuevo Curso"
    idMateria: 1
    idFormato: 1
    precioBase: 100.00
  }) {
    idCurso
    nombre
  }
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer tu_token_aqui" \
  -d '{"query":"mutation { createCurso(input: { nombre: \"Nuevo Curso\" idMateria: 1 idFormato: 1 precioBase: 100.00 }) { idCurso nombre } }"}'
```

---

## 🔧 Troubleshooting

### Error: "Unauthorized"

**Causas posibles:**

1. **No enviaste token:**
   - Verifica que el header `Authorization` esté presente
   - Formato correcto: `Bearer <token>` (con espacio después de "Bearer")

2. **Token expirado:**
   - Los tokens JWT tienen expiración (por defecto 24 horas)
   - Obtén un nuevo token con `/api/auth/login`

3. **Token inválido:**
   - Verifica que copiaste el token completo
   - No debe tener espacios extra o saltos de línea

4. **No tienes el rol necesario:**
   - Verifica el rol de tu usuario en la BD
   - Algunas queries requieren roles específicos

---

### Error: "Access Denied"

**Causa:** Tienes autenticación válida, pero no tienes el rol necesario.

**Solución:**
- Usa un usuario con el rol adecuado
- O modifica temporalmente `@PreAuthorize` para pruebas

---

## 🎯 Resumen

| Situación | ¿Necesita Token? | ¿Cómo Obtenerlo? |
|-----------|------------------|------------------|
| **Desarrollo con GraphiQL** | Sí (para queries con roles) | POST `/api/auth/login` |
| **Desarrollo con Postman** | Sí | POST `/api/auth/login` |
| **Producción** | Sí (siempre) | El frontend obtiene token al hacer login |
| **Pruebas automatizadas** | Sí | Mock token o crear usuario de prueba |

---

## ✅ Checklist

- [ ] Obtener token JWT desde `/api/auth/login`
- [ ] Agregar header `Authorization: Bearer <token>` en GraphiQL/Postman
- [ ] Verificar que las queries funcionan con token
- [ ] (Producción) Deshabilitar GraphiQL
- [ ] (Producción) Requerir autenticación en `/graphql`

---

**Última actualización:** Día 4 - Guía Autenticación GraphQL  
**Estado:** ✅ Listo para usar

