# 🎯 Preguntas de Entrevista: Docker y Kubernetes (Media/Alta Dificultad)

## 📋 Introducción

Este documento contiene preguntas técnicas sobre Docker y Kubernetes diseñadas para entrevistas de nivel **medio a alto**. Las preguntas cubren conceptos fundamentales, mejores prácticas, troubleshooting y arquitectura.

---

## 🐳 Docker: Preguntas Técnicas

### Nivel: Medio

#### 1. ¿Qué es un multi-stage build en Docker y cuáles son sus ventajas?

**Respuesta esperada:**

Un multi-stage build permite usar múltiples `FROM` statements en un Dockerfile, donde cada stage puede tener diferentes bases y propósitos.

**Ventajas:**
- **Reducción de tamaño**: La imagen final solo contiene lo necesario para runtime, no herramientas de build
- **Seguridad**: No incluye compiladores, Maven, etc. en producción
- **Optimización de cache**: Cada stage se cachea independientemente
- **Separación de concerns**: Build tools separados de runtime

**Ejemplo:**
```dockerfile
# Stage 1: Build
FROM maven:3.8-openjdk-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime
FROM openjdk:21-jre-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Seguimiento:**
- "¿Cuánto reduce el tamaño típicamente?" → 50-70% (de ~800MB a ~200MB)
- "¿Cuándo NO usarías multi-stage?" → Si el build es muy simple o necesitas debug tools en producción

---

#### 2. Explica la diferencia entre CMD, ENTRYPOINT y RUN en Dockerfile.

**Respuesta esperada:**

- **RUN**: Ejecuta comandos durante el **build** de la imagen (instalar paquetes, crear directorios)
- **CMD**: Define el comando **por defecto** que se ejecuta al iniciar el contenedor (puede ser sobrescrito)
- **ENTRYPOINT**: Define el comando **fijo** que siempre se ejecuta (los argumentos se agregan, no se sobrescriben)

**Ejemplo práctico:**
```dockerfile
# RUN: Se ejecuta durante build
RUN apt-get update && apt-get install -y curl

# CMD: Comando por defecto (puede ser sobrescrito)
CMD ["java", "-jar", "app.jar"]
# docker run myimage → ejecuta java -jar app.jar
# docker run myimage /bin/bash → sobrescribe CMD, ejecuta /bin/bash

# ENTRYPOINT: Comando fijo (argumentos se agregan)
ENTRYPOINT ["java", "-jar"]
# docker run myimage app.jar → ejecuta java -jar app.jar
# docker run myimage other.jar → ejecuta java -jar other.jar
```

**Seguimiento:**
- "¿Cuándo usarías ENTRYPOINT vs CMD?" → ENTRYPOINT para comandos fijos, CMD para defaults flexibles
- "¿Puedes usar ambos?" → Sí, ENTRYPOINT define el comando base, CMD los argumentos por defecto

---

#### 3. ¿Qué es un Docker layer y cómo afecta al tamaño y build time?

**Respuesta esperada:**

Un layer es una capa de la imagen Docker. Cada instrucción en Dockerfile (excepto algunas) crea un nuevo layer.

**Características:**
- **Inmutables**: Una vez creados, no cambian
- **Cacheables**: Si no cambia el contenido, se reutiliza el layer cacheado
- **Acumulativos**: Cada layer se suma al anterior

**Optimización:**
```dockerfile
# ❌ MAL: Cada COPY crea un layer, si cambia src, se invalidan todas las capas
COPY pom.xml .
COPY src ./src
RUN mvn package

