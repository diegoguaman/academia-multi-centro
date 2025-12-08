como agrego mi cluster PS C:\Users\diego> kubectl config current-context

k3d-mi-cluster-java

PS C:\Users\diego>

para verlo en lens?

Para agregar tu clúster k3d-mi-cluster-java a Lens usando el archivo kubeconfig existente, debes usar la opción "Add kubeconfig from filesystem" (Agregar kubeconfig desde el sistema de archivos) que se muestra en la imagen.

Lens automáticamente usa los archivos kubeconfig que encuentra en las ubicaciones estándar, pero si quieres agregarlo manualmente, sigue estos pasos:

1. 🔍 Localiza tu archivo kubeconfig
El comando kubectl config current-context solo te dice qué contexto estás usando, no dónde está el archivo.

Ubicación por defecto: El archivo kubeconfig se encuentra típicamente en C:\Users\tu_usuario\.kube\config en Windows. Si usas k3d, generalmente se añade la configuración a este archivo principal.

Comprueba el contenido: Revisa el contenido de este archivo para confirmar que el contexto k3d-mi-cluster-java está dentro.

2. ➕ Agrega el archivo a Lens
En el panel "Navigator" de Lens (donde estás), haz clic en el botón "Local Kubeconfigs" o en el símbolo de más (+) asociado.

Selecciona "Add kubeconfig from filesystem" (Agregar kubeconfig desde el sistema de archivos).

En la ventana que se abre, navega y selecciona el archivo config que identificaste en el paso 1 (normalmente C:\Users\tu_usuario\.kube\config). 4. Lens leerá el archivo e identificará todos los clústeres y contextos que contenga. Tu clúster k3d-mi-cluster-java debería aparecer ahora en la lista bajo "Local Kubeconfigs".

💡 Alternativa
Si tu contexto está en el archivo C:\Users\diego\.kube\config (la ubicación predeterminada), a veces basta con reiniciar Lens para que lo detecte automáticamente sin tener que agregarlo manualmente.





Para agregar tu clúster k3d-mi-cluster-java a Lens usando el archivo kubeconfig existente, debes usar la opción "Add kubeconfig from filesystem" (Agregar kubeconfig desde el sistema de archivos) que se muestra en la imagen.



vale, que archivo tengo que elegir?

El archivo que debes elegir es tu archivo de configuración de Kubernetes, conocido como kubeconfig.

1. 📂 Identifica la ruta del archivo
En sistemas Windows (que es el que estás usando según la ruta C:\Users\diego>), la ubicación predeterminada para el archivo kubeconfig es:

C:\Users\tu_usuario.kube\config
En tu caso, lo más probable es que la ruta completa sea: C:\Users\diego\.kube\config

El comando kubectl config current-context te indica que el contexto de tu clúster k3d-mi-cluster-java está definido y, a menos que hayas configurado una variable de entorno KUBECONFIG diferente, ese contexto se encuentra en el archivo predeterminado config.

2. 🧭 Navega hasta la ubicación y selecciónalo
La ventana que tienes abierta en la imagen está en una ruta de instalación de Lens (AppData\Local\Programs\Lens), que NO es la ubicación de tu kubeconfig.

Debes navegar hasta el directorio de usuario:

En esa ventana del explorador, navega a tu carpeta de usuario: C:\Users\diego (o haz clic en "Diego - Personal" en el panel izquierdo, como se ve en tu imagen).

Busca la carpeta oculta .kube (es posible que debas asegurarte de que Windows muestre archivos ocultos).

Dentro de la carpeta .kube, selecciona el archivo llamado config (o config.txt, si le pusiste esa extensión, aunque por defecto no tiene extensión).

Archivo a seleccionar: config (dentro de la carpeta .kube).

ya leha dado a sync, que hago despues?



no me muestra el cluster

¡Entiendo! Has seleccionado el archivo config correctamente y le has dado a "Sync", pero el clúster k3d-mi-cluster-java todavía no aparece en la lista de la izquierda junto a demo-k8s.

Aquí tienes los pasos a seguir para asegurarte de que Lens lo detecte y se conecte:

1. ✅ Confirma la Sincronización
Después de seleccionar el archivo config y hacer clic en "Sync" (o "Abrir" o "Añadir Clúster"—el botón varía ligeramente en la traducción), Lens debería haberlo añadido.

Verifica en la lista de "Local Kubeconfigs": Tu nuevo clúster debería aparecer en la lista del panel izquierdo "KUBERNETES CLUSTERS", debajo de Local Kubeconfigs (donde ahora solo ves demo-k8s).

2. 🔄 Revisa la Lista de Clústeres (Catálogo)
Si no lo ves inmediatamente en la lista, el clúster ya puede haber sido agregado al Catálogo de Lens, pero quizás no está "abierto":

Haz clic en el menú "Home" (que se ve en tu primera imagen) o en el icono de Catálogo (a menudo un cuadrado con varios puntos, si no estás en la vista de "Home").

Busca la pestaña "Clusters".

Tu clúster k3d-mi-cluster-java (o el nombre de su contexto) debería estar listado allí.

Si lo encuentras, haz clic en él y selecciona "Open" (Abrir) para que aparezca en el panel lateral de navegación.

3. 📝 Revisa el Contenido del Archivo config
Si después de los pasos anteriores el clúster sigue sin aparecer, el problema podría estar en cómo k3d escribió la configuración en el archivo config:

Abre el archivo C:\Users\diego\.kube\config con un editor de texto (como VS Code, Notepad++, o incluso el Bloc de Notas).

Busca una sección llamada contexts.

Confirma que haya una entrada para tu clúster k3d-mi-cluster-java bajo esa sección, y que tenga una referencia a un cluster y un user definidos en el mismo archivo.

Si el contexto existe en el archivo y Lens no lo detecta, la solución más simple suele ser:

4. 🚪 Reinicia Lens
Cierra la aplicación Lens por completo (asegúrate de que no quede en la bandeja del sistema) y vuelve a abrirla. Lens escanea las ubicaciones predeterminadas de kubeconfig al inicio y a menudo detecta los cambios o las nuevas configuraciones automáticamente.

¿Encontraste el clúster en la lista de Catálogo, o sigues solo viendo demo-k8s en el panel de la izquierda?

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment

$ k3d image import academia-multi-centro:latest -c mi-cluster-java

INFO[0000] Importing image(s) into cluster 'mi-cluster-java'

INFO[0000] Starting new tools node...

INFO[0000] Starting node 'k3d-mi-cluster-java-tools'

INFO[0000] Saving 1 image(s) from runtime...

INFO[0006] Importing images into nodes...

INFO[0006] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206134857.tar' into node 'k3d-mi-cluster-java-server-0'...

INFO[0006] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206134857.tar' into node 'k3d-mi-cluster-java-agent-1'...

INFO[0006] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206134857.tar' into node 'k3d-mi-cluster-java-agent-0'...

INFO[0010] Removing the tarball(s) from image volume...

INFO[0011] Removing k3d-tools node...

INFO[0012] Successfully imported image(s)

INFO[0012] Successfully imported 1 image(s) into 1 cluster(s)



diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment

$ kubectl apply -f deployment.yaml

deployment.apps/academia-deployment configured



diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment

$ kubectl rollout restart deployment/academia-deployment

deployment.apps/academia-deployment restarted



diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment

$ kubectl get pods -w

NAME                                   READY   STATUS    RESTARTS      AGE

academia-deployment-5bdc4654f9-fqfkl   0/1     Running   0             11s

academia-deployment-7cd9fc568-bxc5w    1/1     Running   2 (25m ago)   64m

backend-deployment-7cdf89b6fd-2fnjm    1/1     Running   5 (25m ago)   10d

backend-deployment-7cdf89b6fd-lgzql    1/1     Running   5 (25m ago)   10d

frontend-deployment-9f48f9d4d-pfbfs    1/1     Running   5 (25m ago)   10d

frontend-deployment-9f48f9d4d-wwhmb    1/1     Running   4 (25m ago)   10d

academia-deployment-5bdc4654f9-fqfkl   1/1     Running   0             21s

academia-deployment-7cd9fc568-bxc5w    1/1     Terminating   2 (25m ago)   65m

academia-deployment-7cd9fc568-bxc5w    0/1     Error         2 (25m ago)   65m

academia-deployment-7cd9fc568-bxc5w    0/1     Error         2 (25m ago)   65m

academia-deployment-7cd9fc568-bxc5w    0/1     Error         2 (25m ago)   65m





diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment

$ kubectl port-forward svc/academia-service 8081:80

Forwarding from 127.0.0.1:8081 -> 8080

Forwarding from [::1]:8081 -> 8080

Handling connection for 8081

Handling connection for 8081

