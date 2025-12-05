# 🐳 Análisis Completo de Dockerización - Día 5

## 📋 Resumen Ejecutivo

Este documento analiza el estado actual de la dockerización del proyecto Academia Multi-Centro, identifica qué está listo para Kubernetes, qué necesita mejorarse, y cómo gestionar las variables de entorno críticas en contenedores.

---

## ✅ Estado Actual del Dockerfile

### Análisis del Dockerfile Existente

El Dockerfile actual está **bien estructurado** y sigue best practices:

```dockerfile
# Multi-stage build (optimizado)
FROM eclipse-temurin:21-jdk-alpine AS builder
# ... build stage ...

FROM eclipse-temurin:21-jre-alpine
# Runtime stage con usuario no-root
USER spring:spring
# Health check configurado
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1
```

### ✅ Puntos Fuertes

1. **Multi-stage build**: Reduce tamaño de imagen final (~50% más pequeño)
2. **Usuario no-root**: Seguridad mejorada (USER spring:spring)
3. **Health check**: Configurado para Kubernetes liveness/readiness probes
4. **JVM optimizado**: Flags para contenedores (UseContainerSupport, MaxRAMPercentage)
5. **Alpine Linux**: Imagen base ligera (~150MB vs ~500MB de Ubuntu)

### ⚠️ Puntos a Mejorar

1. **Spring Boot Actuator**: El healthcheck usa `/actuator/health` pero Actuator no está en `pom.xml`
   - **Solución**: Agregar dependencia o cambiar healthcheck a endpoint simple
2. **Variables de entorno**: No están documentadas explícitamente en Dockerfile
   - **Solución**: Agregar comentarios sobre variables requeridas
3. **Build args**: JAR_FILE está definido pero podría ser más flexible

---

## 🔐 Variables de Entorno Críticas

### Variables Requeridas para Producción

Estas son las variables **CRÍTICAS** que deben estar configuradas en Kubernetes:

```bash
# Database Configuration (Supabase)
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=<SECRET_PASSWORD>

# JWT Configuration
JWT_SECRET_KEY=<SECRET_KEY_256_BITS>
JWT_EXPIRATION_TIME=86400000  # 24 horas en ms

# Spring Profile (opcional, recomendado)
SPRING_PROFILES_ACTIVE=prod
```

### Prioridad de Configuración

La aplicación sigue este orden de prioridad (más alto → más bajo):

1. **Variables de entorno del sistema** (Kubernetes Secrets/ConfigMaps)
2. **System properties** (Java -D flags)
3. **application.properties defaults** (fallback, sin secrets)

**⚠️ IMPORTANTE**: En producción, `.env` files **NO se cargan** (ver `DotenvConfig.java` línea 62-64).

---

## 🚀 Preparación para Kubernetes

### 1. Verificar Dockerfile

El Dockerfile actual **está listo** para Kubernetes con estas consideraciones:

#### ✅ Listo para K8s:
- Multi-stage build optimizado
- Usuario no-root (security context)
- Health check configurado
- JVM flags para contenedores
- Puerto 8080 expuesto

#### ⚠️ Requiere Ajustes:

**Opción A: Agregar Spring Boot Actuator** (Recomendado)

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
# application.properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
management.health.livenessState.enabled=true
management.health.readinessState.enabled=true
```

**Opción B: Cambiar Health Check** (Si no quieres Actuator)

```dockerfile
# Cambiar en Dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/api/auth/health || exit 1
```

### 2. Crear Kubernetes Manifests

#### Deployment.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: academia-app
  labels:
    app: academia-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: academia-app
  template:
    metadata:
      labels:
        app: academia-app
    spec:
      containers:
      - name: academia-app
        image: your-registry/academia-multi-centro:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        envFrom:
        - secretRef:
            name: academia-secrets
        - configMapRef:
            name: academia-config
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
          timeoutSeconds: 3
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
          timeoutSeconds: 2
          failureThreshold: 2
        securityContext:
          runAsNonRoot: true
          runAsUser: 1000
          allowPrivilegeEscalation: false
          readOnlyRootFilesystem: false
          capabilities:
            drop:
            - ALL
```

