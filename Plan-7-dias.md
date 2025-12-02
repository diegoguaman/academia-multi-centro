Commit 1: ""
Commit 2: ""
Commit 3: ""
Commit 4: ""
Commit 5: ""
Commit 6: ""
Commit 7: ""
Commit 8: ""
Commit 9: ""








Plan Detallado para Desarrollar y Desplegar la Aplicación de Academia Multi-Centro en 7 Días
A continuación, te presento un plan estructurado y detallado para completar el proyecto en una semana, basado en los puntos que has descrito. La aplicación será una plataforma para gestionar una academia multi-centro, con funcionalidades como gestión de usuarios (alumnos, profesores, admins), cursos, convocatorias, matrículas, descuentos automáticos por discapacidad, facturación y subvenciones. Usaremos Java 17+ con Spring Boot 3.x como framework principal, PostgreSQL como base de datos, y herramientas modernas para contenedores y orquestación.
El plan está diseñado para ser progresivo: cada día construye sobre el anterior, con hitos verificables. Para cada día, incluyo:

Objetivos principales: Qué lograrás.
Pasos detallados: Guía paso a paso, con explicaciones técnicas basadas en documentación oficial de Java (de Oracle y Spring), incluyendo cómo funcionan líneas de código, conexiones entre clases/interfaces/archivos, manejo de condicionales/bucles/try-catch.
Explicaciones para entrevistas y posts en LinkedIn: Texto técnico detallado que puedes copiar/adaptar para tu candidatura en INNOQA (una consultora low-code, donde enfatizarás tu dominio en Java, Docker y DB relacionales). Esto te ayudará a demostrar que has superado las debilidades de tu entrevista anterior, mostrando un proyecto escalable y profesional.
Tiempo estimado: Asumiendo 6-8 horas/día, con breaks.
Requisitos previos: Herramientas a instalar (gratuitas).

Requisitos generales para la semana:

Instala: JDK 17 (de oracle.com/downloads), Maven (maven.apache.org), IntelliJ IDEA Community (jetbrains.com/idea), PostgreSQL (postgresql.org), PGAdmin (pgadmin.org), Docker Desktop (docker.com), kubectl (kubernetes.io/docs/tasks/tools), Helm (helm.sh), Google Cloud SDK (cloud.google.com/sdk) y una cuenta gratuita en Google Cloud (con crédito inicial para GKE).
Repositorio: Crea un repo en GitHub para versionar todo. Usa branches como feature/db-setup, feature/spring-core, etc.
Documentación: Para cada punto, crea un README.md en el repo con explicaciones. Al final, deploy en GKE y publica un post en LinkedIn sobre el proyecto completo.

Ahora, el plan por días.
Día 1: Fundamentos de Datos (SQL, PostgreSQL, Supabase)
Objetivos principales: Configurar la DB local con PostgreSQL, ejecutar los scripts proporcionados, analizar su estructura, identificar mejoras para escalabilidad empresarial, y preparar vistas en Supabase (o local mientras haces el curso). Esto demuestra tu dominio en relaciones DB, que fue un punto débil en tu entrevista anterior.
Pasos detallados:

Instala PostgreSQL y PGAdmin: Descarga PostgreSQL 16.x y PGAdmin 8.x. Crea una DB local llamada academia_db con usuario postgres y password segura. En PGAdmin, conecta y ejecuta el script SQL proporcionado entero (copia-pega en la Query Tool).
Explicación técnica: El script usa DROP SCHEMA IF EXISTS public CASCADE; para limpiar (condicional IF EXISTS evita errores si no existe, como en docs de PostgreSQL). Crea tablas con PKs SERIAL (autoincremental, equivalente a int en Java con @GeneratedValue(strategy = GenerationType.IDENTITY)). Relaciones FK como REFERENCES empresa(id_empresa) aseguran integridad referencial (en Java, mapeado con @ManyToOne). El trigger trg_calcular_precio_matricula se activa BEFORE INSERT en matricula, llamando a la función PL/pgSQL aplicar_descuento_discapacidad(). Dentro de la función: Bucles no hay, pero condicional IF v_porcentaje_discapacidad >= 33.0 THEN aplica lógica de negocio (descuento 20%). Try-catch no aplica en SQL, pero en Java equivalente sería try { ... } catch (SQLException e) { log.error("Error en trigger simulado"); }. Conexión con archivos: Este script se integra con Spring via JPA/Hibernate en Día 2 (entidades como Matricula mapearán columnas).

