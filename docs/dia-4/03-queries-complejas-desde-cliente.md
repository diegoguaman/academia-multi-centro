# Guía de Queries Complejas desde el Cliente

## Tabla de Contenidos
1. [Fundamentos de Queries GraphQL](#fundamentos-de-queries-graphql)
2. [Queries Anidadas y Relaciones](#queries-anidadas-y-relaciones)
3. [Filtrado y Paginación](#filtrado-y-paginación)
4. [Variables y Fragments](#variables-y-fragments)
5. [Mutations Complejas](#mutations-complejas)
6. [Optimización de Queries](#optimización-de-queries)
7. [Ejemplos Prácticos para la Academia](#ejemplos-prácticos-para-la-academia)
8. [Librerías Cliente Recomendadas](#librerías-cliente-recomendadas)

---

## Fundamentos de Queries GraphQL

### Query Básica

```graphql
query {
  curso(id: 1) {
    nombre
    precioBase
  }
}
```

**Request HTTP:**
```http
POST /graphql
Content-Type: application/json
Authorization: Bearer <jwt-token>

{
  "query": "query { curso(id: 1) { nombre precioBase } }"
}
```

**Response:**
```json
{
  "data": {
    "curso": {
      "nombre": "Java Avanzado",
      "precioBase": "500.00"
    }
  }
}
```

### Múltiples Campos en una Query

```graphql
query {
  curso(id: 1) {
    nombre
    precioBase
    duracionHoras
    activo
  }
}
```

---

## Queries Anidadas y Relaciones

### Ejemplo 1: Matrícula con Relaciones Básicas

```graphql
query {
  matricula(id: 1) {
    codigo
    precioFinal
    estadoPago
    alumno {
      email
      datosPersonales {
        nombre
        apellidos
      }
    }
    convocatoria {
      codigo
      fechaInicio
      fechaFin
    }
  }
}
```

**Beneficio:** Todo en una sola request, sin over-fetching.

### Ejemplo 2: Query Profunda con Múltiples Niveles

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
        discapacidadPorcentaje
      }
    }
    convocatoria {
      codigo
      fechaInicio
      fechaFin
      curso {
        nombre
        precioBase
        materia {
          nombre
        }
        formato {
          nombre
        }
      }
      profesor {
        email
        datosPersonales {
          nombre
          apellidos
        }
      }
      centro {
        nombre
        empresa {
          nombreLegal
        }
        comunidad {
          nombre
        }
      }
    }
    calificaciones {
      nota
      fechaCreacion
    }
  }
}
```

**Nota:** Esta query es muy profunda. En producción, considera:
- Limitar profundidad (configurar `MaxQueryDepthInstrumentation`)
- Usar fragments para reutilizar campos comunes

### Ejemplo 3: Múltiples Entidades en una Query

```graphql
query {
  # Obtener matrícula
  matricula(id: 1) {
    codigo
    precioFinal
  }
  
  # Obtener cursos activos
  cursos(activo: true) {
    nombre
    precioBase
  }
  
  # Obtener convocatorias activas
  convocatorias(activo: true) {
    codigo
    fechaInicio
    curso {
      nombre
    }
  }
}
```

**Beneficio:** Puedes obtener datos de múltiples fuentes en una sola request.

---

## Filtrado y Paginación

### Filtrado Simple

```graphql
query {
  matriculas(estadoPago: PAGADO) {
    codigo
    precioFinal
  }
}
```

### Filtrado Múltiple (Implementar en el Resolver)

```graphql
query {
  convocatorias(activo: true, idCentro: 5) {
    codigo
    fechaInicio
  }
}
```

**Nota:** El schema debe soportar estos argumentos. Si no, agrégalos al resolver.

### Paginación - Opción 1: Offset-Based

```graphql
query {
  matriculas(first: 10, offset: 0) {
    codigo
    precioFinal
  }
}
```

**Implementación en Resolver:**
```java
@QueryMapping
public List<MatriculaResponseDto> matriculas(
        @Argument final Integer first,
        @Argument final Integer offset
) {
    Pageable pageable = PageRequest.of(
        offset != null ? offset : 0,
        first != null ? first : 20
    );
    return matriculaService.findAll(pageable).getContent();
}
```

### Paginación - Opción 2: Cursor-Based (Recomendado)

```graphql
query {
  matriculas(first: 10, after: "cursor123") {
    edges {
      node {
        codigo
        precioFinal
      }
      cursor
    }
    pageInfo {
      hasNextPage
      hasPreviousPage
      startCursor
      endCursor
    }
  }
}
```

**Implementación más compleja, pero mejor para grandes datasets.**

### Búsqueda con Filtros Combinados

```graphql
query {
  matriculas(
    estadoPago: PAGADO
    idAlumno: 5
  ) {
    codigo
    precioFinal
    alumno {
      email
    }
  }
}
```

---

## Variables y Fragments

### Variables

**Query con variables:**
```graphql
query GetMatricula($id: ID!, $includeCalificaciones: Boolean!) {
  matricula(id: $id) {
    codigo
    precioFinal
    calificaciones @include(if: $includeCalificaciones) {
      nota
    }
  }
}
```

**Request con variables:**
```json
{
  "query": "query GetMatricula($id: ID!, $includeCalificaciones: Boolean!) { ... }",
  "variables": {
    "id": 1,
    "includeCalificaciones": true
  }
}
```

**Beneficio:** Reutilizas la misma query con diferentes valores.

### Fragments

**Definir fragment:**
```graphql
fragment MatriculaBasic on Matricula {
  codigo
  precioFinal
  estadoPago
}

fragment MatriculaFull on Matricula {
  ...MatriculaBasic
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
    }
  }
}
```

**Usar fragment:**
```graphql
query {
  matricula(id: 1) {
    ...MatriculaFull
  }
  
  matriculas {
    ...MatriculaBasic
  }
}
```

**Beneficio:** Evitas duplicación de código, mantienes queries DRY.

### Directivas Condicionales

**@include - Incluir campo si condición es true:**
```graphql
query($includeDetalles: Boolean!) {
  matricula(id: 1) {
    codigo
    precioFinal
    calificaciones @include(if: $includeDetalles) {
      nota
    }
  }
}
```

**@skip - Omitir campo si condición es true:**
```graphql
query($skipDetalles: Boolean!) {
  matricula(id: 1) {
    codigo
    precioFinal
    calificaciones @skip(if: $skipDetalles) {
      nota
    }
  }
}
```

---

## Mutations Complejas

### Mutation Simple

```graphql
mutation {
  createMatricula(input: {
    idConvocatoria: 1
    idAlumno: 5
  }) {
    idMatricula
    codigo
    precioFinal
  }
}
```

### Mutation con Variables

```graphql
mutation CreateMatricula($input: MatriculaInput!) {
  createMatricula(input: $input) {
    idMatricula
    codigo
    precioFinal
    alumno {
      email
    }
    convocatoria {
      codigo
    }
  }
}
```

**Variables:**
```json
{
  "input": {
    "idConvocatoria": 1,
    "idAlumno": 5,
    "idEntidadSubvencionadora": 2,
    "importeSubvencionado": 100.00
  }
}
```

### Múltiples Mutations en una Request

```graphql
mutation {
  createCurso: createCurso(input: {
    nombre: "Java Avanzado"
    idMateria: 1
    idFormato: 2
    precioBase: 500.00
  }) {
    idCurso
    nombre
  }
  
  createConvocatoria: createConvocatoria(input: {
    idCurso: 1
    idProfesor: 3
    idCentro: 5
    fechaInicio: "2024-03-01"
    fechaFin: "2024-06-30"
  }) {
    idConvocatoria
    codigo
  }
}
```

**Nota:** Las mutations se ejecutan secuencialmente, no en paralelo.

---

## Optimización de Queries

### 1. Seleccionar Solo Campos Necesarios

**❌ Mal - Over-fetching:**
```graphql
query {
  matricula(id: 1) {
    idMatricula
    codigo
    fechaMatricula
    precioBruto
    descuentoAplicado
    motivoDescuento
    importeSubvencionado
    precioFinal
    estadoPago
    fechaCreacion
    fechaModificacion
    # ... muchos más campos
  }
}
```

**✅ Bien - Solo lo necesario:**
```graphql
query {
  matricula(id: 1) {
    codigo
    precioFinal
  }
}
```

**Impacto:** Reduces bandwidth en un 70-80% en móviles.

### 2. Evitar Queries Demasiado Profundas

**❌ Mal - Demasiada profundidad:**
```graphql
query {
  matricula(id: 1) {
    convocatoria {
      curso {
        materia {
          # ... más anidación innecesaria
        }
      }
    }
  }
}
```

**✅ Bien - Profundidad razonable:**
```graphql
query {
  matricula(id: 1) {
    convocatoria {
      curso {
        nombre
        precioBase
      }
    }
  }
}
```

**Impacto:** Reduce complejidad de query y tiempo de ejecución.

### 3. Usar Aliases para Múltiples Queries del Mismo Tipo

```graphql
query {
  matricula1: matricula(id: 1) {
    codigo
    precioFinal
  }
  
  matricula2: matricula(id: 2) {
    codigo
    precioFinal
  }
}
```

### 4. Cachear Queries (Persisted Queries)

**En desarrollo:**
```graphql
query { cursos { nombre } }
```

**En producción (Persisted Query):**
```json
{
  "id": "abc123",
  "extensions": {
    "persistedQuery": {
      "version": 1,
      "sha256Hash": "hash_del_query"
    }
  }
}
```

**Beneficio:** Reduce tamaño de request, permite cachear por ID.

---

## Ejemplos Prácticos para la Academia

### Dashboard de Alumno

```graphql
query DashboardAlumno($idAlumno: ID!) {
  matriculas(idAlumno: $idAlumno) {
    codigo
    precioFinal
    estadoPago
    convocatoria {
      codigo
      fechaInicio
      fechaFin
      curso {
        nombre
        duracionHoras
      }
      centro {
        nombre
      }
    }
    calificaciones {
      nota
      fechaCreacion
    }
  }
}
```

### Dashboard de Profesor

```graphql
query DashboardProfesor($idProfesor: ID!) {
  convocatorias(idProfesor: $idProfesor) {
    codigo
    fechaInicio
    fechaFin
    curso {
      nombre
      materia {
        nombre
      }
    }
    centro {
      nombre
    }
    matriculas {
      codigo
      alumno {
        email
        datosPersonales {
          nombre
          apellidos
        }
      }
      calificaciones {
        nota
      }
    }
  }
}
```

### Dashboard de Administrador

```graphql
query DashboardAdmin {
  # Estadísticas de matrículas
  matriculas {
    codigo
    precioFinal
    estadoPago
    fechaMatricula
  }
  
  # Cursos activos
  cursos(activo: true) {
    nombre
    precioBase
    convocatorias {
      codigo
      matriculas {
        codigo
      }
    }
  }
  
  # Convocatorias activas
  convocatorias(activo: true) {
    codigo
    fechaInicio
    fechaFin
    centro {
      nombre
    }
  }
}
```

### Búsqueda Avanzada de Matrículas

```graphql
query BuscarMatriculas(
  $estadoPago: EstadoPago
  $idAlumno: ID
  $fechaDesde: DateTime
  $fechaHasta: DateTime
) {
  matriculas(
    estadoPago: $estadoPago
    idAlumno: $idAlumno
  ) {
    codigo
    precioFinal
    estadoPago
    fechaMatricula
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
      }
    }
  }
}
```

**Filtrar por fecha en el cliente (o implementar en el servidor):**

```javascript
const matriculasFiltradas = data.matriculas.filter(matricula => {
  const fecha = new Date(matricula.fechaMatricula);
  if (fechaDesde && fecha < fechaDesde) return false;
  if (fechaHasta && fecha > fechaHasta) return false;
  return true;
});
```

### Crear Matrícula Completa

```graphql
mutation CrearMatricula($input: MatriculaInput!) {
  createMatricula(input: $input) {
    idMatricula
    codigo
    precioFinal
    descuentoAplicado
    motivoDescuento
    estadoPago
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
      centro {
        nombre
      }
    }
  }
}
```

---

## Librerías Cliente Recomendadas

### JavaScript/TypeScript

#### Apollo Client (React/Vue/Angular)

**Instalación:**
```bash
npm install @apollo/client graphql
```

**Uso:**
```javascript
import { ApolloClient, InMemoryCache, gql } from '@apollo/client';

