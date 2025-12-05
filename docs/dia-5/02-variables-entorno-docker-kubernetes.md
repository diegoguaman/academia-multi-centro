# 🔐 Variables de Entorno: Docker y Kubernetes

## 📋 Resumen

Este documento detalla **todas las variables de entorno** necesarias para ejecutar la aplicación en Docker y Kubernetes, con ejemplos prácticos y mejores prácticas de seguridad.

---

## 🔑 Variables Críticas (Requeridas)

### 1. Database Configuration (Supabase)

```bash
# URL de conexión JDBC a Supabase
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require

# Credenciales de base de datos
DB_USERNAME=postgres
DB_PASSWORD=Ac4d3m1a_1994!  # ⚠️ SECRET - No commitear
```

**Formato de URL:**
- Puerto `5432`: Conexión directa (requiere IPv6)
- Puerto `6543`: Session Pooler (compatible IPv4, recomendado)
- `?sslmode=require`: Requiere SSL/TLS (obligatorio en Supabase)

**Mapeo en Spring Boot:**
```properties
# application.properties
spring.datasource.url=${DB_SUPABASE:jdbc:postgresql://localhost:5432/postgres}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:}
```

---

### 2. JWT Configuration

```bash
# Secret key para firmar tokens JWT (256 bits = 64 caracteres hex)
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970

# Tiempo de expiración en milisegundos (86400000 = 24 horas)
JWT_EXPIRATION_TIME=86400000
```

**Generar nuevo JWT_SECRET_KEY:**
```bash
# Opción 1: OpenSSL
openssl rand -hex 32

# Opción 2: Java
java -cp . -c "System.out.println(javax.crypto.KeyGenerator.getInstance(\"HmacSHA256\").generateKey())"

# Opción 3: Online (solo para testing)
# https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx
```

**Mapeo en Spring Boot:**
```properties
# application.properties
jwt.secret.key=${JWT_SECRET_KEY:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
jwt.expiration.time=${JWT_EXPIRATION_TIME:86400000}
```

---

### 3. Spring Profile (Opcional pero Recomendado)

```bash
# Perfil de Spring Boot
SPRING_PROFILES_ACTIVE=prod  # o 'dev', 'staging', 'test'
```

**Efectos:**
- `prod`: No carga `.env` file, usa solo variables de entorno
- `dev`: Carga `.env` file si existe
- `test`: Configuración para tests

---

## 📝 Variables Opcionales

### Logging Configuration

```bash
# Nivel de logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_ACADEMY=DEBUG

# O usando Spring Boot properties
SPRING_LOGGING_LEVEL_ROOT=INFO
SPRING_LOGGING_LEVEL_COM_ACADEMY_ACADEMYMANAGER=DEBUG
```

### JVM Configuration

```bash
# Memory settings (ya configurados en Dockerfile, pero se pueden override)
JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Heap size (alternativa a MaxRAMPercentage)
JAVA_HEAP_SIZE=512m
```

### GraphQL Configuration

```bash
# Path de GraphQL (default: /graphql)
SPRING_GRAPHQL_PATH=/graphql

# Habilitar GraphiQL UI (default: true en dev, false en prod)
GRAPHIQL_ENABLED=false
```

---

## 🐳 Uso en Docker

### Opción 1: Variables de Entorno Directas

```bash
docker run -d \
  --name academia-app \
  -p 8080:8080 \
  -e DB_SUPABASE="jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require" \
  -e DB_USERNAME="postgres" \
  -e DB_PASSWORD="your-password" \
  -e JWT_SECRET_KEY="your-secret-key" \
  -e JWT_EXPIRATION_TIME="86400000" \
  -e SPRING_PROFILES_ACTIVE="prod" \
  academia-multi-centro:latest
```

### Opción 2: Archivo .env (Solo Desarrollo)