Ejecuta inserts y verifica trigger: Corre los INSERTs. Query: SELECT * FROM matricula; para ver descuentos automáticos (precio_final calculado via columna generada).
Explicación técnica: La función usa SELECT INTO para queries (como asignación en Java). El trigger asegura atomicidad (todo o nada, similar a @Transactional en Spring). Para escalabilidad, agrega TRY ... EXCEPTION en funciones PL/pgSQL para manejo de errores: BEGIN ... EXCEPTION WHEN OTHERS THEN RAISE NOTICE 'Error: %', SQLERRM; END;.

Analiza la estructura DB proporcionada: Es suficiente para un MVP personal (cubriendo entidades core: usuarios, cursos, matrículas, con triggers para lógica). Pero para una DB empresarial robusta/escalable, falta:
Auditoría: Agrega columnas created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP, created_by INT REFERENCES usuario(id_usuario). Trigger para update: CREATE TRIGGER trg_update_timestamp BEFORE UPDATE ON tabla FOR EACH ROW EXECUTE FUNCTION update_timestamp(); (función: NEW.updated_at = CURRENT_TIMESTAMP;).
Particionamiento: Para tablas grandes como matricula, usa particionamiento por rango (e.g., por fecha_matricula): CREATE TABLE matricula (...) PARTITION BY RANGE (fecha_matricula); (docs PostgreSQL: partitioning).
Replicación y Sharding: Para escalabilidad horizontal, configura replicas master-slave (pg_basebackup). Usa Citus extension para sharding.
Seguridad: Roles RBAC: CREATE ROLE app_user WITH LOGIN PASSWORD 'secure'; GRANT SELECT, INSERT ON SCHEMA public TO app_user;. Encripta datos sensibles (pgcrypto para password_hash ya está, pero expande a PII).
Backup/Recovery: Scripts para pg_dump. Indexing avanzado: GIN para text search en descripcion.
Migraciones: Usa Flyway/Liquibase en Java para versionar schemas (integra en Día 2).
Vistas materializadas: Para reports: CREATE MATERIALIZED VIEW vw_resumen_matriculas AS SELECT ...; REFRESH MATERIALIZED VIEW vw_resumen_matriculas;.
Faltas específicas: No hay logging de accesos, constraints para precios positivos (CHECK (precio_base > 0)), o soft deletes (en vez de activo BOOLEAN, usa timestamps).

Supabase (curso y vistas): Regístrate en Supabase (supabase.com), crea proyecto con PostgreSQL. Importa el script. Crea vistas: e.g., CREATE VIEW vw_matriculas_activas AS SELECT * FROM matricula WHERE estado_pago = 'PAGADO';. Haz el curso oficial de Supabase (docs.supabase.com/guides/database) mientras configuras DB local como fallback.
Explicación técnica: Supabase expone PostgreSQL via API, con auth integrada (JWT). Vistas se crean igual que en local, pero Supabase las optimiza para realtime (subscriptions via WebSockets).


Explicaciones para entrevistas y posts en LinkedIn:
"En mi proyecto de academia multi-centro, diseñé una DB PostgreSQL relacional con 15+ tablas, enfocándome en normalización 3NF para evitar redundancias (e.g., usuarios unificados en una tabla con roles CHECK constraint para integridad). Implementé triggers PL/pgSQL para lógica de negocio automática, como descuentos por discapacidad: el trigger BEFORE INSERT calcula precio_final usando condicionales IF-THEN, asegurando consistencia sin código app-side. Para escalabilidad, agregué índices compuestos (e.g., CREATE INDEX idx_matricula_alumno ON matricula(id_alumno)) que reducen query times en JOINs frecuentes, y planeo particionamiento por fecha para manejar millones de matrículas. Esto resuelve problemas de mi entrevista anterior en INNOQA, donde faltaba expertise en DB relacionales; ahora controlo FKs, triggers y optimizaciones para entornos enterprise. En LinkedIn: '¡De 0 a hero en DB! Construí una DB robusta para academias, con triggers que automatizan descuentos – ideal para low-code en consultoras como INNOQA. #PostgreSQL #SQL #DatabaseDesign'."
Tiempo estimado: 6 horas (2h instalación, 2h script/analisis, 2h Supabase/curso).
Hito: DB local corriendo, queries verificadas, README con análisis.
Día 2: Arquitectura Spring Boot (Core, Lombok, MapStruct)
Objetivos principales: Configurar proyecto Spring Boot con POM, capas (dominio, repository, service, controller), CRUD básico. Enumera arquitecturas enterprise, pros/contras, y elige una para multi-centro.
Pasos detallados:

