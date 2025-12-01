# Día 2: Arquitectura Spring Boot - Documentación Completa

## Índice de Documentación

Esta carpeta contiene documentación técnica exhaustiva sobre el Día 2 del proyecto Academia Multi-Centro, enfocada en arquitectura por capas, tecnologías modernas de Java y mejores prácticas empresariales.

### 📚 Documentos Técnicos

1. **[01-arquitectura-por-capas.md](./01-arquitectura-por-capas.md)**
   - Fundamentos de arquitectura por capas
   - Comparación con otras arquitecturas (Hexagonal, Microservicios)
   - Estructura de capas en Spring Boot
   - Principios SOLID aplicados
   - Preguntas de entrevista técnica

2. **[02-lombok-y-capa-dominio.md](./02-lombok-y-capa-dominio.md)**
   - Introducción a Lombok
   - Anotaciones principales (@Data, @Builder, @RequiredArgsConstructor, etc.)
   - Lombok en entidades JPA
   - Consideraciones especiales (relaciones, inmutabilidad)
   - Mejores prácticas y troubleshooting

3. **[03-repositorios-spring-data-jpa.md](./03-repositorios-spring-data-jpa.md)**
   - Fundamentos de Spring Data JPA
   - Query Methods automáticos
   - Queries personalizadas con @Query
   - Paginación y ordenamiento
   - Especificaciones (Specifications)
   - Optimización de performance

4. **[04-servicios-logica-negocio.md](./04-servicios-logica-negocio.md)**
   - Estructura de servicios completos
   - Principios para servicios robustos
   - Gestión de transacciones
   - Validación de reglas de negocio
   - Manejo de errores descriptivo
   - Patrones comunes

5. **[05-dtos-request-response.md](./05-dtos-request-response.md)**
   - ¿Por qué DTOs?
   - Request DTOs vs Response DTOs
   - Validaciones en DTOs
   - ¿Qué lógica va en cada tipo?
   - Ventajas (seguridad, performance, versionado)
   - Mejores prácticas

6. **[06-mapstruct-mapeo-automatico.md](./06-mapstruct-mapeo-automatico.md)**
   - ¿Por qué MapStruct?
   - Comparación con ModelMapper (reflection vs code generation)
   - Configuración
   - Mappers básicos y avanzados
   - Mapeo de campos anidados
   - Expresiones y @AfterMapping
   - Debugging y troubleshooting

7. **[07-testing-cobertura-95.md](./07-testing-cobertura-95.md)**
   - ¿Por qué ~95% de cobertura?
   - Tipos de tests (Unit, Integration, E2E)
   - Estructura Arrange-Act-Assert
   - Ejemplos completos de tests
   - Configuración de JaCoCo
   - Estrategias para alcanzar alta cobertura
   - Mejores prácticas

8. **[08-linkedin-post-dia-2.md](./08-linkedin-post-dia-2.md)**
   - Posts para LinkedIn (múltiples versiones)
   - Post principal
   - Post técnico alternativo
   - Post para enseñar a otros
   - Post para destacar en entrevistas
   - Tips para publicar

## 🎯 Objetivos del Día 2

### Completados ✅

- [x] POM con dependencias (Lombok, MapStruct, JaCoCo)
- [x] Configuración de aplicación (Supabase)
- [x] 13 Entidades del dominio con JPA y Lombok
- [x] 13 Repositorios con Spring Data JPA
- [x] 26 DTOs (Request/Response) con validaciones
- [x] 11 Mappers MapStruct
- [x] 8 Servicios con lógica de negocio completa
- [x] Tests unitarios para servicios principales
- [x] Tests de integración para repositorios
- [x] ~95% de cobertura de código

## 📊 Estadísticas del Proyecto

- **Entidades**: 13
- **Repositorios**: 13
- **DTOs**: 26 (13 Request + 13 Response)
- **Mappers**: 11
- **Servicios**: 8
- **Tests Unitarios**: 30+
- **Tests Integración**: 10+
- **Cobertura**: ~95%

