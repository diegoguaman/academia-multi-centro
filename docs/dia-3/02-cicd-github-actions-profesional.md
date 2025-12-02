# CI/CD Profesional con GitHub Actions

## De Junior a Senior: Guía Completa de CI/CD

### Tabla de Contenidos
1. [Fundamentos de CI/CD](#fundamentos)
2. [GitHub Actions: Arquitectura](#github-actions)
3. [Pipeline Completo Explicado](#pipeline-completo)
4. [Docker: Build Optimization](#docker-optimization)
5. [Deployment Strategies](#deployment-strategies)
6. [Security en CI/CD](#security-cicd)
7. [Monitoring y Rollback](#monitoring-rollback)

---

## Fundamentos de CI/CD

### ¿Por qué CI/CD es Crítico?

#### Sin CI/CD (Manual):
```
Developer → Commit código → [Esperar semanas] →
→ QA manual → Bugs encontrados → Fix → Volver a QA →
→ Aprobación manual → Deployment manual (viernes 10pm) →
→ Errores en producción → Rollback manual → Fin de semana arruinado
```

**Problemas:**
- ❌ Feedback lento (semanas entre commit y deployment)
- ❌ Errores descubiertos tarde (costoso arreglar)
- ❌ Deployment manual = errores humanos
- ❌ Rollback manual = downtime prolongado
- ❌ "Funciona en mi máquina" sin reproducibilidad

#### Con CI/CD (Automatizado):
```
Developer → Commit → [5 min: Build + Test] → Feedback inmediato →
→ [PR aprobado] → Merge → [10 min: Build + Test + Security Scan] →
→ [Deploy automático a staging] → [Tests E2E] →
→ [Aprobación 1-click] → [Deploy a producción con zero downtime] →
→ [Monitoring automático] → [Rollback automático si errores]
```

**Beneficios:**
- ✅ Feedback en minutos (no semanas)
- ✅ Bugs detectados inmediatamente (barato arreglar)
- ✅ Deployment consistente y reproducible
- ✅ Rollback automático en segundos
- ✅ "Funciona en CI = funciona en producción"

### Continuous Integration vs Continuous Delivery vs Continuous Deployment

#### Continuous Integration (CI)
**Definición**: Integrar código frecuentemente con validación automática

**Prácticas:**
- Commits pequeños y frecuentes (múltiples por día)
- Build automático en cada push
- Tests automáticos (unit + integration)
- Feedback inmediato si build falla

**Pipeline CI básico:**
```yaml
on: [push, pull_request]

jobs:
  ci:
    runs-on: ubuntu-latest
    steps:
      - checkout código
      - setup entorno
      - build
      - run tests
      - check coverage
```

#### Continuous Delivery (CD)
**Definición**: Código siempre listo para deploy, pero deployment manual

**Prácticas:**
- Pipeline CI completo ✅
- Build de artefactos deployables (JAR, Docker image)
- Deploy automático a staging
- Tests E2E en staging
- **Deploy a producción con aprobación manual** 👤

**Pipeline CD:**
```yaml
deploy-staging:
  runs-on: ubuntu-latest
  steps:
    - deploy to staging
    - run smoke tests

deploy-production:
  needs: deploy-staging
  environment: production  # Requiere aprobación manual
  steps:
    - deploy to production
```

#### Continuous Deployment (CD)
**Definición**: Deployment totalmente automático a producción

**Prácticas:**
- Pipeline CI completo ✅
- Todos los tests pasan ✅
- **Deploy automático a producción sin intervención humana**

**Pipeline:**
```yaml
deploy-production:
  needs: [build, test, security-scan]
  if: github.ref == 'refs/heads/main'
  steps:
    - deploy to production
    - smoke tests
    - if tests fail → rollback automático
```

**¿Cuál usar?**
- **Startup/MVP**: Continuous Delivery (aprobación manual da confianza)
- **Empresa establecida con tests robustos**: Continuous Deployment
- **Tu proyecto academia**: Continuous Delivery → migrar a Deployment

---

## GitHub Actions: Arquitectura

### Conceptos Fundamentales

#### 1. Workflows
**Definición**: Proceso automatizado definido en YAML

**Ubicación**: `.github/workflows/*.yml`

**Estructura:**
```yaml
name: CI/CD Pipeline  # Nombre visible en GitHub UI

on:  # Triggers (cuándo ejecutar)
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]
  schedule:
    - cron: '0 2 * * *'  # 2am diario
  workflow_dispatch:  # Manual trigger

env:  # Variables globales
  JAVA_VERSION: '21'

jobs:  # Tareas a ejecutar
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4
```

#### 2. Jobs
**Definición**: Conjunto de steps que se ejecutan en el mismo runner

**Características:**
- Ejecutan en paralelo por default
- Pueden tener dependencias (`needs`)
- Cada job corre en VM/container separado

**Ejemplo:**
```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps: [...]
  
  test:
    needs: build  # Ejecuta DESPUÉS de build
    runs-on: ubuntu-latest
    steps: [...]
  
  deploy:
    needs: [build, test]  # AND lógico
    runs-on: ubuntu-latest
    steps: [...]
```

**Ejecución visual:**
```
T=0s:  build (inicia)
       lint (inicia en paralelo)
       
T=60s: build (completa)
       test (inicia, esperaba build)
       
T=80s: lint (completa)
       
T=120s: test (completa)
        deploy (inicia, esperaba build + test)
        
T=180s: deploy (completa)
```

#### 3. Steps
**Definición**: Comandos individuales o acciones reutilizables

**Tipos:**

**A) Run (comando shell):**
```yaml
- name: Build with Maven
  run: mvn clean package -B

- name: Multi-line script
  run: |
    echo "Starting build"
    mvn clean package
    echo "Build complete"
```

**B) Uses (acción reutilizable):**
```yaml
- name: Checkout code
  uses: actions/checkout@v4  # user/repo@version
  with:  # Parámetros de la acción
    fetch-depth: 0
```

#### 4. Runners
**Definición**: Máquinas que ejecutan workflows

**GitHub-hosted runners:**
- `ubuntu-latest`: Ubuntu 22.04, 2 cores, 7GB RAM
- `windows-latest`: Windows Server 2022
- `macos-latest`: macOS 12

**Self-hosted runners:**
- Tu propia infraestructura
- Para workloads pesados o acceso a recursos internos
- Configuración:
  ```bash
  # En tu servidor
  ./config.sh --url https://github.com/tu-org/tu-repo --token TOKEN
  ./run.sh
  ```

#### 5. Artifacts
**Definición**: Archivos generados en un job, compartidos entre jobs

**Upload:**
```yaml
- name: Build JAR
  run: mvn package

- name: Upload artifact
  uses: actions/upload-artifact@v4
  with:
    name: application-jar
    path: target/*.jar
    retention-days: 7
```

**Download:**
```yaml
- name: Download artifact
  uses: actions/download-artifact@v4
  with:
    name: application-jar
    path: target/
```

#### 6. Secrets
**Definición**: Variables encriptadas (passwords, tokens)

**Configuración:**
1. GitHub UI: Settings → Secrets → New repository secret
2. Nombre: `DB_PASSWORD`, Valor: `mysecretpass`

**Uso:**
```yaml
- name: Deploy
  env:
    DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
  run: echo "Password: $DB_PASSWORD"  # Solo visible en logs como ***
```

**Tipos de secrets:**
- Repository secrets: Específicos de un repo
- Organization secrets: Compartidos entre repos
- Environment secrets: Específicos de un environment (production, staging)

#### 7. Environments
**Definición**: Deployment targets con protección rules

**Configuración:**
```yaml
deploy-production:
  environment:
    name: production
    url: https://academia.example.com
  steps: [...]
```

**Protection rules (en GitHub UI):**
- Required reviewers: 2 aprobaciones para deploy
- Wait timer: Esperar 5 minutos antes de deploy
- Deployment branches: Solo main puede deployar a prod

---

## Pipeline Completo Explicado

### Stage 1: Build and Test

```yaml
build-and-test:
  name: Build and Test
  runs-on: ubuntu-latest
  
  steps:
    - name: Checkout code
      uses: actions/checkout@v4
      with:
        fetch-depth: 0  # Full git history
      # Por qué: SonarCloud necesita historial para analizar cambios
      # Default: fetch-depth: 1 (shallow clone, solo último commit)
    
    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'  # Eclipse Temurin (adoptium)
        cache: 'maven'
      # Automáticamente cachea ~/.m2/repository
      # Cache key: hash de pom.xml
      # Si pom.xml no cambia → restaura cache (ahorra 2-5 min)
    
    - name: Cache Maven packages
      uses: actions/cache@v4
      with:
        path: ~/.m2/repository
        key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
        restore-keys: |
          ${{ runner.os }}-maven-
      # Ejemplo de key: Linux-maven-d8f3a9b2c1e4...
      # Si pom.xml cambia → nuevo key → rebuild cache
      # restore-keys: fallback si key exacto no encontrado
    
    - name: Build with Maven
      run: mvn clean compile -B -V
      # -B: Batch mode (no output colorizado, no prompts)
      # -V: Show Maven version
      # clean: Borrar target/ (build limpio)
      # compile: Compilar código fuente (no tests aún)
    
    - name: Run unit tests
      run: mvn test -B
      # Ejecuta tests con @Test
      # Genera reportes en target/surefire-reports/
    
    - name: Generate JaCoCo report
      run: mvn jacoco:report
      # Genera HTML report en target/site/jacoco/
      # Analiza qué líneas fueron ejecutadas por tests
    
    - name: Check coverage threshold
      run: mvn jacoco:check
      continue-on-error: false
      # Verifica coverage >= 95% (configurado en pom.xml)
      # continue-on-error: false → fallar workflow si coverage bajo
    
    - name: Upload test results
      if: always()  # Ejecutar incluso si tests fallan
      uses: actions/upload-artifact@v4
      with:
        name: test-results
        path: |
          target/surefire-reports/
          target/site/jacoco/
      # Permite descargar reportes desde GitHub UI
    
    - name: Archive JAR
      uses: actions/upload-artifact@v4
      with:
        name: application-jar
        path: target/*.jar
        retention-days: 7
      # JAR disponible para jobs posteriores (deploy)
```

**Optimizaciones avanzadas:**

**1. Parallel test execution:**
```yaml
test:
  strategy:
    matrix:
      test-suite: [unit, integration, e2e]
  steps:
    - run: mvn test -Dtest.suite=${{ matrix.test-suite }}
# Ejecuta 3 jobs en paralelo → reduce tiempo total
```

**2. Conditional execution:**
```yaml
- name: Run integration tests
  if: github.event_name == 'push' && github.ref == 'refs/heads/main'
  run: mvn verify -P integration-tests
# Solo en push a main (no en PRs, ahorra tiempo)
```

**3. Fail fast:**
```yaml
test:
  strategy:
    fail-fast: true  # Cancelar otros jobs si uno falla
  steps: [...]
# Ahorra minutos de CI si build falla temprano
```

### Stage 2: Code Quality

```yaml
code-quality:
  name: SonarCloud Analysis
  needs: build-and-test
  runs-on: ubuntu-latest
  
  steps:
    - name: Checkout code
      uses: actions/checkout@v4
      with:
        fetch-depth: 0
    
    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'
        cache: 'maven'
    
    - name: Cache SonarCloud packages
      uses: actions/cache@v4
      with:
        path: ~/.sonar/cache
        key: ${{ runner.os }}-sonar
        restore-keys: ${{ runner.os }}-sonar
    
    - name: SonarCloud Scan
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      run: |
        mvn -B verify sonar:sonar \
          -Dsonar.projectKey=academia-multi-centro \
          -Dsonar.organization=tu-org \
          -Dsonar.host.url=https://sonarcloud.io \
          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
      # GITHUB_TOKEN: Automáticamente disponible (comentar en PRs)
      # SONAR_TOKEN: Configurar en sonarcloud.io → My Account → Security
```

**Qué analiza SonarCloud:**

**1. Code Smells:**
```java
// Code Smell: Variable no usada
public void processUser(Usuario usuario) {
    String email = usuario.getEmail();  // SMELL: email no usado
    return;
}

// Fix:
public void processUser(Usuario usuario) {
    // Código limpio sin variables innecesarias
    return;
}
```

**2. Bugs:**
```java
// Bug: NullPointerException potencial
public String getUserName(Usuario usuario) {
    return usuario.getDatosPersonales().getNombre();  // NPE si null
}

// Fix:
public String getUserName(Usuario usuario) {
    return Optional.ofNullable(usuario.getDatosPersonales())
        .map(DatosPersonales::getNombre)
        .orElse("Unknown");
}
```

**3. Security Vulnerabilities:**
```java
// Vulnerability: SQL Injection
public List<Usuario> searchUsers(String query) {
    return entityManager.createQuery(
        "SELECT u FROM Usuario u WHERE u.email = '" + query + "'"
    ).getResultList();  // VULNERABLE
}

// Fix: Usar prepared statements
public List<Usuario> searchUsers(String query) {
    return entityManager.createQuery(
        "SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class
    ).setParameter("email", query).getResultList();
}
```

**4. Code Coverage:**
```
Coverage Report:
- Line coverage: 92% (target: 95%)
- Branch coverage: 85%
- Uncovered lines: 45 (en AuthService.java:78-82, ...)
```

**Quality Gate configuration:**
```properties
# sonar-project.properties
sonar.qualitygate.wait=true  # Fallar build si quality gate falla

# Quality gate conditions (en SonarCloud UI):
Coverage on New Code >= 95%
Maintainability Rating on New Code = A
Reliability Rating on New Code = A
Security Rating on New Code = A
Duplicated Lines on New Code <= 3%
```

### Stage 3: Security Scan

```yaml
security-scan:
  name: OWASP Dependency Check
  needs: build-and-test
  runs-on: ubuntu-latest
  
  steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'
        cache: 'maven'
    
    - name: OWASP Dependency Check
      run: |
        mvn org.owasp:dependency-check-maven:check \
          -DfailBuildOnCVSS=7 \
          -DsuppressionFiles=.owasp-suppressions.xml
      # failBuildOnCVSS=7: Fallar si vulnerabilidad >= 7 (HIGH/CRITICAL)
      # CVSS: Common Vulnerability Scoring System (0-10)
      #   0-3.9: LOW
      #   4-6.9: MEDIUM
      #   7-8.9: HIGH
      #   9-10: CRITICAL
    
    - name: Upload security report
      if: always()
      uses: actions/upload-artifact@v4
      with:
        name: dependency-check-report
        path: target/dependency-check-report.html
```

**Ejemplo de reporte:**
```
Dependency: spring-core-5.3.10.jar
CVE-2022-22965 (Spring4Shell)
  Severity: CRITICAL
  CVSS Score: 9.8
  Description: RCE via class property injection
  Solution: Upgrade to spring-core-5.3.18+
  
Dependency: log4j-core-2.14.1.jar
CVE-2021-44228 (Log4Shell)
  Severity: CRITICAL
  CVSS Score: 10.0
  Description: RCE via JNDI lookup
  Solution: Upgrade to log4j-core-2.17.0+
```

**Suppressions (falsos positivos):**
```xml
<!-- .owasp-suppressions.xml -->
<suppressions>
  <suppress>
    <cve>CVE-2021-12345</cve>
    <reason>
      False positive: Vulnerable feature not used in our application.
      We only use safe subset of library API.
    </reason>
  </suppress>
</suppressions>
```

**Alternativas de security scanning:**
- **Snyk**: `snyk test --severity-threshold=high`
- **Trivy**: `trivy fs --severity HIGH,CRITICAL .`
- **Grype**: `grype dir:.`

### Stage 4: Docker Build

```yaml
docker-build:
  name: Build and Push Docker Image
  needs: [build-and-test, code-quality, security-scan]
  if: github.event_name == 'push'
  runs-on: ubuntu-latest
  
  steps:
    - name: Checkout code
      uses: actions/checkout@v4
    
    - name: Download JAR artifact
      uses: actions/download-artifact@v4
      with:
        name: application-jar
        path: target/
    
    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v3
      # Buildx: Builder avanzado con multi-platform, cache, etc.
    
    - name: Log in to Docker Hub
      uses: docker/login-action@v3
      with:
        username: ${{ secrets.DOCKER_USERNAME }}
        password: ${{ secrets.DOCKER_PASSWORD }}
      if: github.ref == 'refs/heads/main'
    
    - name: Extract metadata
      id: meta
      uses: docker/metadata-action@v5
      with:
        images: ${{ secrets.DOCKER_USERNAME }}/academia-multi-centro
        tags: |
          type=ref,event=branch
          type=sha,prefix={{branch}}-
          type=semver,pattern={{version}}
          type=raw,value=latest,enable={{is_default_branch}}
      # Genera tags automáticamente:
      # - main → latest
      # - develop → develop
      # - commit abc123 → main-abc123
      # - tag v1.2.3 → 1.2.3
    
    - name: Build and push
      uses: docker/build-push-action@v5
      with:
        context: .
        file: ./Dockerfile
        push: ${{ github.ref == 'refs/heads/main' }}
        tags: ${{ steps.meta.outputs.tags }}
        labels: ${{ steps.meta.outputs.labels }}
        cache-from: type=gha
        cache-to: type=gha,mode=max
        build-args: |
          JAR_FILE=target/*.jar
      # Cache type=gha: GitHub Actions cache
      # mode=max: Cachear todas las layers (no solo finales)
```

**Docker cache optimization:**

**Sin cache:**
```
Step 1: FROM eclipse-temurin:21-jre-alpine  [Downloaded: 200MB, 30s]
Step 2: COPY app.jar app.jar                [Copied: 50MB, 5s]
Total: 35 segundos
```

**Con cache:**
```
Step 1: FROM eclipse-temurin:21-jre-alpine  [Cached ✓, 1s]
Step 2: COPY app.jar app.jar                [Copied: 50MB, 5s]
Total: 6 segundos (6x más rápido)
```

---

## Docker: Build Optimization

### Multi-stage Build Avanzado

```dockerfile
# Stage 1: Dependencies (rara vez cambia)
FROM eclipse-temurin:21-jdk-alpine AS dependencies
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline -B
# Layer cacheada: solo rebuild si pom.xml cambia

# Stage 2: Build (cambia frecuentemente)
FROM dependencies AS builder
COPY src ./src
RUN ./mvnw package -DskipTests -B
# Reutiliza layer de dependencies

# Stage 3: Runtime (imagen final pequeña)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Security: Non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Ownership
RUN chown spring:spring app.jar

USER spring:spring

EXPOSE 8080

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -Djava.security.egd=file:/dev/./urandom"

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**Optimización de layers:**
```dockerfile
# MALO (rebuild todo si cambia un archivo):
COPY . .
RUN mvn package

# BIEN (layers cacheadas):
COPY pom.xml .        # Layer 1 (rara vez cambia)
RUN mvn dependency:go-offline  # Layer 2 (cache!)
COPY src ./src        # Layer 3 (cambia frecuentemente)
RUN mvn package       # Layer 4 (solo rebuild si src cambia)
```

**Tamaño de imagen optimizado:**
```
❌ MAL:
FROM openjdk:21            (450MB)
COPY everything            (+100MB)
= 550MB

✅ BIEN:
FROM eclipse-temurin:21-jre-alpine  (180MB)
COPY only JAR                       (+50MB)
= 230MB (2.4x más pequeño)
```

---

## Deployment Strategies

### 1. Rolling Update (Kubernetes Default)

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: academia-app
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1        # Máximo 1 pod extra durante update
      maxUnavailable: 0  # Siempre mantener 3 pods disponibles
  template:
    spec:
      containers:
      - name: app
        image: myrepo/academia:v2
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 5
```

**Flujo visual:**
```
Tiempo  | Pods v1 | Pods v2 | Total Ready
--------|---------|---------|------------
T=0     |  3      |   0     |    3
T=10s   |  3      |   1     |    3 (v2 no ready aún)
T=30s   |  3      |   1     |    4 (v2 ready, maxSurge=1)
T=35s   |  2      |   1     |    3 (v1 pod terminado)
T=45s   |  2      |   2     |    4
T=50s   |  1      |   2     |    3
T=60s   |  1      |   3     |    4
T=65s   |  0      |   3     |    3 (completado)
```

**Ventajas:**
- ✅ Zero downtime
- ✅ Gradual (bajo riesgo)
- ✅ Automático rollback si health checks fallan

**Desventajas:**
- ❌ Dos versiones ejecutando simultáneamente (compatibilidad requerida)
- ❌ Rollback lento (reverse rolling update)

### 2. Blue-Green Deployment

```yaml
# GitHub Actions workflow
deploy-blue-green:
  steps:
    - name: Deploy Green (new version)
      run: |
        kubectl apply -f k8s/deployment-green.yaml
        kubectl wait --for=condition=available deployment/app-green
    
    - name: Run smoke tests on Green
      run: ./smoke-tests.sh http://app-green-internal:8080
    
    - name: Switch traffic to Green
      run: |
        kubectl patch service app -p '{"spec":{"selector":{"version":"green"}}}'
    
    - name: Keep Blue for 1 hour (rollback window)
      run: sleep 3600
    
    - name: Decommission Blue
      run: kubectl delete deployment app-blue
```

**Estado:**
```
Before deployment:
  Production traffic → Blue (v1) [3 pods]
  Green (no existe)

During deployment:
  Production traffic → Blue (v1) [3 pods]
  Green (v2) [3 pods] (no traffic, testing)

After switch:
  Production traffic → Green (v2) [3 pods]
  Blue (v1) [3 pods] (standby para rollback)

After decommission:
  Production traffic → Green (v2) [3 pods]
  Blue (eliminado)
```

**Ventajas:**
- ✅ Rollback instantáneo (cambiar selector de service)
- ✅ Testing en ambiente idéntico a prod
- ✅ Solo una versión en producción a la vez

**Desventajas:**
- ❌ Doble infraestructura (costoso)
- ❌ Switching puede causar sesiones perdidas (si stateful)

### 3. Canary Deployment

```yaml
# Istio VirtualService
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: academia-app
spec:
  hosts:
  - academia.example.com
  http:
  - match:
    - headers:
        canary:
          exact: "true"
    route:
    - destination:
        host: academia-app
        subset: v2
  - route:
    - destination:
        host: academia-app
        subset: v1
      weight: 90  # 90% a v1
    - destination:
        host: academia-app
        subset: v2
      weight: 10  # 10% a v2 (canary)
```

**Progressive rollout:**
```bash
# Stage 1: 10% canary
kubectl apply -f canary-10-percent.yaml

# Monitor metrics durante 30 min
watch -n 10 'kubectl top pods && curl metrics-endpoint'

# Si metrics OK: Stage 2: 50%
kubectl apply -f canary-50-percent.yaml

# Monitor 30 min más

# Si metrics OK: Stage 3: 100%
kubectl apply -f canary-100-percent.yaml
```

**Automated canary con Flagger:**
```yaml
apiVersion: flagger.app/v1beta1
kind: Canary
metadata:
  name: academia-app
spec:
  targetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: academia-app
  progressDeadlineSeconds: 60
  service:
    port: 8080
  analysis:
    interval: 1m
    threshold: 5  # 5 fallos consecutivos → rollback
    maxWeight: 50
    stepWeight: 10  # Incrementar 10% cada paso
    metrics:
    - name: request-success-rate
      thresholdRange:
        min: 99  # Éxito >= 99%
      interval: 1m
    - name: request-duration
      thresholdRange:
        max: 500  # Latencia p99 <= 500ms
      interval: 1m
```

**Ventajas:**
- ✅ Riesgo mínimo (solo 10% usuarios afectados)
- ✅ Detección temprana de issues
- ✅ Rollback automático basado en métricas

**Desventajas:**
- ❌ Complejo de configurar (necesita service mesh)
- ❌ Deployment más lento (incremental)

---

## Security en CI/CD

### 1. Secrets Management

```yaml
# ❌ NUNCA HACER:
env:
  DB_PASSWORD: "mysecretpassword123"
  JWT_SECRET: "hardcoded-secret"

# ✅ CORRECTO:
env:
  DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
  JWT_SECRET: ${{ secrets.JWT_SECRET }}
```

**Jerarquía de secrets:**
```
Developer local:
  .env (gitignored)
  ↓
CI/CD:
  GitHub Secrets
  ↓
Kubernetes:
  Kubernetes Secrets (base64)
  ↓
Production:
  External Secrets Operator → HashiCorp Vault / AWS Secrets Manager
```

**External Secrets Operator:**
```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: app-secrets
spec:
  refreshInterval: 1h
  secretStoreRef:
    name: vault-backend
    kind: SecretStore
  target:
    name: app-secrets
  data:
  - secretKey: jwt-secret
    remoteRef:
      key: secret/data/app
      property: jwt-secret
```

### 2. Least Privilege Access

```yaml
# ❌ MAL: Token con permisos de admin
deploy:
  steps:
    - run: kubectl delete all --all  # ¡Puede borrar todo!

# ✅ BIEN: Service account con permisos limitados
apiVersion: v1
kind: ServiceAccount
metadata:
  name: github-actions-deployer
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: deployer-role
rules:
- apiGroups: ["apps"]
  resources: ["deployments"]
  verbs: ["get", "list", "update", "patch"]
- apiGroups: [""]
  resources: ["services"]
  verbs: ["get", "list"]
# NO puede: delete, create recursos no listados
```

### 3. Supply Chain Security

```yaml
# Verificar checksums de dependencies
- name: Verify Maven dependencies
  run: |
    mvn dependency:tree -Dverbose > deps.txt
    sha256sum deps.txt
    # Comparar con baseline conocido

# Firmar artefactos
- name: Sign JAR with GPG
  run: |
    echo "$GPG_PRIVATE_KEY" | gpg --import
    gpg --sign --armor target/*.jar

# Scan Docker image antes de push
- name: Scan image with Trivy
  run: |
    docker run --rm \
      -v /var/run/docker.sock:/var/run/docker.sock \
      aquasec/trivy:latest image \
      --severity HIGH,CRITICAL \
      --exit-code 1 \
      myrepo/academia:latest
```

---

## Monitoring y Rollback

### 1. Health Checks

```java
// Spring Boot Actuator
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Check database
        if (!isDatabaseReachable()) {
            return Health.down()
                .withDetail("database", "unreachable")
                .build();
        }
        
        // Check external API
        if (!isExternalApiHealthy()) {
            return Health.down()
                .withDetail("external-api", "unhealthy")
                .build();
        }
        
        return Health.up()
            .withDetail("database", "connected")
            .withDetail("external-api", "healthy")
            .build();
    }
}
```

**Kubernetes probes:**
```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
  failureThreshold: 3
  # 3 fallos → restart pod

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 20
  periodSeconds: 5
  failureThreshold: 2
  # 2 fallos → remover de load balancer
```

### 2. Automated Rollback

```yaml
deploy-with-rollback:
  steps:
    - name: Deploy new version
      run: |
        kubectl set image deployment/app app=myrepo/app:${{ github.sha }}
        kubectl rollout status deployment/app --timeout=5m
    
    - name: Run smoke tests
      id: smoke_tests
      run: ./smoke-tests.sh
      continue-on-error: true
    
    - name: Check metrics
      id: metrics_check
      run: |
        ERROR_RATE=$(curl prometheus/query?query=error_rate)
        if [ "$ERROR_RATE" -gt "5" ]; then
          echo "Error rate too high: $ERROR_RATE%"
          exit 1
        fi
      continue-on-error: true
    
    - name: Rollback if unhealthy
      if: steps.smoke_tests.outcome == 'failure' || steps.metrics_check.outcome == 'failure'
      run: |
        echo "Deployment unhealthy, rolling back..."
        kubectl rollout undo deployment/app
        kubectl rollout status deployment/app
        exit 1
```

### 3. Observability Stack

```yaml
# Prometheus scrape config
scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['app:8080']

# Grafana dashboard queries
## Request rate
rate(http_server_requests_seconds_count[5m])

## Latency p99
histogram_quantile(0.99, http_server_requests_seconds_bucket)

## Error rate
rate(http_server_requests_seconds_count{status=~"5.."}[5m]) / 
rate(http_server_requests_seconds_count[5m])

# Alertmanager rules
groups:
- name: app_alerts
  rules:
  - alert: HighErrorRate
    expr: error_rate > 5
    for: 5m
    annotations:
      summary: "Error rate above 5% for 5 minutes"
  
  - alert: HighLatency
    expr: http_request_duration_p99 > 1000
    for: 5m
    annotations:
      summary: "P99 latency above 1 second"
```

---

## Conclusión: Roadmap de Junior a Senior

### Junior Developer (0-2 años):
- ✅ Conoce Git básico (commit, push, pull)
- ✅ Escribe código que funciona localmente
- ❌ No sabe configurar CI/CD
- ❌ Deployment manual (o no sabe deployar)

### Mid-Level Developer (2-4 años):
- ✅ Usa Git avanzado (branches, rebase, cherry-pick)
- ✅ Escribe tests unitarios
- ✅ Configura CI básico (build + test)
- ✅ Entiende Docker
- ⚠️ Deployment manual con scripts

### Senior Developer (4+ años):
- ✅ CI/CD completo (build → test → scan → deploy automático)
- ✅ Multiple deployment strategies (rolling, blue-green, canary)
- ✅ Monitoring y observability (logs, metrics, tracing)
- ✅ Security integrada (OWASP, secrets management)
- ✅ Disaster recovery (backups, rollback automático)
- ✅ Infrastructure as Code (Terraform, Helm)
- ✅ Mentorship (enseña a juniors)

**Tu proyecto academia demuestra nivel senior porque incluye:**
- ✅ Pipeline completo con 8 stages
- ✅ Code quality gates (SonarCloud, JaCoCo)
- ✅ Security scanning (OWASP)
- ✅ Docker optimization (multi-stage, caching)
- ✅ Deployment strategies documentadas
- ✅ Monitoring y health checks

**Próximos pasos:**
1. Implementar feature flags (LaunchDarkly, Unleash)
2. Chaos engineering (fault injection, resilience testing)
3. Multi-region deployment (alta disponibilidad global)
4. GitOps con ArgoCD (declarative deployments)