Crea proyecto: Usa Spring Initializr (start.spring.io): Java 17, Maven, dependencias: Spring Web, Spring Data JPA, PostgreSQL Driver, Lombok, MapStruct. Descarga y abre en IntelliJ.
POM con dependencias: Agrega en pom.xml:XML<dependencies>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
    <dependency><groupId>org.postgresql</groupId><artifactId>pljava</artifactId><version>42.7.1</version><scope>runtime</scope></dependency> <!-- Driver PG -->
    <dependency><groupId>org.projectlombok</groupId><artifactId>lombok</artifactId><optional>true</optional></dependency>
    <dependency><groupId>org.mapstruct</groupId><artifactId>mapstruct</artifactId><version>1.5.5.Final</version></dependency>
    <dependency><groupId>org.mapstruct</groupId><artifactId>mapstruct-processor</artifactId><version>1.5.5.Final</version><scope>provided</scope></dependency> <!-- Para annotations -->
</dependencies>
Explicación técnica: spring-boot-starter-data-jpa incluye Hibernate para ORM (docs Spring: spring.io/projects/spring-data-jpa). Lombok reduce boilerplate con @Data, @Builder (procesado en compile-time via annotation processor). MapStruct genera mappers automáticos (e.g., interface MatriculaMapper con @Mapper, genera impl en build-time). Conexión: POM se conecta a application.yml para props como spring.datasource.url=jdbc:postgresql://localhost:5432/academia_db.


Estructura por capas:
Dominio: Paquete domain: Clases entidad como Matricula.java con @Entity, @Table(name="matricula"), @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long idMatricula;. Usa Lombok: @Data @NoArgsConstructor @AllArgsConstructor.
Repository: Paquete repository: Interface MatriculaRepository extends JpaRepository<Matricula, Long> {} (Spring genera impl CRUD automático).
Service: Paquete service: Clase MatriculaService con @Service, método public Matricula create(Matricula matricula) { try { return repository.save(matricula); } catch (DataIntegrityViolationException e) { throw new CustomException("Error integridad DB"); } }. Bucles: e.g., para listas for (Matricula m : lista) { if (m.getPrecioFinal() < 0) throw Exception; }.
Controller: Paquete controller: @RestController @RequestMapping("/matriculas") con @PostMapping public Matricula create(@RequestBody Matricula m) { return service.create(m); }.
CRUD básico: Implementa GET/POST/PUT/DELETE para Matricula, conectando a DB via application.properties: spring.jpa.hibernate.ddl-auto=validate (valida schema contra entidades).
Explicación técnica: Capas siguen MVC (Model-View-Controller, pero API REST sin View). Interfaces como JpaRepository extienden CrudRepository (docs Java: interfaces definen contratos). Condicionales en service para validaciones (e.g., if (alumno.getDiscapacidadPorcentaje() >= 33) aplicarDescuento();). Try-catch para exceptions DB (e.g., UniqueViolation). Conexión archivos: Entidades se mapean a tablas via annotations; services inyectan repos con @Autowired.

Tipos de arquitecturas enterprise, pros/contras, y elección:
Monolítica: Todo en una app (tu caso inicial). Pros: Simple deploy, transacciones fáciles. Contras: No escala bien, coupling alto. Docs Spring: spring.io/guides/gs/spring-boot.
Microservicios: Apps independientes (e.g., servicio usuarios, servicio cursos). Pros: Escalable, independiente. Contras: Complejidad en comunicación (API Gateway), distributed transactions. Usa Spring Cloud.
Hexagonal (Ports & Adapters): Core business aislado de infra (DB, UI). Pros: Testable, adaptable. Contras: Overhead inicial.
Event-Driven: Con Kafka/RabbitMQ para async. Pros: Resiliente. Contras: Complejidad debugging.
Serverless: Con AWS Lambda/Spring Cloud Function. Pros: Auto-escala. Contras: Cold starts.
Recomendada para tu app multi-centro: Hexagonal. Pros: Aísla lógica de negocio (e.g., descuentos) de DB/externos, ideal para multi-centro (adapta adapters por centro). Contras: Más código inicial, pero escalable. Implementa: Interfaces Ports (e.g., MatriculaPort para repo), Adapters (impl JPA).


