# 🔐 Secrets Management: Solución Enterprise-Grade

## ¿Cómo se Manejan los Secrets en Empresas Reales?

Esta es una pregunta **crítica** para cualquier desarrollador que quiera trabajar en empresas de nivel enterprise. Te explico las mejores prácticas profesionales.

---

## 🏢 Jerarquía de Secrets Management (Enterprise)

### Nivel 1: Desarrollo Local (Tu Máquina)

**Solución implementada:** `.env` file + `dotenv-java`

```env
# .env (gitignored, nunca se sube a Git)
DB_SUPABASE=jdbc:postgresql://...
DB_USERNAME=postgres
DB_PASSWORD=secret_password
JWT_SECRET_KEY=your_secret_key
```

**Ventajas:**
- ✅ Fácil de usar para desarrolladores
- ✅ No necesitas configurar variables del sistema
- ✅ Funciona igual en Windows, Linux, macOS
- ✅ Seguro (archivo en `.gitignore`)

**Cómo funciona:**
- Librería `dotenv-java` lee `.env` automáticamente
- Carga variables como propiedades del sistema
- Spring Boot las detecta automáticamente
- **Solo en desarrollo** (production lo ignora)

---

### Nivel 2: CI/CD (GitHub Actions, GitLab CI, Jenkins)

**Solución:** Secrets en plataforma CI/CD

**GitHub Actions:**
```yaml
# .github/workflows/ci-cd.yml
env:
  DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
  JWT_SECRET: ${{ secrets.JWT_SECRET }}
```

**Configuración:**
1. GitHub → Settings → Secrets → New repository secret
2. Agregar: `DB_PASSWORD`, `JWT_SECRET`, etc.
3. Secrets encriptados automáticamente
4. Solo visibles en logs como `***`

**Ventajas:**
- ✅ Centralizado en GitHub
- ✅ Encriptado automáticamente
- ✅ Acceso controlado (solo admins pueden ver/editar)
- ✅ Rotación fácil

---

### Nivel 3: Staging/Pre-Production

**Solución:** Variables de entorno en Kubernetes / Docker

**Kubernetes Secrets:**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
stringData:
  DB_PASSWORD: "production_password"
  JWT_SECRET: "production_jwt_secret"
```

**Uso en Deployment:**
```yaml
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      containers:
      - name: app
        envFrom:
        - secretRef:
            name: app-secrets
```

**Ventajas:**
- ✅ Secrets encriptados en etcd (Kubernetes)
- ✅ Rotación sin redeploy
- ✅ Acceso controlado con RBAC

---

### Nivel 4: Production (Enterprise)

**Solución:** External Secret Managers

#### Opción A: HashiCorp Vault (Más Popular)

```java
// Spring Cloud Vault integration
@Configuration
public class VaultConfig {
    @Value("${spring.cloud.vault.uri}")
    private String vaultUri;
    
    // Secrets se cargan automáticamente desde Vault
    // Ejemplo: secret/data/app/database → spring.datasource.password
}
```

**Configuración:**
```properties
spring.cloud.vault.uri=https://vault.company.com
spring.cloud.vault.authentication=TOKEN
spring.cloud.vault.token=${VAULT_TOKEN}
spring.cloud.vault.kv.enabled=true
spring.cloud.vault.kv.backend=secret
spring.cloud.vault.kv.default-context=app
```

**Ventajas:**
- ✅ Secrets centralizados
- ✅ Rotación automática
- ✅ Audit logging completo
- ✅ Acceso temporal (tokens con TTL)
- ✅ Integración con LDAP/Active Directory

#### Opción B: AWS Secrets Manager

```java
@Configuration
public class AwsSecretsConfig {
    @Bean
    public SecretsManagerClient secretsManagerClient() {
        return SecretsManagerClient.builder()
            .region(Region.EU_WEST_1)
            .build();
    }
}
```

**Uso:**
```java
@Autowired
private SecretsManagerClient secretsClient;