```bash
# Crear .env
cat > .env << EOF
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=your-password
JWT_SECRET_KEY=your-secret-key
JWT_EXPIRATION_TIME=86400000
SPRING_PROFILES_ACTIVE=prod
EOF

# Ejecutar con --env-file
docker run -d \
  --name academia-app \
  -p 8080:8080 \
  --env-file .env \
  academia-multi-centro:latest
```

**⚠️ IMPORTANTE**: 
- `.env` file **NO se carga automáticamente** en contenedores Docker
- Debes usar `--env-file` o pasar variables con `-e`
- En producción, **NUNCA** uses `.env` files, solo variables de entorno

### Opción 3: Docker Compose

```yaml
# docker-compose.yml
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
      - JWT_EXPIRATION_TIME=${JWT_EXPIRATION_TIME}
    env_file:
      - .env  # Solo para desarrollo local
```

---

## ☸️ Uso en Kubernetes

### Opción 1: Kubernetes Secrets

```yaml
# secrets.yaml (NO commitear valores reales)
apiVersion: v1
kind: Secret
metadata:
  name: academia-secrets
  namespace: production
type: Opaque
stringData:
  DB_SUPABASE: "jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require"
  DB_USERNAME: "postgres"
  DB_PASSWORD: "your-secure-password"
  JWT_SECRET_KEY: "your-256-bit-secret-key"
  JWT_EXPIRATION_TIME: "86400000"
```

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: academia-app
spec:
  template:
    spec:
      containers:
      - name: app
        image: academia-multi-centro:latest
        envFrom:
        - secretRef:
            name: academia-secrets
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
```

**Crear Secret desde línea de comandos:**
```bash
kubectl create secret generic academia-secrets \
  --from-literal=DB_SUPABASE="jdbc:postgresql://..." \
  --from-literal=DB_USERNAME="postgres" \
  --from-literal=DB_PASSWORD="your-password" \
  --from-literal=JWT_SECRET_KEY="your-secret" \
  --from-literal=JWT_EXPIRATION_TIME="86400000" \
  --namespace=production
```

### Opción 2: ConfigMap + Secrets (Separación)

```yaml
# configmap.yaml (valores no-sensibles)
apiVersion: v1
kind: ConfigMap
metadata:
  name: academia-config
data:
  SPRING_PROFILES_ACTIVE: "prod"
  LOGGING_LEVEL_ROOT: "INFO"
  LOGGING_LEVEL_COM_ACADEMY: "DEBUG"
  SPRING_GRAPHQL_PATH: "/graphql"
  GRAPHIQL_ENABLED: "false"
```

```yaml
# secrets.yaml (valores sensibles)
apiVersion: v1
kind: Secret
metadata:
  name: academia-secrets
type: Opaque
stringData:
  DB_PASSWORD: "your-password"
  JWT_SECRET_KEY: "your-secret"
```

```yaml
# deployment.yaml
envFrom:
- configMapRef:
    name: academia-config
- secretRef:
    name: academia-secrets
env:
- name: DB_SUPABASE
  valueFrom:
    configMapKeyRef:
      name: academia-config
      key: DB_SUPABASE
```

### Opción 3: External Secrets Operator (Producción)

```yaml
# external-secret.yaml
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

---

## 🔒 Seguridad: Best Practices

### ✅ DO (Hacer)

1. **Usar Secrets Managers en producción**
   - HashiCorp Vault
   - AWS Secrets Manager
   - Azure Key Vault
   - Google Secret Manager

2. **Rotar secrets periódicamente**
   ```bash
   # Rotar JWT secret cada 90 días
   kubectl create secret generic academia-secrets \
     --from-literal=JWT_SECRET_KEY=$(openssl rand -hex 32) \
     --dry-run=client -o yaml | kubectl apply -f -
   ```

3. **Usar diferentes secrets por entorno**
   - `academia-secrets-dev`
   - `academia-secrets-staging`
   - `academia-secrets-prod`