Explicaciones para entrevistas y posts en LinkedIn:
"Para mi app de academia, usé Spring Boot con arquitectura hexagonal para decoupling: el core domain (entidades con Lombok para getters/setters auto-generados) se conecta via ports a adapters JPA, permitiendo swap DB sin tocar negocio. MapStruct maneja DTO-entity mapping eficientemente (genera código en compile-time, evitando reflection overhead como en ModelMapper). En entrevistas para INNOQA, explico: 'Elegí hexagonal sobre monolítica por escalabilidad en multi-centro – pros: modularidad; contras: curva aprendizaje, pero reduce debt técnico'. Esto cubre mi gap en Java. LinkedIn post: '¡Arquitectura clean en Spring Boot! De monolito a hexagonal para academias escalables. #SpringBoot #Java #CleanArchitecture'."
Tiempo estimado: 7 horas (2h setup, 3h capas/CRUD, 2h arquitecturas).
Hito: App corriendo local, CRUD tested con Postman.


Día 3: Seguridad Avanzada (Spring Security, JWT) ✅ COMPLETADO
Objetivos principales: Implementar auth con Spring Security y JWT para roles (ADMIN, PROFESOR, ALUMNO, ADMINISTRATIVO).

✅ Implementado:
- Dependencias JWT agregadas al pom.xml (jjwt-api, jjwt-impl, jjwt-jackson 0.12.5)
- JwtService: Generación y validación de tokens con HMAC-SHA256
- JwtAuthenticationFilter: Filtro en security chain para validar tokens
- SecurityConfig: Configuración con SecurityFilterChain (Spring Boot 3+)
- UserDetailsServiceImpl: Integración con DB (loadUserByUsername)
- AuthController: Endpoints /api/auth/login y /api/auth/register
- AuthService: Lógica de autenticación con BCrypt
- DTOs: LoginRequest, LoginResponse, RegisterRequest
- GlobalExceptionHandler: Manejo centralizado de excepciones JWT
- BCrypt password encoder con 10 rounds
- Role-based access control (ROLE_ADMIN, ROLE_PROFESOR, etc.)
- Stateless authentication (SessionCreationPolicy.STATELESS)

✅ CI/CD con GitHub Actions:
- Pipeline completo con 8 jobs (build, test, quality, security, docker, deploy)
- JaCoCo coverage enforcement (95% mínimo)
- OWASP Dependency Check (CVSS >= 7 falla build)
- SonarCloud integration (code quality gates)
- Docker multi-stage builds optimizados
- Artifact caching (Maven dependencies)
- Parallel execution de tests
- Deployment strategies documentadas (rolling, blue-green, canary)
- Automated rollback si health checks fallan

✅ Documentación:
- docs/dia-3/01-seguridad-jwt-spring-security.md (guía técnica completa)
- docs/dia-3/02-cicd-github-actions-profesional.md (pipeline profesional)
- docs/dia-3/03-linkedin-post-dia-3.md (posts para redes sociales)
- docs/dia-3/README.md (resumen ejecutivo)

📊 Tiempo real: 8 horas
🎯 Nivel alcanzado: Senior (Security + DevOps)

Explicaciones para entrevistas y posts en LinkedIn:
"Integré Spring Security con JWT para auth role-based: filters validan tokens (parsed via JJWT library, con try-catch para exceptions como ExpiredJwt). Para multi-centro, roles controlan accesos (e.g., PROFESOR solo ve sus convocatorias). En INNOQA, destaco: 'JWT es stateless, ideal low-code – pros: escalable; contras: no revocation fácil, mitigado con short expiry'. #SpringSecurity #JWT #JavaSecurity."
Tiempo estimado: 6 horas.
Hito: Endpoints protegidos, JWT funcional.


Día 4: API Moderna (GraphQL vs REST)
Objetivos principales: Implementa GraphQL, explica por qué mejor que REST tradicional.
Pasos detallados:

Agrega dependencias: com.graphql-java:graphql-spring-boot-starter, com.graphql-java:graphql-java-tools.
Explicación técnica: GraphQL usa schema SDL (e.g., type Query { matriculas: [Matricula] }). Resolvers: Interface @Component con @GraphQlQueryResolver public List<Matricula> getMatriculas() { return service.findAll(); }.