public String getDatabasePassword() {
    GetSecretValueRequest request = GetSecretValueRequest.builder()
        .secretId("prod/academia/database")
        .build();
    
    GetSecretValueResponse response = secretsClient.getSecretValue(request);
    return response.secretString();
}
```

**Ventajas:**
- ✅ Integrado con AWS (IAM, CloudTrail)
- ✅ Rotación automática
- ✅ Versionado de secrets
- ✅ Replicación multi-región

#### Opción C: Azure Key Vault

```java
@Configuration
public class AzureKeyVaultConfig {
    @Bean
    public SecretClient secretClient() {
        return new SecretClientBuilder()
            .vaultUrl("https://academia-vault.vault.azure.net/")
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildClient();
    }
}
```

---

## 📊 Comparación de Soluciones

| Solución | Uso | Seguridad | Complejidad | Costo |
|----------|-----|-----------|-------------|-------|
| `.env` file | Desarrollo local | ⭐⭐ | ⭐ | Gratis |
| CI/CD Secrets | CI/CD pipelines | ⭐⭐⭐ | ⭐⭐ | Gratis |
| Kubernetes Secrets | Staging/Pre-prod | ⭐⭐⭐ | ⭐⭐⭐ | Gratis |
| HashiCorp Vault | Production | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Open Source / Enterprise |
| AWS Secrets Manager | Production (AWS) | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ~$0.40/secret/mes |
| Azure Key Vault | Production (Azure) | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ~$0.03/10k operations |

---

## 🎯 Solución Implementada en Tu Proyecto

### Arquitectura Híbrida (Best Practice)

```
┌─────────────────────────────────────────────────────────┐
│                    SECRETS SOURCE                       │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  1. System Environment Variables (HIGHEST PRIORITY)    │
│     → Production, CI/CD, Docker, K8s                    │
│                                                          │
│  2. .env file (DEVELOPMENT ONLY)                        │
│     → Local development, gitignored                     │
│                                                          │
│  3. application.properties defaults (LOWEST)           │
│     → Fallback values, no secrets                       │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### Código Implementado

**1. DotenvConfig.java** - Carga automática de `.env`
```java
@PostConstruct
public void loadDotenv() {
    // Solo en desarrollo
    if (isProduction()) return;
    
    // Carga .env y lo convierte en system properties
    Dotenv dotenv = Dotenv.configure()
        .directory("./")
        .ignoreIfMissing()
        .load();
}
```

**2. application.properties** - Prioridad de configuración
```properties
# Variables de entorno tienen prioridad
spring.datasource.url=${DB_SUPABASE:jdbc:postgresql://localhost:5432/postgres}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:}
```

**3. .env** - Desarrollo local (gitignored)
```env
DB_SUPABASE=jdbc:postgresql://...
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

---

## 🔒 Seguridad: ¿Es Seguro Usar .env?

### ✅ SÍ, si se hace correctamente:

1. **`.env` está en `.gitignore`**
   ```gitignore
   .env
   .env.*
   .env.local
   ```

2. **Nunca se commitea a Git**
   - Verificar: `git status` no debe mostrar `.env`
   - GitHub tiene escaneo automático de secrets (si se sube, te avisa)

3. **Solo para desarrollo**
   - Production usa variables de entorno del sistema
   - CI/CD usa secrets de la plataforma

4. **Permisos de archivo (Linux/macOS)**
   ```bash
   chmod 600 .env  # Solo lectura/escritura para owner
   ```

### ❌ NO, si:
- Commiteas `.env` a Git
- Compartes `.env` por email/Slack
- Usas `.env` en production
- Tienes permisos abiertos (chmod 777)

---

## 🚀 Flujo Completo: De Desarrollo a Producción

### 1. Desarrollo Local

```bash
# Crear .env
cat > .env << EOF
DB_SUPABASE=jdbc:postgresql://localhost:5432/academia
DB_USERNAME=postgres
DB_PASSWORD=dev_password
JWT_SECRET_KEY=dev_secret_key
EOF

# Ejecutar
mvn spring-boot:run
# → dotenv-java carga .env automáticamente
```

### 2. CI/CD (GitHub Actions)

```yaml
# .github/workflows/ci-cd.yml
jobs:
  test:
    env:
      DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
      JWT_SECRET: ${{ secrets.JWT_SECRET }}
    steps:
      - run: mvn test
      # → Variables de entorno inyectadas por GitHub