# ✅ BIEN: Dependencias se cachean separadamente
COPY pom.xml .
RUN mvn dependency:go-offline  # Cachea dependencias
COPY src ./src
RUN mvn package  # Solo rebuild si cambia src
```

**Seguimiento:**
- "¿Cómo verificas el tamaño de cada layer?" → `docker history <image>`
- "¿Cuántos layers es demasiado?" → No hay límite estricto, pero >30 puede ser problemático

---

### Nivel: Alto

#### 4. Explica cómo funciona el sistema de archivos de Docker (Union File System).

**Respuesta esperada:**

Docker usa un **Union File System** (UFS) que combina múltiples directorios (layers) en una sola vista unificada.

**Conceptos clave:**
- **Read-only layers**: Capas base de la imagen (inmutables)
- **Read-write layer**: Capa superior (container layer) donde se escriben cambios
- **Copy-on-Write (CoW)**: Si modificas un archivo, se copia al layer superior

**Ejemplo:**
```
Layer 4 (Container) [RW] ← Cambios en runtime
Layer 3 (Image)     [RO] ← Tu aplicación
Layer 2 (Image)     [RO] ← Dependencias
Layer 1 (Image)     [RO] ← OS base (Alpine)
```

**Implicaciones:**
- **Performance**: Escribir en layers superiores es más lento
- **Volúmenes**: Para datos persistentes, usar volúmenes (no layers)
- **Tamaño**: Solo se guardan diferencias, no copias completas

**Seguimiento:**
- "¿Por qué no deberías escribir logs en el filesystem del contenedor?" → Se acumulan en el layer RW, aumentando tamaño
- "¿Cómo optimizas para escritura frecuente?" → Usar volúmenes montados

---

#### 5. ¿Cómo implementarías health checks en Docker y qué diferencias hay con Kubernetes probes?

**Respuesta esperada:**

**Docker HEALTHCHECK:**
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1
```

**Parámetros:**
- `interval`: Frecuencia de checks
- `timeout`: Tiempo máximo de respuesta
- `start-period`: Tiempo de gracia al iniciar
- `retries`: Intentos antes de marcar como unhealthy

**Kubernetes Probes:**
```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 10
  failureThreshold: 3
  # Si falla → restart pod

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 5
  failureThreshold: 2
  # Si falla → remover de load balancer (no restart)
```

**Diferencias:**
- **Docker**: Solo marca como healthy/unhealthy (no acción automática)
- **Kubernetes**: Liveness → restart pod, Readiness → remover de servicio
- **Startup Probe**: Nuevo en K8s 1.16+ para apps con startup lento

**Seguimiento:**
- "¿Cuándo usarías startup probe?" → Apps con startup >30s (Spring Boot con muchas dependencias)
- "¿Qué pasa si el health check es muy agresivo?" → Puede causar restart loops

---

## ☸️ Kubernetes: Preguntas Técnicas

### Nivel: Medio

#### 6. Explica la diferencia entre Deployment, ReplicaSet y Pod.

**Respuesta esperada:**

**Jerarquía:**
```
Deployment (orquesta)
  └── ReplicaSet (gestiona réplicas)
      └── Pods (contenedores)
```

**Pod:**
- Unidad más pequeña en K8s
- Contiene uno o más contenedores
- Comparten network y storage
- Efímeros (se recrean si mueren)

**ReplicaSet:**
- Gestiona múltiples Pods idénticos
- Asegura que siempre haya N réplicas
- No se usa directamente (Deployment lo crea)

**Deployment:**
- Abstracción de alto nivel
- Gestiona ReplicaSets
- Permite rolling updates, rollbacks
- Declarativo (describes estado deseado)

**Ejemplo:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: academia-app
spec:
  replicas: 3  # Deployment crea ReplicaSet con 3 Pods
  selector:
    matchLabels:
      app: academia-app
  template:
    metadata:
      labels:
        app: academia-app
    spec:
      containers:
      - name: app
        image: academia-multi-centro:latest
```

**Seguimiento:**
- "¿Cuándo usarías StatefulSet en vez de Deployment?" → Apps con estado (DBs, que necesitan identidad estable)
- "¿Qué es un DaemonSet?" → Un Pod por nodo (para logging, monitoring)

---

#### 7. ¿Cómo funcionan los Secrets en Kubernetes y cuáles son las mejores prácticas?

**Respuesta esperada:**

**Creación:**
```bash
# Desde línea de comandos
kubectl create secret generic my-secret \
  --from-literal=password=secret123