#### Service.yaml

```yaml
apiVersion: v1
kind: Service
metadata:
  name: academia-app-service
spec:
  type: LoadBalancer
  selector:
    app: academia-app
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
```

#### Secret.yaml (Ejemplo - NO commitear valores reales)

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: academia-secrets
type: Opaque
stringData:
  DB_SUPABASE: "jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require"
  DB_USERNAME: "postgres"
  DB_PASSWORD: "your-secure-password"
  JWT_SECRET_KEY: "your-256-bit-secret-key"
  JWT_EXPIRATION_TIME: "86400000"
```

**⚠️ IMPORTANTE**: 
- Secrets **NUNCA** se commitean con valores reales
- Usar `kubectl create secret` o herramientas como External Secrets Operator
- En producción, usar HashiCorp Vault o AWS Secrets Manager

#### ConfigMap.yaml

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: academia-config
data:
  SPRING_PROFILES_ACTIVE: "prod"
  LOGGING_LEVEL_ROOT: "INFO"
  LOGGING_LEVEL_COM_ACADEMY: "DEBUG"
```

---

## 🔒 Gestión de Secrets en Kubernetes

### Opción 1: Kubernetes Secrets (Básico)

```bash
# Crear secret desde archivo
kubectl create secret generic academia-secrets \
  --from-literal=DB_PASSWORD='your-password' \
  --from-literal=JWT_SECRET_KEY='your-secret' \
  --namespace=production

# O desde archivo .env (desarrollo)
kubectl create secret generic academia-secrets \
  --from-env-file=.env \
  --namespace=production
```

**Pros**: Simple, integrado en K8s  
**Contras**: Secrets en base64 (no encriptados por defecto), difícil rotación

### Opción 2: External Secrets Operator (Recomendado)

```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: academia-secrets
spec:
  secretStoreRef:
    name: vault-backend
    kind: SecretStore
  target:
    name: academia-secrets
    creationPolicy: Owner
  data:
  - secretKey: DB_PASSWORD
    remoteRef:
      key: secret/data/academia/prod
      property: db_password
  - secretKey: JWT_SECRET_KEY
    remoteRef:
      key: secret/data/academia/prod
      property: jwt_secret
```

**Pros**: Integración con Vault/AWS Secrets Manager, rotación automática  
**Contras**: Requiere setup adicional

### Opción 3: Sealed Secrets (GitOps)

```bash
# Instalar kubeseal
kubectl apply -f https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.24.0/controller.yaml

# Crear SealedSecret
kubectl create secret generic academia-secrets \
  --from-literal=DB_PASSWORD='secret' \
  --dry-run=client -o yaml | kubeseal -o yaml > sealed-secret.yaml

# Commitear sealed-secret.yaml (seguro, encriptado)
git add sealed-secret.yaml
git commit -m "Add sealed secrets"
```

**Pros**: Secrets versionados en Git (encriptados), GitOps-friendly  
**Contras**: Requiere controller adicional

---

## 📦 Build y Push de Imagen Docker

### Build Local

```bash
# Build con Maven primero (en CI/CD)
mvn clean package -DskipTests

# Build imagen Docker
docker build -t academia-multi-centro:latest \
  --build-arg JAR_FILE=target/academymanager-0.0.1-SNAPSHOT.jar .

# Verificar tamaño
docker images academia-multi-centro
# Debería ser ~200-250MB (con Alpine)
```

### Push a Registry

```bash
# Tag para registry
docker tag academia-multi-centro:latest \
  gcr.io/your-project/academia-multi-centro:v1.0.0

# Push
docker push gcr.io/your-project/academia-multi-centro:v1.0.0
```

### Build Multi-arch (Opcional)

```dockerfile
# Dockerfile con soporte multi-arch
FROM --platform=$BUILDPLATFORM eclipse-temurin:21-jdk-alpine AS builder
# ... resto igual ...
```

