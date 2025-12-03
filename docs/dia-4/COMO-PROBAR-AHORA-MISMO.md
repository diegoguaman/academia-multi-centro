# 🚀 Cómo Probar GraphQL con Autenticación AHORA MISMO

## ⚡ Pasos Rápidos (3 minutos)

### 1️⃣ Obtener Token (Postman o cURL)

**POST** `http://localhost:8080/api/auth/login`

**Body:**
```json
{
  "email": "diengo@diego.com",
  "password": "tu_password_aqui"
}
```

**Respuesta - Copia el `token`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJkaWVuZ29AZGllZ28uY29tIiwiZXhwIjoxNzM1MjY5NjAwfQ...",
  "rol": "ADMIN"
}
```

---

### 2️⃣ Usar Token en GraphiQL

**Opción A: Si GraphiQL tiene campo de Headers**

1. Abre: `http://localhost:8080/graphiql?path=/graphql`
2. Busca sección "HTTP Headers" o "Headers"
3. Agrega:
   ```json
   {
     "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   }
   ```
4. Reemplaza `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...` con tu token real

---

**Opción B: Si NO tiene campo de Headers → Usa Postman**

1. **Nueva Request → POST** `http://localhost:8080/graphql`

2. **Headers:**
   ```
   Authorization: Bearer tu_token_aqui
   Content-Type: application/json
   ```

3. **Body (raw JSON):**
   ```json
   {
     "query": "query { cursos(activo: true) { nombre } }"
   }
   ```

4. **Send** ✅

---

### 3️⃣ Probar

```graphql
query {
  cursos(activo: true) {
    nombre
    precioBase
  }
}
```

**Debería funcionar** ✅

---

## 📝 Ejemplo Completo en Postman

### Request 1: Login

```
POST http://localhost:8080/api/auth/login

Headers:
  Content-Type: application/json

Body:
{
  "email": "diengo@diego.com",
  "password": "tu_password"
}
```

### Request 2: GraphQL Query (con token)

```
POST http://localhost:8080/graphql

Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  Content-Type: application/json

Body:
{
  "query": "query { cursos(activo: true) { nombre precioBase } }"
}
```

---

## ✅ Respuestas Directas a Tus Preguntas

### ❓ ¿Se puede hacer en producción o solo con frontend?

**SÍ, se puede hacer AHORA MISMO sin frontend:**
- Usa Postman o cURL
- Obtén token desde `/api/auth/login`
- Envía token en cada request a `/graphql`

**En producción:** El frontend hará exactamente lo mismo (obtener token y enviarlo).

---

### ❓ ¿Cómo se trabaja con GraphQL en producción para hacer estas pruebas?

**En producción:**
1. **GraphiQL está DESHABILITADO** (seguridad)
2. Se usa **Postman/Insomnia** para pruebas manuales
3. Se usa el endpoint `/graphql` directamente
4. El token se obtiene desde el login y se envía en headers

**En desarrollo:**
- GraphiQL habilitado para facilitar pruebas
- Pero igual necesitas token para queries con roles

---

### ❓ ¿No se usa la interfaz gráfica en producción?

**Correcto:**
- ❌ **GraphiQL NO se usa en producción** (riesgo de seguridad)
- ✅ Solo el endpoint `/graphql`
- ✅ El frontend hace queries desde código JavaScript/TypeScript
- ✅ Para pruebas manuales → Postman/Insomnia

---

## 🎯 Resumen

| Pregunta | Respuesta |
|----------|-----------|
| ¿Puedo probar ahora? | **SÍ, con Postman o GraphiQL + headers** |
| ¿Necesito frontend? | **NO, puedes probar ahora mismo** |
| ¿Se usa GraphiQL en producción? | **NO, solo en desarrollo** |
| ¿Cómo probar en producción? | **Postman/Insomnia con token JWT** |
| ¿El frontend hace lo mismo? | **SÍ, obtiene token y lo envía en headers** |

---

**Tiempo para probar:** 3 minutos  
**Necesitas:** Postman o GraphiQL con headers  
**No necesitas:** Frontend

