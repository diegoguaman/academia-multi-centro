# ✅ Solución Final: Error CORS en GraphiQL

## 🔍 Problema Identificado

Después de comparar con el proyecto que funciona, encontré que **faltaba la configuración CORS explícita** en tu proyecto.

### El Error

```
Access to script at 'https://unpkg.com/@graphiql/plugin-explorer@5.1.1/dist/index.umd.js' 
from origin 'http://localhost:8080' has been blocked by CORS policy
```

---

## ✅ Solución Aplicada

### 1. Configuración CORS Completa en SecurityConfig

**❌ ANTES (Faltaba):**
- No había configuración CORS explícita
- No había bean `CorsConfigurationSource`
- No se usaba `.cors()` en SecurityFilterChain

**✅ AHORA (Corregido):**

Agregado en `SecurityConfig.java`:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // Allow localhost origins (development)
    configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://localhost:8080",
            "http://localhost:5173"
    ));
    
    // Allow all HTTP methods
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    
    // Allow all headers (important for JWT and GraphQL)
    configuration.setAllowedHeaders(List.of("*"));
    
    // Allow credentials
    configuration.setAllowCredentials(true);
    
    // Cache preflight requests
    configuration.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

Y en `SecurityFilterChain`:

```java
http
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // ← AGREGADO
    .csrf(AbstractHttpConfigurer::disable)
    // ... resto
```

### 2. Simplificación de application.properties

**Cambio aplicado:**

Removida la línea `graphiql.endpoint.graphql=/graphql` que no estaba en el otro proyecto.

**Configuración final:**

```properties
# GraphQL Configuration
spring.graphql.path=/graphql
spring.graphql.graphiql.enabled=true

# GraphiQL UI (graphiql-spring-boot-starter)
graphiql.enabled=true
graphiql.mapping=/graphiql
```

---

## 🔍 Diferencias Clave Encontradas

### Proyecto que Funciona vs Tu Proyecto

| Aspecto | Proyecto que Funciona | Tu Proyecto (ANTES) | Tu Proyecto (AHORA) |
|---------|----------------------|---------------------|---------------------|
| **CORS Config** | ✅ Bean completo | ❌ No tenía | ✅ **Agregado** |
| **SecurityFilterChain** | ✅ Usa `.cors()` | ❌ No usaba | ✅ **Agregado** |
| **application.properties** | ✅ 3 propiedades | ⚠️ 4 propiedades | ✅ **Corregido** |
| **Spring Boot** | 3.5.7 | 3.3.0 | 3.3.0 (OK) |

---

## 🚀 Pasos para Probar

### 1. Reiniciar la Aplicación

**IMPORTANTE:** Reinicia completamente la aplicación (no solo recarga).

```bash
# Detén la aplicación
# Luego iníciala de nuevo
mvn spring-boot:run
```

### 2. Limpiar Caché del Navegador

Presiona **Ctrl + Shift + R** (o Cmd + Shift + R en Mac) para hacer hard refresh.

### 3. Acceder a GraphiQL

**URL correcta:**
```
http://localhost:8080/graphiql?path=/graphql
```

O simplemente:
```
http://localhost:8080/graphiql
```

### 4. Probar una Query

```graphql
query {
  usuarios {
    idUsuario
    email
    rol
  }
}
```

---

## 🔧 Cambios Aplicados

### Archivo 1: `SecurityConfig.java`

**Agregado:**
- Bean `corsConfigurationSource()` completo
- Uso de `.cors()` en SecurityFilterChain
- Imports necesarios para CORS

### Archivo 2: `application.properties`

**Simplificado:**
- Removida propiedad `graphiql.endpoint.graphql=/graphql`
- Mantiene solo las 3 propiedades esenciales

---

## ✅ Verificación

Después de aplicar estos cambios, deberías poder:

1. ✅ Acceder a `http://localhost:8080/graphiql` sin errores de CORS
2. ✅ Ver la interfaz de GraphiQL cargada correctamente
3. ✅ Ejecutar queries sin problemas
4. ✅ No ver errores en la consola del navegador

---

## 📋 Checklist Final

- [x] Configuración CORS agregada en SecurityConfig
- [x] Bean CorsConfigurationSource creado
- [x] SecurityFilterChain usa configuración CORS
- [x] application.properties simplificado
- [ ] Aplicación reiniciada completamente
- [ ] Caché del navegador limpiado
- [ ] GraphiQL accesible sin errores

---

## 🎯 Por Qué Esto Soluciona el Problema

### Problema Original

El error de CORS ocurría porque:

1. **GraphiQL intenta cargar recursos** desde diferentes orígenes
2. **El navegador bloquea estas solicitudes** sin headers CORS apropiados
3. **Sin CORS configurado**, el servidor no envía los headers necesarios
4. **El navegador rechaza las solicitudes** → Error CORS

### Solución

1. **Configuración CORS explícita** permite todas las solicitudes desde localhost
2. **Headers CORS apropiados** se envían en todas las respuestas
3. **El navegador acepta las solicitudes** → GraphiQL funciona

---

## 🔍 Comparación con el Proyecto que Funciona

**El otro proyecto tenía:**
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    // ... configuración completa
}

http
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    // ...
```

**Tu proyecto ahora tiene:**
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    // ... misma configuración
}

http
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    // ...
```

**✅ Ahora ambos proyectos tienen la misma configuración CORS.**

---

## 📝 Notas Adicionales

### Si Aún No Funciona

1. **Verifica logs al iniciar:**
   - Busca errores relacionados con CORS
   - Busca mensajes sobre GraphiQL

2. **Verifica en DevTools (F12):**
   - Network tab: ¿Qué requests fallan?
   - Console tab: ¿Hay otros errores?

3. **Prueba el endpoint GraphQL directamente:**
   ```bash
   curl -X POST http://localhost:8080/graphql \
     -H "Content-Type: application/json" \
     -d '{"query": "{ __schema { types { name } } }"}'
   ```

### Alternativa: Postman

Si GraphiQL aún no funciona, puedes usar Postman que es igual de funcional:

**Ver:** `docs/dia-4/EJEMPLO-POSTMAN-GRAPHQL.md`

---

**Última actualización:** Día 4 - Solución Final CORS  
**Estado:** ✅ Configuración CORS aplicada, listo para probar