## 🛠️ Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 4.0.0**
- **Spring Data JPA**
- **Lombok 1.18.x**
- **MapStruct 1.5.5.Final**
- **Jakarta Validation**
- **JUnit 5**
- **Mockito**
- **JaCoCo 0.8.11**
- **PostgreSQL** (Supabase)

## 📖 Cómo Usar Esta Documentación

### Para Preparar Entrevistas

1. Lee **[01-arquitectura-por-capas.md](./01-arquitectura-por-capas.md)** para entender la arquitectura
2. Revisa **[02-lombok-y-capa-dominio.md](./02-lombok-y-capa-dominio.md)** para preguntas sobre Lombok
3. Estudia **[03-repositorios-spring-data-jpa.md](./03-repositorios-spring-data-jpa.md)** para Spring Data JPA
4. Practica con **[07-testing-cobertura-95.md](./07-testing-cobertura-95.md)** para preguntas de testing

### Para Enseñar a Otros

1. Usa los ejemplos de código de cada documento
2. Explica los "¿Por qué?" no solo los "¿Cómo?"
3. Compara con alternativas (ej: MapStruct vs ModelMapper)
4. Muestra las mejores prácticas y cuándo aplicarlas

### Para Posts en LinkedIn

1. Usa **[08-linkedin-post-dia-2.md](./08-linkedin-post-dia-2.md)**
2. Adapta el tono según tu audiencia
3. Incluye hashtags relevantes
4. Comparte aprendizajes específicos

## 🔑 Conceptos Clave

### Arquitectura por Capas

```
Controllers (Futuro)
    ↓
Services (Lógica de Negocio)
    ↓
Repositories (Acceso a Datos)
    ↓
Entities (Modelo de Dominio)
```

### Flujo de Datos

```
RequestDto → Service → Repository → Entity
                                    ↓
ResponseDto ← Mapper ← Entity ← Repository
```

### Principios Aplicados

- **Single Responsibility**: Cada clase tiene una responsabilidad
- **Dependency Inversion**: Depender de abstracciones (interfaces)
- **Separation of Concerns**: Cada capa tiene un propósito claro
- **DRY (Don't Repeat Yourself)**: Lombok y MapStruct reducen repetición

## 💡 Preguntas Frecuentes

### ¿Por qué arquitectura por capas y no hexagonal?

Arquitectura por capas es más pragmática para este proyecto. Es suficiente para aplicaciones monolíticas que serán containerizadas. Hexagonal sería overkill para el scope actual.

### ¿Por qué Lombok y no código tradicional?

Lombok reduce 70-90% del código boilerplate sin sacrificar funcionalidad. El código generado es validado en compile-time y es más mantenible.

### ¿Por qué MapStruct y no ModelMapper?

MapStruct genera código Java puro en compile-time, resultando en:
- 100x más rápido que reflection
- Type-safe (errores en compile-time)
- Código visible y debuggable

### ¿Cómo alcanzar ~95% de cobertura?

1. Tests unitarios para todos los métodos públicos de servicios
2. Tests de integración para repositorios
3. Testear todos los caminos (branches)
4. Excluir código generado (Lombok, MapStruct)
5. Usar JaCoCo para verificar cobertura

## 🚀 Próximos Pasos

- **Día 3**: Spring Security + JWT
- **Día 4**: GraphQL vs REST
- **Día 5**: Dockerización
- **Día 6**: Kubernetes Local
- **Día 7**: Despliegue en GKE

## 📝 Notas Importantes

- Esta documentación es técnica y detallada, diseñada para prepararte para entrevistas técnicas avanzadas
- Todos los ejemplos de código están basados en el código real del proyecto
- Las mejores prácticas mencionadas son aplicables a proyectos empresariales reales
- La cobertura de ~95% es un objetivo alcanzable con disciplina y buenas prácticas

## 🤝 Contribuciones

Si encuentras errores o quieres agregar contenido, por favor:
1. Revisa el código del proyecto para mantener consistencia
2. Asegúrate de que los ejemplos compilen
3. Mantén el tono técnico y profesional

---

**Última actualización**: Día 2 completado
**Autor**: Documentación técnica para proyecto Academia Multi-Centro
**Propósito**: Preparación para entrevistas técnicas y enseñanza