Graph vs REST: GraphQL mejor para tu app: Under/over-fetching evitado (cliente pide exacto fields). Pros: Flexible queries, real-time con subscriptions. Contras: Complejidad caching, n+1 problem (mitiga con DataLoader). REST tradicional: Simple, pero ineficiente para relaciones nested (e.g., matricula con alumno y curso requiere multiple calls).
Implementa: Schema en schema.graphqls, resolvers para CRUD. Manejo errores: @GraphQlExceptionHandler.

Explicaciones para entrevistas y posts en LinkedIn:
"Opté GraphQL sobre REST para queries eficientes en academia: resolvers mapean a services, resolviendo n+1 con batching. En interviews: 'GraphQL reduce bandwidth – e.g., query { matricula(id:1) { precioFinal alumno { nombre } } } vs multiple REST endpoints'. #GraphQL #SpringBoot #API."
Tiempo estimado: 6 horas.
Hito: GraphQL endpoint testable con GraphiQL.


Día 5: Containerización (Docker & Compose)
Objetivos principales: Dockeriza app y DB, corre con Compose.
Pasos detallados:

Dockerfile:dockerfileFROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]Build: mvn package; docker build -t academia-app .
Compose: docker-compose.yml con services: app (puerto 8080), postgres (volumenes para data).
Explicación técnica: Compose orquesta contenedores (docs Docker: docker.com/docs). Volúmenes persisten DB: volumes: - db-data:/var/lib/postgresql/data. Try-catch no aplica, pero en Java maneja conexiones fallidas.

Corre local: docker-compose up, verifica en contenedor con docker exec.

Explicaciones para entrevistas y posts en LinkedIn:
"Dockericé con multi-stage build para optimizar imagenes: FROM builder copia artifacts. Compose maneja redes (app conecta a postgres via hostname). Para INNOQA: 'Resuelve mi gap en Docker – pros: portabilidad; contras: overhead, pero esencial para K8s'. #Docker #Containerization."
Tiempo estimado: 5 horas.
Hito: App en Docker, Compose running.
Día 6: Orquestación Local (K3d/Minikube)
Objetivos principales: Setup K3d (ligero), deploy app local.
Pasos detallados:

Instala K3d: curl -s https://raw.githubusercontent.com/k3d-io/k3d/main/install.sh | bash. Crea cluster: k3d cluster create mycluster.
Deploy: YAML manifests: Deployment para app, Service LoadBalancer, Postgres StatefulSet.
Explicación técnica: K3d es K8s lightweight (docs: k3d.io). Pods corren containers, scaled con replicas. Bucles en scripts Helm para installs.

Test: kubectl apply -f manifests/, port-forward.

Explicaciones para entrevistas y posts en LinkedIn:
"K3d para testing local K8s: clusters multi-node simulan prod. En INNOQA: 'Aprendí orquestación – pros: auto-healing; contras: complejidad, pero clave para escalabilidad'. #Kubernetes #K3d."
Tiempo estimado: 6 horas.
Hito: App en K3d.
Día 7: La Nube (Google GKE, Secretos y Despliegue)
Objetivos principales: Crea cluster GKE, push images, maneja secrets.
Pasos detallados:

GKE setup: gcloud container clusters create academia-cluster --zone us-central1-a. Push image a GCR: gcloud builds submit --tag gcr.io/project-id/academia-app.
Secrets: kubectl create secret generic db-secret --from-literal=password=secure. En Deployment: envFrom secretRef.
Explicación técnica: Secrets son base64-encoded (docs K8s: kubernetes.io/docs/concepts). Seguridad: RBAC policies, encrypt-at-rest.

Deploy: Aplica YAML a GKE, expone con LoadBalancer. Maneja seguridad: Network Policies.
Publica post: Resume proyecto en LinkedIn.

Explicaciones para entrevistas y posts en LinkedIn:
"Desplegué en GKE: clusters auto-scaled, secrets para DB creds (mounted as env vars). Para INNOQA: 'Domino K8s ahora – pros: resiliencia; contras: costo, pero optimizado con autoscaling'. Proyecto completo: de DB a cloud. #GKE #Kubernetes #DevOps."
Tiempo estimado: 7 horas.
Hito: App live en GKE, candidatura lista con repo/link.
Sigue este plan estrictamente para completarlo en 7 días. Si surge issues, ajusta pero mantén momentum. ¡Éxito en INNOQA!