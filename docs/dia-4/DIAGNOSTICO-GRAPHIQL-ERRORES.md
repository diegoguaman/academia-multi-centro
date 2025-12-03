# 🔍 Diagnóstico: Errores de GraphiQL

## 📋 Errores Identificados

### Error 1: CORS Policy
```
Access to script at 'https://unpkg.com/@graphiql/plugin-explorer@5.1.1/dist/index.umd.js' 
from origin 'http://localhost:8080' has been blocked by CORS policy
```

### Error 2: 404 Not Found
```
GET https://unpkg.com/@graphiql/plugin-explorer@5.1.1/dist/index.umd.js 
net::ERR_FAILED 404 (Not Found)
```

### Error 3: GraphiQL Not Defined
```
Uncaught ReferenceError: GraphiQL is not defined
```

---

## 🔍 Análisis del Problema

### Causa Raíz: Recursos Externos Bloqueados

**Problema Principal:**
1. GraphiQL intenta cargar recursos desde `unpkg.com` (CDN externo)
2. El navegador bloquea estos recursos por política CORS
3. Sin los recursos, GraphiQL no puede inicializarse

**¿Por qué ocurre?**
- Spring Boot GraphQL intenta cargar GraphiQL desde CDN externo
- El navegador aplica políticas de seguridad que bloquean recursos externos
- La versión de GraphiQL embebida en Spring Boot puede tener problemas

### URL Incorrecta

**Tu URL:**
```
http://localhost:8080/graphiql?path=/graphql
```

**URL Correcta en Spring Boot 3.3.0:**
```
http://localhost:8080/graphiql
```

El parámetro `?path=/graphql` puede estar causando problemas adicionales.

---

## ✅ Soluciones

### Solución 1: Usar la URL Correcta (MÁS SIMPLE)

**URL correcta:**
```
http://localhost:8080/graphiql
```

**No uses:**
- ❌ `http://localhost:8080/graphiql?path=/graphql`
- ❌ `http://localhost:8080/graphiql/`

### Solución 2: Configurar GraphiQL Correctamente

El problema es que Spring Boot GraphQL 3.3.0 puede tener problemas con GraphiQL embebido. Necesitas configurarlo correctamente.

**Opción A: Configuración Mínima (Intentar primero)**

Verifica que en `application.properties` tengas:

```properties
spring.graphql.graphiql.enabled=true
spring.graphql.path=/graphql
```

**Opción B: Deshabilitar GraphiQL y Usar Postman/GraphQL Playground**

Para desarrollo, es más fácil usar herramientas externas:

1. **Postman:** Configura request POST a `http://localhost:8080/graphql`
2. **GraphQL Playground:** Herramienta standalone
3. **Apollo Studio:** Otra alternativa

### Solución 3: Crear Tu Propia Interfaz GraphiQL (AVANZADO)

Si necesitas GraphiQL funcionando, puedes crear tu propia página HTML.

---

## 🎯 Configuración Recomendada para GraphiQL

### 1. Verificar Configuración Actual

En `application.properties` deberías tener:

```properties
# GraphQL Configuration
spring.graphql.graphiql.enabled=true
spring.graphql.path=/graphql
```

### 2. Verificar SecurityConfig

En `SecurityConfig.java`, GraphiQL debería ser público:

```java
.requestMatchers("/graphql", "/graphiql").permitAll()
```

### 3. URL Correcta para Acceder

```
http://localhost:8080/graphiql
```

**NO uses:** `http://localhost:8080/graphiql?path=/graphql`

---

## 📊 Comparación: URL Correcta vs Incorrecta

| URL | Estado | Descripción |
|-----|--------|-------------|
| `http://localhost:8080/graphiql` | ✅ Correcto | Ruta estándar de Spring Boot |
| `http://localhost:8080/graphiql?path=/graphql` | ❌ Incorrecto | Parámetro extra puede causar problemas |
| `http://localhost:8080/graphiql/` | ❌ Puede fallar | La barra final puede causar problemas |

---

## 🔧 Alternativas si GraphiQL No Funciona

### Alternativa 1: Postman (Recomendado para Desarrollo)

**Configuración:**

1. **Método:** POST
2. **URL:** `http://localhost:8080/graphql`
3. **Headers:**
   ```
   Content-Type: application/json
   Authorization: Bearer <tu-jwt-token>
   ```
4. **Body (raw JSON):**
   ```json
   {
     "query": "query { matricula(id: 1) { codigo precioFinal } }"
   }
   ```

### Alternativa 2: curl (Línea de Comandos)

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_JWT_TOKEN" \
  -d '{"query":"query { matricula(id: 1) { codigo precioFinal } }"}'
```

### Alternativa 3: GraphQL Playground (Aplicación Desktop)

Descarga desde: https://github.com/graphql/graphql-playground

**Configuración:**
- Endpoint: `http://localhost:8080/graphql`
- Headers: `{"Authorization": "Bearer TU_JWT_TOKEN"}`

---

## 🔍 Por Qué Falló

### Problema 1: Recursos desde CDN

GraphiQL embebido en Spring Boot intenta cargar recursos desde `unpkg.com`:
- Estos recursos están bloqueados por CORS
- Pueden devolver 404 si la versión no existe
- Dependen de conectividad externa

### Problema 2: Versión de GraphiQL

Spring Boot 3.3.0 puede tener una versión de GraphiQL embebida que tiene problemas conocidos con:
- Carga de recursos externos
- Configuración de rutas
- Compatibilidad con navegadores modernos

### Problema 3: Configuración de CORS

Aunque GraphiQL está en `permitAll()`, los recursos externos (unpkg.com) tienen sus propias políticas CORS que no puedes controlar.

---

## ✅ Recomendación Final

**Para Desarrollo Local:**

1. **Deshabilita GraphiQL embebido** (tiene muchos problemas)
2. **Usa Postman o GraphQL Playground** (más confiable)
3. **O crea tu propia interfaz** si realmente necesitas GraphiQL

**Para Producción:**

- ❌ **NUNCA** habilitar GraphiQL en producción
- ✅ Solo habilitar en desarrollo
- ✅ Usar herramientas externas para testing

---

## 📝 Acción Inmediata

### Paso 1: Probar URL Sin Parámetros

Intenta acceder a:
```
http://localhost:8080/graphiql
```

(Sin `?path=/graphql`)

### Paso 2: Si Sigue Fallando, Deshabilitar GraphiQL

En `application.properties`:

```properties
# Deshabilitar GraphiQL (tiene problemas)
spring.graphql.graphiql.enabled=false
```

### Paso 3: Usar Postman en su Lugar

Configura Postman como se describe arriba.

---

## 🎯 Resumen

**Problema:** GraphiQL embebido intenta cargar recursos externos que están bloqueados por CORS.

**Solución Rápida:** 
1. Usar URL sin parámetros: `http://localhost:8080/graphiql`
2. Si no funciona, deshabilitar GraphiQL y usar Postman

**Solución Profesional:** 
- Deshabilitar GraphiQL embebido
- Usar herramientas externas (Postman, GraphQL Playground)
- Crear interfaz custom si es necesario

---

**Última actualización:** Día 4 - Diagnóstico GraphiQL
**Estado:** ⚠️ GraphiQL embebido tiene problemas conocidos, usar alternativas

