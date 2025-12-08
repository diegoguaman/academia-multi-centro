# Academia Manager

Backend Spring Boot for multi-campus academy management with REST auth and GraphQL domain APIs. Built for cloud deployment, JWT-secured, and ready to containerize.

## Tech stack
- Java 17, Spring Boot 3, Spring Security (JWT stateless)
- Spring Data JPA (PostgreSQL), DTO mapping with MapStruct
- Lombok for boilerplate reduction
- GraphQL (queries/mutations) + REST for auth bootstrap
- Docker-ready; CI/CD via GitHub Actions (workflow in `.github/workflows/ci-cd.yml`)
- Patterns: layered architecture (controller/resolver → service → repository), DTOs, validators

## Modules & capabilities
- Authentication: `/api/auth/login`, `/api/auth/register` (public) return JWT + user info.
- Authorization: RBAC roles `ADMIN`, `PROFESOR`, `ALUMNO`, `ADMINISTRATIVO`; stateless sessions.
- GraphQL API: CRUD for courses, convocatorias, matriculas, usuarios, materias, formatos, centros, empresas. Schema in `src/main/resources/graphql/schema.graphqls`.
- Observability/ops: Actuator exposed (permitted) for health/readiness.

## Security highlights
- Stateless JWT filter chain, custom `AuthenticationProvider`, password hashing with `BCrypt`.
- CORS enabled for local frontends (`http://localhost:3000`, `:8080`, `:5173`).

## Project layout
- `src/main/java/com/academy/academymanager/controller` → REST (Auth)
- `src/main/java/com/academy/academymanager/graphql/resolver` → GraphQL resolvers
- `src/main/java/com/academy/academymanager/service` → business logic
- `src/main/java/com/academy/academymanager/repository` → JPA repositories
- `src/main/resources/graphql/schema.graphqls` → GraphQL schema
- `docs/` → day-by-day guides (JWT, GraphQL, Docker, CI/CD) for deeper reading

## Quick start (local)
Prereqs: JDK 17+, Maven, PostgreSQL (or use Docker), Node (for frontend consumers).

```bash
git clone https://github.com/<your-user>/academymanager.git
cd academymanager
cp .env.example .env        # fill secrets locally, do not commit
mvn clean spring-boot:run
```

REST auth:
- POST `http://localhost:8080/api/auth/login`
- POST `http://localhost:8080/api/auth/register`

GraphQL:
- Endpoint `http://localhost:8080/graphql`
- Playground `http://localhost:8080/graphiql`

## Environment variables
- Template: `.env.example`. Fill DB credentials, JWT secret, etc. at runtime only.
- Secrets are **not** committed; share via vault/secret manager in real deployments.

## Docker
Build image:
```bash
docker build -t academymanager:latest .
```
Run with env file:
```bash
docker run --env-file .env -p 8080:8080 academymanager:latest
```
For k8s/cloud, mount secrets as env vars or secrets manager (see `docs/dia-5` and `docs/dia-6` for Docker/K8s notes).

## Testing
```bash
mvn test
```


