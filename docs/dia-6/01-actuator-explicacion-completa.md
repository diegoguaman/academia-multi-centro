# 📊 Spring Boot Actuator: Guía Completa para Producción

## ¿Qué es Spring Boot Actuator?

**Spring Boot Actuator** es un módulo de Spring Boot que proporciona **endpoints de producción** para monitorear y gestionar tu aplicación. Es esencial para entornos de producción y especialmente crítico para **Kubernetes deployments**.

### Características Principales

1. **Health Checks**: Verifica el estado de la aplicación (UP/DOWN)
2. **Metrics**: Métricas de rendimiento (memoria, CPU, requests, etc.)
3. **Info**: Información sobre la aplicación (versión, build, etc.)
4. **Audit Events**: Eventos de auditoría
5. **Environment**: Variables de entorno y configuración
6. **Loggers**: Gestión de niveles de logging en tiempo de ejecución

---

## 🎯 ¿Por qué es Importante para Producción?

### 1. **Kubernetes Health Checks (Liveness & Readiness Probes)**

Kubernetes usa Actuator para determinar si un pod está:
- **Liveness**: ¿Está vivo? Si falla, K8s reinicia el pod
- **Readiness**: ¿Está listo para recibir tráfico? Si falla, K8s lo remueve del load balancer

```yaml
# Ejemplo de tu deployment.yaml
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

**Sin Actuator**: Kubernetes no puede verificar el estado → pods se reinician constantemente o no reciben tráfico.

### 2. **Monitoreo y Observabilidad**

- **Prometheus**: Métricas expuestas en `/actuator/prometheus`
- **Grafana**: Dashboards con métricas de Actuator
- **Alertas**: Configurar alertas basadas en health checks

### 3. **Debugging en Producción**

- Ver configuración sin acceso SSH: `/actuator/env`
- Ver logs en tiempo real: `/actuator/loggers`
- Ver métricas de memoria/CPU: `/actuator/metrics`

### 4. **Seguridad en Producción**

Actuator permite:
- Exponer solo endpoints necesarios (health, info)
- Configurar acceso basado en roles
- Ocultar información sensible

---

## 🔧 Configuración Implementada

### 1. Dependencia en `pom.xml`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

✅ **Ya está agregado** en tu proyecto.

### 2. Configuración en `application.properties`

```properties
# Exponer solo endpoints necesarios (seguridad)
management.endpoints.web.exposure.include=health,info

# Mostrar detalles de health sin autenticación (para K8s)
management.endpoint.health.show-details=always

# Habilitar liveness y readiness probes
management.health.livenessState.enabled=true
management.health.readinessState.enabled=true

# Base path (por defecto /actuator)
management.endpoints.web.base-path=/actuator
```

✅ **Ya está configurado** en tu proyecto.

### 3. Configuración de Seguridad

```java
// SecurityConfig.java
.requestMatchers("/actuator/**").permitAll()  // Permitir acceso sin autenticación
```

✅ **Ya está configurado** para permitir acceso a Actuator.

### 4. Optimización del Filtro JWT

```java
// JwtAuthenticationFilter.java
// Skip JWT processing for Actuator endpoints
if (requestPath != null && requestPath.startsWith("/actuator")) {
    filterChain.doFilter(request, response);
    return;
}
```

✅ **Optimizado** para no procesar JWT en endpoints de Actuator.

---

## 🧪 Cómo Probar Actuator

### 1. **Probar Localmente (Desarrollo)**

#### Health Check Básico
```bash
# Desde terminal
curl http://localhost:8080/actuator/health

