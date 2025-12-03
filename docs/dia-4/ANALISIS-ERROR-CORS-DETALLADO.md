# 🔍 Análisis Detallado: Error CORS con GraphiQL

## ❌ El Error Real

```
Access to script at 'https://unpkg.com/@graphiql/plugin-explorer@5.1.1/dist/index.umd.js' 
from origin 'http://localhost:8080' has been blocked by CORS policy
```

### ¿Qué Significa?

1. **GraphiQL está intentando cargar recursos desde Internet** (`unpkg.com`)
2. **El navegador bloquea estos recursos** por política CORS
3. **Sin estos recursos, GraphiQL no puede inicializarse**

---

## 🤔 ¿Por Qué Pasa Esto?

### Problema Principal

La librería `graphiql-spring-boot-starter` **debería** servir los recursos localmente, pero parece que:

1. **No está configurada correctamente**, O
2. **Hay un conflicto** con otra librería, O  
3. **La versión no es compatible** con Spring Boot 3.3.0, O
4. **Falta alguna configuración** que el otro proyecto tiene

---

## 🔍 Diferencias Clave que Veo

### Proyecto que Funciona (Según Imágenes)

**Estructura:**
```
resources/
  graphql/
    base.graphqls          ← Archivo BASE separado
    alumno.graphqls        ← Archivos por feature
    centro.graphqls
    ...
```

**Controllers:**
```
features/
  alumno/
    controller/
      AlumnoGraphQLController.java  ← En carpeta controller
```

### Tu Proyecto Actual

**Estructura:**
```
resources/
  graphql/
    schema.graphqls        ← TODO en un archivo
```

**Resolvers:**
```
graphql/
  resolver/
    MatriculaResolver.java  ← En carpeta resolver
```

---

## 💡 Posibles Soluciones

### Solución 1: Revisar si la Dependencia Está Cargando Correctamente

**Verifica en los logs al iniciar:**
```
¿Aparece algo como "GraphiQL servlet mapped to..."?
```

Si NO aparece, la dependencia no se está cargando.

### Solución 2: Deshabilitar GraphiQL Embebido y Usar Solo la Externa

**En `application.properties`:**
```properties
# Deshabilitar el GraphiQL embebido de Spring Boot
spring.graphql.graphiql.enabled=false

# Usar solo la librería externa
graphiql.enabled=true
graphiql.mapping=/graphiql
graphiql.endpoint.graphql=/graphql
```

### Solución 3: Verificar Versión de la Dependencia

**El otro proyecto podría usar una versión diferente:**

```xml
<!-- Prueba con esta versión si la actual no funciona -->
<dependency>
    <groupId>com.graphql-java-kickstart</groupId>
    <artifactId>graphiql-spring-boot-starter</artifactId>
    <version>11.0.0</version>  <!-- O la versión que usa el otro proyecto -->
</dependency>
```

### Solución 4: Servir GraphiQL desde Static Resources

Si la librería no sirve los recursos, podemos servir GraphiQL manualmente desde `static/`.

---

## 🎯 Lo que Necesito Saber del Otro Proyecto

### Información Crítica (Prioridad ALTA):

1. **Versión exacta de Spring Boot** en el `pom.xml`
2. **Configuración completa de GraphQL** en `application.properties` o `application.yml`
3. **Contenido del archivo `base.graphqls`**
4. **¿Aparecen logs de GraphiQL al iniciar?** (líneas con "GraphiQL")

### Información Útil:

5. Versión exacta de `graphiql-spring-boot-starter`
6. Contenido de un GraphQL Controller ejemplo
7. SecurityConfig completo

---

## 🚀 Solución Temporal: Usar Postman

Mientras resolvemos esto, puedes usar **Postman** que funciona perfectamente:

**Ver:** `docs/dia-4/EJEMPLO-POSTMAN-GRAPHQL.md`

---

## 📋 Checklist de Diagnóstico

Para identificar el problema, ejecuta estos pasos:

### Paso 1: Verificar que la Dependencia se Carga

1. Inicia la aplicación
2. Busca en los logs líneas como:
   - "GraphiQL"
   - "graphiql"
   - "servlet"
3. ¿Aparece algo relacionado?

### Paso 2: Verificar Endpoints

1. Accede a: `http://localhost:8080/graphiql`
2. Abre DevTools (F12) → Network
3. ¿Qué requests se hacen?
4. ¿Cuáles fallan?

### Paso 3: Verificar que GraphQL Funciona

1. Prueba el endpoint directamente:
   ```bash
   curl -X POST http://localhost:8080/graphql \
     -H "Content-Type: application/json" \
     -d '{"query": "{ __schema { types { name } } }"}'
   ```
2. ¿Funciona?

Si GraphQL funciona pero GraphiQL no, el problema es solo con la interfaz.

---

## 🔧 Acción Inmediata

**Mientras tanto, prueba esto:**

1. **Deshabilitar GraphiQL embebido completamente:**
   ```properties
   spring.graphql.graphiql.enabled=false
   ```

2. **Solo usar la librería externa:**
   ```properties
   graphiql.enabled=true
   graphiql.mapping=/graphiql
   graphiql.endpoint.graphql=/graphql
   ```

3. **Reinicia la aplicación completamente**

4. **Limpia caché del navegador** (Ctrl + Shift + R)

5. **Intenta acceder a:** `http://localhost:8080/graphiql`

---

**Una vez que tengas la información del otro proyecto, podré darte la solución exacta.**

