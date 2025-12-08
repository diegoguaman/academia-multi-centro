# 🚀 Guía Completa: Docker → K3d → Presentación para Producción

## 📋 Índice

1. [Preparación de la Imagen Docker](#1-preparación-de-la-imagen-docker)
2. [Configuración de K3d](#2-configuración-de-k3d)
3. [Despliegue en Kubernetes](#3-despliegue-en-kubernetes)
4. [Verificación y Comprobación](#4-verificación-y-comprobación)
5. [Presentación con Lens](#5-presentación-con-lens)
6. [Checklist Final para Producción](#6-checklist-final-para-producción)

---

## 1. Preparación de la Imagen Docker

### 1.1. Compilar el JAR

```powershell
# Navegar al directorio del proyecto
cd C:\Users\diego\academia-multi-centro\academymanager

# Limpiar y compilar el proyecto (sin tests para velocidad)
.\mvnw clean package -DskipTests
```

**Verificación**:
```powershell
# Verificar que el JAR se generó correctamente
Get-ChildItem target\*.jar | Select-Object Name, LastWriteTime, Length
```

**Resultado Esperado**:
```
Name                                    LastWriteTime         Length
----                                    -------------         ------
academymanager-0.0.1-SNAPSHOT.jar      12/06/2025 14:30:00   45MB
```

### 1.2. Construir la Imagen Docker

```powershell
# Construir la imagen con tag latest
docker build -t academia-multi-centro:latest .
```

**Verificación**:
```powershell
# Verificar que la imagen se creó
docker images | grep academia-multi-centro
```

**Resultado Esperado**:
```
academia-multi-centro   latest   abc123def456   2 minutes ago   548MB
```

### 1.3. Probar la Imagen Localmente (Opcional pero Recomendado)

```powershell
# Detener contenedor anterior si existe
docker stop academia-app 2>$null
docker rm academia-app 2>$null

# Ejecutar contenedor con variables de entorno
docker run -d `
  --name academia-app `
  -p 8080:8080 `
  -e DB_SUPABASE="jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres?sslmode=require" `
  -e DB_USERNAME="postgres.wjbbuiiskercelchtaqg" `
  -e DB_PASSWORD="Ac4d3m1a_1994!" `
  -e JWT_SECRET_KEY="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970" `
  -e JWT_EXPIRATION_TIME="86400000" `
  -e SPRING_PROFILES_ACTIVE="prod" `
  academia-multi-centro:latest

# Esperar 10 segundos para que inicie
Start-Sleep -Seconds 10

# Verificar logs
docker logs academia-app | Select-Object -Last 20

# Probar health check
curl http://localhost:8080/actuator/health
```

**Resultado Esperado**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL"
      }
    }
  }
}
```

**Si funciona, detener el contenedor**:
```powershell
docker stop academia-app
docker rm academia-app
```

---

## 2. Configuración de K3d

### 2.1. Verificar Instalación de K3d

```powershell
# Verificar versión de k3d
k3d --version

# Verificar versión de kubectl
kubectl version --client
```

**Resultado Esperado**:
```
k3d version v5.8.3
k3s version v1.31.5-k3s1 (default)
```

### 2.2. Crear el Cluster K3d

```powershell
# Crear cluster con configuración multi-node (simula producción)
k3d cluster create mi-cluster-java `
  --agents 2 `
  --servers 1 `
  --port "8080:80@loadbalancer" `
  --wait
```

**Explicación de Parámetros**:
- `--agents 2`: 2 nodos worker (simula escalabilidad)
- `--servers 1`: 1 nodo master (control plane)
- `--port "8080:80@loadbalancer"`: Mapea puerto 8080 del host al 80 del loadbalancer
- `--wait`: Espera a que el cluster esté listo

**Verificación**:
```powershell
# Verificar que el cluster se creó
kubectl cluster-info

# Verificar nodos
kubectl get nodes
```

**Resultado Esperado**:
```
NAME                        STATUS   ROLES           AGE   VERSION
k3d-mi-cluster-java-server-0   Ready    control-plane   30s   v1.31.5-k3s1
k3d-mi-cluster-java-agent-0     Ready    <none>          28s   v1.31.5-k3s1
k3d-mi-cluster-java-agent-1     Ready    <none>          28s   v1.31.5-k3s1
```

### 2.3. Importar la Imagen al Cluster

```powershell
# Importar la imagen Docker al cluster K3d
k3d image import academia-multi-centro:latest -c mi-cluster-java
```

**Resultado Esperado**:
```
INFO[0000] Importing image(s) into cluster 'mi-cluster-java'
INFO[0000] Starting new tools node...
INFO[0004] Importing images into nodes...
INFO[0009] Successfully imported 1 image(s) into 1 cluster(s)
```

**Verificación**:
```powershell
# Verificar que la imagen está disponible en el cluster
kubectl run test-pod --image=academia-multi-centro:latest --dry-run=client -o yaml
```

---

## 3. Despliegue en Kubernetes

### 3.1. Crear Directorio de Manifests

```powershell
# Crear directorio para los manifests
mkdir k3d-deployment
cd k3d-deployment
```

### 3.2. Crear Deployment.yaml

Crea el archivo `deployment.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: academia-deployment
  labels:
    app: academia
spec:
  replicas: 1
  selector:
    matchLabels:
      app: academia
  template:
    metadata:
      labels:
        app: academia
    spec:
      containers:
      - name: academia
        image: academia-multi-centro:latest
        imagePullPolicy: Never  # Importante: usar imagen local
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: SPRING_BOOT_GRACEFUL_SHUTDOWN_ENABLED
          value: "false"
        - name: DB_SUPABASE
          value: "jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres?sslmode=require"
        - name: DB_USERNAME
          value: "postgres.wjbbuiiskercelchtaqg"
        - name: DB_PASSWORD
          value: "Ac4d3m1a_1994!"
        - name: JWT_SECRET_KEY
          value: "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
        - name: JWT_EXPIRATION_TIME
          value: "86400000"
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "2000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 15
          timeoutSeconds: 5
          failureThreshold: 5
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        securityContext:
          runAsNonRoot: true
          runAsUser: 1000
```

### 3.3. Crear Service.yaml

Crea el archivo `service.yaml`:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: academia-service
spec:
  selector:
    app: academia
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: LoadBalancer
```

### 3.4. Aplicar los Manifests

```powershell
# Aplicar deployment
kubectl apply -f deployment.yaml

# Aplicar service
kubectl apply -f service.yaml
```

**Resultado Esperado**:
```
deployment.apps/academia-deployment created
service/academia-service created
```

### 3.5. Verificar Estado Inicial

```powershell
# Ver estado del deployment
kubectl get deployments

# Ver pods (puede tardar unos segundos en iniciar)
kubectl get pods -w
```

**Resultado Esperado** (después de 20-30 segundos):
```
NAME                  READY   STATUS    RESTARTS   AGE
academia-deployment-xxx   1/1     Running   0          30s
```

---

## 4. Verificación y Comprobación

### 4.1. Verificación del Deployment

```powershell
# Estado del deployment
kubectl get deployments academia-deployment

# Historial de rollouts
kubectl rollout history deployment/academia-deployment

# Estado del rollout
kubectl rollout status deployment/academia-deployment
```

**Resultado Esperado**:
```
NAME                  READY   UP-TO-DATE   AVAILABLE   AGE
academia-deployment   1/1     1            1           2m
```

### 4.2. Verificación de Pods

```powershell
# Listar pods con detalles
kubectl get pods -l app=academia -o wide

# Describir el pod (información detallada)
kubectl describe pod -l app=academia

# Ver logs del pod
kubectl logs -l app=academia --tail=50
```

**Buscar en los Logs**:
- ✅ `Started AcademymanagerApplication in X seconds`
- ✅ `HikariPool-1 - Start completed`
- ✅ `Tomcat started on port 8080`
- ❌ NO debe haber: `Connection refused`, `CrashLoopBackOff`, `ImagePullBackOff`

### 4.3. Verificación de Health Checks

```powershell
# Ver eventos del pod (para verificar probes)
kubectl describe pod -l app=academia | Select-String -Pattern "Liveness|Readiness"

# Verificar que los probes están funcionando
kubectl get pod -l app=academia -o jsonpath='{.items[0].status.containerStatuses[0].ready}'
```

**Resultado Esperado**: `true`

### 4.4. Verificación del Servicio

```powershell
# Ver estado del servicio
kubectl get svc academia-service

# Describir el servicio
kubectl describe svc academia-service

# Ver endpoints del servicio
kubectl get endpoints academia-service
```

**Resultado Esperado**:
```
NAME               TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)   AGE
academia-service   LoadBalancer   10.43.24.253   <pending>     80/TCP    5m
```

**Nota**: `EXTERNAL-IP` mostrará `<pending>` en K3d local. Esto es normal. En producción (GKE/AWS) obtendrá una IP real.

### 4.5. Prueba de Conectividad (Port Forward)

```powershell
# Port forward del servicio (en una terminal separada)
kubectl port-forward svc/academia-service 8081:80
```

**En otra terminal o navegador**:
```powershell
# Probar health check
curl http://localhost:8081/actuator/health

# Probar info endpoint
curl http://localhost:8081/actuator/info
```

**Resultado Esperado**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL"
      }
    },
    "livenessState": {
      "status": "UP"
    },
    "readinessState": {
      "status": "UP"
    }
  }
}
```

### 4.6. Verificación de Recursos

```powershell
# Ver uso de recursos del pod
kubectl top pod -l app=academia

# Ver eventos del namespace
kubectl get events --sort-by='.lastTimestamp' | Select-Object -Last 20
```

---

## 5. Presentación con Lens

### 5.1. Configurar Lens

1. **Abrir Lens**
2. **Agregar Cluster**:
   - Click en "Add Cluster" o "+"
   - Seleccionar "Add kubeconfig from filesystem"
   - Navegar a: `C:\Users\diego\.kube\config`
   - Click en "Sync" o "Add"

3. **Verificar Conexión**:
   - El cluster `k3d-mi-cluster-java` debe aparecer en la lista
   - Click en el cluster para conectarse

### 5.2. Vistas Clave para la Presentación

#### Vista 1: Overview del Cluster

**Navegación**: Home → Cluster Overview

**Qué Mostrar**:
- ✅ Número de nodos (3: 1 server + 2 agents)
- ✅ Estado de los nodos (Ready)
- ✅ Versión de Kubernetes (v1.31.5-k3s1)

#### Vista 2: Deployments

**Navegación**: Workloads → Deployments → academia-deployment

**Qué Mostrar**:
- ✅ **Replicas**: Current: 1 / Ready: 1 / Available: 1
- ✅ **Status**: Running
- ✅ **Age**: Tiempo desde el despliegue
- ✅ **Pestaña "Pods"**: Ver el pod asociado

**Screenshot Clave**: Mostrar la sección de Replicas con todos los valores en verde.

#### Vista 3: Pod Details

**Navegación**: Workloads → Pods → Click en el pod `academia-deployment-xxx`

**Qué Mostrar**:

1. **Pestaña "Overview"**:
   - ✅ Status: Running
   - ✅ Ready: 1/1
   - ✅ Restarts: 0 (o bajo)
   - ✅ Node: En qué nodo está corriendo
   - ✅ IP: IP interna del pod

2. **Pestaña "Events"**:
   - ✅ Buscar: `Started`, `Created`, `Pulled`
   - ❌ NO debe haber: `ImagePullBackOff`, `CrashLoopBackOff`, `Error`

3. **Pestaña "Logs"**:
   - ✅ Mostrar logs de inicio exitoso
   - ✅ Buscar: "Started AcademymanagerApplication"
   - ✅ Mostrar que Actuator está expuesto

**Screenshot Clave**: Logs mostrando inicio exitoso.

#### Vista 4: Service

**Navegación**: Network → Services → academia-service

**Qué Mostrar**:
- ✅ **Type**: LoadBalancer
- ✅ **Ports**: 80 → 8080
- ✅ **Endpoints**: Debe mostrar el pod asociado
- ✅ **Selector**: `app: academia`

**Nota sobre Pending**: Si `EXTERNAL-IP` muestra `<pending>`, explicar:
- "En K3d local, el LoadBalancer muestra pending porque no hay un proveedor de LoadBalancer real"
- "En producción (GKE/AWS), esto se resolverá automáticamente y obtendrá una IP pública"

**Screenshot Clave**: Service con Endpoints conectados.

#### Vista 5: Health Checks (Probes)

**Navegación**: Workloads → Pods → Pod → Pestaña "Events"

**Qué Mostrar**:
- ✅ Eventos de `Liveness probe` ejecutándose
- ✅ Eventos de `Readiness probe` ejecutándose
- ✅ No debe haber fallos de probes

**Explicación para la Presentación**:
- "Los health checks están configurados para verificar `/actuator/health`"
- "Liveness probe: Si falla, Kubernetes reinicia el pod"
- "Readiness probe: Si falla, Kubernetes remueve el pod del load balancer"

#### Vista 6: Resource Usage (Opcional)

**Navegación**: Workloads → Pods → Pod → Pestaña "Metrics"

**Qué Mostrar** (si está disponible):
- CPU usage
- Memory usage
- Comparar con los límites configurados (2Gi memory, 2000m CPU)

---

## 6. Checklist Final para Producción

### ✅ Checklist Técnico

```powershell
# Script de verificación completo
Write-Host "=== VERIFICACIÓN PRE-PRODUCCIÓN ===" -ForegroundColor Cyan

# 1. Deployment
Write-Host "`n1. Deployment Status:" -ForegroundColor Yellow
kubectl get deployments academia-deployment

# 2. Pods
Write-Host "`n2. Pods Status:" -ForegroundColor Yellow
kubectl get pods -l app=academia

# 3. Service
Write-Host "`n3. Service Status:" -ForegroundColor Yellow
kubectl get svc academia-service

# 4. Health Check
Write-Host "`n4. Health Check:" -ForegroundColor Yellow
kubectl exec -it $(kubectl get pod -l app=academia -o jsonpath='{.items[0].metadata.name}') -- wget -qO- http://localhost:8080/actuator/health

# 5. Logs (últimas 10 líneas)
Write-Host "`n5. Application Logs (last 10 lines):" -ForegroundColor Yellow
kubectl logs -l app=academia --tail=10

# 6. Events
Write-Host "`n6. Recent Events:" -ForegroundColor Yellow
kubectl get events --sort-by='.lastTimestamp' | Select-Object -Last 5

Write-Host "`n=== VERIFICACIÓN COMPLETA ===" -ForegroundColor Green
```

### ✅ Checklist Funcional

- [ ] **Deployment**: 1/1 replicas disponibles
- [ ] **Pods**: Estado `Running`, `Ready: 1/1`, `Restarts: 0`
- [ ] **Service**: Endpoints conectados correctamente
- [ ] **Health Check**: `/actuator/health` responde `{"status":"UP"}`
- [ ] **Database**: Componente `db` muestra `"status": "UP"`
- [ ] **Logs**: Sin errores críticos (Connection refused, CrashLoop, etc.)
- [ ] **Probes**: Liveness y Readiness funcionando
- [ ] **Resources**: CPU y Memory dentro de los límites

### ✅ Checklist de Presentación

- [ ] **Lens Configurado**: Cluster visible y conectado
- [ ] **Screenshots Preparados**:
  - [ ] Overview del cluster
  - [ ] Deployment con replicas correctas
  - [ ] Pod en estado Running
  - [ ] Logs de inicio exitoso
  - [ ] Service con endpoints
  - [ ] Health check funcionando
- [ ] **Explicaciones Preparadas**:
  - [ ] Por qué K3d (simulación local de producción)
  - [ ] Cómo funcionan los health checks
  - [ ] Por qué LoadBalancer muestra pending (normal en K3d)
  - [ ] Cómo se escalaría en producción
  - [ ] Migración a GKE/AWS (solo cambiar kubeconfig)

---

## 7. Comandos de Demostración en Vivo

### Script de Demostración Completa

```powershell
# Script para ejecutar durante la presentación
Write-Host "=== DEMOSTRACIÓN EN VIVO ===" -ForegroundColor Cyan

# 1. Mostrar cluster
Write-Host "`n1. Cluster Info:" -ForegroundColor Yellow
kubectl cluster-info
kubectl get nodes

# 2. Mostrar deployment
Write-Host "`n2. Deployment:" -ForegroundColor Yellow
kubectl get deployments -o wide

# 3. Mostrar pods
Write-Host "`n3. Pods:" -ForegroundColor Yellow
kubectl get pods -o wide

# 4. Mostrar service
Write-Host "`n4. Service:" -ForegroundColor Yellow
kubectl get svc -o wide

# 5. Health check en vivo
Write-Host "`n5. Health Check (en vivo):" -ForegroundColor Yellow
kubectl port-forward svc/academia-service 8081:80 &
Start-Sleep -Seconds 2
curl http://localhost:8081/actuator/health
Stop-Process -Name kubectl -Force -ErrorAction SilentlyContinue

# 6. Mostrar logs
Write-Host "`n6. Application Logs:" -ForegroundColor Yellow
kubectl logs -l app=academia --tail=20

Write-Host "`n=== DEMOSTRACIÓN COMPLETA ===" -ForegroundColor Green
```

---

## 8. Preparación para Migración a Producción

### 8.1. Cambios Necesarios para GKE

```yaml
# deployment.yaml - Cambios para GKE
spec:
  containers:
  - name: academia
    image: gcr.io/PROJECT-ID/academia-multi-centro:latest  # Cambiar a GCR
    imagePullPolicy: Always  # Cambiar de Never a Always
    # ... resto igual
```

### 8.2. Cambios Necesarios para AWS EKS

```yaml
# deployment.yaml - Cambios para EKS
spec:
  containers:
  - name: academia
    image: ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/academia-multi-centro:latest
    imagePullPolicy: Always
    # ... resto igual
```

### 8.3. Secrets Management (Producción)

**En lugar de variables de entorno en texto plano, usar Secrets**:

```yaml
# secrets.yaml (NO commitear a Git)
apiVersion: v1
kind: Secret
metadata:
  name: academia-secrets
type: Opaque
stringData:
  DB_SUPABASE: "jdbc:postgresql://..."
  DB_USERNAME: "postgres.wjbbuiiskercelchtaqg"
  DB_PASSWORD: "Ac4d3m1a_1994!"
  JWT_SECRET_KEY: "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
```

**Actualizar deployment.yaml**:
```yaml
env:
- name: DB_PASSWORD
  valueFrom:
    secretKeyRef:
      name: academia-secrets
      key: DB_PASSWORD
```

---

## 9. Troubleshooting Rápido

### Problema: Pod en CrashLoopBackOff

```powershell
# Ver logs del pod
kubectl logs -l app=academia --previous

# Ver eventos
kubectl describe pod -l app=academia
```

### Problema: ImagePullBackOff

```powershell
# Verificar que la imagen está importada
kubectl get pod -l app=academia -o jsonpath='{.items[0].status.containerStatuses[0].image}'

# Re-importar imagen
k3d image import academia-multi-centro:latest -c mi-cluster-java
```

### Problema: Health Check Falla

```powershell
# Verificar que Actuator está accesible
kubectl exec -it $(kubectl get pod -l app=academia -o jsonpath='{.items[0].metadata.name}') -- wget -qO- http://localhost:8080/actuator/health

# Verificar configuración de probes
kubectl get deployment academia-deployment -o yaml | Select-String -Pattern "livenessProbe|readinessProbe"
```

---

## 10. Resumen de Comandos Esenciales

```powershell
# === CONSTRUCCIÓN ===
.\mvnw clean package -DskipTests
docker build -t academia-multi-centro:latest .

# === K3D ===
k3d cluster create mi-cluster-java --agents 2 --servers 1
k3d image import academia-multi-centro:latest -c mi-cluster-java

# === DESPLIEGUE ===
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

# === VERIFICACIÓN ===
kubectl get deployments
kubectl get pods -w
kubectl get svc
kubectl logs -l app=academia
kubectl port-forward svc/academia-service 8081:80

# === HEALTH CHECK ===
curl http://localhost:8081/actuator/health
```

---

**¡Listo para la Presentación!** 🎉

Esta guía te permite demostrar que tu aplicación está completamente lista para producción, con todos los checks necesarios y una presentación visual profesional usando Lens.
