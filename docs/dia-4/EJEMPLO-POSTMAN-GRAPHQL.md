# 📬 Ejemplos de Queries GraphQL con Postman

## 🎯 Configuración Inicial de Postman

### Paso 1: Crear una Nueva Request

1. Abre Postman
2. Clic en **"New"** → **"HTTP Request"**
3. Configura el método: **POST**
4. URL: `http://localhost:8080/graphql`

### Paso 2: Configurar Headers

En la pestaña **"Headers"**, agrega:

| Key | Value |
|-----|-------|
| `Content-Type` | `application/json` |
| `Authorization` | `Bearer TU_JWT_TOKEN_AQUI` |

**Nota:** Para obtener el JWT token:
1. Primero haz login en `/api/auth/login`
2. Copia el token de la respuesta
3. Úsalo en el header `Authorization`

---

## 📝 Ejemplo 1: Query de Usuarios (Todos)

### Query GraphQL

```graphql
query {
  usuarios {
    idUsuario
    email
    rol
    activo
    fechaCreacion
    datosPersonales {
      nombre
      apellidos
      dni
      telefono
    }
  }
}
```

### Body en Postman (raw JSON)

```json
{
  "query": "query { usuarios { idUsuario email rol activo fechaCreacion datosPersonales { nombre apellidos dni telefono } } }"
}
```

### Respuesta Esperada

```json
{
  "data": {
    "usuarios": [
      {
        "idUsuario": "1",
        "email": "admin@academia.com",
        "rol": "ADMIN",
        "activo": true,
        "fechaCreacion": "2024-01-01T10:00:00",
        "datosPersonales": {
          "nombre": "Juan",
          "apellidos": "Pérez",
          "dni": "12345678A",
          "telefono": "600123456"
        }
      }
    ]
  }
}
```

---

## 📝 Ejemplo 2: Query de Usuario por ID

### Query GraphQL

```graphql
query {
  usuario(id: "1") {
    idUsuario
    email
    rol
    activo
    fechaCreacion
    datosPersonales {
      nombre
      apellidos
      dni
      telefono
      direccion
    }
  }
}
```

### Body en Postman (raw JSON)

```json
{
  "query": "query { usuario(id: \"1\") { idUsuario email rol activo fechaCreacion datosPersonales { nombre apellidos dni telefono direccion } } }"
}
```

**Nota:** Escapa las comillas dobles dentro de la query con `\"`

---

## 📝 Ejemplo 3: Query de Usuarios Filtrados por Rol

### Query GraphQL

```graphql
query {
  usuarios(rol: ALUMNO) {
    idUsuario
    email
    rol
    activo
    datosPersonales {
      nombre
      apellidos
    }
  }
}
```

### Body en Postman (raw JSON)

```json
{
  "query": "query { usuarios(rol: ALUMNO) { idUsuario email rol activo datosPersonales { nombre apellidos } } }"
}
```

---

## 📝 Ejemplo 4: Query con Variables (Recomendado)

### Usando Variables en Postman

**Ventajas de usar variables:**
- ✅ Evita escapar comillas
- ✅ Reutilizable
- ✅ Más limpio y mantenible

### Configuración:

1. En la pestaña **"Body"**, selecciona **"raw"** y **"JSON"**
2. Usa este formato:

```json
{
  "query": "query GetUsuario($id: ID!) { usuario(id: $id) { idUsuario email rol activo datosPersonales { nombre apellidos dni } } }",
  "variables": {
    "id": "1"
  }
}
```

### Ejemplo con Múltiples Variables

```json
{
  "query": "query GetUsuarios($rol: Rol) { usuarios(rol: $rol) { idUsuario email rol activo } }",
  "variables": {
    "rol": "ALUMNO"
  }
}
```

---

## 📝 Ejemplo 5: Query de Cursos (Relaciones)

### Query GraphQL Completa

```graphql
query {
  cursos(activo: true) {
    idCurso
    nombre
    precioBase
    duracionHoras
    materia {
      nombre
      descripcion
    }
    formato {
      nombre
    }
  }
}
```

### Body en Postman

```json
{
  "query": "query { cursos(activo: true) { idCurso nombre precioBase duracionHoras materia { nombre descripcion } formato { nombre } } }"
}
```

---

## 📝 Ejemplo 6: Query de Matrículas con Relaciones

### Query GraphQL

```graphql
query {
  matriculas {
    idMatricula
    codigo
    precioFinal
    estadoPago
    fechaMatricula
    convocatoria {
      codigo
      curso {
        nombre
      }
    }
    alumno {
      email
      datosPersonales {
        nombre
        apellidos
      }
    }
  }
}
```

### Body en Postman

```json
{
  "query": "query { matriculas { idMatricula codigo precioFinal estadoPago fechaMatricula convocatoria { codigo curso { nombre } } alumno { email datosPersonales { nombre apellidos } } } }"
}
```

---

## 📝 Ejemplo 7: Mutation - Crear Curso

### Mutation GraphQL