ahora mismo los endpoint /actuator/** funcionan pero los pods pone cosas raras, esta todo bien para presentar que la imagen esta lista para pasar a produccion en google kubernetes o AWS, quiero que me des una lista de ckecks, con los comandos para que vean que los pods estan perfectos y listos para subir



que tengo que enseñarles de lens tambien

¡Excelente! Has importado la nueva imagen, aplicado la configuración y reiniciado el deployment (academia-deployment).

Aunque ves entradas de Error para el pod antiguo (academia-deployment-7cd9fc568-bxc5w), el nuevo pod (academia-deployment-5bdc4654f9-fqfkl) se ve en estado 1/1 Running. Esto es un comportamiento normal durante un rollout de reinicio: el deployment antiguo se está terminando (Terminating → Error → eliminado), mientras que el nuevo se ha iniciado y está listo.

Aquí tienes una lista de checks esenciales, con comandos, para demostrar que tus pods están perfectos y listos para la transición a producción en GKE o AWS EKS, y qué mostrar en Lens.

📝 Lista de Verificación (Pre-Production Checks)
Utiliza estos comandos para demostrar la salud y la configuración correcta de tu aplicación.

1. 🔍 Verificación del Estado General del Deployment
Este comando confirma que solo existe el nuevo pod y que está listo.

Verificación	Comando	Resultado Esperado
Despliegues y Réplicas	kubectl get deployments	academia-deployment debe mostrar 1/1 disponible y 1 actualizado.
Pods Listos	kubectl get pods -l app=academia-app	El pod más nuevo debe mostrar 1/1 en READY y Running en STATUS.
2. 🚀 Confirmación del Rollout Exitoso
Esto demuestra que la nueva versión se desplegó correctamente y reemplazó a la antigua.

Verificación	Comando	Propósito
Historial del Rollout	kubectl rollout history deployment/academia-deployment	Muestra el ID de la revisión más reciente (la nueva imagen) y confirma que el rollout finalizó sin fallos.
Estado del Rollout	kubectl rollout status deployment/academia-deployment	Debería decir "successfully rolled out" (desplegado exitosamente).
3. 💾 Inspección Detallada del Pod (La Prueba de Fuego)
Esta es la clave para demostrar que no hay problemas de Image Pull, CrashLoop o errores de configuración.

Verificación	Comando	Propósito
Eventos y Estado	kubectl describe pod -l app=academia-app	BUSCA: Al final, en la sección Events, debe haber solo entradas de Pulled, Created, y Started (sin errores ni CrashLoopBackOff). Confirma que el Image es academia-multi-centro:latest.
Logs de la Aplicación	kubectl logs -l app=academia-app	Muestra los logs de tu aplicación Java. BUSCA: Mensajes de inicialización exitosa de Spring Boot (o el framework que uses), como "Started Application in X seconds" y que el puerto 8080 esté activo.
4. 🌐 Prueba de Conectividad y Funcionalidad (Port-Forwarding)
Como ya has hecho el port-forward, solo tienes que demostrar que funciona.

Verificación	URL de Prueba	Propósito
Endpoint Actuator	http://localhost:8081/actuator/health	Demuestra que la aplicación está respondiendo y que el servicio de Kubernetes (academia-service) está enrutando correctamente.
Endpoint de la Aplicación	http://localhost:8081/tu-endpoint-principal	Demuestra la funcionalidad del negocio.
💻 Qué Enseñar en Lens
Lens es una herramienta visual que simplifica la presentación. Debes centrarte en las siguientes vistas:

1. 🖼️ Vista General del Deployment
Navegación: Workloads → Deployments → academia-deployment

Qué Mostrar:

Verifica que la sección "Replicas" muestre Current: 1 / Ready: 1 / Available: 1.

Muestra la pestaña "Pods" y verifica que el pod más nuevo esté en estado Running.

2. 📦 Inspección del Pod
Navegación: Workloads → Pods → Haz clic en el pod academia-deployment-5bdc4654f9-fqfkl

Qué Mostrar:

Pestaña "Events": Muestra que el pod fue Started exitosamente y no hay eventos de error recurrentes como ImagePullBackOff o CrashLoopBackOff.

Pestaña "Logs": Muestra los logs de inicio del contenedor (como hiciste con kubectl logs), confirmando visualmente que la aplicación se inició sin excepciones.

3. ⚙️ Verificación del Servicio
Navegación: Network → Services → academia-service

Qué Mostrar:

Muestra el Type: LoadBalancer (o NodePort/ClusterIP dependiendo de tu configuración) y los Ports mapeados (p. ej., 8081:31234/TCP).

Muestra la sección "Endpoints" para verificar que el servicio apunta correctamente al pod academia-deployment-5bdc4654f9-fqfkl.

2025-12-06T13:31:38.227Z WARN 1 --- [academymanager] [nio-8080-exec-5] com.zaxxer.hikari.pool.PoolBase : HikariPool-1 - Failed to validate connection org.postgresql.jdbc.PgConnection@369a8b91 (This connection has been closed.). Possibly consider using a shorter maxLifetime value.

2025-12-06T13:35:41.529Z WARN 1 --- [academymanager] [l-1:housekeeper] com.zaxxer.hikari.pool.PoolBase : HikariPool-1 - Failed to validate connection org.postgresql.jdbc.PgConnection@3c3eace4 (This connection has been closed.). Possibly consider using a shorter maxLifetime value.

2025-12-06T13:35:43.964Z WARN 1 --- [academymanager] [nio-8080-exec-6] com.zaxxer.hikari.pool.PoolBase : HikariPool-1 - Failed to validate connection org.postgresql.jdbc.PgConnection@5166d5 (This connection has been closed.). Possibly consider using a shorter maxLifetime value.

2025-12-06T13:37:04.297Z WARN 1 --- [academymanager] [nio-8080-exec-8] com.zaxxer.hikari.pool.PoolBase : HikariPool-1 - Failed to validate connection org.postgresql.jdbc.PgConnection@66ce9020 (This connection has been closed.). Possibly consider using a shorter maxLifetime value.

2025-12-06T13:37:10.341Z WARN 1 --- [academymanager] [nio-8080-exec-8] o.s.b.a.health.HealthEndpointSupport : Health contributor org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator (db) took 11699ms to respond

2025-12-06T14:18:48.385Z WARN 1 --- [academymanager] [nio-8080-exec-6] com.zaxxer.hikari.pool.PoolBase : HikariPool-1 - Failed to validate connection org.postgresql.jdbc.PgConnection@1332c577 (This connection has been closed.). Possibly consider using a shorter maxLifetime value.

2025-12-06T14:18:48.500Z WARN 1 --- [academymanager] [nio-8080-exec-6] o.s.b.a.health.HealthEndpointSupport : Health contributor org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator (db) took 10125ms to respond

2025-12-06T14:26:43.587Z WARN 1 --- [academymanager] [nio-8080-exec-1] com.zaxxer.hikari.pool.PoolBase : HikariPool-1 - Failed to validate connection org.postgresql.jdbc.PgConnection@46deebe4 (This connection has been closed.). Possibly consider using a shorter maxLifetime value.

2025-12-06T14:26:52.945Z WARN 1 --- [academymanager] [nio-8080-exec-1] com.zaxxer.hikari.pool.PoolBase : HikariPool-1 - Failed to validate connection org.postgresql.jdbc.PgConnection@191686c7 (This connection has been closed.). Possibly consider using a shorter maxLifetime value.

2025-12-06T14:26:53.433Z WARN 1 --- [academymanager] [nio-8080-exec-1] o.s.b.a.health.HealthEndpointSupport : Health contributor org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator (db) took 15098ms to respond

2025-12-06T16:13:34.054Z WARN 1 --- [academymanager] [l-1:housekeeper] com.zaxxer.hikari.pool.PoolBase : HikariPool-1 - Failed to validate connection org.postgresql.jdbc.PgConnection@2f4d3084 (This connection has been closed.). Possibly consider using a shorter maxLifetime value.

2025-12-06T16:13:37.067Z WARN 1 --- [academymanager] [nio-8080-exec-7] com.zaxxer.hikari.pool.PoolBase : HikariPool-1 - Failed to validate connection org.postgresql.jdbc.PgConnection@13265427 (This connection has been closed.). Possibly consider using a shorter maxLifetime value.

2025-12-06T16:13:38.022Z WARN 1 --- [academymanager] [nio-8080-exec-5] com.zaxxer.hikari.pool.PoolBase : HikariPool-1 - Failed to validate connection org.postgresql.jdbc.PgConnection@5856805c (This connection has been closed.). Possibly consider using a shorter maxLifetime value.



de la parte de lens lo unico que me preocupa es el pending en el service, el resto esta bien no?

¡Tienes razón! Después de revisar las imágenes de Lens, el estado general de tu despliegue es excelente para la demostración, excepto por el estado Pending en el servicio.

Aquí está el análisis y la solución para el único punto que debes abordar:

🧐 Análisis del Estado de Lens
Tu clúster k3d-mi-cluster-java está ahora visible y el estado de tu aplicación es muy bueno.

✅ Lo que está Perfecto
Clúster Agregado: El clúster k3d-mi-cluster-java está visible y conectado en el Navigator. 2. Deployment Saludable: El academia-deployment muestra 1/1 Pods y Running en estado. Esto confirma que la nueva imagen está activa. 3. Pod Listo: El pod (academia-deployment-5bdc4654f9-fqfkl) está en estado 1/1 Running. 4. Rollout Exitoso: El historial de revisiones muestra Available y Progressing, con la última revisión (#8) teniendo 1/1 Pods, lo que confirma que el rollout fue exitoso. 5. Logs de la Base de Datos (Advertencias): Los mensajes de WARN sobre HikariPool-1 - Failed to validate connection... (This connection has been closed.) son advertencias de salud de la piscina de conexiones (HikariCP) y son comunes en entornos de desarrollo (especialmente cuando no hay tráfico constante). No impiden el despliegue a producción. Simplemente sugieren ajustar el tiempo de vida máximo (maxLifetime) de las conexiones. Mientras los endpoints funcionen, esto es aceptable para una demo.

⚠️ Lo que Debes Abordar: Service Pending
El Problema: El servicio academia-service tiene el Status: Pending y el campo External IP está vacío. * La Razón: Tu servicio es de Type: LoadBalancer. K3d, al igual que K3s y la mayoría de los clústeres locales basados en Docker, no tiene un controlador de LoadBalancer (como Google Cloud Load Balancer o AWS ELB) por defecto. Sin este controlador, el servicio se queda esperando (Pending) a que se le asigne una IP Externa.

El Solución (para k3d/local): Debes usar una herramienta local que simule la funcionalidad de un LoadBalancer. K3d utiliza MetalLB (o similar) automáticamente, pero a veces no se habilita o se configura correctamente.

🛠️ Acción Requerida: Solucionar el Estado Pending
Para resolver el Pending en un entorno K3d y tener una IP Externa que puedas demostrar, generalmente se utiliza una herramienta como K3d-Ingress o MetalLB.

Dado que estás usando port-forward exitosamente, la forma más rápida de demostrar la conectividad sin depender de un IP externa en K3d es la siguiente:

Opción 1: Confiar en el Port-Forward (Recomendado para la Demo)
Ya has demostrado que la aplicación funciona usando:

kubectl port-forward svc/academia-service 8081:80

Para la presentación, enfatiza esto: Indica que la IP externa no es necesaria en el entorno local (K3d) porque estás utilizando Port Forwarding . Muestra la pestaña de Lens "Port Forwarding" (donde se ve academia-service activo).

Opción 2: Cambiar el Tipo de Servicio (Si Port-Forward no es suficiente)
Si quieres que el servicio deje de estar Pending y asuma un estado de "activo" visiblemente:

Edita el servicio en Lens (haz clic en los tres puntos → Edit) o vía kubectl edit svc academia-service.

Cambia type: LoadBalancer a type: NodePort.

Guarda y cierra.

El servicio se volverá Active inmediatamente, y el puerto NodePort (que ya se muestra como 31577/TCP en la imagen) será el puerto de acceso en el nodo del clúster .

Conclusión: Tu clúster y despliegue están listos. Para la presentación, simplemente explícale a tu audiencia por qué el LoadBalancer está Pending en un entorno local como K3d, y demuestra la conectividad con el Port Forwarding.

esta parte es de grok 

Este es el contexto, ya tengo instalado y funcionando k3d y lens
Asi que lo que quiero es que me des una guia detallada con todos los pasos y los comandos que debo ejecutar para subir a kubernetes agregarlo a un nodo y que este funcionando
Quiero que me digas todas las pruebas que tengo que realizar para dejarla lista para subir a kubernetes de google o de AWS, la plataforma que sea
 
Que es lo que tengo que hacer para realizar pruebas exhaustivas y ser profesional y lo que las empresas buscan, quiero un checklist de todo lo que tengo que comprobar en k3d y que es lo que aporta lens, que tengo que mostrar al cliente o a la empresa para que sepa que el la imagen que me han dado esta lista para ser subida en cualquier plataforma google o awsGuía Detallada para Desplegar y Probar una Imagen Docker en Kubernetes con k3d y Lens
Dado que ya tienes k3d y Lens instalados y funcionando, y tu imagen Docker está lista (asumiré que se llama algo como mi-imagen:tag, por ejemplo mi-app:v1), te proporciono una guía paso a paso con todos los comandos necesarios para:

Crear un clúster local en k3d (si no lo tienes ya).
Desplegar la imagen en Kubernetes (agregarla a un nodo y hacerla funcionar).
Realizar pruebas exhaustivas en k3d para asegurar que esté lista para producción en plataformas como Google Kubernetes Engine (GKE) o Amazon EKS (AWS).
Usar Lens para gestionar y visualizar el clúster.
Un checklist profesional de verificaciones, basado en lo que las empresas buscan (como estabilidad, seguridad, escalabilidad y observabilidad).
Qué mostrar al cliente o empresa para demostrar que la imagen está lista para cualquier plataforma cloud.

Esta guía asume que estás en un entorno Linux/macOS con Docker instalado (requerido por k3d). Si usas Windows, ajusta los comandos en consecuencia. Usa kubectl como CLI principal para Kubernetes. Si no tienes kubectl instalado, instálalo con brew install kubectl (macOS) o el equivalente en tu OS.
Paso 1: Verificar y Crear un Clúster en k3d
k3d crea clústers locales ligeros usando Docker. Si ya tienes uno corriendo, salta al Paso 2.

Comando para listar clústers existentes:textk3d cluster list
Si ves uno (ej. "k3s-default"), úsalo. Si no, crea uno.

Comando para crear un clúster simple (1 nodo master + 1 worker):textk3d cluster create mi-cluster --agents 1
Esto crea un clúster llamado "mi-cluster". Espera a que termine (verás "Successfully created cluster!").

Comando para configurar kubectl para usar este clúster:textk3d kubeconfig get mi-cluster > ~/.kube/config
O fusiona con tu config existente: k3d kubeconfig merge mi-cluster.

Verificar que el clúster está listo:textkubectl get nodes
Deberías ver nodos como "k3d-mi-cluster-server-0" y "k3d-mi-cluster-agent-0" en estado "Ready".


Paso 2: Preparar y Desplegar la Imagen en Kubernetes
Asumimos que tu imagen Docker (mi-imagen:tag) está en tu registry local o Docker Hub. Si está local, k3d la detectará automáticamente ya que usa Docker.

Crear un manifiesto YAML para el Deployment (guarda esto en un archivo deployment.yaml):textapiVersion: apps/v1
kind: Deployment
metadata:
  name: mi-app-deployment
spec:
  replicas: 1  # Comienza con 1 pod, escala después en pruebas
  selector:
    matchLabels:
      app: mi-app
  template:
    metadata:
      labels:
        app: mi-app
    spec:
      containers:
      - name: mi-app
        image: mi-imagen:tag  # Reemplaza con tu imagen
        ports:
        - containerPort: 80  # Reemplaza con el puerto de tu app (ej. 8080)
        resources:
          requests:
            cpu: "100m"
            memory: "128Mi"
          limits:
            cpu: "500m"
            memory: "256Mi"
        livenessProbe:  # Para pruebas de salud
          httpGet:
            path: /health  # Reemplaza con tu endpoint de health
            port: 80
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /ready  # Reemplaza con tu endpoint de readiness
            port: 80
          initialDelaySeconds: 30
          periodSeconds: 10
Crear un manifiesto YAML para el Service (guarda en service.yaml para exponer la app):textapiVersion: v1
kind: Service
metadata:
  name: mi-app-service
spec:
  selector:
    app: mi-app
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80  # Ajusta según tu app
  type: LoadBalancer  # O NodePort para pruebas locales
Aplicar los manifiestos:textkubectl apply -f deployment.yaml
kubectl apply -f service.yaml
Verificar que el pod está corriendo:textkubectl get pods
Deberías ver "mi-app-deployment-xxx" en estado "Running".

Acceder a la app localmente (si usas LoadBalancer, k3d expone en localhost):textkubectl get svc mi-app-service  # Nota el puerto externo
curl http://localhost:<puerto>  # Prueba el endpoint

Paso 3: Conectar Lens al Clúster
Lens es una herramienta gráfica para gestionar Kubernetes. Aporta visualización de recursos, logs en tiempo real, métricas, edición de YAML, y depuración sin CLI.

Abrir Lens y agregar el clúster:
En Lens, ve a "Catalog" > "Clusters" > "Add Cluster".
Selecciona "From Kubeconfig" y sube tu ~/.kube/config (o el generado por k3d).
Lens detectará "mi-cluster". Conéctate.

Usar Lens para verificar despliegue:
En el dashboard, ve a "Workloads" > "Deployments" > selecciona "mi-app-deployment".
Inspecciona pods, eventos, logs (clic en pod > "Logs").
Ve a "Network" > "Services" para ver "mi-app-service".
Usa "Metrics" para monitorear CPU/Memoria (si tienes metrics-server instalado; si no, instala con kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml).


Lens aporta: Facilidad para clientes/empresas (interfaz UI intuitiva), depuración rápida (shell en pods, port-forwarding gráfico), y reportes visuales (gráficos de métricas).
Pruebas Exhaustivas en k3d para Preparar para GKE/AWS EKS
Para ser profesional, realiza pruebas que cubran estabilidad, seguridad, escalabilidad y compliance. Empresas buscan imágenes "production-ready": sin vulnerabilidades, eficientes en recursos, con probes de salud, y testeadas bajo carga. Usa k3d para simular entornos cloud (es ligero pero compatible con Kubernetes estándar).
Instalar herramientas de prueba si es necesario (en tu host, no en k3d):

hey para load testing: go install github.com/rakyll/hey@latest.
trivy para scans de seguridad: brew install trivy (o equivalente).

Pruebas Paso a Paso con Comandos:

Prueba de Despliegue Básico:textkubectl rollout status deployment/mi-app-deployment  # Espera a que esté ready
kubectl logs -f deployment/mi-app-deployment  # Ver logs
En Lens: Ve a pods y streams logs.

Prueba de Accesibilidad:textkubectl port-forward svc/mi-app-service 8080:80  # Forward local
curl http://localhost:8080  # Prueba endpoints
En Lens: Clic derecho en service > "Port Forward".

Prueba de Salud (Liveness/Readiness Probes):
Asegúrate de que tu imagen tenga endpoints /health y /ready (implementa si no).
textkubectl describe pod <pod-name>  # Ver eventos de probes
Simula fallo: Edita deployment para romper probe, aplica, y verifica que Kubernetes reinicie el pod.

Prueba de Escalabilidad:textkubectl scale deployment/mi-app-deployment --replicas=3  # Escala a 3
kubectl get pods  # Verifica
hey -n 1000 -c 10 http://localhost:<puerto>  # Load test
En Lens: Monitorea métricas en "Metrics" durante el test.

Prueba de Recursos y Limits:
Edita deployment con limits altos, aplica, y usa kubectl top pods (requiere metrics-server).
textkubectl top pods
Simula sobrecarga: En Lens, ve a pod > "Exec" y corre comandos para consumir CPU/Memoria.

Prueba de Seguridad:texttrivy image mi-imagen:tag  # Scan vulnerabilidades
kubectl apply -f <security-context.yaml>  # Agrega SecurityContext: runAsNonRoot: true
Verifica no root: kubectl exec -it <pod-name> -- whoami.

Prueba de Persistencia (si aplica):
Agrega PersistentVolumeClaim si tu app necesita storage.
textkubectl apply -f pvc.yaml  # Manifiesto PVC
Prueba de Rollouts y Rollbacks:textkubectl set image deployment/mi-app-deployment mi-app=mi-imagen:new-tag  # Update
kubectl rollout undo deployment/mi-app-deployment  # Rollback
Prueba de Networking y Service:textkubectl run test-pod --image=busybox --rm -it -- sh  # Pod temporal
wget mi-app-service:80  # Prueba desde dentro del clúster
Prueba de Observabilidad:
Instala Prometheus o usa Lens metrics.
textkubectl logs -f <pod-name>  # Logs

Checklist Profesional de Verificaciones en k3d

Despliegue: Pods en Running, sin crashes (kubectl get pods -w).
Salud: Probes pasan (kubectl describe pod).
Recursos: Uso < limits (kubectl top), no OOMKilled.
Seguridad: No root, sin vulnerabilidades críticas (trivy), SecurityContext aplicado.
Escalabilidad: Soporta carga (hey), autoescala con HPA (prueba instalando HorizontalPodAutoscaler).
Networking: Service accesible, DNS resuelve (desde test-pod).
Persistencia: Volúmenes montados correctamente si aplica.
Updates: Rollouts sin downtime (kubectl rollout status).
Logs/Métricas: Accesibles y útiles (Lens ayuda aquí).
Compliance: Imagen basada en base segura (ej. alpine), multi-arch si posible.
Portabilidad: Manifiestos estándar (no vendor-specific), prueba en minikube para validar.

Qué Mostrar al Cliente/Empresa
Para demostrar que la imagen está lista para GKE/EKS:

Reportes: Output de trivy (vulnerabilidades), kubectl describe (detalles), métricas de load tests.
Screenshots de Lens: Dashboard con pods green, gráficos de CPU/Memoria, logs.
Demo en Vivo: Despliegue en k3d, acceso via browser, escalado.
Documento: Checklist firmado, con evidencias (logs, comandos outputs).
Certificación: "La imagen cumple Kubernetes best practices: probes, limits, non-root, probada bajo carga. Lista para cloud sin cambios."

Si algo falla, ajusta y re-prueba. Para cloud, sube la imagen a un registry como GCR/ECR y usa los mismos manifests. ¡Éxito!PS C:\Users\diego> k3d cluster list
FATA[0000] runtime failed to list nodes: docker failed to get containers with labels 'map[app:k3d]': failed to list containers: error during connect: Get "http://%2F%2F.%2Fpipe%2FdockerDesktopLinuxEngine/v1.46/containers/json?all=1&filters=%7B%22label%22%3A%7B%22app%3Dk3d%22%3Atrue%7D%7D": open //./pipe/dockerDesktopLinuxEngine: The system cannot find the file specified.
PS C:\Users\diego> k3d cluster list
NAME SERVERS AGENTS LOADBALANCER
mi-cluster-java 0/1 0/2 true
PS C:\Users\diego> kubectl get nodos
E1206 08:12:11.728699 3704 memcache.go:265] "Unhandled Error" err="couldn't get current server API group list: Get "https://host.docker.internal:55524/api?timeout=32s\": dial tcp 192.168.0.14:55524: connectex: No connection could be made because the target machine actively refused it."
E1206 08:12:13.773863 3704 memcache.go:265] "Unhandled Error" err="couldn't get current server API group list: Get "https://host.docker.internal:55524/api?timeout=32s\": dial tcp 192.168.0.14:55524: connectex: No connection could be made because the target machine actively refused it."
E1206 08:12:15.818661 3704 memcache.go:265] "Unhandled Error" err="couldn't get current server API group list: Get "https://host.docker.internal:55524/api?timeout=32s\": dial tcp 192.168.0.14:55524: connectex: No connection could be made because the target machine actively refused it."
E1206 08:12:17.871105 3704 memcache.go:265] "Unhandled Error" err="couldn't get current server API group list: Get "https://host.docker.internal:55524/api?timeout=32s\": dial tcp 192.168.0.14:55524: connectex: No connection could be made because the target machine actively refused it."
E1206 08:12:19.929410 3704 memcache.go:265] "Unhandled Error" err="couldn't get current server API group list: Get "https://host.docker.internal:55524/api?timeout=32s\": dial tcp 192.168.0.14:55524: connectex: No connection could be made because the target machine actively refused it."
Unable to connect to the server: dial tcp 192.168.0.14:55524: connectex: No connection could be made because the target machine actively refused it.
PS C:\Users\diego> kubectl get nodos
error: the server doesn't have a resource type "nodos"
PS C:\Users\diego> kubectl get nodos
error: the server doesn't have a resource type "nodos"
PS C:\Users\diego> k3d kubeconfig get mi-cluster-java > ~/.kube/config
PS C:\Users\diego> kubectl get nodos
error: the server doesn't have a resource type "nodos"
PS C:\Users\diego>
 
la imagen que quiero subir es esta
academia-multi-centro:latest
el lenguaje y las tecnologias usadan en la imagen son relevantes para agregar al cluster?
que hay que tener en cuenta de la imagen?
esto lo que tengo ahora de k3d que debo hacer ?Guía Detallada para Corregir Problemas en k3d, Desplegar y Probar la Imagen "academia-multi-centro:latest" en k3d y Lens
Basado en el output que proporcionas, parece que hay problemas con tu clúster k3d en Windows (usando Docker Desktop). El clúster "mi-cluster-java" está creado pero no saludable (0/1 servers y 0/2 agents), lo que causa errores de conexión en kubectl ("connection refused"). Además, estás usando "nodos" en lugar de "nodes" (typo común en español). El primer error de k3d sugiere que Docker Desktop no estaba corriendo o configurado correctamente en ese momento, pero el segundo comando funcionó, lo que indica que se resolvió temporalmente.
La imagen "academia-multi-centro:latest": Kubernetes es agnóstico al lenguaje y tecnologías internas de la imagen (ej. Java, Python, Node.js, etc.), siempre y cuando sea una imagen Docker válida y runnable. No afecta directamente el despliegue en el clúster, pero es relevante para personalizar configuraciones como puertos expuestos, probes de salud (liveness/readiness), variables de entorno, o dependencias (ej. si usa bases de datos, necesitarás services adicionales). Lo que hay que tener en cuenta de la imagen:

Compatibilidad: Debe correr en un contenedor (verifica con docker run -it academia-multi-centro:latest localmente para probar que inicia sin errores).
Puertos: Identifica qué puerto expone la app (ej. 8080 para Java/Spring). Usa docker inspect academia-multi-centro:latest | Select-String "ExposedPorts" en PowerShell.
Recursos: Estima CPU/Memoria (ej. apps Java pueden necesitar más heap; agrega -Xmx via ENV si es necesario).
Seguridad: No debe correr como root; verifica con docker run --user 1000 academia-multi-centro:latest.
Health Checks: Ideal si tiene endpoints como /health o /actuator/health (común en Java).
Dependencias: Si usa tech como databases (ej. MySQL), configura sidecar containers o external services.
Portabilidad: Asegúrate de que sea multi-arch (amd64/arm64) para clouds como AWS/GKE; verifica con docker manifest inspect academia-multi-centro:latest.
Tamaño/Optimización: Minimiza capas; usa base images ligeras (ej. openjdk:alpine para Java).

Ahora, lo que debes hacer con k3d: Dado que el clúster no está saludable, recomiendo borrarlo y recrearlo para un inicio limpio. Luego, despliega la imagen y realiza pruebas. Usa PowerShell (PS) ya que estás en Windows. Asegúrate de que Docker Desktop esté corriendo y configurado para Linux containers (k3d no soporta WSL2 directamente sin tweaks, pero funciona con Docker Desktop).
Paso 1: Verificar y Corregir Configuración Básica

Asegúrate de que Docker Desktop esté corriendo:
Abre Docker Desktop desde el menú de Windows. Verifica que el icono en la barra de tareas esté verde (running).
Comando para verificar Docker:textdocker ps
Si falla con error similar al tuyo, reinicia Docker Desktop.


Instala o verifica herramientas (si no las tienes):
kubectl: Descarga desde https://kubernetes.io/docs/tasks/tools/install-kubectl-windows/ e instala.
k3d: Si no funciona, reinstala con choco install k3d (si usas Chocolatey) o descarga binario de https://k3d.io/.
Lens: Abre Kubernetes Lens (ahora OpenLens o similar; descarga de https://k8slens.dev/ si no).

Corrige el kubeconfig:
El error de conexión indica que el config apunta a un puerto/IP inválido (192.168.0.14:55524). Regenera.textk3d kubeconfig get mi-cluster-java > $HOME/.kube/config
Nota: En Windows, $HOME/.kube/config es C:\Users\diego\.kube\config. Crea la carpeta si no existe.


Lista clústers y verifica:textk3d cluster list
Deberías ver "mi-cluster-java" con 0/1 y 0/2. Si falla, reinicia Docker.


Paso 2: Borrar y Recrear el Clúster (para solucionar problemas)
Dado que el clúster no tiene nodos running, bórralo y crea uno nuevo. Usa "mi-cluster" para simplicidad.

Borrar el clúster existente:textk3d cluster delete mi-cluster-java
Confirma que se borra (verifica con k3d cluster list).

Crear un nuevo clúster (1 server + 2 agents, como el tuyo original):textk3d cluster create mi-cluster --agents 2 --port 8080:80@loadbalancer --port 8443:443@loadbalancer
--agents 2: Para simular multi-nodo.
--port: Expone puertos para LoadBalancer (útil para pruebas).
Espera ~1-2 min. Verifica:textk3d cluster list
Debería mostrar "mi-cluster" con 1/1 servers y 2/2 agents.


Configura kubeconfig para el nuevo clúster:textk3d kubeconfig get mi-cluster > $HOME/.kube/config
Verifica nodos (corrige "nodos" a "nodes"):textkubectl get nodes
Deberías ver nodos como "k3d-mi-cluster-server-0" en "Ready". Si falla conexión, verifica kubectl config view y asegúrate de que el server sea algo como "https://0.0.0.0:xxxxx" o "https://host.docker.internal:xxxxx".


Paso 3: Conectar Lens al Clúster

Abre Lens.
Ve a "File" > "Add Cluster" (o icono + en Clusters).
Selecciona "From kubeconfig file" y elige C:\Users\diego\.kube\config.
Lens detectará "mi-cluster". Conecta y verifica en el dashboard: Nodos ready, namespaces, etc.

Paso 4: Preparar y Desplegar la Imagen "academia-multi-centro:latest"
Asumimos que la imagen está en tu Docker local (k3d la detecta automáticamente). Si está en un registry remoto, usa image: registry/academia-multi-centro:latest.

Verifica la imagen localmente:textdocker images | Select-String "academia-multi-centro"
Debería listar "academia-multi-centro latest".
textdocker run -d -p 8080:8080 academia-multi-centro:latest  # Ajusta puerto; prueba acceso en browser localhost:8080
Mata el container después: docker stop <container-id>.

Crea manifiesto Deployment (deployment.yaml):
Crea un archivo deployment.yaml en tu directorio actual con Notepad o VS Code:textapiVersion: apps/v1
kind: Deployment
metadata:
  name: academia-deployment
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
        ports:
        - containerPort: 8080  # Ajusta al puerto de tu app (ej. 8080 para Java)
        resources:
          requests:
            cpu: "250m"
            memory: "512Mi"
          limits:
            cpu: "1"
            memory: "1Gi"
        livenessProbe:
          httpGet:
            path: /health  # Ajusta si tu app tiene /actuator/health o similar
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /ready  # Ajusta
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10

Crea manifiesto Service (service.yaml):textapiVersion: v1
kind: Service
metadata:
  name: academia-service
spec:
  selector:
    app: academia
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080  # Ajusta
  type: LoadBalancer
Aplica los manifiestos:textkubectl apply -f deployment.yaml
kubectl apply -f service.yaml
Verifica despliegue:textkubectl get deployments
kubectl get pods
kubectl get services
Pods deberían estar "Running". Logs: kubectl logs -f deployment/academia-deployment.

Accede a la app:textkubectl port-forward svc/academia-service 8080:80
Abre browser: http://localhost:8080. O usa curl: curl http://localhost:8080.


Paso 5: Pruebas Exhaustivas en k3d y Lens
Instala tools si necesitas: hey para load (descarga binario), trivy para security (choco install trivy).

Prueba Básica:textkubectl rollout status deployment/academia-deployment
En Lens: Workloads > Deployments > academia-deployment > Ver pods/logs.

Prueba Salud:textkubectl describe pod <pod-name>  # Ver probes
Prueba Escalabilidad:textkubectl scale deployment/academia-deployment --replicas=3
hey -n 500 -c 5 http://localhost:8080  # Load test
En Lens: Metrics > Monitorea CPU.

Prueba Recursos:textkubectl top pods  # Instala metrics-server si no: kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
Prueba Seguridad:texttrivy image academia-multi-centro:latest
Prueba Rollouts:textkubectl set image deployment/academia-deployment academia=academia-multi-centro:new-tag
kubectl rollout undo deployment/academia-deployment

Usa Lens para visuales: Logs, exec en pods (kubectl exec -it <pod> -- sh), métricas.
Si todo pasa, la imagen está lista para GKE/EKS: Sube a registry (gcr.io o ecr), usa mismos YAML.PS C:\Users\diego> kubectl get nodes
NAME STATUS ROLES AGE VERSION
k3d-mi-cluster-java-agent-0 Ready <none> 10d v1.31.5+k3s1
k3d-mi-cluster-java-agent-1 Ready <none> 10d v1.31.5+k3s1
k3d-mi-cluster-java-server-0 Ready control-plane,master 10d v1.31.5+k3s1
PS C:\Users\diego>
 
ahora si que he usado el comandoGuía Detallada para Desplegar y Probar la Imagen "academia-multi-centro:latest" en tu Clúster k3d Actual y Lens
¡Perfecto! Veo que tu clúster mi-cluster-java ya está completamente saludable: tienes 1 nodo server (control-plane) y 2 agents, todos en estado Ready con Kubernetes v1.31.5+k3s1. Esto significa que ya puedes desplegar directamente sin necesidad de recrear el clúster.
Vamos a proceder paso a paso con todos los comandos que debes ejecutar en PowerShell (PS C:\Users\diego>).
Asumiré que tu imagen academia-multi-centro:latest ya está construida y disponible localmente en Docker (k3d la detectará automáticamente).
Importante antes de empezar:

Identifica el puerto que expone tu aplicación dentro del contenedor (muy común en apps Java/Spring: 8080). Si no lo sabes, ejecútalo localmente para verificarlo:textdocker run -d --name test-app -p 8080:8080 academia-multi-centro:latest
docker ps  # Mira el puerto
docker logs test-app  # Ve si arranca bien
docker stop test-app
Si tu app tiene endpoints de salud (ej. /actuator/health en Spring Boot), anótalos. Si no los tiene, podemos omitir o simular probes básicos.

Paso 1: Verificar que kubectl apunta al clúster correcto
textkubectl config current-context
Debería mostrar algo como k3d-mi-cluster-java. Si no, configúralo:
textk3d kubeconfig get mi-cluster-java > $HOME\.kube\config
Paso 2: Crear los archivos YAML de despliegue
Crea una carpeta para organizar (opcional) y usa Notepad o VS Code para crear estos dos archivos.
Archivo 1: deployment.yaml
textnotepad deployment.yaml
Pega este contenido (ajusta el puerto y los paths de health si es necesario):
YAMLapiVersion: apps/v1
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
        imagePullPolicy: Never  # Importante: usa la imagen local, no intenta pull de registry
        ports:
        - containerPort: 8080   # <<< AJUSTA SI TU APP USA OTRO PUERTO (ej. 9000, 3000)
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:    # Opcional pero recomendado
          httpGet:
            path: /actuator/health   # <<< AJUSTA: /health, /actuator/health, /ping, etc.
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 15
          failureThreshold: 5
        readinessProbe:
          httpGet:
            path: /actuator/health   # <<< AJUSTA igual que arriba
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 15
Archivo 2: service.yaml
textnotepad service.yaml
Pega esto:
YAMLapiVersion: v1
kind: Service
metadata:
  name: academia-service
spec:
  selector:
    app: academia
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080   # <<< MISMO PUERTO que containerPort arriba
  type: LoadBalancer   # En k3d se expondrá en localhost con un puerto aleatorio
Paso 3: Desplegar la aplicación
textkubectl apply -f deployment.yaml
kubectl apply -f service.yaml
Paso 4: Verificar que todo está corriendo
textkubectl get deployments
kubectl get pods -w          # Observa en tiempo real hasta que esté Running
kubectl get services

Espera a que el pod pase a Running y Ready 1/1.
Si se queda en CrashLoopBackOff o ImagePullBackOff, revisa logs:textkubectl logs deployment/academia-deployment -f

Paso 5: Acceder a la aplicación localmente
k3d con tipo LoadBalancer asigna un puerto externo en localhost.
textkubectl get svc academia-service
Busca la columna PORT(S), verás algo como 80:xxxxx/TCP donde xxxxx es el puerto local.
Ejemplo de salida:
textNAME              TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)        AGE
academia-service   LoadBalancer   10.43.123.45   <pending>     80:32567/TCP   2m
→ El puerto local es 32567.
Accede con:
texthttp://localhost:32567
O con curl:
textcurl http://localhost:32567
Alternativa rápida (port-forward) si no quieres depender del LoadBalancer:
textkubectl port-forward svc/academia-service 8080:80
Luego abre http://localhost:8080
Paso 6: Conectar y usar Lens para visualización y pruebas

Abre Kubernetes Lens (OpenLens).
Si no está conectado:
File → Add Cluster → From kubeconfig → selecciona C:\Users\diego\.kube\config
Conéctate al contexto k3d-mi-cluster-java.

En Lens podrás ver gráficamente:
Workloads → Deployments: academia-deployment (estado verde).
Workloads → Pods: haz clic en el pod → pestañas Logs, Events, Exec (para entrar con shell).
Network → Services: academia-service → puedes hacer Port-Forward con clic derecho.
Metrics (si no ves métricas, instala metrics-server):textkubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml


Lens es lo que más impresiona a clientes/empresas: muestra todo visualmente, logs en tiempo real, métricas, y permite depurar sin comandos.
Paso 7: Pruebas Exhaustivas (Checklist Profesional)
Ejecuta estos comandos uno por uno y guarda los outputs (capturas o copia-pega) para demostrar al cliente.

Verificación básica de saludtextkubectl rollout status deployment/academia-deployment
kubectl get pods -o wide
Logs y eventostextkubectl logs deployment/academia-deployment -f
kubectl describe deployment academia-deployment
kubectl describe pod <nombre-del-pod>
Pruebas de probes (liveness/readiness)
Si fallan, verás restarts en kubectl get pods.
Simula fallo: cambia el path del probe a uno inexistente, aplica y observa reinicio.

Escalabilidadtextkubectl scale deployment/academia-deployment --replicas=3
kubectl get pods
Prueba carga (instala hey si quieres: descarga desde https://github.com/rakyll/hey/releases)texthey -n 1000 -c 20 http://localhost:32567

Uso de recursostextkubectl top pods
kubectl top nodes
Seguridad de la imagen
Instala Trivy si no lo tienes: choco install trivy o descarga binario.texttrivy image academia-multi-centro:latest
Verifica que no corra como root:textkubectl exec deployment/academia-deployment -- whoami

Actualización y rollback (zero-downtime)
Tag nueva versión (simulada):textdocker tag academia-multi-centro:latest academia-multi-centro:v2
kubectl set image deployment/academia-deployment academia=academia-multi-centro:v2
kubectl rollout status deployment/academia-deployment
kubectl rollout undo deployment/academia-deployment  # Vuelve atrás

Networking internotextkubectl run tmp --image=busybox --rm -it -- sh
# Dentro del pod temporal:
wget -O- academia-service
exit

Qué mostrar al cliente/empresa para demostrar que está lista para GKE/AWS EKS

Capturas de Lens: Dashboard general, pods Running, métricas estables, logs limpios.
Outputs de comandos: kubectl get all, trivy image, resultados de load test.
Acceso en vivo: Muestra http://localhost:xxxxx funcionando.
Checklist firmado:
 Pods Running y Ready
 Probes de salud funcionando
 Recursos limitados y estables
 Escalado manual exitoso
 Zero-downtime rollout/rollback
 Sin vulnerabilidades críticas (trivy)
 Imagen local funciona → portable a cualquier registry cloud


Con esto la imagen está production-ready y demostrablemente portable a cualquier proveedor Kubernetes (solo cambia el registry de la imagen y aplica los mismos YAML).
Si en algún paso falla (ej. pod no arranca), copia el output de kubectl describe pod y kubectl logs y lo revisamos. ¡Adelante!

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME READY STATUS RESTARTS AGE
academia-deployment-5c8664bc74-w54vs 0/1 Running 0 10s
academia-deployment-7cd9fc568-kpgr6 1/1 Running 0 129m
backend-deployment-7cdf89b6fd-2fnjm 1/1 Running 4 (4h17m ago) 10d
backend-deployment-7cdf89b6fd-lgzql 1/1 Running 4 (4h17m ago) 10d
frontend-deployment-9f48f9d4d-pfbfs 1/1 Running 4 (4h17m ago) 10d
frontend-deployment-9f48f9d4d-wwhmb 1/1 Running 3 (4h17m ago) 10d
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get svc academia-service
NAME TYPE CLUSTER-IP EXTERNAL-IP PORT(S) AGE
academia-service LoadBalancer 10.43.4.85 <pending> 80:31577/TCP 178m
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl logs deployment/academia-deployment -f
Found 2 pods, using pod/academia-deployment-7cd9fc568-kpgr6
Production profile - skipping .env file
  . ____ _ __ _ _
 /\ / ***'* __ _ *(*)* __ __ _ \ \ \ 
( ( )_*_ | '_ | '*| | '* / *` | \ \ \ 
 \/ __*)| |*)| | | | | || (*| | ) ) ) )
  ' |****| .__|*| |**| |*_*, | / / / /
 =========|*|==============|***/=/*/*/*/
 :: Spring Boot :: (v3.5.7)
