# ⚡ Quick Start: Autenticación en GraphQL (5 minutos)

## 🎯 Problema Actual

Recibes este error:
```json
{
  "errors": [
    {
      "message": "Unauthorized"
    }
  ]
}
```

**Causa:** Las queries tienen `@PreAuthorize` que requiere autenticación con roles.

---

## ✅ Solución Rápida (3 pasos)

### Paso 1: Obtener Token JWT

**Opción A: Usando Postman/cURL**

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "diengo@diego.com",
  "password": "tu_password_aqui"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "diengo@diego.com",
  "rol": "ADMIN"
}
```

**Copia el valor de `token`** (la cadena larga).

---

### Paso 2: Agregar Token en GraphiQL

1. **Abre GraphiQL:** `http://localhost:8080/graphiql?path=/graphql`

2. **Busca el panel de "HTTP Headers"** o "Headers" (normalmente abajo o en un panel lateral)

3. **Agrega este JSON:**
   ```json
   {
     "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   }
   ```
   
   **Reemplaza** `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...` con tu token real.

4. **¡Listo!** Ahora todas las queries usarán ese token automáticamente.

---

### Paso 3: Probar Query

```graphql
query {
  cursos(activo: true) {
    nombre
    precioBase
  }
}
```

**Debería funcionar ahora** ✅

---

## 🔍 Si GraphiQL No Tiene Campo de Headers

Algunas versiones no tienen interfaz para headers. Soluciones:

### Opción A: Usar Postman (Recomendado)

1. **Nueva Request:**
   - Method: `POST`
   - URL: `http://localhost:8080/graphql`

2. **Headers Tab:**
   ```
   Key: Authorization
   Value: Bearer tu_token_aqui
   ```

3. **Body Tab (raw JSON):**
   ```json
   {
     "query": "query { cursos(activo: true) { nombre } }"
   }
   ```

4. **Send**

---

### Opción B: Usar cURL desde Terminal

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -d '{"query":"{ cursos(activo: true) { nombre } }"}'
```

---

### Opción C: Modificar Temporalmente SecurityConfig

Si solo quieres probar sin token (NO para producción):

**Comenta las anotaciones `@PreAuthorize` en los resolvers:**

```java
// @PreAuthorize("hasAnyRole('ADMIN', 'ALUMNO', 'PROFESOR', 'ADMINISTRATIVO')")
public CursoResponseDto curso(@Argument final Long id) {
    return cursoService.findById(id);
}
```

**⚠️ IMPORTANTE:** Solo para desarrollo. Restaura después.

---

## 🎯 Queries de Ejemplo para Probar

### Query 1: Obtener Usuario (requiere autenticación)

```graphql
query {
  usuario(id: 1) {
    idUsuario
    email
    rol
    activo
  }
}
```

---

### Query 2: Listar Cursos (requiere autenticación)

```graphql
query {
  cursos(activo: true) {
    idCurso
    nombre
    precioBase
  }
}
```

---

### Query 3: Listar Usuarios (solo ADMIN)

```graphql
query {
  usuarios {
    idUsuario
    email
    rol
  }
}
```

**Solo funciona si tu token tiene rol ADMIN.**

---

## 🔐 Cómo Saber qué Rol Tiene tu Token

Decodifica el token JWT en: https://jwt.io

1. Pega tu token completo
2. Verás el payload (segunda parte)
3. Busca el campo `authorities` o `rol`

O simplemente ejecuta esta query (si funciona, tienes el rol necesario):
```graphql
query {
  usuarios {
    idUsuario
  }
}
```

Si funciona → tienes rol ADMIN ✅  
Si da error → no tienes rol ADMIN ❌

---

## 📝 Resumen Visual

```
┌─────────────────────────────────────────┐
│  1. POST /api/auth/login                │
│     { email, password }                 │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  2. Recibes token JWT                   │
│     { token: "eyJhbGc..." }            │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  3. POST /graphql                       │
│     Header: Authorization: Bearer ...   │
│     Body: { query: "..." }             │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  4. ✅ Query ejecutada con roles        │
└─────────────────────────────────────────┘
```

---

## ❓ Preguntas Frecuentes

### ¿Necesito frontend para esto?

**NO.** Puedes probar todo desde GraphiQL, Postman o cURL. El frontend solo obtendrá el token igual que tú.

---

### ¿Funciona en producción?

**Sí, pero:**
- En producción normalmente **no se usa GraphiQL** (se deshabilita)
- Solo se usa el endpoint `/graphql`
- El frontend obtiene el token al hacer login y lo envía automáticamente

---

### ¿El token expira?

**Sí**, por defecto expira en **24 horas**. Si expira:
- Obtén un nuevo token con `/api/auth/login`
- Reemplázalo en los headers

---

### ¿Puedo probar sin token?

**Sí, temporalmente:**
1. Comenta `@PreAuthorize` en los resolvers
2. O quita `permitAll()` de `/graphql/**` en SecurityConfig

**⚠️ Solo para desarrollo local. NO en producción.**

---

## ✅ Checklist

- [ ] Obtener token desde `/api/auth/login`
- [ ] Copiar token recibido
- [ ] Agregar header `Authorization: Bearer <token>` en GraphiQL/Postman
- [ ] Ejecutar query de prueba
- [ ] Verificar que funciona

---

**Tiempo estimado:** 5 minutos  
**Última actualización:** Día 4  
**Estado:** ✅ Listo para usar ahora mismo

