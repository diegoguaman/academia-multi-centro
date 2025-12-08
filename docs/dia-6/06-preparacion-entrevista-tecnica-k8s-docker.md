# 🎯 Preparación para Entrevista Técnica: Kubernetes y Docker

## 📋 Índice

1. [Conceptos Básicos](#1-conceptos-básicos)
2. [Conceptos Intermedios](#2-conceptos-intermedios)
3. [Conceptos Avanzados](#3-conceptos-avanzados)
4. [Preguntas Técnicas con Ejemplos de tu Proyecto](#4-preguntas-técnicas-con-ejemplos-de-tu-proyecto)
5. [Escenarios Prácticos](#5-escenarios-prácticos)
6. [Preguntas de Arquitectura](#6-preguntas-de-arquitectura)

---

## 1. Conceptos Básicos

### 1.1. ¿Qué es Docker?

**Respuesta Estándar**:
"Docker es una plataforma de contenedorización que permite empaquetar aplicaciones y sus dependencias en contenedores ligeros y portables. Los contenedores son aislados del sistema host pero comparten el kernel del sistema operativo."

**Ejemplo de tu Proyecto**:
"En mi proyecto de academia, dockericé mi aplicación Spring Boot usando un Dockerfile multi-stage. Esto redujo el tamaño de la imagen de 800MB a 548MB, optimizando el tiempo de despliegue."

**Conceptos Clave**:
- **Imagen**: Plantilla read-only para crear contenedores
- **Contenedor**: Instancia ejecutable de una imagen
- **Dockerfile**: Script que define cómo construir una imagen
- **Registry**: Repositorio de imágenes (Docker Hub, GCR, ECR)

### 1.2. ¿Qué es Kubernetes?

**Respuesta Estándar**:
"Kubernetes (K8s) es una plataforma de orquestación de contenedores que automatiza el despliegue, escalado y gestión de aplicaciones contenedorizadas. Proporciona abstracciones como Pods, Deployments y Services."

**Ejemplo de tu Proyecto**:
"Desplegué mi aplicación en un cluster K3d local con 3 nodos (1 master + 2 workers). Configuré un Deployment con health checks usando Spring Boot Actuator, y un Service tipo LoadBalancer para exponer la aplicación."

**Conceptos Clave**:
- **Pod**: Unidad más pequeña de despliegue (1 o más contenedores)
- **Node**: Máquina física o virtual que corre pods
- **Cluster**: Conjunto de nodos que trabajan juntos
- **Namespace**: División lógica de recursos en un cluster

### 1.3. ¿Qué es K3d?

**Respuesta Estándar**:
"K3d es una herramienta que permite ejecutar K3s (Kubernetes ligero) en contenedores Docker. Es ideal para desarrollo y testing local, ya que simula un cluster real sin la complejidad de minikube."

**Ejemplo de tu Proyecto**:
"Usé K3d para crear un cluster local con `k3d cluster create mi-cluster-java --agents 2 --servers 1`. Esto me permitió probar mi despliegue antes de migrar a producción en GKE o AWS EKS."

---

## 2. Conceptos Intermedios

### 2.1. Docker Multi-Stage Build

**Pregunta**: "¿Qué es un multi-stage build y por qué lo usas?"

**Respuesta**:
"Un multi-stage build permite usar múltiples imágenes en un solo Dockerfile. La primera etapa compila la aplicación, y la segunda copia solo los artefactos necesarios. Esto reduce significativamente el tamaño de la imagen final."

**Ejemplo de tu Proyecto**:
```dockerfile
# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
RUN ./mvnw package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
COPY --from=builder /app/target/*.jar app.jar
```

"Esto redujo mi imagen de 800MB a 548MB, eliminando el JDK y las herramientas de build que no son necesarias en runtime."

**Beneficios**:
- ✅ Imágenes más pequeñas
- ✅ Mejor seguridad (menos superficie de ataque)
- ✅ Builds más rápidos (cache de layers)

### 2.2. Health Checks en Kubernetes

**Pregunta**: "¿Qué son los liveness y readiness probes?"

**Respuesta**:
"Los probes son verificaciones que Kubernetes hace periódicamente para determinar el estado de un pod:
- **Liveness probe**: Verifica si el pod está vivo. Si falla, K8s reinicia el pod.
- **Readiness probe**: Verifica si el pod está listo para recibir tráfico. Si falla, K8s lo remueve del Service."

**Ejemplo de tu Proyecto**:
```yaml
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  initialDelaySeconds: 20
  periodSeconds: 15

readinessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 10
```

"Configuré ambos probes para usar Spring Boot Actuator. El liveness probe tiene un delay inicial mayor porque la aplicación tarda ~7 segundos en iniciar."

**Diferencia Clave**:
- **Liveness**: "¿Está el proceso corriendo?" → Si no, reinicia
- **Readiness**: "¿Puede manejar requests?" → Si no, remueve del load balancer

### 2.3. Spring Boot Actuator

**Pregunta**: "¿Por qué es importante Actuator en Kubernetes?"

**Respuesta**:
"Spring Boot Actuator expone endpoints de producción para monitoreo y gestión. En Kubernetes, es crítico porque:
1. Los health checks dependen de `/actuator/health`
2. Proporciona métricas para Prometheus
3. Permite debugging sin acceso SSH"

**Ejemplo de tu Proyecto**:
"Tuve un problema inicial: Actuator devolvía 403 Forbidden. Descubrí que Spring Security bloqueaba los endpoints por defecto. Solucioné configurando `.requestMatchers("/actuator/**").permitAll()` en SecurityConfig."

**Configuración**:
```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
management.health.livenessState.enabled=true
management.health.readinessState.enabled=true
```

### 2.4. Variables de Entorno en Docker vs Kubernetes

**Pregunta**: "¿Cómo gestionas las variables de entorno?"

**Respuesta**:
"En desarrollo local, uso un archivo `.env` que se carga automáticamente. En Docker, paso las variables con `-e`. En Kubernetes, uso Secrets y ConfigMaps para producción."

**Ejemplo de tu Proyecto**:

**Docker**:
```powershell
docker run -e DB_SUPABASE="..." -e DB_PASSWORD="..." academia-multi-centro:latest
```

**Kubernetes** (producción):
```yaml
env:
- name: DB_PASSWORD
  valueFrom:
    secretKeyRef:
      name: academia-secrets
      key: DB_PASSWORD
```

"Implementé un sistema de prioridad: variables de entorno del sistema > .env (solo dev) > application.properties defaults. En producción, el perfil 'prod' desactiva la carga de .env."

### 2.5. Tipos de Services en Kubernetes

**Pregunta**: "¿Qué tipos de Services conoces y cuándo usar cada uno?"

**Respuesta**:

1. **ClusterIP** (default):
   - Solo accesible dentro del cluster
   - Uso: Comunicación interna entre pods

2. **NodePort**:
   - Expone el servicio en un puerto de cada nodo
   - Uso: Testing, acceso directo desde fuera del cluster

3. **LoadBalancer**:
   - Crea un balanceador de carga externo
   - Uso: Producción en cloud (GKE, AWS EKS)
   - En K3d local muestra `<pending>` (normal)

4. **ExternalName**:
   - Mapea a un DNS externo
   - Uso: Conectar a servicios externos

**Ejemplo de tu Proyecto**:
"Usé LoadBalancer porque simula mejor el comportamiento de producción. En K3d muestra `<pending>`, pero en GKE o AWS EKS obtendría automáticamente una IP pública."

---

## 3. Conceptos Avanzados

### 3.1. Resource Limits y Requests

**Pregunta**: "¿Qué son requests y limits?"

**Respuesta**:
"Requests y limits definen los recursos que un pod necesita:
- **Requests**: Recursos garantizados (scheduling)
- **Limits**: Máximo de recursos permitidos (throttling)"

**Ejemplo de tu Proyecto**:
```yaml
resources:
  requests:
    memory: "1Gi"
    cpu: "500m"
  limits:
    memory: "2Gi"
    cpu: "2000m"
```

"Configuré requests basándome en el uso real de la aplicación (~800MB memory, ~200m CPU). Los limits son el doble para permitir picos de carga."

**Por qué es importante**:
- **Requests**: Kubernetes usa esto para decidir en qué nodo colocar el pod
- **Limits**: Previene que un pod consuma todos los recursos del nodo

### 3.2. Image Pull Policies

**Pregunta**: "¿Qué es imagePullPolicy y cuándo usar cada valor?"

**Respuesta**:

1. **Always**: Siempre descarga la imagen (producción)
2. **IfNotPresent**: Descarga solo si no existe localmente
3. **Never**: Nunca descarga, solo usa imagen local (K3d local)

**Ejemplo de tu Proyecto**:
"En K3d local usé `imagePullPolicy: Never` porque importé la imagen manualmente con `k3d image import`. En producción (GKE/AWS), cambiaría a `Always` para asegurar que siempre use la última versión del registry."

### 3.3. Security Context

**Pregunta**: "¿Por qué es importante el security context?"

**Respuesta**:
"El security context define permisos y restricciones de seguridad para pods y contenedores. Ejecutar como root es un riesgo de seguridad."

**Ejemplo de tu Proyecto**:
```yaml
securityContext:
  runAsNonRoot: true
  runAsUser: 1000
```

"Configuré el Dockerfile para crear un usuario no-root (`spring:spring` con UID 1000) y lo especifiqué en el deployment. Esto sigue las mejores prácticas de seguridad."

**Beneficios**:
- ✅ Reduce superficie de ataque
- ✅ Cumple con compliance (PCI-DSS, SOC 2)
- ✅ Previene escalación de privilegios

### 3.4. Graceful Shutdown

**Pregunta**: "¿Cómo manejas el shutdown de la aplicación?"

**Respuesta**:
"Spring Boot tiene graceful shutdown que espera a que las requests en curso terminen antes de cerrar. En Kubernetes, esto es importante para evitar cortes abruptos."

**Ejemplo de tu Proyecto**:
```yaml
env:
- name: SPRING_BOOT_GRACEFUL_SHUTDOWN_ENABLED
  value: "false"  # En K3d, pero true en producción
```

"En producción, habilitaría graceful shutdown para que cuando Kubernetes termine un pod (durante un rollout), la aplicación termine las requests activas antes de cerrar."

**Configuración Spring Boot**:
```properties
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
```

### 3.5. Rolling Updates

**Pregunta**: "¿Cómo funciona un rolling update?"

**Respuesta**:
"Un rolling update actualiza los pods gradualmente, creando nuevos pods y terminando los antiguos. Esto asegura zero-downtime deployments."

**Ejemplo de tu Proyecto**:
"Cuando actualicé la imagen con `kubectl rollout restart`, Kubernetes:
1. Creó un nuevo pod con la nueva imagen
2. Esperó a que el readiness probe pasara
3. Terminó el pod antiguo
4. El servicio enrutó el tráfico al nuevo pod"

**Comandos**:
```powershell
# Ver estado del rollout
kubectl rollout status deployment/academia-deployment

# Ver historial
kubectl rollout history deployment/academia-deployment

# Rollback si es necesario
kubectl rollout undo deployment/academia-deployment
```

---

## 4. Preguntas Técnicas con Ejemplos de tu Proyecto

### 4.1. "¿Qué problemas enfrentaste y cómo los resolviste?"

**Respuesta Estructurada**:

**Problema 1: Error 403 en Actuator**
- **Causa**: Spring Security bloqueaba `/actuator/health`
- **Solución**: Configuré `.requestMatchers("/actuator/**").permitAll()` en SecurityConfig
- **Aprendizaje**: Actuator debe estar fuera de la autenticación para health checks

**Problema 2: Connection Refused en Docker**
- **Causa**: Variables de entorno no se pasaban al contenedor
- **Solución**: Pasé variables explícitamente con `-e` o usé el script `docker-run.ps1`
- **Aprendizaje**: Docker no hereda variables de entorno del host automáticamente

**Problema 3: ImagePullBackOff en Kubernetes**
- **Causa**: La imagen no estaba disponible en el cluster
- **Solución**: Importé la imagen con `k3d image import academia-multi-centro:latest -c mi-cluster-java`
- **Aprendizaje**: K3d necesita importar imágenes manualmente, a diferencia de GKE/AWS que las descarga del registry

**Problema 4: Pods en CrashLoopBackOff**
- **Causa**: Health checks fallaban porque la app tardaba en iniciar
- **Solución**: Aumenté `initialDelaySeconds` en los probes de 10s a 20s
- **Aprendizaje**: Los probes deben esperar suficiente tiempo para que la app inicie

### 4.2. "¿Cómo optimizaste tu imagen Docker?"

**Respuesta**:
"Implementé un multi-stage build que redujo el tamaño de 800MB a 548MB (31% de reducción):

1. **Stage 1 (Builder)**: Compila con JDK completo
2. **Stage 2 (Runtime)**: Solo copia el JAR y usa JRE

Además:
- Usé imagen base Alpine (más ligera)
- Creé usuario no-root para seguridad
- Configuré JVM flags para contenedores (`UseContainerSupport`, `MaxRAMPercentage`)"

**Métricas**:
- Tamaño inicial: 800MB
- Tamaño final: 548MB
- Reducción: 31%
- Tiempo de build: ~2 minutos

### 4.3. "¿Cómo verificas que tu despliegue está funcionando?"

**Respuesta**:
"Tengo un checklist de verificación:

1. **Deployment**: `kubectl get deployments` → 1/1 replicas
2. **Pods**: `kubectl get pods` → Running, Ready: 1/1, Restarts: 0
3. **Service**: `kubectl get svc` → Endpoints conectados
4. **Health Check**: `curl /actuator/health` → `{"status":"UP"}`
5. **Logs**: `kubectl logs` → Sin errores críticos
6. **Events**: `kubectl get events` → Sin ImagePullBackOff o CrashLoopBackOff

También uso Lens IDE para visualización y debugging."

### 4.4. "¿Cómo escalarías tu aplicación?"

**Respuesta**:
"Para escalar horizontalmente:

1. **Manual**: `kubectl scale deployment academia-deployment --replicas=3`
2. **HPA (Horizontal Pod Autoscaler)**: Basado en CPU/Memory
3. **VPA (Vertical Pod Autoscaler)**: Ajusta requests/limits automáticamente

**Ejemplo de HPA**:
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: academia-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: academia-deployment
  minReplicas: 1
  maxReplicas: 5
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

"En producción, configuraría HPA para escalar automáticamente entre 1 y 5 pods basado en CPU usage."

---

## 5. Escenarios Prácticos

### 5.1. Escenario: Pod No Inicia

**Pregunta**: "Un pod está en CrashLoopBackOff. ¿Cómo lo debuggeas?"

**Respuesta Paso a Paso**:

1. **Ver logs del pod**:
   ```powershell
   kubectl logs <pod-name> --previous
   ```

2. **Describir el pod**:
   ```powershell
   kubectl describe pod <pod-name>
   ```
   Buscar en Events: ImagePullBackOff, CrashLoopBackOff, Error

3. **Verificar configuración**:
   ```powershell
   kubectl get deployment <deployment-name> -o yaml
   ```

4. **Verificar recursos**:
   ```powershell
   kubectl top pod <pod-name>
   ```

**Ejemplo Real de tu Proyecto**:
"Tuve un pod en CrashLoopBackOff. Los logs mostraban 'Connection refused' a la base de datos. Descubrí que las variables de entorno no se estaban pasando correctamente. Solucioné verificando el deployment.yaml y asegurándome de que todas las variables estuvieran definidas."

### 5.2. Escenario: Health Check Falla

**Pregunta**: "Los health checks fallan constantemente. ¿Qué revisas?"

**Respuesta**:

1. **Verificar que Actuator está accesible**:
   ```powershell
   kubectl exec -it <pod-name> -- wget -qO- http://localhost:8080/actuator/health
   ```

2. **Verificar configuración de probes**:
   ```powershell
   kubectl get deployment -o yaml | grep -A 10 "livenessProbe\|readinessProbe"
   ```

3. **Verificar tiempos**:
   - `initialDelaySeconds`: ¿Es suficiente para que la app inicie?
   - `periodSeconds`: ¿Es muy frecuente?
   - `timeoutSeconds`: ¿Es muy corto?

4. **Ver logs de la aplicación**:
   ```powershell
   kubectl logs <pod-name> | grep -i "actuator\|health"
   ```

**Ejemplo Real**:
"Los probes fallaban porque `initialDelaySeconds` era 10s pero la app tardaba ~7s en iniciar más ~3s en conectar a la DB. Aumenté a 20s y funcionó perfectamente."

### 5.3. Escenario: Migración a Producción

**Pregunta**: "¿Qué cambios harías para migrar de K3d a GKE?"

**Respuesta**:

1. **Cambiar imagePullPolicy**:
   ```yaml
   imagePullPolicy: Always  # Era Never en K3d
   ```

2. **Cambiar imagen a GCR**:
   ```yaml
   image: gcr.io/PROJECT-ID/academia-multi-centro:latest
   ```

3. **Usar Secrets en lugar de env vars**:
   ```yaml
   env:
   - name: DB_PASSWORD
     valueFrom:
       secretKeyRef:
         name: academia-secrets
         key: DB_PASSWORD
   ```

4. **Configurar Ingress** (opcional):
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
               name: academia-service
               port:
                 number: 80
   ```

5. **Habilitar HPA**:
   ```yaml
   # HorizontalPodAutoscaler configurado
   ```

6. **Configurar Network Policies** (seguridad):
   ```yaml
   apiVersion: networking.k8s.io/v1
   kind: NetworkPolicy
   metadata:
     name: academia-netpol
   spec:
     podSelector:
       matchLabels:
         app: academia
     policyTypes:
     - Ingress
     - Egress
   ```

---

## 6. Preguntas de Arquitectura

### 6.1. "¿Por qué elegiste K3d sobre Minikube?"

**Respuesta**:
"K3d tiene varias ventajas:
1. **Más ligero**: K3s es más pequeño que Kubernetes completo
2. **Multi-node fácil**: Crear múltiples nodos es más simple
3. **Mejor para CI/CD**: Se integra mejor en pipelines
4. **Simula mejor producción**: Comportamiento más cercano a un cluster real

Minikube es bueno para aprender, pero K3d es mejor para desarrollo profesional."

### 6.2. "¿Cómo manejarías secrets en producción?"

**Respuesta**:
"En producción, nunca pondría secrets en texto plano. Usaría:

1. **Kubernetes Secrets** (básico):
   ```yaml
   apiVersion: v1
   kind: Secret
   metadata:
     name: academia-secrets
   type: Opaque
   stringData:
     DB_PASSWORD: "secret-value"
   ```

2. **External Secrets Operator** (recomendado):
   - Integra con AWS Secrets Manager, HashiCorp Vault, etc.
   - Sincroniza automáticamente
   - Rotación automática

3. **HashiCorp Vault** (enterprise):
   - Gestión centralizada
   - Audit logging
   - Políticas de acceso granulares

**En mi proyecto**, implementaría External Secrets Operator para integrar con AWS Secrets Manager o GCP Secret Manager."

### 6.3. "¿Cómo monitorearías la aplicación en producción?"

**Respuesta**:
"Implementaría un stack completo de observabilidad:

1. **Métricas**: Prometheus + Grafana
   - Actuator expone métricas en `/actuator/prometheus`
   - Grafana dashboards para visualización

2. **Logs**: ELK Stack o Loki
   - Centralización de logs
   - Búsqueda y análisis

3. **Tracing**: Jaeger o Zipkin
   - Trazado distribuido de requests
   - Identificar cuellos de botella

4. **Alertas**: AlertManager
   - Alertas basadas en métricas
   - Notificaciones a Slack/PagerDuty

**Ejemplo de configuración**:
```yaml
# ServiceMonitor para Prometheus
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: academia-monitor
spec:
  selector:
    matchLabels:
      app: academia
  endpoints:
  - port: http
    path: /actuator/prometheus
```

### 6.4. "¿Cómo implementarías CI/CD?"

**Respuesta**:
"Usaría GitHub Actions con este flujo:

1. **Build**: Compilar JAR con Maven
2. **Test**: Ejecutar tests unitarios e integración
3. **Build Image**: Construir imagen Docker
4. **Push to Registry**: Subir a GCR o ECR
5. **Deploy to K8s**: Actualizar deployment con nueva imagen
6. **Smoke Tests**: Verificar que el despliegue funciona
7. **Rollback**: Automático si los tests fallan

**Ejemplo de pipeline**:
```yaml
# .github/workflows/deploy.yml
name: Deploy to Kubernetes
on:
  push:
    branches: [main]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Build JAR
      run: ./mvnw package -DskipTests
    - name: Build Docker Image
      run: docker build -t gcr.io/$PROJECT_ID/academia:$GITHUB_SHA .
    - name: Push to GCR
      run: docker push gcr.io/$PROJECT_ID/academia:$GITHUB_SHA
    - name: Deploy to GKE
      run: |
        kubectl set image deployment/academia-deployment \
          academia=gcr.io/$PROJECT_ID/academia:$GITHUB_SHA
        kubectl rollout status deployment/academia-deployment
```

---

## 7. Preguntas de Seguridad

### 7.1. "¿Qué medidas de seguridad implementaste?"

**Respuesta**:
"Implementé varias capas de seguridad:

1. **Usuario no-root**: Contenedor corre como usuario sin privilegios
2. **Resource limits**: Previene DoS por consumo excesivo de recursos
3. **Secrets management**: Secrets en Kubernetes, no en código
4. **Network policies**: Restricción de tráfico entre pods
5. **Image scanning**: Escaneo de vulnerabilidades en imágenes
6. **RBAC**: Control de acceso basado en roles

**Ejemplo de mi proyecto**:
```yaml
securityContext:
  runAsNonRoot: true
  runAsUser: 1000
  allowPrivilegeEscalation: false
  readOnlyRootFilesystem: true  # En producción
```

### 7.2. "¿Cómo protegerías la comunicación entre servicios?"

**Respuesta**:
"Implementaría:

1. **mTLS (Mutual TLS)**: Certificados para autenticación mutua
2. **Service Mesh (Istio/Linkerd)**: Gestión automática de mTLS
3. **Network Policies**: Restricción de tráfico a nivel de red
4. **Secrets en tránsito**: Encriptación de datos sensibles

**Ejemplo con Istio**:
```yaml
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
spec:
  mtls:
    mode: STRICT
```

---

## 8. Preguntas de Performance

### 8.1. "¿Cómo optimizaste el rendimiento?"

**Respuesta**:
"Varias optimizaciones:

1. **Multi-stage build**: Imagen más pequeña = pull más rápido
2. **JVM flags para contenedores**:
   ```dockerfile
   ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
   ```
3. **Connection pooling**: HikariCP configurado correctamente
4. **Resource requests/limits**: Basados en uso real
5. **Readiness probes**: Evita enviar tráfico a pods no listos

**Métricas de mi proyecto**:
- Tiempo de inicio: ~7 segundos
- Health check: < 100ms
- Memory usage: ~800MB de 2Gi límite
- CPU usage: ~200m de 2000m límite"

### 8.2. "¿Cómo manejarías un pico de tráfico?"

**Respuesta**:
"Estrategia multi-capa:

1. **HPA**: Escalar automáticamente basado en CPU/Memory
2. **Pod Disruption Budget**: Mantener mínimo de pods durante updates
3. **Resource quotas**: Asegurar recursos disponibles
4. **CDN**: Para assets estáticos
5. **Caching**: Redis para datos frecuentes
6. **Database connection pooling**: Optimizar conexiones DB

**Ejemplo de HPA**:
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
spec:
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        averageUtilization: 70
```

---

## 9. Preguntas de Troubleshooting

### 9.1. "¿Cuál es tu proceso de debugging?"

**Respuesta**:
"Proceso sistemático:

1. **Ver estado general**:
   ```powershell
   kubectl get all
   kubectl get events --sort-by='.lastTimestamp'
   ```

2. **Inspeccionar el pod problemático**:
   ```powershell
   kubectl describe pod <pod-name>
   kubectl logs <pod-name> --previous
   ```

3. **Verificar configuración**:
   ```powershell
   kubectl get deployment <name> -o yaml
   kubectl get service <name> -o yaml
   ```

4. **Probar conectividad**:
   ```powershell
   kubectl exec -it <pod-name> -- sh
   # Dentro del pod: wget, curl, ping
   ```

5. **Usar herramientas visuales**: Lens IDE para overview rápido

**Ejemplo real**: "Cuando tuve ImagePullBackOff, el proceso fue:
1. `kubectl describe pod` → Mostró que la imagen no se encontraba
2. Verifiqué que la imagen estaba importada en K3d
3. Re-importé con `k3d image import`
4. Reinicié el deployment"

---

## 10. Resumen: Puntos Clave para Destacar

### ✅ Lo que Sabes (Nivel Intermedio-Alto)

1. **Docker**:
   - Multi-stage builds
   - Optimización de imágenes
   - Security best practices

2. **Kubernetes**:
   - Deployments, Services, Pods
   - Health checks (liveness/readiness)
   - Resource management
   - Rolling updates

3. **Spring Boot**:
   - Actuator para health checks
   - Configuración de seguridad
   - Variables de entorno

4. **Troubleshooting**:
   - Debugging de pods
   - Resolución de problemas comunes
   - Uso de herramientas (Lens, kubectl)

### 🎯 Lo que Puedes Aprender Rápido (Mencionar)

1. **Service Mesh** (Istio/Linkerd)
2. **Advanced Autoscaling** (VPA, Cluster Autoscaler)
3. **GitOps** (ArgoCD, Flux)
4. **Advanced Monitoring** (Prometheus, Grafana)

### 💡 Frases Clave para la Entrevista

- "Implementé un despliegue completo de Docker a Kubernetes"
- "Resolví problemas de configuración que me enseñaron mucho sobre cómo funcionan los contenedores"
- "Optimicé la imagen Docker reduciendo el tamaño en 31%"
- "Configuré health checks correctamente para asegurar alta disponibilidad"
- "Estoy listo para migrar a producción en GKE o AWS EKS"

---

## 11. Preguntas para Hacer al Entrevistador

1. "¿Qué stack de observabilidad usan? (Prometheus, Datadog, etc.)"
2. "¿Cómo manejan los secrets? ¿Usan Vault o Secrets Manager nativo?"
3. "¿Qué estrategia de deployment usan? (Blue-green, Canary, Rolling)"
4. "¿Tienen Service Mesh implementado?"
5. "¿Cómo es su proceso de CI/CD?"

---

**¡Buena suerte en tu entrevista!** 🚀

Recuerda: No necesitas saber todo. Lo importante es demostrar que puedes aprender y resolver problemas.
