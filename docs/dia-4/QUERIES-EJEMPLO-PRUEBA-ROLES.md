# 🔍 Queries de Ejemplo para Probar GraphQL con Roles

## 📋 Usuario en la Base de Datos

Según la imagen proporcionada, tienes un usuario en la base de datos:

- **ID:** `1`
- **Email:** `diengo@diego.com`
- **Rol:** `ADMIN`
- **Activo:** `true`

---

## ✅ Query 1: Obtener el Usuario por ID

**Descripción:** Esta query obtiene un usuario específico por su ID. Requiere rol ADMIN, PROFESOR o ADMINISTRATIVO.

**Query GraphQL:**
```graphql
query {
  usuario(id: 1) {
    idUsuario
    email
    rol
    activo
    fechaCreacion
  }
}
```

**Respuesta Esperada:**
```json
{
  "data": {
    "usuario": {
      "idUsuario": "1",
      "email": "diengo@diego.com",
      "rol": "ADMIN",
      "activo": true,
      "fechaCreacion": "2025-12-02T12:13:21.017853"
    }
  }
}
```

**Autorización:** `@PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ADMINISTRATIVO')")`

---

## ✅ Query 2: Obtener Todos los Usuarios (Solo ADMIN)

**Descripción:** Esta query lista todos los usuarios del sistema. **Solo ADMIN puede ejecutarla.**

**Query GraphQL:**
```graphql
query {
  usuarios {
    idUsuario
    email
    rol
    activo
    fechaCreacion
  }
}
```

**Respuesta Esperada:**
```json
{
  "data": {
    "usuarios": [
      {
        "idUsuario": "1",
        "email": "diengo@diego.com",
        "rol": "ADMIN",
        "activo": true,
        "fechaCreacion": "2025-12-02T12:13:21.017853"
      }
    ]
  }
}
```

**Autorización:** `@PreAuthorize("hasRole('ADMIN')")`

**⚠️ IMPORTANTE:** Si no tienes el rol ADMIN, recibirás un error `403 Forbidden`:
```json
{
  "errors": [
    {
      "message": "Access Denied",
      "extensions": {
        "classification": "DataFetchingException"
      }
    }
  ],
  "data": null
}
```

---

## ✅ Query 3: Obtener Usuarios por Rol (Solo ADMIN)

**Descripción:** Filtra usuarios por rol específico. **Solo ADMIN puede ejecutarla.**

### 3.1 Obtener Solo ADMIN

**Query GraphQL:**
```graphql
query {
  usuarios(rol: ADMIN) {
    idUsuario
    email
    rol
    activo
  }
}
```

**Respuesta Esperada:**
```json
{
  "data": {
    "usuarios": [
      {
        "idUsuario": "1",
        "email": "diengo@diego.com",
        "rol": "ADMIN",
        "activo": true
      }
    ]
  }
}
```

### 3.2 Obtener Solo PROFESOR

**Query GraphQL:**
```graphql
query {
  usuarios(rol: PROFESOR) {
    idUsuario
    email
    rol
    activo
  }
}
```

**Respuesta Esperada (si no hay profesores):**
```json
{
  "data": {
    "usuarios": []
  }
}
```

### 3.3 Obtener Solo ALUMNO

**Query GraphQL:**
```graphql
query {
  usuarios(rol: ALUMNO) {
    idUsuario
    email
    rol
    activo
  }
}
```

### 3.4 Obtener Solo ADMINISTRATIVO

**Query GraphQL:**
```graphql
query {
  usuarios(rol: ADMINISTRATIVO) {
    idUsuario
    email
    rol
    activo
  }
}
```

---

## 🔐 Cómo Probar los Roles

### Opción 1: Sin Autenticación (GraphiQL Público)

Actualmente, GraphiQL está configurado como público (`permitAll()`), por lo que **NO se validan los roles**. Todas las queries funcionarán sin autenticación.

**Para probar con roles reales**, necesitas:

1. **Habilitar autenticación en GraphQL** (comentar `permitAll()` para `/graphql/**`)
2. **Obtener un token JWT** desde el endpoint de login
3. **Agregar el token** en las headers de GraphiQL

### Opción 2: Con Autenticación JWT

#### Paso 1: Login y Obtener Token

**POST** a `http://localhost:8080/api/auth/login`

**Body (JSON):**
```json
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

#### Paso 2: Usar Token en GraphiQL

En GraphiQL, agrega el header:

**HTTP Headers:**
```json
{
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Paso 3: Ejecutar Query con Roles

Ahora las queries respetarán los roles definidos en `@PreAuthorize`.

---

## 📊 Resumen de Autorizaciones

| Query | Roles Permitidos | Descripción |
|-------|-----------------|-------------|
| `usuario(id: ID!)` | ADMIN, PROFESOR, ADMINISTRATIVO | Ver un usuario específico |
| `usuarios` | ADMIN | Listar todos los usuarios |
| `usuarios(rol: Rol)` | ADMIN | Filtrar usuarios por rol |

---

## 🧪 Queries Adicionales para Probar

### Query con Datos Personales (si existe relación)

```graphql
query {
  usuario(id: 1) {
    idUsuario
    email
    rol
    datosPersonales {
      nombre
      apellidos
      dni
      telefono
    }
  }
}
```

**Nota:** Solo funcionará si el usuario tiene `datosPersonales` asociados.

---

## ❌ Errores Comunes

### Error 1: NullValueInNonNullableField

**Síntoma:**
```json
{
  "errors": [
    {
      "message": "The field at path '/usuarios' was declared as a non null type, but the code involved in retrieving data has wrongly returned a null value."
    }
  ]
}
```

**Causa:** El resolver no está implementado o devuelve `null`.

**Solución:** ✅ Ya implementamos `UsuarioResolver`, este error debería desaparecer.

---

### Error 2: Access Denied (403)

**Síntoma:**
```json
{
  "errors": [
    {
      "message": "Access Denied"
    }
  ]
}
```

**Causa:** No tienes el rol necesario para ejecutar la query.

**Solución:** 
- Asegúrate de estar autenticado con un usuario que tenga el rol adecuado
- O usa GraphiQL sin autenticación (actualmente permitido)

---

## 🎯 Próximos Pasos

1. ✅ Probar la query `usuario(id: 1)` para ver tu usuario
2. ✅ Probar la query `usuarios` para listar todos
3. ✅ Probar `usuarios(rol: ADMIN)` para filtrar por rol
4. 🔄 (Opcional) Implementar autenticación JWT en GraphQL para probar roles reales
5. 🔄 (Futuro) Crear más usuarios con diferentes roles para probar filtros

---

## 📝 Notas Importantes

### Mapeo de Enum

El enum de GraphQL `Rol` se mapea automáticamente al enum de Java `Usuario.Rol` porque:

- Ambos tienen los mismos valores: `ADMIN`, `PROFESOR`, `ALUMNO`, `ADMINISTRATIVO`
- Spring GraphQL hace el mapeo automático por nombre

### ID Type en GraphQL

En GraphQL, los IDs pueden ser:
- `String`: `"1"`
- `Int`: `1`

Spring GraphQL acepta ambos formatos y los convierte automáticamente a `Long`.

---

**Última actualización:** Día 4 - Queries de Ejemplo  
**Estado:** ✅ Listo para probar