# Respuesta esperada:
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL"
      }
    },
    "diskSpace": {
      "status": "UP"
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

#### Info Endpoint
```bash
curl http://localhost:8080/actuator/info

# Respuesta esperada:
{
  "app": {
    "name": "academymanager",
    "version": "0.0.1-SNAPSHOT"
  }
}
```

#### Desde Navegador
1. Abre: `http://localhost:8080/actuator/health`
2. Deberías ver el JSON con el estado de la aplicación

### 2. **Probar en Docker**

```bash
# Construir imagen
docker build -t academia-multi-centro:latest .

# Ejecutar contenedor
docker run -d \
  -p 8080:8080 \
  -e DB_SUPABASE="jdbc:postgresql://..." \
  -e DB_USERNAME="postgres" \
  -e DB_PASSWORD="password" \
  -e JWT_SECRET_KEY="..." \
  --name academia-test \
  academia-multi-centro:latest

# Probar health check
curl http://localhost:8080/actuator/health
```

### 3. **Probar en Kubernetes (K3d)**

```bash
# Verificar que el pod está corriendo
kubectl get pods

# Ver logs del pod
kubectl logs <pod-name>

# Port forward para probar localmente
kubectl port-forward <pod-name> 8080:8080

# Probar health check
curl http://localhost:8080/actuator/health

# Verificar que los probes funcionan
kubectl describe pod <pod-name>
# Buscar "Liveness" y "Readiness" en la salida
```

### 4. **Probar desde el Navegador (K3d)**

Si tu servicio está expuesto con LoadBalancer:

```bash
# Obtener la IP externa
kubectl get svc academia-service

# Abrir en navegador
http://<EXTERNAL-IP>/actuator/health
```

---

## 🔒 Seguridad: ¿Qué Endpoints Exponer?

### ✅ **Recomendado para Producción**

```properties
# Solo health e info (mínimo necesario)
management.endpoints.web.exposure.include=health,info
```

**Razón**: Health e info no exponen información sensible.

### ⚠️ **Endpoints Sensibles (NO Exponer en Producción)**

- `/actuator/env`: Variables de entorno (incluye secrets)
- `/actuator/configprops`: Configuración completa
- `/actuator/beans`: Todos los beans de Spring
- `/actuator/mappings`: Todas las rutas de la aplicación
- `/actuator/threaddump`: Información de threads
- `/actuator/heapdump`: Volcado de memoria

### 🔐 **Configuración Segura para Producción**

```properties
# Exponer solo lo necesario
management.endpoints.web.exposure.include=health,info

# Ocultar detalles sensibles
management.endpoint.health.show-details=when-authorized

# Requerir autenticación para endpoints sensibles
# (si necesitas exponer más endpoints)
management.endpoints.web.base-path=/actuator
```

**En tu caso**: Como usas `show-details=always`, está bien porque solo expones `health` e `info`, que no contienen información sensible.

---

## 📊 Endpoints Disponibles

### `/actuator/health`

**Propósito**: Verificar el estado de la aplicación.

**Respuesta**:
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "livenessState": { "status": "UP" },
    "readinessState": { "status": "UP" }
  }
}
```

**Estados posibles**:
- `UP`: Aplicación funcionando correctamente
- `DOWN`: Aplicación no está funcionando
- `OUT_OF_SERVICE`: Aplicación temporalmente no disponible

### `/actuator/info`

**Propósito**: Información sobre la aplicación (versión, build, etc.).

**Respuesta**:
```json
{
  "app": {
    "name": "academymanager",
    "version": "0.0.1-SNAPSHOT"
  }
}
```

**Uso**: Identificar qué versión está corriendo en producción.

---

## 🚨 Troubleshooting: Error 403 (Acceso Denegado)

### Problema Común

Si obtienes **HTTP 403 Forbidden** al acceder a `/actuator/health`:

### Soluciones

#### 1. **Verificar SecurityConfig**

Asegúrate de que tienes:
```java
.requestMatchers("/actuator/**").permitAll()
```

✅ **Ya está configurado** en tu proyecto.

#### 2. **Verificar que el Filtro JWT no Interfiere**

El filtro JWT debe saltar el procesamiento para Actuator:
```java
if (requestPath != null && requestPath.startsWith("/actuator")) {
    filterChain.doFilter(request, response);
    return;
}
```

✅ **Ya está optimizado** en tu proyecto.

#### 3. **Verificar Configuración de Actuator**

Asegúrate de que `application.properties` tiene:
```properties
management.endpoints.web.exposure.include=health,info
```

✅ **Ya está configurado** en tu proyecto.

#### 4. **Verificar que Actuator está en el Classpath**

Verifica en `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

✅ **Ya está agregado** en tu proyecto.

---

## 📝 Resumen para Entrevistas

### ¿Qué es Actuator?

"Spring Boot Actuator es un módulo que expone endpoints de producción para monitoreo y gestión. En mi proyecto, lo uso para health checks de Kubernetes: los probes de liveness y readiness verifican `/actuator/health` para determinar si el pod está funcionando correctamente."

### ¿Por qué es Importante?

"Actuator es crítico para producción porque:
1. **Kubernetes depende de él** para health checks (sin él, K8s no sabe si un pod está vivo)
2. **Monitoreo**: Expone métricas para Prometheus/Grafana
3. **Debugging**: Permite ver el estado de la aplicación sin acceso SSH
4. **Seguridad**: Permite exponer solo endpoints necesarios"

### ¿Cómo lo Configuraste?

"Configuré Actuator con:
- Solo expongo `health` e `info` (seguridad)
- Configuré Spring Security para permitir acceso sin autenticación a `/actuator/**`
- Optimicé el filtro JWT para saltar el procesamiento en endpoints de Actuator
- Habilitado liveness y readiness probes para K8s"

---

## ✅ Checklist de Verificación

- [x] Dependencia de Actuator en `pom.xml`
- [x] Configuración en `application.properties`
- [x] SecurityConfig permite acceso a `/actuator/**`
- [x] Filtro JWT optimizado para Actuator
- [x] Health check funciona localmente
- [x] Health check funciona en Docker
- [x] Health check funciona en Kubernetes
- [x] Liveness probe configurado en deployment.yaml
- [x] Readiness probe configurado en deployment.yaml

---

## 🎯 Próximos Pasos (Día 6)

1. ✅ Verificar que Actuator funciona localmente
2. ✅ Probar en Docker
3. ✅ Probar en K3d
4. ✅ Verificar que los probes de K8s funcionan correctamente
5. ✅ Documentar cualquier problema encontrado

---

**Referencias**:
- [Spring Boot Actuator Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Kubernetes Liveness and Readiness Probes](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/)