4. **Encriptar secrets en etcd** (K8s)
   ```yaml
   # Habilitar encryption at rest
   apiVersion: apiserver.config.k8s.io/v1
   kind: EncryptionConfiguration
   resources:
   - resources:
     - secrets
     providers:
     - aescbc:
         keys:
         - name: key1
           secret: <base64-encoded-secret>
   ```

5. **RBAC para acceso a secrets**
   ```yaml
   apiVersion: rbac.authorization.k8s.io/v1
   kind: Role
   metadata:
     name: secret-reader
   rules:
   - apiGroups: [""]
     resources: ["secrets"]
     resourceNames: ["academia-secrets"]
     verbs: ["get", "list"]
   ```

### ❌ DON'T (No Hacer)

1. **NO hardcodear secrets en código**
   ```java
   // ❌ MAL
   String password = "Ac4d3m1a_1994!";
   
   // ✅ BIEN
   String password = System.getenv("DB_PASSWORD");
   ```

2. **NO commitear .env files**
   ```gitignore
   # .gitignore
   .env
   .env.*
   .env.local
   ```

3. **NO usar secrets en Dockerfiles**
   ```dockerfile
   # ❌ MAL
   ENV DB_PASSWORD=secret123
   
   # ✅ BIEN
   # Secrets se pasan en runtime
   ```

4. **NO loggear secrets**
   ```java
   // ❌ MAL
   logger.info("Password: {}", password);
   
   // ✅ BIEN
   logger.info("Database connection configured");
   ```

5. **NO compartir secrets por email/Slack**
   - Usar herramientas seguras (1Password, LastPass)
   - O mejor: External Secrets Operator

---

## 📊 Tabla de Variables: Resumen

| Variable | Tipo | Requerida | Sensible | Default | Uso |
|----------|------|-----------|----------|---------|-----|
| `DB_SUPABASE` | String | ✅ | ⚠️ | `jdbc:postgresql://localhost:5432/postgres` | URL JDBC |
| `DB_USERNAME` | String | ✅ | ⚠️ | `postgres` | Usuario DB |
| `DB_PASSWORD` | String | ✅ | 🔴 | - | Password DB |
| `JWT_SECRET_KEY` | String | ✅ | 🔴 | - | Secret JWT |
| `JWT_EXPIRATION_TIME` | Number | ✅ | ❌ | `86400000` | Expiración (ms) |
| `SPRING_PROFILES_ACTIVE` | String | ❌ | ❌ | `default` | Perfil Spring |
| `LOGGING_LEVEL_ROOT` | String | ❌ | ❌ | `INFO` | Nivel de log |
| `SPRING_GRAPHQL_PATH` | String | ❌ | ❌ | `/graphql` | Path GraphQL |
| `GRAPHIQL_ENABLED` | Boolean | ❌ | ❌ | `true` (dev) | GraphiQL UI |

**Leyenda:**
- ✅ = Requerida
- ❌ = Opcional
- 🔴 = Sensible (Secret)
- ⚠️ = Semi-sensible (ConfigMap OK)

---

## 🧪 Testing de Variables

### Verificar Variables en Contenedor

```bash
# Docker
docker exec academia-app env | grep -E "DB_|JWT_|SPRING_"

# Kubernetes
kubectl exec -it deployment/academia-app -- env | grep -E "DB_|JWT_|SPRING_"
```

### Test de Conexión

```bash
# Test health endpoint
curl http://localhost:8080/actuator/health

# Test con variables incorrectas (debe fallar)
docker run -e DB_PASSWORD="wrong" academia-multi-centro:latest
# Debe mostrar error de conexión
```

---

## 📚 Referencias

- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/)
- [External Secrets Operator](https://external-secrets.io/)
- [Docker Environment Variables](https://docs.docker.com/compose/environment-variables/)

---

**✅ Conclusión**: Las variables de entorno están **bien estructuradas** y preparadas para Docker/Kubernetes. La aplicación prioriza variables del sistema sobre `.env` files en producción, lo cual es una **best practice enterprise**.
