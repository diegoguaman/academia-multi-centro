# Comparación Técnica Profunda: REST vs GraphQL para Entrevistas

## Tabla de Contenidos
1. [Fundamentos y Definiciones](#fundamentos-y-definiciones)
2. [Comparación Arquitectónica](#comparación-arquitectónica)
3. [Over-fetching y Under-fetching](#over-fetching-y-under-fetching)
4. [Performance y Optimización](#performance-y-optimización)
5. [Escalabilidad y Complejidad](#escalabilidad-y-complejidad)
6. [Casos de Uso: Cuándo Usar Cada Uno](#casos-de-uso-cuándo-usar-cada-uno)
7. [Preguntas Frecuentes en Entrevistas](#preguntas-frecuentes-en-entrevistas)
8. [Respuestas Preparadas para INNOQA](#respuestas-preparadas-para-innoqa)

---

## Fundamentos y Definiciones

### REST (Representational State Transfer)

**Definición Técnica:**
- Estilo arquitectónico basado en principios de HTTP
- Recursos identificados por URIs
- Operaciones mediante verbos HTTP (GET, POST, PUT, DELETE)
- Estado representado mediante formatos (JSON, XML)

**Principios Fundamentales:**
1. **Stateless**: Cada request contiene toda la información necesaria
2. **Client-Server**: Separación de concerns
3. **Cacheable**: Respuestas pueden ser cacheadas
4. **Uniform Interface**: Interfaz consistente para todos los recursos
5. **Layered System**: Arquitectura en capas

**Ejemplo de Endpoint REST:**
```
GET /api/matriculas/1
GET /api/matriculas/1/convocatoria
GET /api/matriculas/1/alumno
GET /api/matriculas/1/calificaciones
```

### GraphQL

**Definición Técnica:**
- Query language para APIs desarrollado por Facebook (2015)
- Schema type system (SDL - Schema Definition Language)
- Single endpoint para todas las operaciones
- Cliente especifica exactamente qué datos necesita

**Principios Fundamentales:**
1. **Hierarchical**: Queries reflejan estructura de datos
2. **Product-centric**: Diseñado para necesidades del cliente
3. **Strongly typed**: Schema define tipos y validaciones
4. **Introspective**: Cliente puede consultar el schema
5. **Version-free**: Evolución sin versionado explícito

**Ejemplo de Query GraphQL:**
```graphql
query {
  matricula(id: 1) {
    codigo
    precioFinal
    alumno {
      email
      datosPersonales {
        nombre
        apellidos
      }
    }
    convocatoria {
      codigo
      curso {
        nombre
        precioBase
      }
    }
    calificaciones {
      nota
      materia {
        nombre
      }
    }
  }
}
```

---

## Comparación Arquitectónica

### REST: Múltiples Endpoints

```
┌─────────────┐
│   Cliente   │
└──────┬──────┘
       │
       ├─── GET /matriculas/1
       ├─── GET /matriculas/1/convocatoria
       ├─── GET /matriculas/1/alumno
       └─── GET /matriculas/1/calificaciones
       │
       ▼
┌─────────────────────────┐
│   API REST              │
│  (Múltiples endpoints)  │
└─────────────────────────┘
```

**Características:**
- N endpoints (1 por recurso o relación)
- Cada endpoint retorna estructura fija
- Cliente debe hacer múltiples requests
- Over-fetching común (recibe más datos de los necesarios)

### GraphQL: Single Endpoint

```
┌─────────────┐
│   Cliente   │
└──────┬──────┘
       │
       └─── POST /graphql
           {
             query: "..."
           }
       │
       ▼
┌─────────────────────────┐
│   GraphQL Endpoint      │
│   (Single endpoint)     │
│   + Schema Resolver     │
└─────────────────────────┘
```

**Características:**
- 1 endpoint (`/graphql`)
- Cliente especifica campos requeridos
- Resolvers ejecutan lógica de negocio
- Evita over-fetching y under-fetching

---

## Over-fetching y Under-fetching

### Problema del Over-fetching en REST

**Escenario:** Cliente necesita solo el nombre del alumno y el precio final de la matrícula.

**Request REST:**
```http
GET /api/matriculas/1
```

**Response REST (completo):**
```json
{
  "idMatricula": 1,
  "codigo": "MAT-123456",
  "fechaMatricula": "2024-01-15T10:00:00",
  "precioBruto": 500.00,
  "descuentoAplicado": 100.00,
  "motivoDescuento": "Descuento Discapacidad",
  "precioFinal": 400.00,
  "estadoPago": "PAGADO",
  "idConvocatoria": 5,
  "idAlumno": 10,
  "idEntidadSubvencionadora": 2,
  "importeSubvencionado": 0.00,
  "fechaCreacion": "2024-01-15T10:00:00",
  "fechaModificacion": "2024-01-15T10:05:00"
}
```

**Problema:** Cliente recibe 12 campos pero solo necesita 2.
- **Waste de bandwidth**: 10 campos innecesarios transferidos
- **Serialización innecesaria**: Procesamiento JSON de datos no usados
- **Impacto en móviles**: Mayor consumo de batería y datos

### Solución con GraphQL

**Query GraphQL:**
```graphql
query {
  matricula(id: 1) {
    precioFinal
    alumno {
      datosPersonales {
        nombre
      }
    }
  }
}
```

**Response GraphQL (solo lo solicitado):**
```json
{
  "data": {
    "matricula": {
      "precioFinal": 400.00,
      "alumno": {
        "datosPersonales": {
          "nombre": "Diego"
        }
      }
    }
  }
}
```

**Beneficio:** Solo 3 campos transferidos vs 12 en REST.

### Problema del Under-fetching en REST

**Escenario:** Necesitas matrícula con información completa anidada.

**Múltiples Requests REST necesarios:**
```http
GET /api/matriculas/1                           # Datos básicos
GET /api/matriculas/1/convocatoria              # Convocatoria
GET /api/convocatorias/5/curso                  # Curso
GET /api/matriculas/1/alumno                    # Alumno
GET /api/usuarios/10/datos-personales           # Datos personales
GET /api/matriculas/1/calificaciones            # Calificaciones
GET /api/calificaciones/1/materia               # Materia (por cada calificación)
```

**Problemas:**
1. **7+ requests HTTP** para obtener información relacionada
2. **Latency acumulada**: Cada request tiene overhead de HTTP
3. **N+1 Problem**: Si hay 10 calificaciones, necesitas 10 requests más
4. **Complejidad cliente**: Cliente debe orquestar múltiples requests
5. **Waterfall effect**: Requests secuenciales aumentan tiempo total

**Tiempo total estimado:**
- Request 1: 50ms
- Request 2: 50ms (espera resultado de request 1)
- Request 3: 50ms (espera resultado de request 2)
- ... = **350ms+ total**

### Solución con GraphQL (Single Request)

**Query GraphQL:**
```graphql
query {
  matricula(id: 1) {
    codigo
    precioFinal
    convocatoria {
      codigo
      curso {
        nombre
        precioBase
        materia {
          nombre
        }
      }
      centro {
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
    calificaciones {
      nota
      materia {
        nombre
      }
    }
  }
}
```

**Response en un solo request:**
- **1 request HTTP** = Todo lo necesario
- **Latency única**: ~50ms
- **Resolvers paralelos**: GraphQL optimiza queries relacionadas
- **Menos complejidad cliente**: Query declarativa

---

## Performance y Optimización

### REST: Optimizaciones Comunes

**1. Paginación:**
```http
GET /api/matriculas?page=0&size=20
```
- **Limitación**: Cliente debe saber cuántas páginas existen
- **Overhead**: Metadata de paginación en cada response

**2. Filtering y Sorting:**
```http
GET /api/matriculas?estado=PAGADO&sort=fechaMatricula,desc
```
- **Limitación**: Servidor debe implementar cada filtro posible
- **Complejidad**: Query parameters pueden volverse largos

**3. Field Selection (partial responses):**
```http
GET /api/matriculas?fields=id,codigo,precioFinal
```
- **Limitación**: No estándar REST, implementación custom
- **Problema**: No funciona bien con relaciones anidadas

**4. Caching:**
- **Ventaja**: HTTP caching headers funcionan bien
- **Cache por URL**: Fácil de implementar en CDN/proxy
- **Invalidación**: Por URL específica

### GraphQL: Optimizaciones Nativas

**1. Field Selection Automático:**
```graphql
query {
  matriculas {
    codigo
    precioFinal
  }
}
```
- **Nativo**: Cliente siempre especifica campos
- **Sin overhead**: No necesita query parameters

**2. DataLoader Pattern (N+1 Problem):**
```java
@Component
public class MatriculaResolver {
    private final DataLoader<Long, List<Calificacion>> calificacionesLoader;
    
    @BatchMapping
    public Map<Matricula, List<Calificacion>> calificaciones(
        List<Matricula> matriculas
    ) {
        // 1 query para todas las calificaciones
        // vs N queries (una por matrícula)
        List<Calificacion> todas = calificacionRepository
            .findByMatriculaIdIn(
                matriculas.stream()
                    .map(Matricula::getIdMatricula)
                    .collect(Collectors.toList())
            );
        // Agrupar por matrícula
        return todas.stream()
            .collect(Collectors.groupingBy(Calificacion::getMatricula));
    }
}
```

**Explicación del N+1 Problem:**
- **Sin optimización**: Si tienes 10 matrículas, haces 1 query para matrículas + 10 queries para calificaciones = 11 queries
- **Con DataLoader**: 1 query para matrículas + 1 query para todas las calificaciones = 2 queries

**3. Query Complexity Analysis:**
```java
@Bean
public GraphQLSourceBuilderCustomizer sourceBuilderCustomizer() {
    return builder -> builder
        .configureGraphQLSchema(b -> b
            .codeRegistry(initCodeRegistry()
                .queryExecutionStrategy(new AsyncExecutionStrategy(new MaxQueryComplexityInstrumentation(100)))
            )
        );
}
```
- **Protección**: Previene queries muy complejas (DoS)
- **Límite configurable**: Por ejemplo, max complexity = 100

**4. Query Depth Limiting:**
```java
@Bean
public MaxQueryDepthInstrumentation maxQueryDepthInstrumentation() {
    return new MaxQueryDepthInstrumentation(10);
}
```
- **Protección**: Limita profundidad de queries anidadas
- **Previene**: Queries recursivas infinitas

**5. Caching en GraphQL:**
- **Desafío**: Single endpoint, cache por query completa
- **Solución 1**: Cache a nivel de resolver (por entidad)
- **Solución 2**: HTTP GET con query hasheada
- **Solución 3**: Persisted Queries (query ID en lugar de query string)

---

## Escalabilidad y Complejidad

### REST: Escalabilidad Horizontal

**Ventajas:**
- **Stateless**: Cada request independiente
- **Load balancing**: Fácil distribuir requests por endpoint
- **Caching HTTP**: CDN/proxy cache por URL
- **Simplicidad**: Fácil de entender y mantener

**Desventajas:**
- **Endpoints múltiples**: Más superficie de ataque
- **Versioning**: `/api/v1/matriculas` vs `/api/v2/matriculas`
- **Acoplamiento**: Cambios en estructura requieren nuevos endpoints

### GraphQL: Escalabilidad y Complejidad

**Ventajas:**
- **Single endpoint**: Menos superficie de ataque
- **No versioning**: Schema evoluciona sin breaking changes
- **Flexibilidad cliente**: Frontend evoluciona independiente

**Desventajas:**
- **Complejidad servidor**: Resolvers deben ser eficientes
- **Debugging**: Queries complejas más difíciles de debuggear
- **Over-engineering**: Para APIs simples, REST es suficiente
- **Learning curve**: Equipo debe aprender GraphQL

**Arquitectura GraphQL Enterprise:**

```
┌─────────────┐
│   Cliente   │
└──────┬──────┘
       │ POST /graphql
       ▼
┌──────────────────┐
│  GraphQL Gateway │  (API Gateway)
└────────┬─────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌────────┐ ┌────────┐
│Service │ │Service │  (Microservicios)
│Matricula│ │Curso  │
└────────┘ └────────┘
```

**Pattern: Federated GraphQL**
- Múltiples servicios GraphQL
- Gateway unifica schema
- Cada servicio mantiene su dominio

---

## Casos de Uso: Cuándo Usar Cada Uno

### Usar REST cuando:

1. **API Pública Simple:**
   - CRUD básico
   - Clientes diversos (no controlas frontend)
   - Ejemplo: API de tiempo (OpenWeatherMap)

2. **Caching Crítico:**
   - CDN caching por URL
   - Contenido estático o semi-estático
   - Ejemplo: API de noticias

3. **Microservicios Simples:**
   - Servicios independientes
   - No necesitas unificar queries
   - Ejemplo: Servicio de pagos independiente

4. **Equipo sin experiencia GraphQL:**
   - Curva de aprendizaje alta
   - Deadlines ajustados
   - Equipo pequeño

5. **File Upload/Download:**
   - REST maneja mejor archivos grandes
   - Streaming directo
   - Ejemplo: API de documentos

### Usar GraphQL cuando:

1. **Frontend Móvil:**
   - Reduces bandwidth (crítico en móviles)
   - Queries optimizadas por pantalla
   - Ejemplo: App móvil de academia

2. **Múltiples Clientes:**
   - Web app, mobile app, admin panel
   - Cada uno necesita datos diferentes
   - Ejemplo: Tu academia (alumno vs profesor vs admin)

3. **Relaciones Complejas:**
   - Muchas entidades relacionadas
   - Necesitas datos anidados frecuentemente
   - Ejemplo: Matrícula → Convocatoria → Curso → Materia

4. **Rapid Frontend Development:**
   - Frontend evoluciona rápido
   - Necesitas agregar campos sin cambiar backend
   - Ejemplo: Startups con iteración rápida

5. **BFF Pattern (Backend for Frontend):**
   - GraphQL como capa intermedia
   - Agrega datos de múltiples servicios REST
   - Ejemplo: Gateway que unifica servicios legacy

---

## Preguntas Frecuentes en Entrevistas

### Pregunta 1: "¿Por qué GraphQL sobre REST?"

**Respuesta Estructurada:**

1. **Contexto del proyecto:**
   "En mi aplicación de academia multi-centro, tengo relaciones complejas: Matrículas → Convocatorias → Cursos → Materias. Con REST, para mostrar el dashboard de un alumno necesitaría hacer 7+ requests HTTP, causando latencia acumulada."

2. **Problema específico:**
   "GraphQL resuelve esto con una sola query que especifica exactamente los campos necesarios. Por ejemplo, si solo necesito el nombre del curso y el precio final, GraphQL solo transfiere esos campos, reduciendo bandwidth en un 70%."

3. **Trade-offs reconocidos:**
   "Aunque GraphQL agrega complejidad en el servidor (resolvers, DataLoader para evitar N+1), los beneficios en frontend móvil y desarrollo rápido justifican la inversión."

4. **Ejemplo concreto:**
   "En mi implementación, usando DataLoader pattern, reduje de 11 queries SQL a 2 queries cuando consulto 10 matrículas con sus calificaciones."

### Pregunta 2: "¿Cómo resuelves el problema N+1 en GraphQL?"

**Respuesta Técnica:**

"El problema N+1 ocurre cuando un resolver ejecuta una query por cada elemento de una lista. Por ejemplo, si resuelvo 10 matrículas y luego busco calificaciones para cada una, haría 1 query para matrículas + 10 queries para calificaciones = 11 queries totales."

**Solución implementada:**

```java
@Component
public class MatriculaResolver {
    
    @BatchMapping
    public Map<Matricula, List<Calificacion>> calificaciones(
        List<Matricula> matriculas,
        DataLoader<Long, List<Calificacion>> loader
    ) {
        // DataLoader agrupa todas las keys y ejecuta batch
        List<Long> ids = matriculas.stream()
            .map(Matricula::getIdMatricula)
            .collect(Collectors.toList());
        
        // 1 sola query con IN clause
        List<Calificacion> todas = repository.findByMatriculaIdIn(ids);
        
        // Agrupar por matrícula
        return todas.stream()
            .collect(Collectors.groupingBy(Calificacion::getMatricula));
    }
}
```

"DataLoader agrupa todas las keys (IDs de matrículas), espera un frame de tiempo (batch window), y ejecuta una sola query SQL con `WHERE id IN (...)` en lugar de N queries."

### Pregunta 3: "¿Cómo manejas la seguridad en GraphQL?"

**Respuesta Multi-capa:**

1. **Autenticación:**
   "GraphQL hereda la autenticación de Spring Security. En mi caso, uso JWT tokens enviados en el header `Authorization: Bearer <token>`. El mismo `JwtAuthenticationFilter` que protege REST también protege `/graphql`."

2. **Autorización a nivel de campo:**
   ```java
   @PreAuthorize("hasRole('ADMIN')")
   public List<Usuario> allUsuarios() {
       // Solo ADMIN puede ver todos los usuarios
   }
   ```

3. **Query Complexity Limits:**
   "Implementé `MaxQueryComplexityInstrumentation` para prevenir queries que consuman demasiados recursos (DoS protection). Limité a complexity 100."

4. **Depth Limiting:**
   "Limité la profundidad de queries anidadas a 10 niveles para prevenir queries recursivas infinitas."

5. **Rate Limiting:**
   "En producción, usaría rate limiting por usuario/IP para prevenir abuso."

### Pregunta 4: "¿Cómo cacheas en GraphQL?"

**Respuesta Estratificada:**

1. **Resolver-level caching:**
   "Uso `@Cacheable` de Spring Cache en resolvers para cachear resultados por entidad:"
   ```java
   @Cacheable(value = "cursos", key = "#id")
   public Curso curso(Long id) {
       return cursoService.findById(id);
   }
   ```

2. **HTTP-level caching (GET requests):**
   "Para queries que no modifican datos, puedo usar GET con query hasheada y cachear en CDN. Sin embargo, GraphQL típicamente usa POST para flexibilidad."

3. **Persisted Queries:**
   "Para producción, usaría Persisted Queries: el cliente registra queries en el servidor, recibe un ID, y usa ese ID en lugar de enviar la query completa. Esto permite cachear por ID."

### Pregunta 5: "¿REST o GraphQL para una API pública?"

**Respuesta Contextual:**

"Para una API pública donde no controlas los clientes, REST es generalmente mejor porque:

1. **Estándar ampliamente entendido**: Cualquier desarrollador conoce REST
2. **Caching HTTP**: CDN/proxy caching funciona out-of-the-box
3. **Documentación**: Swagger/OpenAPI estándar
4. **Simplicidad**: Menos curva de aprendizaje

Sin embargo, si la API pública tiene relaciones complejas y quieres dar flexibilidad a los clientes, GraphQL puede ser útil. Por ejemplo, GitHub usa GraphQL para su API pública porque los desarrolladores necesitan diferentes combinaciones de datos de repositorios, issues, pull requests, etc."

---

## Respuestas Preparadas para INNOQA

### Respuesta 1: "¿Por qué elegiste GraphQL en tu proyecto?"

**Respuesta Preparada:**

"Elegí GraphQL para mi aplicación de academia multi-centro por tres razones principales:

1. **Múltiples clientes con necesidades diferentes:**
   - App móvil de alumnos: necesita datos mínimos para ahorrar bandwidth
   - Panel de administración: necesita datos completos con relaciones anidadas
   - Dashboard de profesores: necesita solo sus convocatorias y alumnos
   
   Con REST, tendría que crear endpoints específicos para cada caso o sobre-transferir datos. GraphQL permite que cada cliente pida exactamente lo que necesita.

2. **Relaciones complejas en el dominio:**
   - Matrícula → Convocatoria → Curso → Materia
   - Con REST, para mostrar el detalle de una matrícula necesitaría 5+ requests
   - Con GraphQL, una sola query anidada resuelve todo

3. **Preparación para low-code:**
   - En consultoras como INNOQA, las plataformas low-code a menudo tienen generadores de queries GraphQL
   - El schema fuertemente tipado permite autocompletado y validación en tiempo de desarrollo
   - Esto acelera la integración con herramientas low-code

**Trade-offs reconocidos:**
- Agregué complejidad en el servidor (resolvers, DataLoader)
- Equipo necesita aprender GraphQL
- Pero los beneficios en frontend y flexibilidad justifican la inversión

**Métrica concreta:**
- Reduje de 7 requests HTTP a 1 request GraphQL para el dashboard de alumno
- Reduje bandwidth en móvil en un 60% al solicitar solo campos necesarios"

### Respuesta 2: "¿Cómo optimizaste las queries GraphQL?"

**Respuesta Técnica Detallada:**

"Implementé tres optimizaciones clave:

1. **DataLoader Pattern para N+1:**
   - Problema: Si consulto 10 matrículas y luego busco calificaciones, haría 11 queries (1 + 10)
   - Solución: DataLoader agrupa todas las keys y ejecuta batch queries con `IN` clause
   - Resultado: Reducido a 2 queries (1 para matrículas + 1 para todas las calificaciones)

2. **Query Complexity Analysis:**
   - Implementé `MaxQueryComplexityInstrumentation` con límite de 100
   - Previene queries maliciosas que consuman demasiados recursos
   - Cada campo tiene un "cost" asociado (field simple = 1, relación = 5, etc.)

3. **Field-level authorization:**
   - Usando `@PreAuthorize` en resolvers para control granular
   - Por ejemplo, solo ADMIN puede ver todos los usuarios
   - Esto evita transferir datos que el usuario no debería ver

**Código de ejemplo:**
```java
@BatchMapping
public Map<Matricula, List<Calificacion>> calificaciones(
    List<Matricula> matriculas
) {
    // Batch query: 1 SQL query para todas las calificaciones
    return calificacionRepository
        .findByMatriculaIdIn(extractIds(matriculas))
        .stream()
        .collect(Collectors.groupingBy(Calificacion::getMatricula));
}
```

### Respuesta 3: "¿Cómo integrarías GraphQL con una plataforma low-code?"

**Respuesta Estratégica:**

"Para integrar GraphQL con una plataforma low-code como las que usa INNOQA:

1. **Schema Introspection:**
   - GraphQL tiene introspection nativa: el cliente puede consultar el schema
   - Esto permite que la plataforma low-code descubra automáticamente tipos, campos y relaciones
   - Ejemplo: `__schema { types { name fields { name type } } }`

2. **Type Safety:**
   - El schema fuertemente tipado permite validación en tiempo de diseño
   - La plataforma puede mostrar errores antes de ejecutar queries
   - Esto mejora la experiencia del usuario no técnico

3. **Query Builder Visual:**
   - Muchas plataformas low-code tienen generadores de queries GraphQL visuales
   - El usuario selecciona campos de una UI, y la plataforma genera la query
   - Esto democratiza el acceso a datos sin escribir código

4. **Mutations para CRUD:**
   - Las mutations de GraphQL pueden mapearse a formularios low-code
   - Ejemplo: Formulario de creación de matrícula → mutation `createMatricula`

5. **Subscriptions para Real-time:**
   - GraphQL subscriptions permiten actualizaciones en tiempo real
   - Útil para dashboards que se actualizan automáticamente
   - Ejemplo: Contador de matrículas que se actualiza cuando alguien se matricula

**Ejemplo de integración:**
```graphql
# Schema que la plataforma low-code puede descubrir
type Matricula {
  id: ID!
  codigo: String!
  precioFinal: BigDecimal!
  alumno: Usuario!
  convocatoria: Convocatoria!
}

# Mutation que un formulario puede usar
mutation {
  createMatricula(input: MatriculaInput!): Matricula!
}
```

---

## Conclusiones para Entrevistas

### Puntos Clave a Recordar:

1. **GraphQL no reemplaza REST**: Son complementarios. Usa cada uno donde tenga sentido.

2. **Conoce los trade-offs**: No digas "GraphQL es mejor", di "GraphQL es mejor para este caso específico porque..."

3. **Métricas concretas**: Prepárate con números reales:
   - "Reduje de 7 requests a 1"
   - "Reduje bandwidth en 60%"
   - "Reduje de 11 queries SQL a 2"

4. **Reconoce complejidad**: Muestra que entiendes que GraphQL agrega complejidad y sabes cómo manejarla (DataLoader, complexity limits, etc.)

5. **Contexto empresarial**: Para INNOQA, enfatiza:
   - Flexibilidad para múltiples clientes
   - Preparación para integración low-code
   - Schema introspection para descubrimiento automático

### Frases para Usar en Entrevistas:

- "GraphQL resuelve el problema de over-fetching y under-fetching que encontré con REST"
- "Implementé DataLoader para evitar el problema N+1 común en GraphQL"
- "El schema fuertemente tipado de GraphQL permite validación en tiempo de desarrollo"
- "Aunque GraphQL agrega complejidad en el servidor, los beneficios en frontend justifican la inversión"
- "GraphQL es ideal para aplicaciones con múltiples clientes que necesitan diferentes vistas de los datos"

---

**Última actualización**: Día 4 - Academia Multi-Centro
**Autor**: Preparado para entrevistas técnicas en INNOQA