2025-12-06T09:21:25.829Z INFO 1 --- [academymanager] [ main] c.a.a.AcademymanagerApplication : Starting AcademymanagerApplication v0.0.1-SNAPSHOT using Java 21.0.9 with PID 1 (/app/app.jar started by ? in /app)
2025-12-06T09:21:25.831Z DEBUG 1 --- [academymanager] [ main] c.a.a.AcademymanagerApplication : Running with Spring Boot v3.5.7, Spring v6.2.12
2025-12-06T09:21:25.832Z INFO 1 --- [academymanager] [ main] c.a.a.AcademymanagerApplication : The following 1 profile is active: "prod"
2025-12-06T09:21:26.881Z INFO 1 --- [academymanager] [ main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2025-12-06T09:21:27.041Z INFO 1 --- [academymanager] [ main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 145 ms. Found 13 JPA repository interfaces.
Production profile detected - skipping .env file loading
2025-12-06T09:21:27.819Z INFO 1 --- [academymanager] [ main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat initialized with port 8080 (http)
2025-12-06T09:21:27.840Z INFO 1 --- [academymanager] [ main] o.apache.catalina.core.StandardService : Starting service [Tomcat]
2025-12-06T09:21:27.840Z INFO 1 --- [academymanager] [ main] o.apache.catalina.core.StandardEngine : Starting Servlet engine: [Apache Tomcat/10.1.48]
2025-12-06T09:21:27.872Z INFO 1 --- [academymanager] [ main] o.a.c.c.C.[Tomcat].[localhost].[/] : Initializing Spring embedded WebApplicationContext
2025-12-06T09:21:27.873Z INFO 1 --- [academymanager] [ main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1985 ms
2025-12-06T09:21:28.200Z INFO 1 --- [academymanager] [ main] o.hibernate.jpa.internal.util.LogHelper : HHH000204: Processing PersistenceUnitInfo [name: default]
2025-12-06T09:21:28.257Z INFO 1 --- [academymanager] [ main] org.hibernate.Version : HHH000412: Hibernate ORM core version 6.6.33.Final
2025-12-06T09:21:28.288Z INFO 1 --- [academymanager] [ main] o.h.c.internal.RegionFactoryInitiator : HHH000026: Second-level cache disabled
2025-12-06T09:21:28.514Z INFO 1 --- [academymanager] [ main] o.s.o.j.p.SpringPersistenceUnitInfo : No LoadTimeWeaver setup: ignoring JPA class transformer
2025-12-06T09:21:28.536Z INFO 1 --- [academymanager] [ main] com.zaxxer.hikari.HikariDataSource : HikariPool-1 - Starting...
2025-12-06T09:21:29.320Z INFO 1 --- [academymanager] [ main] com.zaxxer.hikari.pool.HikariPool : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@70e5737f
2025-12-06T09:21:29.322Z INFO 1 --- [academymanager] [ main] com.zaxxer.hikari.HikariDataSource : HikariPool-1 - Start completed.
2025-12-06T09:21:29.402Z WARN 1 --- [academymanager] [ main] org.hibernate.orm.deprecation : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2025-12-06T09:21:29.580Z INFO 1 --- [academymanager] [ main] org.hibernate.orm.connections.pooling : HHH10001005: Database info:
        Database JDBC URL [Connecting through datasource 'HikariDataSource (HikariPool-1)']
        Database driver: undefined/unknown
        Database version: 17.6
        Autocommit mode: undefined/unknown
        Isolation level: undefined/unknown
        Minimum pool size: undefined/unknown
        Maximum pool size: undefined/unknown
2025-12-06T09:21:30.593Z INFO 1 --- [academymanager] [ main] o.h.e.t.j.p.i.JtaPlatformInitiator : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2025-12-06T09:21:30.960Z INFO 1 --- [academymanager] [ main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T09:21:31.249Z DEBUG 1 --- [academymanager] [ main] c.a.a.security.JwtAuthenticationFilter : Filter 'jwtAuthenticationFilter' configured for use
2025-12-06T09:21:31.335Z INFO 1 --- [academymanager] [ main] eAuthenticationProviderManagerConfigurer : Global AuthenticationManager configured with AuthenticationProvider bean with name authenticationProvider
2025-12-06T09:21:31.335Z WARN 1 --- [academymanager] [ main] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean. UserDetailsService beans will not be used by Spring Security for automatically configuring username/password login. Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated DaoAuthenticationProvider. If the current configuration is intentional, to turn off this warning, increase the logging level of 'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
2025-12-06T09:21:31.689Z WARN 1 --- [academymanager] [ main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2025-12-06T09:21:32.141Z INFO 1 --- [academymanager] [ main] efaultSchemaResourceGraphQlSourceBuilder : Loaded 1 resource(s) in the GraphQL schema.
2025-12-06T09:21:32.302Z INFO 1 --- [academymanager] [ main] o.s.b.a.g.GraphQlAutoConfiguration : GraphQL schema inspection:
        Unmapped fields: {Matricula=[fechaModificacion, convocatoria, alumno, entidadSubvencionadora, calificaciones, facturas], Curso=[fechaModificacion, materia, formato, convocatorias], Usuario=[datosPersonales], Convocatoria=[fechaModificacion, curso, profesor, centro, matriculas], Materia=[descripcion], Formato=[descripcion, activo], Centro=[empresa, comunidad]}
        Unmapped registrations: {}
        Unmapped arguments: {}
        Skipped types: []
2025-12-06T09:21:32.320Z INFO 1 --- [academymanager] [ main] s.b.a.g.s.GraphQlWebMvcAutoConfiguration : GraphQL endpoint HTTP POST /graphql
2025-12-06T09:21:32.452Z INFO 1 --- [academymanager] [ main] o.s.b.a.e.web.EndpointLinksResolver : Exposing 2 endpoints beneath base path '/actuator'
2025-12-06T09:21:32.890Z INFO 1 --- [academymanager] [ main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port 8080 (http) with context path '/'
2025-12-06T09:21:32.901Z INFO 1 --- [academymanager] [ main] c.a.a.AcademymanagerApplication : Started AcademymanagerApplication in 7.507 seconds (process running for 8.024)
2025-12-06T09:31:37.975Z INFO 1 --- [academymanager] [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/] : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-12-06T09:31:37.976Z INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet : Initializing Servlet 'dispatcherServlet'
2025-12-06T09:31:37.983Z INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet : Completed initialization in 6 ms
 
estoy intentando hacer la correccion hice los cambios de
   .requestMatchers("/actuator/health", "/actuator/info").permitAll()
y hice la nueva imagen
PS C:\Users\diego\academia-multi-centro\academymanager> docker build -t academia-multi-centro:latest .
[+] Building 1.5s (12/12) FINISHED docker:desktop-linux
 => [internal] load build definition from Dockerfile 0.0s
 => => transferring dockerfile: 2.56kB 0.0s
 => [internal] load metadata for docker.io/library/eclipse-temurin:21-jre-alpine 1.2s
 => [auth] library/eclipse-temurin:pull token for registry-1.docker.io 0.0s
 => [internal] load .dockerignore 0.0s
 => => transferring context: 478B 0.0s
 => [stage-1 1/6] FROM docker.io/library/eclipse-temurin:21-jre-alpine@sha256:326837fba06a8ff5482a17bafbd65319e64a6e997febb7c85ebe7e3f73c12b11 0.0s
 => => resolve docker.io/library/eclipse-temurin:21-jre-alpine@sha256:326837fba06a8ff5482a17bafbd65319e64a6e997febb7c85ebe7e3f73c12b11 0.0s
 => [internal] load build context 0.0s
 => => transferring context: 90B 0.0s
 => CACHED [stage-1 2/6] RUN apk add --no-cache wget 0.0s
 => CACHED [stage-1 3/6] RUN addgroup -S spring && adduser -S spring -G spring 0.0s
 => CACHED [stage-1 4/6] WORKDIR /app 0.0s
 => CACHED [stage-1 5/6] COPY target/*.jar app.jar 0.0s
 => CACHED [stage-1 6/6] RUN chown spring:spring app.jar 0.0s
 => exporting to image 0.1s
 => => exporting layers 0.0s
 => => exporting manifest sha256:340164d0cedd02865bbe9df69bb7beee041299cc992347968ca982b9c787b0d4 0.0s
 => => exporting config sha256:1d1d84fb1eb8067f309101e64122e7b9e5893730741cbfbc3ff7cccc1ce3563c 0.0s
 => => exporting attestation manifest sha256:0ef2686928849eb7564d8018ff45dadea428297e8d6831502745fe95d94d7fac 0.0s
 => => exporting manifest list sha256:818969db586eda6ba1302a44d61e00e0e3afcda257749d8ddc9f7d9914a39d57 0.0s
 => => naming to docker.io/library/academia-multi-centro:latest 0.0s
 => => unpacking to docker.io/library/academia-multi-centro:latest 0.0s
View build details: docker-desktop://dashboard/build/desktop-linux/desktop-linux/0qlxkd3z4isqi4ban6lwet7up
What's next:
    View a summary of image vulnerabilities and recommendations → docker scout quickview
PS C:\Users\diego\academia-multi-centro\academymanager>
 
la volvi a cargar
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ k3d image import academia-multi-centro:latest -c mi-cluster-java
INFO[0000] Importing image(s) into cluster 'mi-cluster-java'
INFO[0000] Starting new tools node...
INFO[0000] Starting node 'k3d-mi-cluster-java-tools'
INFO[0001] Saving 1 image(s) from runtime...
INFO[0006] Importing images into nodes...
INFO[0006] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206122524.tar' into node 'k3d-mi-cluster-java-agent-1'...
INFO[0006] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206122524.tar' into node 'k3d-mi-cluster-java-server-0'...
INFO[0006] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206122524.tar' into node 'k3d-mi-cluster-java-agent-0'...
INFO[0008] Removing the tarball(s) from image volume...
INFO[0009] Removing k3d-tools node...
INFO[0009] Successfully imported image(s)
INFO[0009] Successfully imported 1 image(s) into 1 cluster(s)
 
pero me salen los erroes del principio el pod no esta ready que debo hacer ?¡Perfecto! Vamos a solucionar esto paso a paso y de forma muy clara. El problema es que el nuevo pod que se está levantando con tu imagen actualizada se queda en 0/1 (no Ready) mientras el pod viejo sigue en 1/1.
Eso pasa porque Kubernetes no ha detectado que la imagen realmente cambió (o el Deployment no está haciendo rolling update correctamente). Vamos a forzar que se cree un pod 100% nuevo con la imagen que acabas de importar.
Guía detallada con todos los comandos que tienes que ejecutar (en orden)

Comprueba que la imagen nueva está realmente en el clusterBashk3d image list | grep academia-multi-centroDebe aparecer academia-multi-centro:latest con una fecha/hora reciente.
Verifica qué imagen está usando ahora tu DeploymentBashkubectl get deployment academia-deployment -o jsonpath='{.spec.template.spec.containers[0].image}{"\n"}'Probablemente te sigue diciendo algo como academia-multi-centro:latest (el mismo tag), por eso Kubernetes piensa que no cambió nada.
FORZAR el redeploy con la imagen nueva (3 formas, elige la que más te guste)Opción A – La más rápida y segura (recomendada)Bashkubectl rollout restart deployment/academia-deploymentEsto mata todos los pods viejos y crea pods nuevos con la imagen que acabas de importar.Opción B – Cambiar temporalmente el tag (truco clásico)Bashkubectl patch deployment academia-deployment -p "{\"spec\":{\"template\":{\"spec\":{\"containers\":[{\"name\":\"academia-container\",\"image\":\"academia-multi-centro:latest-$(date +%s)\"}}}}}"(esto fuerza el rolling update aunque el tag sea el mismo).Opción C – Borrar los pods manualmente y dejar que el ReplicaSet los recreeBashkubectl delete pod -l app=academia-deployment
Observa que se crean pods nuevos y se quedan en Running → ReadyBashkubectl get pods -wVerás algo como:textacademia-deployment-XXXXX-abc12   0/1     Pending   0          0s
academia-deployment-XXXXX-abc12   0/1     ContainerCreating   0          1s
academia-deployment-XXXXX-abc12   1/1     Running             0          5s   ← ¡Ready!
academia-deployment-old-xxxxx     1/1     Terminating         0          10s
Comprueba los logs del pod NUEVO (muy importante, el viejo sigue teniendo los logs antiguos)
Primero copia el nombre del pod nuevo que acaba de quedar Ready:Bashkubectl get pods -l app=academia-deploymentLuego:Bashkubectl logs -f deployment/academia-deployment
# o directamente con el nombre del pod nuevo
kubectl logs -f academia-deployment-5c8664bc74-newnnnAhí tienes que ver que arranca de nuevo y que ya aparecen tus nuevos permitAll en los logs de seguridad (busca líneas de SecurityConfig o los requestMatchers que añadiste).
Prueba que el health y el GraphQL funcionanBashcurl http://localhost:31577/actuator/health
curl -X POST http://localhost:31577/graphql -H "Content-Type: application/json" -d '{"query": "query { __schema { queryType { name } } }"}'

Resumen de comandos que tienes que copiar-pegar ahora mismo
Bash# 1. Forzar redeploy (la forma más limpia)
kubectl rollout restart deployment/academia-deployment

# 2. Ver los pods en tiempo real
kubectl get pods -w

# 3. Cuando veas el pod nuevo en 1/1 Running, ver logs del deployment (siempre coge el pod activo)
kubectl logs -f deployment/academia-deployment

# 4. Probar los endpoints
curl http://localhost:31577/actuator/health
curl http://localhost:31577/actuator/info   # este también debería funcionar ahora
Con el kubectl rollout restart el pod viejo muere y el nuevo arranca con tu código actualizado (los .requestMatchers("/actuator/health", "/actuator/info").permitAll() ya estarán aplicados).
¡Ejecuta eso y me dices qué ves en los logs del pod nuevo! En 30 segundos lo tienes solucionado. 🚀diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ k3d image list | grep academia-multi-centro
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get deployment academia-deployment -o jsonpath='{.spec.template.spec.containers[0].image}{"\n"}'
academia-multi-centro:latest
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl rollout restart deployment/academia-deployment
deployment.apps/academia-deployment restarted
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME READY STATUS RESTARTS AGE
academia-deployment-7cd9fc568-kpgr6 1/1 Running 0 137m
academia-deployment-c68cb8c6f-74dnk 0/1 Running 0 21s
backend-deployment-7cdf89b6fd-2fnjm 1/1 Running 4 (4h26m ago) 10d
backend-deployment-7cdf89b6fd-lgzql 1/1 Running 4 (4h26m ago) 10d
frontend-deployment-9f48f9d4d-pfbfs 1/1 Running 4 (4h26m ago) 10d
frontend-deployment-9f48f9d4d-wwhmb 1/1 Running 3 (4h26m ago) 10d
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl delete pod -l app=academia-deployment
No resources found
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl patch deployment academia-deployment -p "{"spec":{"template":{"spec":{"containers":[{"name":"academia-container","image":"academia-multi-centro:latest-$(date +%s)"}}}}}"
Error from server (BadRequest): invalid character '}' after array element
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME READY STATUS RESTARTS AGE
academia-deployment-7cd9fc568-kpgr6 1/1 Running 0 139m
academia-deployment-c68cb8c6f-74dnk 0/1 Running 1 (14s ago) 105s
backend-deployment-7cdf89b6fd-2fnjm 1/1 Running 4 (4h27m ago) 10d
backend-deployment-7cdf89b6fd-lgzql 1/1 Running 4 (4h27m ago) 10d
frontend-deployment-9f48f9d4d-pfbfs 1/1 Running 4 (4h27m ago) 10d
frontend-deployment-9f48f9d4d-wwhmb 1/1 Running 3 (4h27m ago) 10d
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -l app=academia-deployment
No resources found in default namespace.
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -l
error: flag needs an argument: 'l' in -l
See 'kubectl get --help' for usage.
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods
NAME READY STATUS RESTARTS AGE
academia-deployment-7cd9fc568-kpgr6 1/1 Running 0 140m
academia-deployment-c68cb8c6f-74dnk 0/1 Running 1 (59s ago) 2m30s
backend-deployment-7cdf89b6fd-2fnjm 1/1 Running 4 (4h28m ago) 10d
backend-deployment-7cdf89b6fd-lgzql 1/1 Running 4 (4h28m ago) 10d
frontend-deployment-9f48f9d4d-pfbfs 1/1 Running 4 (4h28m ago) 10d
frontend-deployment-9f48f9d4d-wwhmb 1/1 Running 3 (4h28m ago) 10d¡Vamos a arreglar esto de una vez! El problema principal es que tu Deployment no tiene la label app=academia-deployment, por eso todos los comandos con -l app=academia-deployment fallan y el pod nuevo se queda en 0/1 porque no está pasando los readiness probes (seguramente porque la seguridad sigue bloqueando /actuator/health).
Paso a paso EXACTO que tienes que ejecutar ahora mismo (copia y pega uno por uno)
Bash# 1. Mira cuál es la label REAL que tiene tu Deployment
kubectl get deployment academia-deployment --show-labels
Probablemente verás algo como:
textapp=academymanager   o   app.kubernetes.io/name=academia   o   algo distinto
Bash# 2. Mira las labels del pod que está en 0/1
kubectl get pods -l app=academymanager --show-labels
# o prueba estas hasta que veas los pods:
kubectl get pods -l app=academia
kubectl get pods -l app=academymanager
kubectl get pods --show-labels | grep academia-deployment
Una vez que descubras la label correcta (casi seguro es app=academymanager), ejecuta esto:
Bash# 3. Forzar matar TODOS los pods del deployment (así se recrean con la imagen nueva)
kubectl delete pod -l app=academymanager --force
# o si la label es otra, cambia aquí:
# kubectl delete pod -l app=academia --force
Bash# 4. Observa cómo se crean pods nuevos y se quedan en 1/1
kubectl get pods -w
Deberías ver algo así en menos de 30 segundos:
textacademia-deployment-c68cb8c6f-xxxxx   0/1     Terminating   0   3m
academia-deployment-c68cb8c6f-yyyyy   0/1     Pending       0   0s
academia-deployment-c68cb8c6f-yyyyy   0/1     ContainerCreating   0   1s
academia-deployment-c68cb8c6f-yyyyy   1/1     Running       0   8s   ← ¡Listo!
Bash# 5. Cuando esté en 1/1, mira los logs del pod NUEVO
kubectl logs -f deployment/academia-deployment
Ahí tienes que ver que arranca limpio y que ya NO tiene el error de seguridad en /actuator/health.
Bash# 6. Prueba que ahora sí funciona
curl http://localhost:31577/actuator/health
# Debe devolver: {"status":"UP"}

curl http://localhost:31577/actuator/info
curl -X POST http://localhost:31577/graphql -H "Content-Type: application/json" -d '{"query":"{ __schema { queryType { name } }}"}'
Comando rápido si quieres saltarte todo y hacerlo ya (99% de probabilidad de que funcione)
Bash# Mata todos los pods del deployment (da igual la label)
kubectl delete pod --all --force

# O más preciso (casi siempre es esta label en proyectos Spring Boot):
kubectl delete pod -l app=academymanager --force

# Y observa:
kubectl get pods -w
Ejecuta eso ahora mismo y en menos de 1 minuto tendrás el pod nuevo en 1/1 con tu código corregido funcionando.
¡Dime qué label te sale en el paso 1 o 2 y te confirmo el comando exacto! Pero con kubectl delete pod --all --force lo solucionas ya mismo. 🚀diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get deployment academia-deployment --show-labels
NAME READY UP-TO-DATE AVAILABLE AGE LABELS
academia-deployment 1/1 1 1 3h9m app=academia
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -l app=academymanager --show-labels
No resources found in default namespace.
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -l app=academia --show-labels
NAME READY STATUS RESTARTS AGE LABELS
academia-deployment-7cd9fc568-kpgr6 1/1 Running 0 142m app=academia,pod-template-hash=7cd9fc568
academia-deployment-c68cb8c6f-74dnk 0/1 Running 3 (17s ago) 4m48s app=academia,pod-template-hash=c68cb8c6f
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -l app=academia
NAME READY STATUS RESTARTS AGE
academia-deployment-7cd9fc568-kpgr6 1/1 Running 0 142m
academia-deployment-c68cb8c6f-74dnk 0/1 Running 3 (57s ago) 5m28s
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl delete pod -l app=academymanager --force
Warning: Immediate deletion does not wait for confirmation that the running resource has been terminated. The resource may continue to run on the cluster indefinitely.
No resources found
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl delete pod -l app=academia --force
Warning: Immediate deletion does not wait for confirmation that the running resource has been terminated. The resource may continue to run on the cluster indefinitely.
pod "academia-deployment-7cd9fc568-kpgr6" force deleted from default namespace
pod "academia-deployment-c68cb8c6f-74dnk" force deleted from default namespace
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME READY STATUS RESTARTS AGE
academia-deployment-7cd9fc568-bxc5w 1/1 Running 0 14s
academia-deployment-c68cb8c6f-gb7wc 0/1 Running 0 14s
backend-deployment-7cdf89b6fd-2fnjm 1/1 Running 4 (4h32m ago) 10d
backend-deployment-7cdf89b6fd-lgzql 1/1 Running 4 (4h32m ago) 10d
frontend-deployment-9f48f9d4d-pfbfs 1/1 Running 4 (4h32m ago) 10d
frontend-deployment-9f48f9d4d-wwhmb 1/1 Running 3 (4h32m ago) 10d
diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl logs -f deployment/academia-deployment
Found 2 pods, using pod/academia-deployment-7cd9fc568-bxc5w
Production profile - skipping .env file
  . ____ _ __ _ _
 /\ / ***'* __ _ *(*)* __ __ _ \ \ \ 
( ( )_*_ | '_ | '*| | '* / *` | \ \ \ 
 \/ __*)| |*)| | | | | || (*| | ) ) ) )
  ' |****| .__|*| |**| |*_*, | / / / /
 =========|*|==============|***/=/*/*/*/
 :: Spring Boot :: (v3.5.7)
2025-12-06T11:45:04.119Z INFO 1 --- [academymanager] [ main] c.a.a.AcademymanagerApplication : Starting AcademymanagerApplication v0.0.1-SNAPSHOT using Java 21.0.9 with PID 1 (/app/app.jar started by ? in /app)
2025-12-06T11:45:04.125Z DEBUG 1 --- [academymanager] [ main] c.a.a.AcademymanagerApplication : Running with Spring Boot v3.5.7, Spring v6.2.12
2025-12-06T11:45:04.126Z INFO 1 --- [academymanager] [ main] c.a.a.AcademymanagerApplication : The following 1 profile is active: "prod"
2025-12-06T11:45:05.774Z INFO 1 --- [academymanager] [ main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2025-12-06T11:45:05.995Z INFO 1 --- [academymanager] [ main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 207 ms. Found 13 JPA repository interfaces.
Production profile detected - skipping .env file loading
2025-12-06T11:45:07.463Z INFO 1 --- [academymanager] [ main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat initialized with port 8080 (http)
2025-12-06T11:45:07.505Z INFO 1 --- [academymanager] [ main] o.apache.catalina.core.StandardService : Starting service [Tomcat]
2025-12-06T11:45:07.506Z INFO 1 --- [academymanager] [ main] o.apache.catalina.core.StandardEngine : Starting Servlet engine: [Apache Tomcat/10.1.48]
2025-12-06T11:45:07.570Z INFO 1 --- [academymanager] [ main] o.a.c.c.C.[Tomcat].[localhost].[/] : Initializing Spring embedded WebApplicationContext
2025-12-06T11:45:07.576Z INFO 1 --- [academymanager] [ main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 3361 ms
2025-12-06T11:45:08.064Z INFO 1 --- [academymanager] [ main] o.hibernate.jpa.internal.util.LogHelper : HHH000204: Processing PersistenceUnitInfo [name: default]
2025-12-06T11:45:08.142Z INFO 1 --- [academymanager] [ main] org.hibernate.Version : HHH000412: Hibernate ORM core version 6.6.33.Final
2025-12-06T11:45:08.174Z INFO 1 --- [academymanager] [ main] o.h.c.internal.RegionFactoryInitiator : HHH000026: Second-level cache disabled
2025-12-06T11:45:08.433Z INFO 1 --- [academymanager] [ main] o.s.o.j.p.SpringPersistenceUnitInfo : No LoadTimeWeaver setup: ignoring JPA class transformer
2025-12-06T11:45:08.469Z INFO 1 --- [academymanager] [ main] com.zaxxer.hikari.HikariDataSource : HikariPool-1 - Starting...
2025-12-06T11:45:09.357Z INFO 1 --- [academymanager] [ main] com.zaxxer.hikari.pool.HikariPool : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@70e5737f
2025-12-06T11:45:09.359Z INFO 1 --- [academymanager] [ main] com.zaxxer.hikari.HikariDataSource : HikariPool-1 - Start completed.
2025-12-06T11:45:09.440Z WARN 1 --- [academymanager] [ main] org.hibernate.orm.deprecation : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2025-12-06T11:45:09.603Z INFO 1 --- [academymanager] [ main] org.hibernate.orm.connections.pooling : HHH10001005: Database info:
        Database JDBC URL [Connecting through datasource 'HikariDataSource (HikariPool-1)']
        Database driver: undefined/unknown
        Database version: 17.6
        Autocommit mode: undefined/unknown
        Isolation level: undefined/unknown
        Minimum pool size: undefined/unknown
        Maximum pool size: undefined/unknown
2025-12-06T11:45:10.883Z INFO 1 --- [academymanager] [ main] o.h.e.t.j.p.i.JtaPlatformInitiator : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2025-12-06T11:45:11.222Z INFO 1 --- [academymanager] [ main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T11:45:11.654Z DEBUG 1 --- [academymanager] [ main] c.a.a.security.JwtAuthenticationFilter : Filter 'jwtAuthenticationFilter' configured for use
2025-12-06T11:45:11.783Z INFO 1 --- [academymanager] [ main] eAuthenticationProviderManagerConfigurer : Global AuthenticationManager configured with AuthenticationProvider bean with name authenticationProvider
2025-12-06T11:45:11.784Z WARN 1 --- [academymanager] [ main] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean. UserDetailsService beans will not be used by Spring Security for automatically configuring username/password login. Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated DaoAuthenticationProvider. If the current configuration is intentional, to turn off this warning, increase the logging level of 'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
2025-12-06T11:45:12.294Z WARN 1 --- [academymanager] [ main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2025-12-06T11:45:12.977Z INFO 1 --- [academymanager] [ main] efaultSchemaResourceGraphQlSourceBuilder : Loaded 1 resource(s) in the GraphQL schema.
2025-12-06T11:45:13.192Z INFO 1 --- [academymanager] [ main] o.s.b.a.g.GraphQlAutoConfiguration : GraphQL schema inspection:
        Unmapped fields: {Matricula=[fechaModificacion, convocatoria, alumno, entidadSubvencionadora, calificaciones, facturas], Curso=[fechaModificacion, materia, formato, convocatorias], Usuario=[datosPersonales], Convocatoria=[fechaModificacion, curso, profesor, centro, matriculas], Materia=[descripcion], Formato=[descripcion, activo], Centro=[empresa, comunidad]}
        Unmapped registrations: {}
        Unmapped arguments: {}
        Skipped types: []
2025-12-06T11:45:13.217Z INFO 1 --- [academymanager] [ main] s.b.a.g.s.GraphQlWebMvcAutoConfiguration : GraphQL endpoint HTTP POST /graphql
2025-12-06T11:45:13.380Z INFO 1 --- [academymanager] [ main] o.s.b.a.e.web.EndpointLinksResolver : Exposing 2 endpoints beneath base path '/actuator'
2025-12-06T11:45:13.842Z INFO 1 --- [academymanager] [ main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port 8080 (http) with context path '/'
2025-12-06T11:45:13.854Z INFO 1 --- [academymanager] [ main] c.a.a.AcademymanagerApplication : Started AcademymanagerApplication in 10.284 seconds (process running for 11.234)
 
lo mejor seria crear una imagen con los cambios del back con otro tag o borrar los pods para que me genere uno solo, o tienes que ser los dos pods con corecto y el otro con 0/1
 
lo mejor no seria crear un cluster nuevo y agregar todo nuevo para ver que pasa?Guía Detallada para Diagnosticar y Corregir el Problema en k3d y Lens
Voy a proporcionarte una guía paso a paso completa y detallada para resolver el problema con tus pods en k3d. El issue principal es que tienes un rolling update atascado: el Deployment está configurado para 1 réplica (READY 1/1), pero hay dos pods porque Kubernetes crea el nuevo antes de eliminar el viejo durante un update. Si el nuevo pod no pasa el readiness probe (probablemente porque /actuator/health falla por configuración de seguridad), se queda en 0/1 Running y el update no termina.
No es necesario crear un clúster nuevo (eso es overkill y pierde tiempo; podemos fixearlo en el actual). En cambio, vamos a:

Diagnosticar por qué el pod nuevo está en 0/1 (logs, describe, probes).
Forzar el uso de tu imagen nueva (con un tag diferente para evitar cachés).
Probar los endpoints directamente.
Usar Lens (Kubernetes Lens) para inspeccionar visualmente y hacer pruebas.

Asumo que tu imagen academia-multi-centro:latest ya está built y importada en k3d (como hiciste antes). Si no, repite el build e import primero.
Requisitos previos:

Asegúrate de tener k3d, kubectl y Docker instalados.
Descarga e instala Kubernetes Lens si no lo tienes (es gratis: https://k8slens.dev/). Lens te permite ver pods, logs, events y editar recursos gráficamente.
Todos los comandos se ejecutan en tu terminal (MINGW64 o PowerShell) desde ~/k3d-deployment.
Usa kubectl get pods -w en una terminal separada para monitorear en tiempo real mientras ejecutas otros comandos.

Paso 1: Diagnosticar el Pod Fallido (Por Qué Está en 0/1 Running)
El pod nuevo (academia-deployment-c68cb8c6f-gb7wc o similar) está "Running" pero no "Ready", lo que significa que el contenedor arranca, pero falla el readiness probe (probablemente una llamada a /actuator/health que devuelve error 401 por seguridad no actualizada).
Comandos a ejecutar:

Lista los pods con labels para confirmar nombres:textkubectl get pods -l app=academia --show-labels(Deberías ver los dos pods: uno con hash 7cd9fc568 en 1/1 y el otro con c68cb8c6f en 0/1).
Describe el pod fallido para ver eventos y por qué no está ready (busca "Readiness probe failed" o errores en Events):textkubectl describe pod academia-deployment-c68cb8c6f-gb7wc(Reemplaza con el nombre exacto del pod en 0/1. Esto te dirá si es el probe fallando, image pull error, etc.).
Obtén logs del pod fallido (no del deployment, para evitar que coja el viejo):textkubectl logs -f academia-deployment-c68cb8c6f-gb7wc(Busca errores como conexión a DB, Spring Security bloqueando paths, o Tomcat no starting).
Prueba el endpoint de health directamente en el pod fallido (para confirmar si /actuator/health falla):textkubectl port-forward pod/academia-deployment-c68cb8c6f-gb7wc 8080:8080(En otra terminal, ejecuta:)textcurl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/info(Si devuelve 401 Unauthorized, confirma que tu cambio de permitAll no está aplicado = imagen vieja. Presiona Ctrl+C para parar el port-forward).

En Lens:

Abre Lens y conecta a tu clúster k3d (busca "mi-cluster-java" en la lista de clústers).
Ve a "Workloads > Pods", selecciona el pod en 0/1.
En la vista de detalles: mira "Events" (abajo) para errors, "Logs" para output, y "YAML" para spec.
Esto es más visual y fácil que kubectl.

Paso 2: Limpiar el Update Atascado (Eliminar Pods Viejos y Forzar Recreación)
Borrar pods fuerza al ReplicaSet a crear nuevos, pero como el Deployment tiene 1 réplica, quedará solo uno. Si el nuevo falla, se recreará automáticamente.
Comandos a ejecutar:

Borra todos los pods del Deployment (usa la label correcta: app=academia):textkubectl delete pod -l app=academia --force --grace-period=0(Esto mata ambos pods inmediatamente. Monitorea con kubectl get pods -w para ver nuevos creándose).
Espera 10-20 segundos y verifica:textkubectl get pods -l app=academia(Deberías ver un solo pod nuevo en 1/1 si todo OK, o en 0/1 si persiste el issue).

En Lens:

En "Workloads > Pods", selecciona los pods con checkbox, haz click derecho > Delete.
Observa en tiempo real cómo se recrean.

Paso 3: Actualizar la Imagen con un Tag Nuevo (Para Evitar Cachés y Forzar Update)
Usar :latest causa problemas porque Kubernetes cachea imágenes. Crea una imagen con tag nuevo (e.g., :v1), actualiza el Deployment y aplica rolling update.
Comandos a ejecutar:

Build la imagen con tag nuevo (desde el directorio del proyecto: C:\Users\diego\academia-multi-centro\academymanager):textdocker build -t academia-multi-centro:v1 .
Importa la nueva imagen a k3d:textk3d image import academia-multi-centro:v1 -c mi-cluster-java
Edita el Deployment para usar el nuevo tag y setear imagePullPolicy: Always (para forzar pull):textkubectl edit deployment academia-deployment(En el editor vim/nano, busca la sección spec.template.spec.containers[0]: cambia image: academia-multi-centro:latest a academia-multi-centro:v1, y agrega imagePullPolicy: Always si no está. Guarda con :wq).Alternativa si no quieres editar: usa patch:textkubectl patch deployment academia-deployment -p '{"spec":{"template":{"spec":{"containers":[{"name":"academia-deployment","image":"academia-multi-centro:v1","imagePullPolicy":"Always"}]}}}}'(Ajusta "name" si tu container se llama diferente, e.g., "academia-container").
Forzar rolling update:textkubectl rollout restart deployment/academia-deployment
Monitorea el rollout:textkubectl rollout status deployment/academia-deployment(Debería decir "successfully rolled out". Si se atasca, vuelve al Paso 1).

En Lens:

Ve a "Workloads > Deployments", selecciona "academia-deployment".
Click en "Edit", cambia el image tag y imagePullPolicy en el YAML, Apply.
Luego, click derecho > Restart para el rollout.

Paso 4: Probar los Endpoints y el Servicio en k3d
Una vez el pod esté en 1/1 Ready, prueba que funcione.
Comandos a ejecutar:

Verifica el servicio (debería tener EXTERNAL-IP <pending>, pero usa el port):textkubectl get svc academia-service
Prueba health e info (usa el port expuesto: 31577):textcurl http://localhost:31577/actuator/health
curl http://localhost:31577/actuator/info(Debe devolver {"status":"UP"} y info sin 401).
Prueba GraphQL:textcurl -X POST http://localhost:31577/graphql -H "Content-Type: application/json" -d '{"query": "{ __schema { queryType { name } } }"}'(Debe devolver data sin errores).
Si necesitas auth, agrega headers JWT si tu app lo requiere (de logs, ve si menciona JWT).

En Lens:

Ve a "Network > Services", selecciona "academia-service".
Usa "Port Forward" en Lens (click derecho > Port Forward) para mapear 31577 localmente y probar con curl o browser.
Para GraphQL, usa el GraphQL Playground si tu app lo expone, o Postman.

Paso 5: Si Persiste el Problema (Rollback o Escalar)
Si el pod nuevo sigue en 0/1:

Rollback al estado anterior:textkubectl rollout undo deployment/academia-deployment
Escala temporalmente a 0 réplicas para limpiar, luego a 1:textkubectl scale deployment academia-deployment --replicas=0
kubectl scale deployment academia-deployment --replicas=1
Verifica el YAML completo del Deployment por errores:textkubectl get deployment academia-deployment -o yaml

En Lens: Usa la interfaz para scale (click en el Deployment > Scale) y rollback (History tab).
Consejos Finales y Pruebas

Monitorea siempre con kubectl get pods -w y kubectl get events -w para events en tiempo real.
Si usas Lens, habilita "Metrics" para ver CPU/Memoria del pod (ayuda si es OOM).
Una vez funcionando, prueba tu app completa (frontend/backend si aplica).
Si el issue es seguridad, confirma en logs del pod nuevo si menciona "permitAll" aplicado (busca líneas de SecurityConfig).

Ejecuta estos pasos en orden y dime qué output ves en el describe/logs para ajustar. ¡Deberías tenerlo fix en 10-15 mins! 🚀

al final los errores fueron que tenia que corregir la seguirdad de la aplicacion, que tenia que generar el .jar de nuevo para que aplique los cambios, de lo contrario estaba usando la misma imagen