```graphql
mutation {
  createCurso(input: {
    nombre: "Spring Boot Avanzado"
    idMateria: "1"
    idFormato: "1"
    precioBase: "299.99"
    duracionHoras: 40
    activo: true
  }) {
    idCurso
    nombre
    precioBase
    duracionHoras
  }
}
```

### Body en Postman

```json
{
  "query": "mutation { createCurso(input: { nombre: \"Spring Boot Avanzado\" idMateria: \"1\" idFormato: \"1\" precioBase: \"299.99\" duracionHoras: 40 activo: true }) { idCurso nombre precioBase duracionHoras } }"
}
```

### Mutation con Variables (Más Limpio)

```json
{
  "query": "mutation CreateCurso($input: CursoInput!) { createCurso(input: $input) { idCurso nombre precioBase duracionHoras } }",
  "variables": {
    "input": {
      "nombre": "Spring Boot Avanzado",
      "idMateria": "1",
      "idFormato": "1",
      "precioBase": "299.99",
      "duracionHoras": 40,
      "activo": true
    }
  }
}
```

---

## 🔐 Autenticación: Obtener JWT Token

### Request de Login

**Método:** POST  
**URL:** `http://localhost:8080/api/auth/login`

**Body (raw JSON):**

```json
{
  "email": "admin@academia.com",
  "password": "tu_password"
}
```

**Respuesta:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400000
}
```

**Copia el token** y úsalo en el header `Authorization: Bearer <token>` de tus requests GraphQL.

---

## 📋 Colección de Postman Recomendada

### Estructura Sugerida

Crea una colección en Postman con estas carpetas:

```
Academia Manager - GraphQL
├── Auth
│   ├── Login
│   └── Register
├── Queries
│   ├── Usuarios
│   │   ├── Get All Usuarios
│   │   ├── Get Usuario by ID
│   │   └── Get Usuarios by Rol
│   ├── Cursos
│   │   ├── Get All Cursos
│   │   └── Get Curso by ID
│   └── Matrículas
│       └── Get All Matrículas
└── Mutations
    ├── Create Curso
    ├── Update Curso
    └── Create Matrícula
```

### Variables de Colección

En la pestaña **"Variables"** de la colección, agrega:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `baseUrl` | `http://localhost:8080` | `http://localhost:8080` |
| `jwtToken` | (vacío) | (se llena después de login) |

Luego usa en las requests:
- URL: `{{baseUrl}}/graphql`
- Header Authorization: `Bearer {{jwtToken}}`

---

## 🚀 Scripts Automáticos de Postman

### Script Pre-request: Auto-login

En la carpeta de la colección, agrega este script en **"Pre-request Script"**:

```javascript
// Solo si no hay token o está expirado
if (!pm.collectionVariables.get("jwtToken")) {
    pm.sendRequest({
        url: pm.collectionVariables.get("baseUrl") + "/api/auth/login",
        method: 'POST',
        header: {
            'Content-Type': 'application/json'
        },
        body: {
            mode: 'raw',
            raw: JSON.stringify({
                email: "admin@academia.com",
                password: "tu_password"
            })
        }
    }, function (err, res) {
        if (!err && res.code === 200) {
            const token = res.json().token;
            pm.collectionVariables.set("jwtToken", token);
        }
    });
}
```

**⚠️ Nota:** Esto es solo para desarrollo. En producción, nunca hardcodees credenciales.

---

## 🎨 Formato de Queries Multilínea

Para queries largas, puedes usar formato multilínea en Postman usando `\n`:

```json
{
  "query": "query {\n  usuarios {\n    idUsuario\n    email\n    rol\n    datosPersonales {\n      nombre\n      apellidos\n    }\n  }\n}"
}
```

O mejor aún, usa variables de Postman para queries reutilizables.

---

## 📊 Ejemplos de Respuestas de Error

### Error de Autenticación

```json
{
  "errors": [
    {
      "message": "Access Denied",
      "extensions": {
        "classification": "AUTHORIZATION_ERROR"
      }
    }
  ]
}
```

### Error de Validación

```json
{
  "errors": [
    {
      "message": "Invalid input",
      "extensions": {
        "classification": "VALIDATION_ERROR",
        "field": "precioBase"
      }
    }
  ]
}
```

### Error de Query Invalida

```json
{
  "errors": [
    {
      "message": "Cannot query field 'campoNoExistente' on type 'Usuario'",
      "locations": [
        {
          "line": 2,
          "column": 3
        }
      ]
    }
  ]
}
```

---

## ✅ Checklist para Usar Postman con GraphQL

- [ ] Método POST configurado
- [ ] URL: `http://localhost:8080/graphql`
- [ ] Header `Content-Type: application/json`
- [ ] Header `Authorization: Bearer <token>` (si requiere autenticación)
- [ ] Body en formato raw JSON
- [ ] Query bien formada (escapar comillas si no usas variables)
- [ ] Variables definidas (opcional pero recomendado)

---

**Última actualización:** Día 4 - Ejemplos Postman GraphQL