# Desde archivo
kubectl create secret generic my-secret \
  --from-file=password.txt
```

**Uso en Pod:**
```yaml
env:
- name: DB_PASSWORD
  valueFrom:
    secretKeyRef:
      name: my-secret
      key: password

# O inyectar todo el secret
envFrom:
- secretRef:
    name: my-secret
```

**Mejores prácticas:**
1. **NO commitear secrets** en Git (usar External Secrets Operator)
2. **Encriptar en etcd** (encryption at rest)
3. **RBAC** para limitar acceso
4. **Rotar periódicamente** (cada 90 días)
5. **Usar External Secrets** (Vault, AWS Secrets Manager)

**Limitaciones:**
- Secrets están en **base64** (no encriptados por defecto)
- Cualquiera con acceso a etcd puede leerlos
- No hay rotación automática nativa

**Seguimiento:**
- "¿Cómo encriptas secrets en etcd?" → EncryptionConfiguration en kube-apiserver
- "¿Qué es External Secrets Operator?" → Integración con Vault/AWS para secrets externos

---

#### 8. Explica los diferentes tipos de Services en Kubernetes.

**Respuesta esperada:**

**ClusterIP** (default):
- IP interna del cluster
- Solo accesible dentro del cluster
- Usado para comunicación entre pods

**NodePort**:
- Expone puerto en cada nodo (30000-32767)
- Accesible desde fuera: `<NodeIP>:<NodePort>`
- Útil para desarrollo/testing

**LoadBalancer**:
- Crea un load balancer externo (cloud provider)
- IP pública asignada automáticamente
- Usado en producción (GKE, EKS, AKS)

**ExternalName**:
- Mapea a DNS externo
- No crea proxy, solo DNS CNAME
- Útil para servicios externos

**Ejemplo:**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: academia-app-service
spec:
  type: LoadBalancer  # o ClusterIP, NodePort
  selector:
    app: academia-app
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
```

**Seguimiento:**
- "¿Cuándo usarías Ingress en vez de LoadBalancer?" → Para múltiples servicios con routing por path/host
- "¿Qué es Headless Service?" → ClusterIP con `clusterIP: None`, devuelve IPs de pods directamente

---

### Nivel: Alto

#### 9. ¿Cómo implementarías un rolling update sin downtime en Kubernetes?

**Respuesta esperada:**

**Estrategia de Deployment:**
```yaml
apiVersion: apps/v1
kind: Deployment
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1        # Máximo pods nuevos durante update
      maxUnavailable: 0  # Máximo pods inaccesibles (0 = sin downtime)
  template:
    spec:
      containers:
      - name: app
        image: academia-multi-centro:v2.0.0
```

**Proceso:**
1. K8s crea nuevo ReplicaSet con imagen nueva
2. Crea 1 pod nuevo (maxSurge: 1)
3. Espera a que pase readiness probe
4. Elimina 1 pod viejo
5. Repite hasta completar

**Sin downtime garantizado si:**
- `maxUnavailable: 0` (siempre hay pods disponibles)
- `replicas >= 2` (mínimo para HA)
- Readiness probe funcional

**Alternativas:**
- **Blue-Green**: Deploy versión nueva, cambiar tráfico
- **Canary**: Deploy a % de tráfico, monitorear, escalar gradualmente

**Seguimiento:**
- "¿Qué pasa si el nuevo deployment falla?" → Rollback automático si health checks fallan
- "¿Cómo haces rollback manual?" → `kubectl rollout undo deployment/academia-app`

---

#### 10. Explica cómo funciona el DNS en Kubernetes y cómo resuelven los Pods entre sí.

**Respuesta esperada:**

**DNS interno:**
- Cada Service tiene un DNS entry: `<service-name>.<namespace>.svc.cluster.local`
- Pods pueden resolverse por IP o DNS name

**Componentes:**
- **CoreDNS**: Servidor DNS del cluster (reemplazó a kube-dns)
- **Service discovery**: Automático vía DNS