```bash
# Build para múltiples arquitecturas
docker buildx build --platform linux/amd64,linux/arm64 \
  -t academia-multi-centro:latest \
  --push .
```

---

## 🧪 Testing de Imagen Docker

### Test Local

```bash
# Ejecutar contenedor local
docker run -d \
  --name academia-test \
  -p 8080:8080 \
  -e DB_SUPABASE="jdbc:postgresql://..." \
  -e DB_USERNAME="postgres" \
  -e DB_PASSWORD="password" \
  -e JWT_SECRET_KEY="test-key" \
  academia-multi-centro:latest

# Verificar logs
docker logs -f academia-test

# Test health check
curl http://localhost:8080/actuator/health

# Limpiar
docker stop academia-test && docker rm academia-test
```

### Test con Docker Compose (Opcional - Solo App)

```yaml
# docker-compose.yml (solo app, sin DB)
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_SUPABASE=${DB_SUPABASE}
      - DB_USERNAME=${DB_USERNAME}
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET_KEY=${JWT_SECRET_KEY}
    env_file:
      - .env  # Solo para desarrollo local
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 3s
      retries: 3
      start_period: 60s
```

```bash
# Ejecutar
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Test
curl http://localhost:8080/actuator/health
```

---

## 📊 Checklist de Dockerización

### ✅ Pre-Deployment

- [ ] Dockerfile multi-stage optimizado
- [ ] Imagen < 300MB (con Alpine)
- [ ] Usuario no-root configurado
- [ ] Health check funcional
- [ ] Variables de entorno documentadas
- [ ] Secrets NO hardcodeados
- [ ] Build exitoso sin errores
- [ ] Test local exitoso

### ✅ Kubernetes Ready

- [ ] Deployment manifest creado
- [ ] Service manifest creado
- [ ] Secrets configurados (External Secrets o K8s Secrets)
- [ ] ConfigMaps para configuración no-sensible
- [ ] Resource limits definidos
- [ ] Liveness/Readiness probes configurados
- [ ] Security context configurado
- [ ] Replicas >= 2 para HA

### ✅ Production Ready

- [ ] Secrets en Vault/AWS Secrets Manager
- [ ] Logging centralizado (ELK/CloudWatch)
- [ ] Monitoring (Prometheus/Grafana)
- [ ] Auto-scaling configurado (HPA)
- [ ] Network policies configuradas
- [ ] Backup strategy definida
- [ ] Disaster recovery plan
- [ ] Documentation completa

---

## 🎯 Resumen: ¿Está Listo para Kubernetes?

### ✅ SÍ, con estas consideraciones:

1. **Dockerfile**: ✅ Listo (multi-stage, optimizado, security)
2. **Variables de entorno**: ✅ Preparado (DotenvConfig ignora .env en prod)
3. **Health checks**: ⚠️ Requiere Actuator o endpoint alternativo
4. **Secrets**: ⚠️ Requiere configuración en K8s (Secrets/External Secrets)
5. **Manifests**: ⚠️ Requiere crear Deployment/Service/Secrets

### 🚀 Próximos Pasos:

1. **Agregar Spring Boot Actuator** (recomendado) o cambiar health check
2. **Crear Kubernetes manifests** (Deployment, Service, Secrets, ConfigMap)
3. **Configurar External Secrets Operator** (para producción)
4. **Test en cluster local** (minikube/k3d) antes de producción
5. **Configurar CI/CD** para build y push automático de imágenes

---

## 📚 Referencias

- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Kubernetes Best Practices](https://kubernetes.io/docs/concepts/configuration/secret/)
- [External Secrets Operator](https://external-secrets.io/)
- [Docker Multi-stage Builds](https://docs.docker.com/build/building/multi-stage/)

---

**✅ Conclusión**: El Dockerfile está **bien estructurado** y listo para Kubernetes. Solo requiere:
1. Actuator o health check alternativo
2. Crear manifests de Kubernetes
3. Configurar gestión de secrets

**Tiempo estimado para completar**: 2-3 horas