```

### 3. Staging (Kubernetes)

```yaml
# k8s/secrets.yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
stringData:
  DB_PASSWORD: "staging_password"
  JWT_SECRET: "staging_jwt_secret"
```

```yaml
# k8s/deployment.yaml
spec:
  containers:
  - name: app
    envFrom:
    - secretRef:
        name: app-secrets
    # → Secrets inyectados como variables de entorno
```

### 4. Production (HashiCorp Vault)

```properties
# application-prod.properties
spring.cloud.vault.uri=https://vault.company.com
spring.cloud.vault.authentication=TOKEN
spring.cloud.vault.token=${VAULT_TOKEN}
spring.cloud.vault.kv.enabled=true
```

```bash
# En K8s, inyectar token de Vault
kubectl create secret generic vault-token \
  --from-literal=VAULT_TOKEN=$(vault token create -format=json | jq -r .auth.client_token)
```

---

## 📝 Checklist de Seguridad

### ✅ Desarrollo Local
- [ ] `.env` en `.gitignore`
- [ ] `.env` no aparece en `git status`
- [ ] Permisos restrictivos (600 en Linux/macOS)
- [ ] No compartir `.env` por canales inseguros

### ✅ CI/CD
- [ ] Secrets configurados en plataforma (GitHub/GitLab)
- [ ] Secrets no hardcodeados en workflows
- [ ] Logs no muestran valores de secrets (usan `***`)

### ✅ Staging/Production
- [ ] Secrets en external manager (Vault/AWS/Azure)
- [ ] Rotación periódica de secrets
- [ ] Audit logging habilitado
- [ ] Acceso controlado con RBAC/IAM
- [ ] Secrets encriptados at rest y in transit

---

## 🎓 Para Entrevistas: ¿Qué Decir?

**Pregunta:** "¿Cómo manejas secrets en tu aplicación?"

**Respuesta Senior:**

> "Implemento una estrategia de múltiples capas según el entorno:
> 
> **Desarrollo local:** Uso archivos `.env` con `dotenv-java` para facilitar el setup de desarrolladores. El archivo está en `.gitignore` y nunca se commitea.
> 
> **CI/CD:** Secrets almacenados en la plataforma (GitHub Secrets, GitLab CI Variables) e inyectados como variables de entorno durante el pipeline.
> 
> **Staging:** Kubernetes Secrets encriptados en etcd, inyectados como variables de entorno en los pods.
> 
> **Production:** HashiCorp Vault o AWS Secrets Manager para centralización, rotación automática, y audit logging completo.
> 
> La prioridad siempre es: variables de entorno del sistema > .env (solo dev) > defaults en properties. Esto garantiza que production nunca use archivos locales y permite escalabilidad horizontal sin problemas."

---

## 🔗 Recursos Adicionales

### Documentación Oficial:
- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [HashiCorp Vault](https://www.vaultproject.io/)
- [AWS Secrets Manager](https://aws.amazon.com/secrets-manager/)
- [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/)

### Librerías:
- [dotenv-java](https://github.com/cdimascio/dotenv-java) - Leer .env files
- [Spring Cloud Vault](https://spring.io/projects/spring-cloud-vault) - Integración con Vault
- [External Secrets Operator](https://external-secrets.io/) - K8s operator para secrets externos

---

## ✅ Conclusión

**Tu solución actual es enterprise-grade porque:**

1. ✅ Usa `.env` solo para desarrollo (gitignored)
2. ✅ Prioriza variables de entorno del sistema
3. ✅ Separación clara entre dev/staging/prod
4. ✅ Preparado para escalar a Vault/AWS en producción
5. ✅ Documentación completa de la estrategia

**Próximos pasos para producción:**
- Implementar HashiCorp Vault o AWS Secrets Manager
- Configurar rotación automática de secrets
- Habilitar audit logging
- Implementar External Secrets Operator en K8s

**Estás listo para trabajar en cualquier empresa enterprise.** 🚀