**Ejemplo:**
```yaml
# Service
apiVersion: v1
kind: Service
metadata:
  name: academia-app
  namespace: production
spec:
  selector:
    app: academia-app
```

**Resolución desde otro Pod:**
```bash
# FQDN completo
academia-app.production.svc.cluster.local

# Namespace actual (si estás en 'production')
academia-app

# Namespace diferente
academia-app.staging.svc.cluster.local
```

**Configuración CoreDNS:**
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: coredns
  namespace: kube-system
data:
  Corefile: |
    .:53 {
        errors
        health
        kubernetes cluster.local {
            pods insecure
            fallthrough in-addr.arpa ip6.arpa
        }
        forward . /etc/resolv.conf
        cache 30
    }
```

**Seguimiento:**
- "¿Cómo debuggeas problemas de DNS?" → `kubectl exec -it <pod> -- nslookup <service>`
- "¿Qué es un Headless Service y cómo afecta DNS?" → Devuelve IPs de pods directamente (útil para StatefulSets)

---

## 🔧 Troubleshooting: Preguntas Prácticas

### Nivel: Medio-Alto

#### 11. Un Pod está en estado `CrashLoopBackOff`. ¿Cómo lo debuggeas?

**Respuesta esperada:**

**Pasos de debugging:**

1. **Ver logs del Pod:**
   ```bash
   kubectl logs <pod-name> -n <namespace>
   kubectl logs <pod-name> --previous  # Logs del contenedor anterior
   ```

2. **Ver eventos:**
   ```bash
   kubectl describe pod <pod-name>
   # Buscar Events section
   ```

3. **Verificar configuración:**
   ```bash
   kubectl get pod <pod-name> -o yaml
   # Verificar:
   # - Variables de entorno
   # - Resource limits
   # - Health checks
   ```

4. **Ejecutar comando en Pod (si está corriendo):**
   ```bash
   kubectl exec -it <pod-name> -- /bin/sh
   # Verificar archivos, variables de entorno
   ```

5. **Verificar dependencias:**
   - Secrets/ConfigMaps existen
   - Services están disponibles
   - Volúmenes montados correctamente

**Causas comunes:**
- Variables de entorno faltantes/incorrectas
- Health checks fallando
- Resource limits muy bajos (OOMKilled)
- Imagen incorrecta o no existe
- Puertos incorrectos

**Seguimiento:**
- "¿Cómo diferencias CrashLoopBackOff de ImagePullBackOff?" → CrashLoop = contenedor inicia y muere, ImagePull = no puede descargar imagen

---

#### 12. ¿Cómo optimizarías el uso de recursos (CPU/Memory) en un cluster Kubernetes?

**Respuesta esperada:**

**1. Resource Requests y Limits:**
```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "500m"      # 0.5 cores
  limits:
    memory: "1Gi"
    cpu: "1000m"     # 1 core
```

**2. Horizontal Pod Autoscaler (HPA):**
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: academia-app-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: academia-app
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

**3. Vertical Pod Autoscaler (VPA):**
- Ajusta requests/limits automáticamente
- Basado en uso histórico

**4. Cluster Autoscaler:**
- Agrega/elimina nodos según demanda
- Requiere cloud provider (GKE, EKS, AKS)

**5. Node Affinity/Anti-Affinity:**
```yaml
affinity:
  nodeAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      nodeSelectorTerms:
      - matchExpressions:
        - key: instance-type
          operator: In
          values:
          - compute-optimized