const client = new ApolloClient({
  uri: 'http://localhost:8080/graphql',
  cache: new InMemoryCache(),
  headers: {
    Authorization: `Bearer ${token}`
  }
});

const GET_MATRICULA = gql`
  query GetMatricula($id: ID!) {
    matricula(id: $id) {
      codigo
      precioFinal
      alumno {
        email
      }
    }
  }
`;

// En un componente React
const { loading, error, data } = useQuery(GET_MATRICULA, {
  variables: { id: 1 }
});
```

**Ventajas:**
- Cache automático
- Loading states
- Error handling
- React hooks integrados
- TypeScript support

#### urql (Ligero, React)

```javascript
import { Client, cacheExchange, fetchExchange } from 'urql';

const client = new Client({
  url: 'http://localhost:8080/graphql',
  exchanges: [cacheExchange, fetchExchange],
  fetchOptions: () => ({
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
});
```

**Ventajas:**
- Más ligero que Apollo
- Bueno para proyectos pequeños
- React hooks incluidos

#### graphql-request (Vanilla JS/TypeScript)

```javascript
import { GraphQLClient } from 'graphql-request';

const client = new GraphQLClient('http://localhost:8080/graphql', {
  headers: {
    Authorization: `Bearer ${token}`
  }
});

const query = `
  query {
    matricula(id: 1) {
      codigo
      precioFinal
    }
  }
`;

const data = await client.request(query);
```

**Ventajas:**
- Simple, sin dependencias pesadas
- Bueno para proyectos vanilla JS
- TypeScript support

### Java (Android/Backend)

#### Apollo Android

```kotlin
val apolloClient = ApolloClient.builder()
    .serverUrl("http://localhost:8080/graphql")
    .addHttpHeader("Authorization", "Bearer $token")
    .build()

val query = GetMatriculaQuery(1)
val response = apolloClient.query(query).execute()
```

### Python

#### gql

```python
from gql import gql, Client
from gql.transport.aiohttp import AIOHTTPTransport

transport = AIOHTTPTransport(
    url="http://localhost:8080/graphql",
    headers={"Authorization": f"Bearer {token}"}
)

client = Client(transport=transport, fetch_schema_from_transport=True)

query = gql("""
    query {
      matricula(id: 1) {
        codigo
        precioFinal
      }
    }
""")

result = client.execute(query)
```

---

## Mejores Prácticas para Queries desde el Cliente

1. **Usar Variables:** Siempre usa variables en lugar de valores hardcodeados
2. **Fragments:** Extrae campos comunes a fragments para reutilizar
3. **Aliases:** Usa aliases cuando necesitas múltiples queries del mismo tipo
4. **Seleccionar Solo Campos Necesarios:** No pidas campos que no vas a usar
5. **Manejar Errores:** Siempre maneja errores de GraphQL (network, validation, execution)
6. **Cachear Resultados:** Usa librerías con cache automático (Apollo Client)
7. **Loading States:** Muestra estados de carga mientras se ejecuta la query
8. **Optimistic Updates:** En mutations, actualiza UI inmediatamente (optimistic update)
9. **Paginación:** Usa cursor-based pagination para grandes datasets
10. **TypeScript:** Si usas TypeScript, genera tipos desde el schema GraphQL

---

## Ejemplo Completo: Cliente React con Apollo

```typescript
// queries/matricula.queries.ts
import { gql } from '@apollo/client';

export const GET_MATRICULA = gql`
  query GetMatricula($id: ID!) {
    matricula(id: $id) {
      codigo
      precioFinal
      estadoPago
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
        fechaCreacion
      }
    }
  }
`;

export const CREATE_MATRICULA = gql`
  mutation CreateMatricula($input: MatriculaInput!) {
    createMatricula(input: $input) {
      idMatricula
      codigo
      precioFinal
    }
  }
`;

// components/MatriculaDetail.tsx
import { useQuery } from '@apollo/client';
import { GET_MATRICULA } from '../queries/matricula.queries';

function MatriculaDetail({ matriculaId }: { matriculaId: number }) {
  const { loading, error, data } = useQuery(GET_MATRICULA, {
    variables: { id: matriculaId },
    errorPolicy: 'all'
  });

  if (loading) return <div>Cargando...</div>;
  if (error) return <div>Error: {error.message}</div>;

  const matricula = data.matricula;

  return (
    <div>
      <h2>Matrícula: {matricula.codigo}</h2>
      <p>Precio Final: {matricula.precioFinal}€</p>
      <p>Estado: {matricula.estadoPago}</p>
      <h3>Alumno</h3>
      <p>{matricula.alumno.datosPersonales.nombre} {matricula.alumno.datosPersonales.apellidos}</p>
      <h3>Curso</h3>
      <p>{matricula.convocatoria.curso.nombre}</p>
    </div>
  );
}
```

---

**Última actualización**: Día 4 - Academia Multi-Centro
**Próximos pasos**: Implementar cliente frontend cuando el backend esté en producción