```

**Seguimiento:**
- "¿Qué pasa si no defines requests?" → Scheduler no puede optimizar placement, puede causar overcommit
- "¿Cuándo usarías VPA vs HPA?" → VPA para ajustar recursos de pods existentes, HPA para escalar número de pods

---

## 🏗️ Arquitectura: Preguntas de Diseño

### Nivel: Alto

#### 13. Diseña una arquitectura Kubernetes para una aplicación Spring Boot con alta disponibilidad.

**Respuesta esperada:**

**Componentes:**

1. **Deployment con múltiples réplicas:**
   ```yaml
   replicas: 3
   strategy:
     type: RollingUpdate
     rollingUpdate:
       maxSurge: 1
       maxUnavailable: 0
   ```

2. **Service LoadBalancer:**
   ```yaml
   type: LoadBalancer
   sessionAffinity: ClientIP  # Si necesitas sticky sessions
   ```

3. **Ingress para routing:**
   ```yaml
   apiVersion: networking.k8s.io/v1
   kind: Ingress
   metadata:
     name: academia-ingress
   spec:
     rules:
     - host: api.academia.com
       http:
         paths:
         - path: /
           pathType: Prefix
           backend:
             service:
               name: academia-app-service
               port:
                 number: 80
   ```

4. **ConfigMap + Secrets:**
   - ConfigMap: Configuración no-sensible
   - Secrets: Passwords, JWT keys (desde Vault)

5. **HPA para auto-scaling:**
   ```yaml
   minReplicas: 3
   maxReplicas: 10
   targetCPUUtilization: 70
   ```

6. **Pod Disruption Budget:**
   ```yaml
   apiVersion: policy/v1
   kind: PodDisruptionBudget
   metadata:
     name: academia-pdb
   spec:
     minAvailable: 2
     selector:
       matchLabels:
         app: academia-app
   ```

7. **Monitoring:**
   - Prometheus para métricas
   - Grafana para dashboards
   - ELK para logs

**Seguimiento:**
- "¿Cómo manejarías sesiones stateful?" → Sticky sessions o external session store (Redis)
- "¿Qué es Pod Disruption Budget?" → Garantiza mínimo de pods disponibles durante mantenimiento

---

## 📚 Preguntas de Seguimiento Generales

### Conceptos Avanzados

1. **"Explica la diferencia entre StatefulSet y Deployment"**
   - StatefulSet: Identidad estable, ordenado, volúmenes persistentes
   - Deployment: Stateless, sin orden, sin identidad

2. **"¿Qué es un Operator en Kubernetes?"**
   - Extensión de K8s que gestiona aplicaciones complejas
   - Usa Custom Resources (CRDs)
   - Ejemplo: PostgreSQL Operator, Prometheus Operator

3. **"¿Cómo implementarías CI/CD con Kubernetes?"**
   - GitOps (ArgoCD, Flux)
   - Jenkins X
   - Tekton Pipelines
   - GitHub Actions → Build → Push → Deploy

4. **"Explica Network Policies en Kubernetes"**
   - Firewall a nivel de pod
   - Controla tráfico entre pods
   - Default: todo permitido (sin NetworkPolicy)

---

## ✅ Checklist de Preparación

Antes de la entrevista, asegúrate de poder:

- [ ] Explicar multi-stage builds y sus ventajas
- [ ] Diferenciar CMD, ENTRYPOINT, RUN
- [ ] Entender layers y optimización de cache
- [ ] Explicar Pod, ReplicaSet, Deployment
- [ ] Gestionar Secrets de forma segura
- [ ] Implementar rolling updates sin downtime
- [ ] Debuggear Pods en CrashLoopBackOff
- [ ] Diseñar arquitectura HA en K8s
- [ ] Explicar DNS y service discovery
- [ ] Optimizar recursos (HPA, VPA, requests/limits)

---

## 🎯 Tips para la Entrevista

1. **Sé específico**: Da ejemplos de código/YAML cuando sea posible
2. **Menciona trade-offs**: "X es mejor para Y, pero Z tiene limitaciones..."
3. **Habla de experiencia**: "En mi proyecto usé multi-stage builds y reduje el tamaño 60%"
4. **Pregunta contexto**: "¿Están usando cloud provider o on-premise?"
5. **Menciona best practices**: Seguridad, monitoring, observability

---

**✅ Conclusión**: Estas preguntas cubren desde conceptos fundamentales hasta arquitectura avanzada. Prepárate para explicar no solo el "qué" sino el "por qué" y "cuándo usar cada enfoque".
