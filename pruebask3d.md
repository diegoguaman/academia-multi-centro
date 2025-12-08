
diego@DESKTOP-GJ8H97B MINGW64 ~
$ mkdir k3d-deployment

diego@DESKTOP-GJ8H97B MINGW64 ~
$ cd k3d-deployment/

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ code

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ code .

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ ls
deployment.yaml  service.yaml

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ k3d --version
k3d version v5.8.3
k3s version v1.31.5-k3s1 (default)

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl apply -f deployment.yaml
deployment.apps/academia-deployment created

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl apply -f service.yaml
service/academia-service created

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS              RESTARTS      AGE
academia-deployment-54458475fb-72sth   0/1     ErrImageNeverPull   0             32s
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running             4 (24m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running             4 (24m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running             4 (24m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running             3 (24m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get svc academia-service
NAME               TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)        AGE
academia-service   LoadBalancer   10.43.24.253   <pending>     80:30419/TCP   87s

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get deployments
NAME                  READY   UP-TO-DATE   AVAILABLE   AGE
academia-deployment   0/1     1            0           3m38s
backend-deployment    2/2     2            2           10d
frontend-deployment   2/2     2            2           10d

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get services
NAME               TYPE           CLUSTER-IP      EXTERNAL-IP                        PORT(S)        AGE
academia-service   LoadBalancer   10.43.24.253    <pending>                          80:30419/TCP   4m2s
backend-service    ClusterIP      10.43.251.249   <none>                             8080/TCP       10d
frontend-service   LoadBalancer   10.43.191.133   172.24.0.3,172.24.0.4,172.24.0.5   80:32753/TCP   10d
kubernetes         ClusterIP      10.43.0.1       <none>                             443/TCP        10d

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods
NAME                                   READY   STATUS              RESTARTS      AGE
academia-deployment-54458475fb-72sth   0/1     ErrImageNeverPull   0             7m17s
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running             4 (31m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running             4 (31m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running             4 (31m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running             3 (31m ago)   10d

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl describe pod academiaacademia-deployment-54458475fb-72sth
Error from server (NotFound): pods "academiaacademia-deployment-54458475fb-72sth" not found

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl logs pod academiaacademia-deployment-54458475fb-72sth
error: error from server (NotFound): pods "pod" not found in namespace "default"

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ docker -v
Docker version 28.5.1, build e180ab8

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ docker images | grep academia-multi-centro
academia-multi-centro      latest         b1558ea12098   22 hours ago    548MB

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ k3d image import academia-multi-centro:latest -c k3d-mi-cluster-java
FATA[0000] failed to get cluster k3d-mi-cluster-java: No nodes found for given cluster

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ k3d image import academia-multi-centro:latest -c mi-cluster-java
INFO[0000] Importing image(s) into cluster 'mi-cluster-java'
INFO[0000] Starting new tools node...
INFO[0000] Starting node 'k3d-mi-cluster-java-tools'
INFO[0000] Saving 1 image(s) from runtime...
INFO[0004] Importing images into nodes...
INFO[0004] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206084834.tar' into node 'k3d-mi-cluster-java-server-0'...
INFO[0004] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206084834.tar' into node 'k3d-mi-cluster-java-agent-1'...
INFO[0004] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206084834.tar' into node 'k3d-mi-cluster-java-agent-0'...
INFO[0008] Removing the tarball(s) from image volume...
INFO[0009] Removing k3d-tools node...
INFO[0009] Successfully imported image(s)
INFO[0009] Successfully imported 1 image(s) into 1 cluster(s)

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl delete pod academia-deployment-54458475fb-72sth
pod "academia-deployment-54458475fb-72sth" deleted from default namespace

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS    RESTARTS      AGE
academia-deployment-54458475fb-zxvsn   0/1     Running   0             26s
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running   4 (38m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running   4 (38m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running   4 (38m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running   3 (38m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get svc academia-service
NAME               TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)        AGE
academia-service   LoadBalancer   10.43.24.253   <pending>     80:30419/TCP   15m

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -o wide
NAME                                   READY   STATUS    RESTARTS      AGE     IP           NODE                          NOMINATED NODE   READINESS GATES
academia-deployment-54458475fb-zxvsn   0/1     Running   4 (58s ago)   8m59s   10.42.1.31   k3d-mi-cluster-java-agent-1   <none>           <none>
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running   4 (46m ago)   10d     10.42.1.30   k3d-mi-cluster-java-agent-1   <none>           <none>
backend-deployment-7cdf89b6fd-lgzql    1/1     Running   4 (46m ago)   10d     10.42.0.25   k3d-mi-cluster-java-agent-0   <none>           <none>
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running   4 (46m ago)   10d     10.42.0.26   k3d-mi-cluster-java-agent-0   <none>           <none>
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running   3 (46m ago)   10d     10.42.1.27   k3d-mi-cluster-java-agent-1   <none>           <none>

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get deployments academia-deployment
NAME                  READY   UP-TO-DATE   AVAILABLE   AGE
academia-deployment   0/1     1            0           23m

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods | grep academia-deployment
academia-deployment-54458475fb-zxvsn   0/1     Running   4 (105s ago)   9m46s

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl logs -f academia-deployment-54458475fb-zxvsn
Production profile - skipping .env file

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.7)

2025-12-06T08:00:32.406Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Starting AcademymanagerApplication v0.0.1-SNAPSHOT using Java 21.0.9 with PID 1 (/app/app.jar started by ? in /app)
2025-12-06T08:00:32.408Z DEBUG 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Running with Spring Boot v3.5.7, Spring v6.2.12
2025-12-06T08:00:32.409Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : The following 1 profile is active: "prod"
2025-12-06T08:00:33.459Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2025-12-06T08:00:33.628Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 158 ms. Found 13 JPA repository interfaces.
Production profile detected - skipping .env file loading
2025-12-06T08:00:34.277Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2025-12-06T08:00:34.294Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-12-06T08:00:34.295Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.48]
2025-12-06T08:00:34.322Z  INFO 1 --- [academymanager] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-12-06T08:00:34.323Z  INFO 1 --- [academymanager] [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1878 ms
2025-12-06T08:00:34.570Z  INFO 1 --- [academymanager] [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2025-12-06T08:00:34.616Z  INFO 1 --- [academymanager] [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.6.33.Final
2025-12-06T08:00:34.647Z  INFO 1 --- [academymanager] [           main] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
2025-12-06T08:00:34.874Z  INFO 1 --- [academymanager] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2025-12-06T08:00:34.896Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2025-12-06T08:00:35.662Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@2f39b534
2025-12-06T08:00:35.663Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2025-12-06T08:00:35.740Z  WARN 1 --- [academymanager] [           main] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2025-12-06T08:00:35.910Z  INFO 1 --- [academymanager] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
        Database JDBC URL [Connecting through datasource 'HikariDataSource (HikariPool-1)']
        Database driver: undefined/unknown
        Database version: 17.6
        Autocommit mode: undefined/unknown
        Isolation level: undefined/unknown
        Minimum pool size: undefined/unknown
        Maximum pool size: undefined/unknown
2025-12-06T08:00:36.889Z  INFO 1 --- [academymanager] [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2025-12-06T08:00:37.258Z  INFO 1 --- [academymanager] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T08:00:37.532Z DEBUG 1 --- [academymanager] [           main] c.a.a.security.JwtAuthenticationFilter   : Filter 'jwtAuthenticationFilter' configured for use
2025-12-06T08:00:37.602Z  INFO 1 --- [academymanager] [           main] eAuthenticationProviderManagerConfigurer : Global AuthenticationManager configured with AuthenticationProvider bean with name authenticationProvider
2025-12-06T08:00:37.603Z  WARN 1 --- [academymanager] [           main] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean. UserDetailsService beans will not be used by Spring Security for automatically configuring username/password login. Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated DaoAuthenticationProvider. If the current configuration is intentional, to turn off this warning, increase the logging level of 'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
2025-12-06T08:00:37.865Z  WARN 1 --- [academymanager] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2025-12-06T08:00:38.326Z  INFO 1 --- [academymanager] [           main] efaultSchemaResourceGraphQlSourceBuilder : Loaded 1 resource(s) in the GraphQL schema.
2025-12-06T08:00:38.491Z  INFO 1 --- [academymanager] [           main] o.s.b.a.g.GraphQlAutoConfiguration       : GraphQL schema inspection:
        Unmapped fields: {Matricula=[fechaModificacion, convocatoria, alumno, entidadSubvencionadora, calificaciones, facturas], Curso=[fechaModificacion, materia, formato, convocatorias], Usuario=[datosPersonales], Convocatoria=[fechaModificacion, curso, profesor, centro, matriculas], Materia=[descripcion], Formato=[descripcion, activo], Centro=[empresa, comunidad]}
        Unmapped registrations: {}
        Unmapped arguments: {}
        Skipped types: []
2025-12-06T08:00:38.513Z  INFO 1 --- [academymanager] [           main] s.b.a.g.s.GraphQlWebMvcAutoConfiguration : GraphQL endpoint HTTP POST /graphql
2025-12-06T08:00:38.632Z  INFO 1 --- [academymanager] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoints beneath base path '/actuator'
2025-12-06T08:00:38.974Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-12-06T08:00:38.985Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Started AcademymanagerApplication in 6.968 seconds (process running for 7.373)
2025-12-06T08:01:31.265Z  INFO 1 --- [academymanager] [nio-8080-exec-2] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-12-06T08:01:31.266Z  INFO 1 --- [academymanager] [nio-8080-exec-2] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-12-06T08:01:31.269Z  INFO 1 --- [academymanager] [nio-8080-exec-2] o.s.web.servlet.DispatcherServlet        : Completed initialization in 2 ms
2025-12-06T08:02:31.231Z  INFO 1 --- [academymanager] [ionShutdownHook] o.s.b.w.e.tomcat.GracefulShutdown        : Commencing graceful shutdown. Waiting for active requests to complete
2025-12-06T08:02:31.232Z  INFO 1 --- [academymanager] [tomcat-shutdown] o.s.b.w.e.tomcat.GracefulShutdown        : Graceful shutdown complete
2025-12-06T08:02:31.239Z  INFO 1 --- [academymanager] [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T08:02:31.241Z  INFO 1 --- [academymanager] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2025-12-06T08:02:31.503Z  INFO 1 --- [academymanager] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS             RESTARTS      AGE
academia-deployment-54458475fb-zxvsn   0/1     CrashLoopBackOff   6 (36s ago)   14m
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running            4 (52m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running            4 (52m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running            4 (52m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running            3 (52m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get svc academia-service
NAME               TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)        AGE
academia-service   LoadBalancer   10.43.24.253   <pending>     80:30419/TCP   32m

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS    RESTARTS        AGE
academia-deployment-54458475fb-zxvsn   0/1     Running   7 (4m52s ago)   18m
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running   4 (56m ago)     10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running   4 (56m ago)     10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running   4 (56m ago)     10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running   3 (56m ago)     10d
academia-deployment-54458475fb-zxvsn   0/1     Running   8 (1s ago)      19m


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS    RESTARTS      AGE
academia-deployment-54458475fb-zxvsn   0/1     Running   8 (82s ago)   20m
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running   4 (58m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running   4 (58m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running   4 (58m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running   3 (58m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS    RESTARTS      AGE
academia-deployment-54458475fb-zxvsn   0/1     Running   8 (96s ago)   20m
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running   4 (58m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running   4 (58m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running   4 (58m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running   3 (58m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS             RESTARTS      AGE
academia-deployment-54458475fb-zxvsn   0/1     CrashLoopBackOff   8 (2m ago)    23m
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running            4 (60m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running            4 (60m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running            4 (60m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running            3 (60m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl apply -f deployment.yaml
deployment.apps/academia-deployment configured

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl rollout restart deployment/academia-deployment
deployment.apps/academia-deployment restarted

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS    RESTARTS      AGE
academia-deployment-549bc6645f-c7kh5   0/1     Running   0             10s
academia-deployment-bf8f4f55f-kthw4    0/1     Running   0             19s
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running   4 (62m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running   4 (62m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running   4 (62m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running   3 (62m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl logs -f deployment/academia-deployment
Found 2 pods, using pod/academia-deployment-bf8f4f55f-kthw4
✓ .env file loaded successfully

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.7)

2025-12-06T08:15:15.077Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Starting AcademymanagerApplication v0.0.1-SNAPSHOT using Java 21.0.9 with PID 1 (/app/app.jar started by ? in /app)
2025-12-06T08:15:15.078Z DEBUG 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Running with Spring Boot v3.5.7, Spring v6.2.12
2025-12-06T08:15:15.079Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : No active profile set, falling back to 1 default profile: "default"
2025-12-06T08:15:16.088Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2025-12-06T08:15:16.240Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 137 ms. Found 13 JPA repository interfaces.
✓ .env file loaded successfully (development mode)
2025-12-06T08:15:16.892Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2025-12-06T08:15:16.908Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-12-06T08:15:16.909Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.48]
2025-12-06T08:15:16.940Z  INFO 1 --- [academymanager] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-12-06T08:15:16.941Z  INFO 1 --- [academymanager] [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1827 ms
2025-12-06T08:15:17.195Z  INFO 1 --- [academymanager] [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2025-12-06T08:15:17.242Z  INFO 1 --- [academymanager] [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.6.33.Final
2025-12-06T08:15:17.272Z  INFO 1 --- [academymanager] [           main] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
2025-12-06T08:15:17.521Z  INFO 1 --- [academymanager] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2025-12-06T08:15:17.548Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2025-12-06T08:15:18.328Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@3a239dac
2025-12-06T08:15:18.330Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2025-12-06T08:15:18.428Z  WARN 1 --- [academymanager] [           main] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2025-12-06T08:15:18.600Z  INFO 1 --- [academymanager] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
        Database JDBC URL [Connecting through datasource 'HikariDataSource (HikariPool-1)']
        Database driver: undefined/unknown
        Database version: 17.6
        Autocommit mode: undefined/unknown
        Isolation level: undefined/unknown
        Minimum pool size: undefined/unknown
        Maximum pool size: undefined/unknown
2025-12-06T08:15:19.682Z  INFO 1 --- [academymanager] [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2025-12-06T08:15:20.089Z  INFO 1 --- [academymanager] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T08:15:20.382Z DEBUG 1 --- [academymanager] [           main] c.a.a.security.JwtAuthenticationFilter   : Filter 'jwtAuthenticationFilter' configured for use
2025-12-06T08:15:20.409Z  INFO 1 --- [academymanager] [           main] c.a.academymanager.config.DotenvConfig   : Development mode - .env file support enabled
2025-12-06T08:15:20.469Z  INFO 1 --- [academymanager] [           main] eAuthenticationProviderManagerConfigurer : Global AuthenticationManager configured with AuthenticationProvider bean with name authenticationProvider
2025-12-06T08:15:20.469Z  WARN 1 --- [academymanager] [           main] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean. UserDetailsService beans will not be used by Spring Security for automatically configuring username/password login. Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated DaoAuthenticationProvider. If the current configuration is intentional, to turn off this warning, increase the logging level of 'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
2025-12-06T08:15:20.724Z  WARN 1 --- [academymanager] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2025-12-06T08:15:21.186Z  INFO 1 --- [academymanager] [           main] efaultSchemaResourceGraphQlSourceBuilder : Loaded 1 resource(s) in the GraphQL schema.
2025-12-06T08:15:21.361Z  INFO 1 --- [academymanager] [           main] o.s.b.a.g.GraphQlAutoConfiguration       : GraphQL schema inspection:
        Unmapped fields: {Matricula=[fechaModificacion, convocatoria, alumno, entidadSubvencionadora, calificaciones, facturas], Curso=[fechaModificacion, materia, formato, convocatorias], Usuario=[datosPersonales], Convocatoria=[fechaModificacion, curso, profesor, centro, matriculas], Materia=[descripcion], Formato=[descripcion, activo], Centro=[empresa, comunidad]}
        Unmapped registrations: {}
        Unmapped arguments: {}
        Skipped types: []
2025-12-06T08:15:21.383Z  INFO 1 --- [academymanager] [           main] s.b.a.g.s.GraphQlWebMvcAutoConfiguration : GraphQL endpoint HTTP POST /graphql
2025-12-06T08:15:21.509Z  INFO 1 --- [academymanager] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoints beneath base path '/actuator'
2025-12-06T08:15:21.866Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-12-06T08:15:21.877Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Started AcademymanagerApplication in 7.165 seconds (process running for 7.596)
2025-12-06T08:16:14.203Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-12-06T08:16:14.204Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-12-06T08:16:14.207Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 3 ms
2025-12-06T08:17:14.174Z  INFO 1 --- [academymanager] [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T08:17:14.177Z  INFO 1 --- [academymanager] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2025-12-06T08:17:14.487Z  INFO 1 --- [academymanager] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS    RESTARTS      AGE
academia-deployment-549bc6645f-c7kh5   0/1     Running   1 (10s ago)   2m11s
academia-deployment-bf8f4f55f-kthw4    0/1     Running   1 (19s ago)   2m20s
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running   4 (64m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running   4 (64m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running   4 (64m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running   3 (64m ago)   10d
academia-deployment-bf8f4f55f-kthw4    0/1     Running   2 (1s ago)    4m2s
academia-deployment-549bc6645f-c7kh5   0/1     Running   2 (0s ago)    4m1s
academia-deployment-bf8f4f55f-kthw4    0/1     Running   3 (1s ago)    6m2s
academia-deployment-549bc6645f-c7kh5   0/1     Running   3 (0s ago)    6m1s
academia-deployment-bf8f4f55f-kthw4    0/1     Running   4 (0s ago)    8m1s
academia-deployment-549bc6645f-c7kh5   0/1     Running   4 (1s ago)    8m2s
academia-deployment-bf8f4f55f-kthw4    0/1     Running   5 (0s ago)    10m
academia-deployment-549bc6645f-c7kh5   0/1     Running   5 (1s ago)    10m
academia-deployment-bf8f4f55f-kthw4    0/1     Running   6 (1s ago)    12m
academia-deployment-549bc6645f-c7kh5   0/1     Running   6 (0s ago)    12m


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl delete deployment academia-deployment --ignore-not-found
deployment.apps "academia-deployment" deleted from default namespace

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl delete service academia-service --ignore-not-found
service "academia-service" deleted from default namespace

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get all
NAME                                      READY   STATUS    RESTARTS      AGE
pod/backend-deployment-7cdf89b6fd-2fnjm   1/1     Running   4 (76m ago)   10d
pod/backend-deployment-7cdf89b6fd-lgzql   1/1     Running   4 (76m ago)   10d
pod/frontend-deployment-9f48f9d4d-pfbfs   1/1     Running   4 (76m ago)   10d
pod/frontend-deployment-9f48f9d4d-wwhmb   1/1     Running   3 (76m ago)   10d

NAME                       TYPE           CLUSTER-IP      EXTERNAL-IP                        PORT(S)        AGE
service/backend-service    ClusterIP      10.43.251.249   <none>                             8080/TCP       10d
service/frontend-service   LoadBalancer   10.43.191.133   172.24.0.3,172.24.0.4,172.24.0.5   80:32753/TCP   10d
service/kubernetes         ClusterIP      10.43.0.1       <none>                             443/TCP        10d

NAME                                  READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/backend-deployment    2/2     2            2           10d
deployment.apps/frontend-deployment   2/2     2            2           10d

NAME                                            DESIRED   CURRENT   READY   AGE
replicaset.apps/backend-deployment-7cdf89b6fd   2         2         2       10d
replicaset.apps/frontend-deployment-9f48f9d4d   2         2         2       10d

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ k3d image import academia-multi-centro:latest -c mi-cluster-java
INFO[0000] Importing image(s) into cluster 'mi-cluster-java'
INFO[0000] Starting new tools node...
INFO[0000] Starting node 'k3d-mi-cluster-java-tools'
INFO[0000] Saving 1 image(s) from runtime...
INFO[0004] Importing images into nodes...
INFO[0004] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206093058.tar' into node 'k3d-mi-cluster-java-server-0'...
INFO[0004] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206093058.tar' into node 'k3d-mi-cluster-java-agent-1'...
INFO[0004] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206093058.tar' into node 'k3d-mi-cluster-java-agent-0'...
INFO[0007] Removing the tarball(s) from image volume...
INFO[0008] Removing k3d-tools node...
INFO[0008] Successfully imported image(s)
INFO[0008] Successfully imported 1 image(s) into 1 cluster(s)

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                  READY   STATUS    RESTARTS      AGE
backend-deployment-7cdf89b6fd-2fnjm   1/1     Running   4 (79m ago)   10d
backend-deployment-7cdf89b6fd-lgzql   1/1     Running   4 (79m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs   1/1     Running   4 (79m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb   1/1     Running   3 (79m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl apply -f deployment.yaml
deployment.apps/academia-deployment created

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl apply -f service.yaml
service/academia-service created

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS    RESTARTS      AGE
academia-deployment-65d95f5bb8-944dq   0/1     Running   0             21s
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running   4 (80m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running   4 (80m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running   4 (80m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running   3 (80m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get svc academia-service
NAME               TYPE           CLUSTER-IP   EXTERNAL-IP   PORT(S)        AGE
academia-service   LoadBalancer   10.43.4.85   <pending>     80:31577/TCP   37s

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl logs deployment/academia-deployment -f
Production profile - skipping .env file

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.7)

2025-12-06T08:32:42.330Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Starting AcademymanagerApplication v0.0.1-SNAPSHOT using Java 21.0.9 with PID 1 (/app/app.jar started by ? in /app)
2025-12-06T08:32:42.331Z DEBUG 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Running with Spring Boot v3.5.7, Spring v6.2.12
2025-12-06T08:32:42.332Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : The following 1 profile is active: "prod"
2025-12-06T08:32:43.351Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2025-12-06T08:32:43.542Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 178 ms. Found 13 JPA repository interfaces.
Production profile detected - skipping .env file loading
2025-12-06T08:32:44.398Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2025-12-06T08:32:44.427Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-12-06T08:32:44.428Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.48]
2025-12-06T08:32:44.470Z  INFO 1 --- [academymanager] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-12-06T08:32:44.472Z  INFO 1 --- [academymanager] [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 2097 ms
2025-12-06T08:32:44.841Z  INFO 1 --- [academymanager] [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2025-12-06T08:32:44.879Z  INFO 1 --- [academymanager] [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.6.33.Final
2025-12-06T08:32:44.903Z  INFO 1 --- [academymanager] [           main] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
2025-12-06T08:32:45.116Z  INFO 1 --- [academymanager] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2025-12-06T08:32:45.148Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2025-12-06T08:32:45.969Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@35c00c
2025-12-06T08:32:45.970Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2025-12-06T08:32:46.049Z  WARN 1 --- [academymanager] [           main] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2025-12-06T08:32:46.223Z  INFO 1 --- [academymanager] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
        Database JDBC URL [Connecting through datasource 'HikariDataSource (HikariPool-1)']
        Database driver: undefined/unknown
        Database version: 17.6
        Autocommit mode: undefined/unknown
        Isolation level: undefined/unknown
        Minimum pool size: undefined/unknown
        Maximum pool size: undefined/unknown
2025-12-06T08:32:47.206Z  INFO 1 --- [academymanager] [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2025-12-06T08:32:47.569Z  INFO 1 --- [academymanager] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T08:32:47.857Z DEBUG 1 --- [academymanager] [           main] c.a.a.security.JwtAuthenticationFilter   : Filter 'jwtAuthenticationFilter' configured for use
2025-12-06T08:32:47.941Z  INFO 1 --- [academymanager] [           main] eAuthenticationProviderManagerConfigurer : Global AuthenticationManager configured with AuthenticationProvider bean with name authenticationProvider
2025-12-06T08:32:47.941Z  WARN 1 --- [academymanager] [           main] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean. UserDetailsService beans will not be used by Spring Security for automatically configuring username/password login. Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated DaoAuthenticationProvider. If the current configuration is intentional, to turn off this warning, increase the logging level of 'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
2025-12-06T08:32:48.248Z  WARN 1 --- [academymanager] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2025-12-06T08:32:48.718Z  INFO 1 --- [academymanager] [           main] efaultSchemaResourceGraphQlSourceBuilder : Loaded 1 resource(s) in the GraphQL schema.
2025-12-06T08:32:48.908Z  INFO 1 --- [academymanager] [           main] o.s.b.a.g.GraphQlAutoConfiguration       : GraphQL schema inspection:
        Unmapped fields: {Matricula=[fechaModificacion, convocatoria, alumno, entidadSubvencionadora, calificaciones, facturas], Curso=[fechaModificacion, materia, formato, convocatorias], Usuario=[datosPersonales], Convocatoria=[fechaModificacion, curso, profesor, centro, matriculas], Materia=[descripcion], Formato=[descripcion, activo], Centro=[empresa, comunidad]}
        Unmapped registrations: {}
        Unmapped arguments: {}
        Skipped types: []
2025-12-06T08:32:48.930Z  INFO 1 --- [academymanager] [           main] s.b.a.g.s.GraphQlWebMvcAutoConfiguration : GraphQL endpoint HTTP POST /graphql
2025-12-06T08:32:49.063Z  INFO 1 --- [academymanager] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoints beneath base path '/actuator'
2025-12-06T08:32:49.477Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-12-06T08:32:49.489Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Started AcademymanagerApplication in 7.51 seconds (process running for 7.942)
2025-12-06T08:32:51.448Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-12-06T08:32:51.449Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-12-06T08:32:51.450Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
2025-12-06T08:34:11.434Z  INFO 1 --- [academymanager] [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T08:34:11.437Z  INFO 1 --- [academymanager] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2025-12-06T08:34:11.708Z  INFO 1 --- [academymanager] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl logs deployment/academia-deployment -f
Production profile - skipping .env file

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.7)

2025-12-06T08:34:12.661Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Starting AcademymanagerApplication v0.0.1-SNAPSHOT using Java 21.0.9 with PID 1 (/app/app.jar started by ? in /app)
2025-12-06T08:34:12.663Z DEBUG 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Running with Spring Boot v3.5.7, Spring v6.2.12
2025-12-06T08:34:12.664Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : The following 1 profile is active: "prod"
2025-12-06T08:34:13.709Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2025-12-06T08:34:13.868Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 148 ms. Found 13 JPA repository interfaces.
Production profile detected - skipping .env file loading
2025-12-06T08:34:14.608Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2025-12-06T08:34:14.622Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-12-06T08:34:14.622Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.48]
2025-12-06T08:34:14.647Z  INFO 1 --- [academymanager] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-12-06T08:34:14.648Z  INFO 1 --- [academymanager] [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1935 ms
2025-12-06T08:34:14.977Z  INFO 1 --- [academymanager] [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2025-12-06T08:34:15.013Z  INFO 1 --- [academymanager] [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.6.33.Final
2025-12-06T08:34:15.037Z  INFO 1 --- [academymanager] [           main] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
2025-12-06T08:34:15.244Z  INFO 1 --- [academymanager] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2025-12-06T08:34:15.263Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2025-12-06T08:34:16.042Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@57cff804
2025-12-06T08:34:16.044Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2025-12-06T08:34:16.121Z  WARN 1 --- [academymanager] [           main] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2025-12-06T08:34:16.293Z  INFO 1 --- [academymanager] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
        Database JDBC URL [Connecting through datasource 'HikariDataSource (HikariPool-1)']
        Database driver: undefined/unknown
        Database version: 17.6
        Autocommit mode: undefined/unknown
        Isolation level: undefined/unknown
        Minimum pool size: undefined/unknown
        Maximum pool size: undefined/unknown
2025-12-06T08:34:17.305Z  INFO 1 --- [academymanager] [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2025-12-06T08:34:17.660Z  INFO 1 --- [academymanager] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T08:34:17.944Z DEBUG 1 --- [academymanager] [           main] c.a.a.security.JwtAuthenticationFilter   : Filter 'jwtAuthenticationFilter' configured for use
2025-12-06T08:34:18.022Z  INFO 1 --- [academymanager] [           main] eAuthenticationProviderManagerConfigurer : Global AuthenticationManager configured with AuthenticationProvider bean with name authenticationProvider
2025-12-06T08:34:18.022Z  WARN 1 --- [academymanager] [           main] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean. UserDetailsService beans will not be used by Spring Security for automatically configuring username/password login. Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated DaoAuthenticationProvider. If the current configuration is intentional, to turn off this warning, increase the logging level of 'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
2025-12-06T08:34:18.296Z  WARN 1 --- [academymanager] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2025-12-06T08:34:18.751Z  INFO 1 --- [academymanager] [           main] efaultSchemaResourceGraphQlSourceBuilder : Loaded 1 resource(s) in the GraphQL schema.
2025-12-06T08:34:18.917Z  INFO 1 --- [academymanager] [           main] o.s.b.a.g.GraphQlAutoConfiguration       : GraphQL schema inspection:
        Unmapped fields: {Matricula=[fechaModificacion, convocatoria, alumno, entidadSubvencionadora, calificaciones, facturas], Curso=[fechaModificacion, materia, formato, convocatorias], Usuario=[datosPersonales], Convocatoria=[fechaModificacion, curso, profesor, centro, matriculas], Materia=[descripcion], Formato=[descripcion, activo], Centro=[empresa, comunidad]}
        Unmapped registrations: {}
        Unmapped arguments: {}
        Skipped types: []
2025-12-06T08:34:18.938Z  INFO 1 --- [academymanager] [           main] s.b.a.g.s.GraphQlWebMvcAutoConfiguration : GraphQL endpoint HTTP POST /graphql
2025-12-06T08:34:19.066Z  INFO 1 --- [academymanager] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoints beneath base path '/actuator'
2025-12-06T08:34:19.450Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-12-06T08:34:19.461Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Started AcademymanagerApplication in 7.204 seconds (process running for 7.645)
2025-12-06T08:34:21.452Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-12-06T08:34:21.452Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-12-06T08:34:21.453Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
2025-12-06T08:35:41.428Z  INFO 1 --- [academymanager] [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T08:35:41.432Z  INFO 1 --- [academymanager] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2025-12-06T08:35:41.695Z  INFO 1 --- [academymanager] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl port-forward svc/academia-service 8080:80
Forwarding from 127.0.0.1:8080 -> 8080
Forwarding from [::1]:8080 -> 8080
Handling connection for 8080
Handling connection for 8080
Handling connection for 8080
Handling connection for 8080
E1206 09:37:12.462498   21400 portforward.go:424] "Unhandled Error" err="an error occurred forwarding 8080 -> 8080: error forwarding port 8080 to pod a228b3b64f38a98d2a500e18e1ae9e49a49c76c29bd3213df0933eab3f76c6cf, uid : failed to execute portforward in network namespace \"/var/run/netns/cni-4e821d61-4dd5-bf75-d052-32beb4a2c042\": failed to connect to localhost:8080 inside namespace \"a228b3b64f38a98d2a500e18e1ae9e49a49c76c29bd3213df0933eab3f76c6cf\", IPv4: dial tcp4 127.0.0.1:8080: connect: connection refused IPv6 dial tcp6 [::1]:8080: connect: connection refused "
error: lost connection to pod

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl port-forward svc/academia-service 8080:80
Forwarding from 127.0.0.1:8080 -> 8080
Forwarding from [::1]:8080 -> 8080
Handling connection for 8080
Handling connection for 8080
Handling connection for 8080
Handling connection for 8080
Handling connection for 8080
Handling connection for 8080
Handling connection for 8080


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS    RESTARTS      AGE
academia-deployment-65d95f5bb8-944dq   0/1     Running   6 (74s ago)   10m
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running   4 (90m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running   4 (90m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running   4 (90m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running   3 (90m ago)   10d
academia-deployment-65d95f5bb8-944dq   0/1     CrashLoopBackOff   6 (0s ago)    10m
academia-deployment-65d95f5bb8-944dq   0/1     Running            7 (2m43s ago)   13m
academia-deployment-65d95f5bb8-944dq   0/1     CrashLoopBackOff   7 (1s ago)      14m
academia-deployment-65d95f5bb8-944dq   0/1     Running            8 (5m6s ago)    19m
academia-deployment-65d95f5bb8-944dq   0/1     Running            9 (1s ago)      21m
academia-deployment-65d95f5bb8-944dq   0/1     CrashLoopBackOff   9 (1s ago)      22m
academia-deployment-65d95f5bb8-944dq   0/1     Running            10 (5m4s ago)   27m
academia-deployment-65d95f5bb8-944dq   0/1     Running            11 (0s ago)     29m
academia-deployment-65d95f5bb8-944dq   0/1     CrashLoopBackOff   11 (1s ago)     30m


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods | grep academia-deployment
academia-deployment-65d95f5bb8-944dq   0/1     CrashLoopBackOff   11 (16s ago)   31m

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl describe pod academia-deployment-65d95f5bb8-944dq
Name:             academia-deployment-65d95f5bb8-944dq
Namespace:        default
Priority:         0
Service Account:  default
Node:             k3d-mi-cluster-java-server-0/172.24.0.5
Start Time:       Sat, 06 Dec 2025 09:32:41 +0100
Labels:           app=academia
                  pod-template-hash=65d95f5bb8
Annotations:      <none>
Status:           Running
IP:               10.42.2.17
IPs:
  IP:           10.42.2.17
Controlled By:  ReplicaSet/academia-deployment-65d95f5bb8
Containers:
  academia:
    Container ID:   containerd://30e4cb3ef91a28dea34cba753abb9bc2e277194d1bb5dbe5e86ecac97bf364bb
    Image:          academia-multi-centro:latest
    Image ID:       sha256:1d1d84fb1eb8067f309101e64122e7b9e5893730741cbfbc3ff7cccc1ce3563c
    Port:           8080/TCP
    Host Port:      0/TCP
    State:          Waiting
      Reason:       CrashLoopBackOff
    Last State:     Terminated
      Reason:       Error
      Exit Code:    143
      Started:      Sat, 06 Dec 2025 10:01:56 +0100
      Finished:     Sat, 06 Dec 2025 10:03:26 +0100
    Ready:          False
    Restart Count:  11
    Limits:
      cpu:     2
      memory:  2Gi
    Requests:
      cpu:      500m
      memory:   1Gi
    Liveness:   http-get http://:8080/actuator/health delay=20s timeout=5s period=15s #success=1 #failure=5
    Readiness:  http-get http://:8080/actuator/health delay=10s timeout=5s period=10s #success=1 #failure=3
    Environment:
      SPRING_PROFILES_ACTIVE:  prod
      SERVER_SHUTDOWN:         immediate
      DB_SUPABASE:             jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres?sslmode=require
      DB_USERNAME:             postgres.wjbbuiiskercelchtaqg
      DB_PASSWORD:             Ac4d3m1a_1994!
      JWT_SECRET_KEY:          404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
      JWT_EXPIRATION_TIME:     86400000
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-m6xkx (ro)
Conditions:
  Type                        Status
  PodReadyToStartContainers   True
  Initialized                 True
  Ready                       False
  ContainersReady             False
  PodScheduled                True
Volumes:
  kube-api-access-m6xkx:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    Optional:                false
    DownwardAPI:             true
QoS Class:                   Burstable
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type     Reason     Age                   From               Message
  ----     ------     ----                  ----               -------
  Normal   Scheduled  31m                   default-scheduler  Successfully assigned default/academia-deployment-65d95f5bb8-944dq to k3d-mi-cluster-java-server-0
  Normal   Pulled     30m (x2 over 31m)     kubelet            Container image "academia-multi-centro:latest" already present on machine
  Normal   Created    30m (x2 over 31m)     kubelet            Created container academia
  Normal   Started    30m (x2 over 31m)     kubelet            Started container academia
  Warning  Unhealthy  30m (x5 over 31m)     kubelet            Liveness probe failed: HTTP probe failed with statuscode: 403
  Normal   Killing    30m                   kubelet            Container academia failed liveness probe, will be restarted
  Warning  BackOff    6m30s (x50 over 21m)  kubelet            Back-off restarting failed container academia in pod academia-deployment-65d95f5bb8-944dq_default(3ea4e25e-8de6-4a3c-b78e-363b4063b22e)
  Warning  Unhealthy  81s (x121 over 31m)   kubelet            Readiness probe failed: HTTP probe failed with statuscode: 403

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl logs academia-deployment-65d95f5bb8-944dq --previous
Production profile - skipping .env file

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.7)

2025-12-06T09:01:57.529Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Starting AcademymanagerApplication v0.0.1-SNAPSHOT using Java 21.0.9 with PID 1 (/app/app.jar started by ? in /app)
2025-12-06T09:01:57.530Z DEBUG 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Running with Spring Boot v3.5.7, Spring v6.2.12
2025-12-06T09:01:57.531Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : The following 1 profile is active: "prod"
2025-12-06T09:01:58.526Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2025-12-06T09:01:58.671Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 136 ms. Found 13 JPA repository interfaces.
Production profile detected - skipping .env file loading
2025-12-06T09:01:59.416Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2025-12-06T09:01:59.432Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-12-06T09:01:59.432Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.48]
2025-12-06T09:01:59.457Z  INFO 1 --- [academymanager] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-12-06T09:01:59.459Z  INFO 1 --- [academymanager] [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1883 ms
2025-12-06T09:01:59.757Z  INFO 1 --- [academymanager] [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2025-12-06T09:01:59.792Z  INFO 1 --- [academymanager] [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.6.33.Final
2025-12-06T09:01:59.816Z  INFO 1 --- [academymanager] [           main] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
2025-12-06T09:02:00.014Z  INFO 1 --- [academymanager] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2025-12-06T09:02:00.035Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2025-12-06T09:02:00.789Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@33ccead
2025-12-06T09:02:00.791Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2025-12-06T09:02:00.868Z  WARN 1 --- [academymanager] [           main] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2025-12-06T09:02:01.039Z  INFO 1 --- [academymanager] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
        Database JDBC URL [Connecting through datasource 'HikariDataSource (HikariPool-1)']
        Database driver: undefined/unknown
        Database version: 17.6
        Autocommit mode: undefined/unknown
        Isolation level: undefined/unknown
        Minimum pool size: undefined/unknown
        Maximum pool size: undefined/unknown
2025-12-06T09:02:02.048Z  INFO 1 --- [academymanager] [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2025-12-06T09:02:02.400Z  INFO 1 --- [academymanager] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T09:02:02.669Z DEBUG 1 --- [academymanager] [           main] c.a.a.security.JwtAuthenticationFilter   : Filter 'jwtAuthenticationFilter' configured for use
2025-12-06T09:02:02.747Z  INFO 1 --- [academymanager] [           main] eAuthenticationProviderManagerConfigurer : Global AuthenticationManager configured with AuthenticationProvider bean with name authenticationProvider
2025-12-06T09:02:02.748Z  WARN 1 --- [academymanager] [           main] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean. UserDetailsService beans will not be used by Spring Security for automatically configuring username/password login. Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated DaoAuthenticationProvider. If the current configuration is intentional, to turn off this warning, increase the logging level of 'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
2025-12-06T09:02:03.024Z  WARN 1 --- [academymanager] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2025-12-06T09:02:03.492Z  INFO 1 --- [academymanager] [           main] efaultSchemaResourceGraphQlSourceBuilder : Loaded 1 resource(s) in the GraphQL schema.
2025-12-06T09:02:03.653Z  INFO 1 --- [academymanager] [           main] o.s.b.a.g.GraphQlAutoConfiguration       : GraphQL schema inspection:
        Unmapped fields: {Matricula=[fechaModificacion, convocatoria, alumno, entidadSubvencionadora, calificaciones, facturas], Curso=[fechaModificacion, materia, formato, convocatorias], Usuario=[datosPersonales], Convocatoria=[fechaModificacion, curso, profesor, centro, matriculas], Materia=[descripcion], Formato=[descripcion, activo], Centro=[empresa, comunidad]}
        Unmapped registrations: {}
        Unmapped arguments: {}
        Skipped types: []
2025-12-06T09:02:03.672Z  INFO 1 --- [academymanager] [           main] s.b.a.g.s.GraphQlWebMvcAutoConfiguration : GraphQL endpoint HTTP POST /graphql
2025-12-06T09:02:03.804Z  INFO 1 --- [academymanager] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoints beneath base path '/actuator'
2025-12-06T09:02:04.206Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-12-06T09:02:04.217Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Started AcademymanagerApplication in 7.061 seconds (process running for 7.487)
2025-12-06T09:02:11.359Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-12-06T09:02:11.359Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-12-06T09:02:11.361Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 2 ms
2025-12-06T09:03:26.338Z  INFO 1 --- [academymanager] [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T09:03:26.340Z  INFO 1 --- [academymanager] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2025-12-06T09:03:26.604Z  INFO 1 --- [academymanager] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl apply -f deployment.yaml
deployment.apps/academia-deployment configured

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl rollout restart deployment/academia-deployment
deployment.apps/academia-deployment restarted

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                  READY   STATUS    RESTARTS       AGE
academia-deployment-7cd9fc568-kpgr6   1/1     Running   0              9s
backend-deployment-7cdf89b6fd-2fnjm   1/1     Running   4 (128m ago)   10d
backend-deployment-7cdf89b6fd-lgzql   1/1     Running   4 (128m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs   1/1     Running   4 (128m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb   1/1     Running   3 (128m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl logs deployment/academia-deployment -f
Production profile - skipping .env file

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.7)

2025-12-06T09:21:25.829Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Starting AcademymanagerApplication v0.0.1-SNAPSHOT using Java 21.0.9 with PID 1 (/app/app.jar started by ? in /app)
2025-12-06T09:21:25.831Z DEBUG 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Running with Spring Boot v3.5.7, Spring v6.2.12
2025-12-06T09:21:25.832Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : The following 1 profile is active: "prod"
2025-12-06T09:21:26.881Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2025-12-06T09:21:27.041Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 145 ms. Found 13 JPA repository interfaces.
Production profile detected - skipping .env file loading
2025-12-06T09:21:27.819Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2025-12-06T09:21:27.840Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-12-06T09:21:27.840Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.48]
2025-12-06T09:21:27.872Z  INFO 1 --- [academymanager] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-12-06T09:21:27.873Z  INFO 1 --- [academymanager] [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1985 ms
2025-12-06T09:21:28.200Z  INFO 1 --- [academymanager] [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2025-12-06T09:21:28.257Z  INFO 1 --- [academymanager] [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.6.33.Final
2025-12-06T09:21:28.288Z  INFO 1 --- [academymanager] [           main] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
2025-12-06T09:21:28.514Z  INFO 1 --- [academymanager] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2025-12-06T09:21:28.536Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2025-12-06T09:21:29.320Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@70e5737f
2025-12-06T09:21:29.322Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2025-12-06T09:21:29.402Z  WARN 1 --- [academymanager] [           main] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2025-12-06T09:21:29.580Z  INFO 1 --- [academymanager] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
        Database JDBC URL [Connecting through datasource 'HikariDataSource (HikariPool-1)']
        Database driver: undefined/unknown
        Database version: 17.6
        Autocommit mode: undefined/unknown
        Isolation level: undefined/unknown
        Minimum pool size: undefined/unknown
        Maximum pool size: undefined/unknown
2025-12-06T09:21:30.593Z  INFO 1 --- [academymanager] [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2025-12-06T09:21:30.960Z  INFO 1 --- [academymanager] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T09:21:31.249Z DEBUG 1 --- [academymanager] [           main] c.a.a.security.JwtAuthenticationFilter   : Filter 'jwtAuthenticationFilter' configured for use
2025-12-06T09:21:31.335Z  INFO 1 --- [academymanager] [           main] eAuthenticationProviderManagerConfigurer : Global AuthenticationManager configured with AuthenticationProvider bean with name authenticationProvider
2025-12-06T09:21:31.335Z  WARN 1 --- [academymanager] [           main] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean. UserDetailsService beans will not be used by Spring Security for automatically configuring username/password login. Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated DaoAuthenticationProvider. If the current configuration is intentional, to turn off this warning, increase the logging level of 'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
2025-12-06T09:21:31.689Z  WARN 1 --- [academymanager] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2025-12-06T09:21:32.141Z  INFO 1 --- [academymanager] [           main] efaultSchemaResourceGraphQlSourceBuilder : Loaded 1 resource(s) in the GraphQL schema.
2025-12-06T09:21:32.302Z  INFO 1 --- [academymanager] [           main] o.s.b.a.g.GraphQlAutoConfiguration       : GraphQL schema inspection:
        Unmapped fields: {Matricula=[fechaModificacion, convocatoria, alumno, entidadSubvencionadora, calificaciones, facturas], Curso=[fechaModificacion, materia, formato, convocatorias], Usuario=[datosPersonales], Convocatoria=[fechaModificacion, curso, profesor, centro, matriculas], Materia=[descripcion], Formato=[descripcion, activo], Centro=[empresa, comunidad]}
        Unmapped registrations: {}
        Unmapped arguments: {}
        Skipped types: []
2025-12-06T09:21:32.320Z  INFO 1 --- [academymanager] [           main] s.b.a.g.s.GraphQlWebMvcAutoConfiguration : GraphQL endpoint HTTP POST /graphql
2025-12-06T09:21:32.452Z  INFO 1 --- [academymanager] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoints beneath base path '/actuator'
2025-12-06T09:21:32.890Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-12-06T09:21:32.901Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Started AcademymanagerApplication in 7.507 seconds (process running for 8.024)


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get svc academia-service
NAME               TYPE           CLUSTER-IP   EXTERNAL-IP   PORT(S)        AGE
academia-service   LoadBalancer   10.43.4.85   <pending>     80:31577/TCP   49m

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                  READY   STATUS    RESTARTS       AGE
academia-deployment-7cd9fc568-kpgr6   1/1     Running   0              111s
backend-deployment-7cdf89b6fd-2fnjm   1/1     Running   4 (130m ago)   10d
backend-deployment-7cdf89b6fd-lgzql   1/1     Running   4 (130m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs   1/1     Running   4 (130m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb   1/1     Running   3 (130m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ curl http://localhost:31577/api/auth/login
curl: (7) Failed to connect to localhost port 31577 after 2260 ms: Could not connect to server

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl port-forward svc/academia-service 8081:80
Forwarding from 127.0.0.1:8081 -> 8080
Forwarding from [::1]:8081 -> 8080
Handling connection for 8081
Handling connection for 8081
Handling connection for 8081


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

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl apply -f deployment.yaml
The request is invalid: patch: Invalid value: "map[metadata:map[annotations:map[kubectl.kubernetes.io/last-applied-configuration:{\"apiVersion\":\"apps/v1\",\"kind\":\"Deployment\",\"metadata\":{\"annotations\":{},\"labels\":{\"app\":\"academia\"},\"name\":\"academia-deployment\",\"namespace\":\"default\"},\"spec\":{\"replicas\":1,\"selector\":{\"matchLabels\":{\"app\":\"academia\"}},\"template\":{\"metadata\":{\"labels\":{\"app\":\"academia\"}},\"spec\":{\"containers\":[{\"env\":[{\"name\":\"SPRING_PROFILES_ACTIVE\",\"value\":\"prod\"},{\"name\":\"SPRING_BOOT_GRACEFUL_SHUTDOWN_ENABLED\",\"value\":\"false\"},{\"name\":\"DB_SUPABASE\",\"value\":\"jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres?sslmode=require\"},{\"name\":\"DB_USERNAME\",\"value\":\"postgres.wjbbuiiskercelchtaqg\"},{\"name\":\"DB_PASSWORD\",\"value\":\"Ac4d3m1a_1994!\"},{\"name\":\"JWT_SECRET_KEY\",\"value\":\"404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970\"},{\"name\":\"JWT_EXPIRATION_TIME\",\"value\":\"86400000\"}],\"image\":\"academia-multi-centro:latest\",\"imagePullPolicy\":\"Never\",\"name\":\"academia\",\"ports\":[{\"containerPort\":8080}],\"resources\":{\"limits\":{\"cpu\":\"2000m\",\"memory\":\"2Gi\"},\"livenessProbe\":{\"failureThreshold\":5,\"httpGet\":{\"path\":\"/actuator/health\",\"port\":8080},\"initialDelaySeconds\":\"20                  \\u003c\\u003c\\u003c BAJADO (arranca en ~9s)\",\"periodSeconds\":15,\"timeoutSeconds\":5},\"readinessProbe\":{\"failureThreshold\":3,\"httpGet\":{\"path\":\"/actuator/health\",\"port\":8080},\"initialDelaySeconds\":\"10                  \\u003c\\u003c\\u003c BAJADO\",\"periodSeconds\":10,\"timeoutSeconds\":5},\"requests\":{\"cpu\":\"500m\",\"memory\":\"1Gi\"}},\"securityContext\":{\"runAsNonRoot\":true,\"runAsUser\":1000}}]}}}}\n]] spec:map[template:map[spec:map[]]]]": strict decoding error: unknown field "spec.template.spec.containers[0].resources.livenessProbe", unknown field "spec.template.spec.containers[0].resources.readinessProbe"

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl apply -f deployment.yaml
deployment.apps/academia-deployment configured

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl rollout restart deployment/academia-deployment
deployment.apps/academia-deployment restarted

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS    RESTARTS        AGE
academia-deployment-5c8664bc74-w54vs   0/1     Running   0               10s
academia-deployment-7cd9fc568-kpgr6    1/1     Running   0               129m
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running   4 (4h17m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running   4 (4h17m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running   4 (4h17m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running   3 (4h17m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get svc academia-service
NAME               TYPE           CLUSTER-IP   EXTERNAL-IP   PORT(S)        AGE
academia-service   LoadBalancer   10.43.4.85   <pending>     80:31577/TCP   178m

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl logs deployment/academia-deployment -f
Found 2 pods, using pod/academia-deployment-7cd9fc568-kpgr6
Production profile - skipping .env file

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.7)

2025-12-06T09:21:25.829Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Starting AcademymanagerApplication v0.0.1-SNAPSHOT using Java 21.0.9 with PID 1 (/app/app.jar started by ? in /app)
2025-12-06T09:21:25.831Z DEBUG 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Running with Spring Boot v3.5.7, Spring v6.2.12
2025-12-06T09:21:25.832Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : The following 1 profile is active: "prod"
2025-12-06T09:21:26.881Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2025-12-06T09:21:27.041Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 145 ms. Found 13 JPA repository interfaces.
Production profile detected - skipping .env file loading
2025-12-06T09:21:27.819Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2025-12-06T09:21:27.840Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-12-06T09:21:27.840Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.48]
2025-12-06T09:21:27.872Z  INFO 1 --- [academymanager] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-12-06T09:21:27.873Z  INFO 1 --- [academymanager] [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1985 ms
2025-12-06T09:21:28.200Z  INFO 1 --- [academymanager] [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2025-12-06T09:21:28.257Z  INFO 1 --- [academymanager] [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.6.33.Final
2025-12-06T09:21:28.288Z  INFO 1 --- [academymanager] [           main] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
2025-12-06T09:21:28.514Z  INFO 1 --- [academymanager] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2025-12-06T09:21:28.536Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2025-12-06T09:21:29.320Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@70e5737f
2025-12-06T09:21:29.322Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2025-12-06T09:21:29.402Z  WARN 1 --- [academymanager] [           main] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2025-12-06T09:21:29.580Z  INFO 1 --- [academymanager] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
        Database JDBC URL [Connecting through datasource 'HikariDataSource (HikariPool-1)']
        Database driver: undefined/unknown
        Database version: 17.6
        Autocommit mode: undefined/unknown
        Isolation level: undefined/unknown
        Minimum pool size: undefined/unknown
        Maximum pool size: undefined/unknown
2025-12-06T09:21:30.593Z  INFO 1 --- [academymanager] [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2025-12-06T09:21:30.960Z  INFO 1 --- [academymanager] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T09:21:31.249Z DEBUG 1 --- [academymanager] [           main] c.a.a.security.JwtAuthenticationFilter   : Filter 'jwtAuthenticationFilter' configured for use
2025-12-06T09:21:31.335Z  INFO 1 --- [academymanager] [           main] eAuthenticationProviderManagerConfigurer : Global AuthenticationManager configured with AuthenticationProvider bean with name authenticationProvider
2025-12-06T09:21:31.335Z  WARN 1 --- [academymanager] [           main] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean. UserDetailsService beans will not be used by Spring Security for automatically configuring username/password login. Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated DaoAuthenticationProvider. If the current configuration is intentional, to turn off this warning, increase the logging level of 'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
2025-12-06T09:21:31.689Z  WARN 1 --- [academymanager] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2025-12-06T09:21:32.141Z  INFO 1 --- [academymanager] [           main] efaultSchemaResourceGraphQlSourceBuilder : Loaded 1 resource(s) in the GraphQL schema.
2025-12-06T09:21:32.302Z  INFO 1 --- [academymanager] [           main] o.s.b.a.g.GraphQlAutoConfiguration       : GraphQL schema inspection:
        Unmapped fields: {Matricula=[fechaModificacion, convocatoria, alumno, entidadSubvencionadora, calificaciones, facturas], Curso=[fechaModificacion, materia, formato, convocatorias], Usuario=[datosPersonales], Convocatoria=[fechaModificacion, curso, profesor, centro, matriculas], Materia=[descripcion], Formato=[descripcion, activo], Centro=[empresa, comunidad]}
        Unmapped registrations: {}
        Unmapped arguments: {}
        Skipped types: []
2025-12-06T09:21:32.320Z  INFO 1 --- [academymanager] [           main] s.b.a.g.s.GraphQlWebMvcAutoConfiguration : GraphQL endpoint HTTP POST /graphql
2025-12-06T09:21:32.452Z  INFO 1 --- [academymanager] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoints beneath base path '/actuator'
2025-12-06T09:21:32.890Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-12-06T09:21:32.901Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Started AcademymanagerApplication in 7.507 seconds (process running for 8.024)
2025-12-06T09:31:37.975Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-12-06T09:31:37.976Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-12-06T09:31:37.983Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 6 ms


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ k3d image list | grep academia-multi-centro

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get deployment academia-deployment -o jsonpath='{.spec.template.spec.containers[0].image}{"\n"}'
academia-multi-centro:latest

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl rollout restart deployment/academia-deployment
deployment.apps/academia-deployment restarted

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                  READY   STATUS    RESTARTS        AGE
academia-deployment-7cd9fc568-kpgr6   1/1     Running   0               137m
academia-deployment-c68cb8c6f-74dnk   0/1     Running   0               21s
backend-deployment-7cdf89b6fd-2fnjm   1/1     Running   4 (4h26m ago)   10d
backend-deployment-7cdf89b6fd-lgzql   1/1     Running   4 (4h26m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs   1/1     Running   4 (4h26m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb   1/1     Running   3 (4h26m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl delete pod -l app=academia-deployment
No resources found

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl patch deployment academia-deployment -p "{\"spec\":{\"template\":{\"spec\":{\"containers\":[{\"name\":\"academia-container\",\"image\":\"academia-multi-centro:latest-$(date +%s)\"}}}}}"
Error from server (BadRequest): invalid character '}' after array element

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                  READY   STATUS    RESTARTS        AGE
academia-deployment-7cd9fc568-kpgr6   1/1     Running   0               139m
academia-deployment-c68cb8c6f-74dnk   0/1     Running   1 (14s ago)     105s
backend-deployment-7cdf89b6fd-2fnjm   1/1     Running   4 (4h27m ago)   10d
backend-deployment-7cdf89b6fd-lgzql   1/1     Running   4 (4h27m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs   1/1     Running   4 (4h27m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb   1/1     Running   3 (4h27m ago)   10d


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
NAME                                  READY   STATUS    RESTARTS        AGE
academia-deployment-7cd9fc568-kpgr6   1/1     Running   0               140m
academia-deployment-c68cb8c6f-74dnk   0/1     Running   1 (59s ago)     2m30s
backend-deployment-7cdf89b6fd-2fnjm   1/1     Running   4 (4h28m ago)   10d
backend-deployment-7cdf89b6fd-lgzql   1/1     Running   4 (4h28m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs   1/1     Running   4 (4h28m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb   1/1     Running   3 (4h28m ago)   10d

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get deployment academia-deployment --show-labels
NAME                  READY   UP-TO-DATE   AVAILABLE   AGE    LABELS
academia-deployment   1/1     1            1           3h9m   app=academia

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -l app=academymanager --show-labels
No resources found in default namespace.

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -l app=academia --show-labels
NAME                                  READY   STATUS    RESTARTS      AGE     LABELS
academia-deployment-7cd9fc568-kpgr6   1/1     Running   0             142m    app=academia,pod-template-hash=7cd9fc568
academia-deployment-c68cb8c6f-74dnk   0/1     Running   3 (17s ago)   4m48s   app=academia,pod-template-hash=c68cb8c6f

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -l app=academia
NAME                                  READY   STATUS    RESTARTS      AGE
academia-deployment-7cd9fc568-kpgr6   1/1     Running   0             142m
academia-deployment-c68cb8c6f-74dnk   0/1     Running   3 (57s ago)   5m28s

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
NAME                                  READY   STATUS    RESTARTS        AGE
academia-deployment-7cd9fc568-bxc5w   1/1     Running   0               14s
academia-deployment-c68cb8c6f-gb7wc   0/1     Running   0               14s
backend-deployment-7cdf89b6fd-2fnjm   1/1     Running   4 (4h32m ago)   10d
backend-deployment-7cdf89b6fd-lgzql   1/1     Running   4 (4h32m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs   1/1     Running   4 (4h32m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb   1/1     Running   3 (4h32m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl logs -f deployment/academia-deployment
Found 2 pods, using pod/academia-deployment-7cd9fc568-bxc5w
Production profile - skipping .env file

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.7)

2025-12-06T11:45:04.119Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Starting AcademymanagerApplication v0.0.1-SNAPSHOT using Java 21.0.9 with PID 1 (/app/app.jar started by ? in /app)
2025-12-06T11:45:04.125Z DEBUG 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Running with Spring Boot v3.5.7, Spring v6.2.12
2025-12-06T11:45:04.126Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : The following 1 profile is active: "prod"
2025-12-06T11:45:05.774Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2025-12-06T11:45:05.995Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 207 ms. Found 13 JPA repository interfaces.
Production profile detected - skipping .env file loading
2025-12-06T11:45:07.463Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2025-12-06T11:45:07.505Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-12-06T11:45:07.506Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.48]
2025-12-06T11:45:07.570Z  INFO 1 --- [academymanager] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-12-06T11:45:07.576Z  INFO 1 --- [academymanager] [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 3361 ms
2025-12-06T11:45:08.064Z  INFO 1 --- [academymanager] [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2025-12-06T11:45:08.142Z  INFO 1 --- [academymanager] [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.6.33.Final
2025-12-06T11:45:08.174Z  INFO 1 --- [academymanager] [           main] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
2025-12-06T11:45:08.433Z  INFO 1 --- [academymanager] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2025-12-06T11:45:08.469Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2025-12-06T11:45:09.357Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@70e5737f
2025-12-06T11:45:09.359Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2025-12-06T11:45:09.440Z  WARN 1 --- [academymanager] [           main] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2025-12-06T11:45:09.603Z  INFO 1 --- [academymanager] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
        Database JDBC URL [Connecting through datasource 'HikariDataSource (HikariPool-1)']
        Database driver: undefined/unknown
        Database version: 17.6
        Autocommit mode: undefined/unknown
        Isolation level: undefined/unknown
        Minimum pool size: undefined/unknown
        Maximum pool size: undefined/unknown
2025-12-06T11:45:10.883Z  INFO 1 --- [academymanager] [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2025-12-06T11:45:11.222Z  INFO 1 --- [academymanager] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T11:45:11.654Z DEBUG 1 --- [academymanager] [           main] c.a.a.security.JwtAuthenticationFilter   : Filter 'jwtAuthenticationFilter' configured for use
2025-12-06T11:45:11.783Z  INFO 1 --- [academymanager] [           main] eAuthenticationProviderManagerConfigurer : Global AuthenticationManager configured with AuthenticationProvider bean with name authenticationProvider
2025-12-06T11:45:11.784Z  WARN 1 --- [academymanager] [           main] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean. UserDetailsService beans will not be used by Spring Security for automatically configuring username/password login. Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated DaoAuthenticationProvider. If the current configuration is intentional, to turn off this warning, increase the logging level of 'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
2025-12-06T11:45:12.294Z  WARN 1 --- [academymanager] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2025-12-06T11:45:12.977Z  INFO 1 --- [academymanager] [           main] efaultSchemaResourceGraphQlSourceBuilder : Loaded 1 resource(s) in the GraphQL schema.
2025-12-06T11:45:13.192Z  INFO 1 --- [academymanager] [           main] o.s.b.a.g.GraphQlAutoConfiguration       : GraphQL schema inspection:
        Unmapped fields: {Matricula=[fechaModificacion, convocatoria, alumno, entidadSubvencionadora, calificaciones, facturas], Curso=[fechaModificacion, materia, formato, convocatorias], Usuario=[datosPersonales], Convocatoria=[fechaModificacion, curso, profesor, centro, matriculas], Materia=[descripcion], Formato=[descripcion, activo], Centro=[empresa, comunidad]}
        Unmapped registrations: {}
        Unmapped arguments: {}
        Skipped types: []
2025-12-06T11:45:13.217Z  INFO 1 --- [academymanager] [           main] s.b.a.g.s.GraphQlWebMvcAutoConfiguration : GraphQL endpoint HTTP POST /graphql
2025-12-06T11:45:13.380Z  INFO 1 --- [academymanager] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoints beneath base path '/actuator'
2025-12-06T11:45:13.842Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-12-06T11:45:13.854Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Started AcademymanagerApplication in 10.284 seconds (process running for 11.234)


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                  READY   STATUS             RESTARTS        AGE
academia-deployment-7cd9fc568-bxc5w   1/1     Running            0               15m
academia-deployment-c68cb8c6f-gb7wc   0/1     CrashLoopBackOff   7 (48s ago)     15m
backend-deployment-7cdf89b6fd-2fnjm   1/1     Running            4 (4h47m ago)   10d
backend-deployment-7cdf89b6fd-lgzql   1/1     Running            4 (4h47m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs   1/1     Running            4 (4h47m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb   1/1     Running            3 (4h47m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl logs deployment/academia-deployment -f
Found 2 pods, using pod/academia-deployment-7cd9fc568-bxc5w
Production profile - skipping .env file

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.7)

2025-12-06T11:45:04.119Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Starting AcademymanagerApplication v0.0.1-SNAPSHOT using Java 21.0.9 with PID 1 (/app/app.jar started by ? in /app)
2025-12-06T11:45:04.125Z DEBUG 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Running with Spring Boot v3.5.7, Spring v6.2.12
2025-12-06T11:45:04.126Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : The following 1 profile is active: "prod"
2025-12-06T11:45:05.774Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2025-12-06T11:45:05.995Z  INFO 1 --- [academymanager] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 207 ms. Found 13 JPA repository interfaces.
Production profile detected - skipping .env file loading
2025-12-06T11:45:07.463Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2025-12-06T11:45:07.505Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-12-06T11:45:07.506Z  INFO 1 --- [academymanager] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.48]
2025-12-06T11:45:07.570Z  INFO 1 --- [academymanager] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-12-06T11:45:07.576Z  INFO 1 --- [academymanager] [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 3361 ms
2025-12-06T11:45:08.064Z  INFO 1 --- [academymanager] [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2025-12-06T11:45:08.142Z  INFO 1 --- [academymanager] [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.6.33.Final
2025-12-06T11:45:08.174Z  INFO 1 --- [academymanager] [           main] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
2025-12-06T11:45:08.433Z  INFO 1 --- [academymanager] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2025-12-06T11:45:08.469Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2025-12-06T11:45:09.357Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@70e5737f
2025-12-06T11:45:09.359Z  INFO 1 --- [academymanager] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2025-12-06T11:45:09.440Z  WARN 1 --- [academymanager] [           main] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2025-12-06T11:45:09.603Z  INFO 1 --- [academymanager] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
        Database JDBC URL [Connecting through datasource 'HikariDataSource (HikariPool-1)']
        Database driver: undefined/unknown
        Database version: 17.6
        Autocommit mode: undefined/unknown
        Isolation level: undefined/unknown
        Minimum pool size: undefined/unknown
        Maximum pool size: undefined/unknown
2025-12-06T11:45:10.883Z  INFO 1 --- [academymanager] [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2025-12-06T11:45:11.222Z  INFO 1 --- [academymanager] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-12-06T11:45:11.654Z DEBUG 1 --- [academymanager] [           main] c.a.a.security.JwtAuthenticationFilter   : Filter 'jwtAuthenticationFilter' configured for use
2025-12-06T11:45:11.783Z  INFO 1 --- [academymanager] [           main] eAuthenticationProviderManagerConfigurer : Global AuthenticationManager configured with AuthenticationProvider bean with name authenticationProvider
2025-12-06T11:45:11.784Z  WARN 1 --- [academymanager] [           main] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean. UserDetailsService beans will not be used by Spring Security for automatically configuring username/password login. Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually instantiated DaoAuthenticationProvider. If the current configuration is intentional, to turn off this warning, increase the logging level of 'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR
2025-12-06T11:45:12.294Z  WARN 1 --- [academymanager] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2025-12-06T11:45:12.977Z  INFO 1 --- [academymanager] [           main] efaultSchemaResourceGraphQlSourceBuilder : Loaded 1 resource(s) in the GraphQL schema.
2025-12-06T11:45:13.192Z  INFO 1 --- [academymanager] [           main] o.s.b.a.g.GraphQlAutoConfiguration       : GraphQL schema inspection:
        Unmapped fields: {Matricula=[fechaModificacion, convocatoria, alumno, entidadSubvencionadora, calificaciones, facturas], Curso=[fechaModificacion, materia, formato, convocatorias], Usuario=[datosPersonales], Convocatoria=[fechaModificacion, curso, profesor, centro, matriculas], Materia=[descripcion], Formato=[descripcion, activo], Centro=[empresa, comunidad]}
        Unmapped registrations: {}
        Unmapped arguments: {}
        Skipped types: []
2025-12-06T11:45:13.217Z  INFO 1 --- [academymanager] [           main] s.b.a.g.s.GraphQlWebMvcAutoConfiguration : GraphQL endpoint HTTP POST /graphql
2025-12-06T11:45:13.380Z  INFO 1 --- [academymanager] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoints beneath base path '/actuator'
2025-12-06T11:45:13.842Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-12-06T11:45:13.854Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Started AcademymanagerApplication in 10.284 seconds (process running for 11.234)


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ k3d image import academia-multi-centro:latest -c mi-cluster-java
INFO[0000] Importing image(s) into cluster 'mi-cluster-java'
INFO[0000] Starting new tools node...
INFO[0000] Starting node 'k3d-mi-cluster-java-tools'
INFO[0001] Saving 1 image(s) from runtime...
INFO[0006] Importing images into nodes...
INFO[0006] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206130207.tar' into node 'k3d-mi-cluster-java-server-0'...
INFO[0006] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206130207.tar' into node 'k3d-mi-cluster-java-agent-1'...
INFO[0006] Importing images from tarball '/k3d/images/k3d-mi-cluster-java-images-20251206130207.tar' into node 'k3d-mi-cluster-java-agent-0'...
INFO[0008] Removing the tarball(s) from image volume...
INFO[0009] Removing k3d-tools node...
INFO[0009] Successfully imported image(s)
INFO[0009] Successfully imported 1 image(s) into 1 cluster(s)

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl apply -f deployment.yaml
deployment.apps/academia-deployment configured

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl rollout restart deployment/academia-deployment
deployment.apps/academia-deployment restarted

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS    RESTARTS        AGE
academia-deployment-584bb8695d-4zndp   0/1     Running   0               9s
academia-deployment-7cd9fc568-bxc5w    1/1     Running   0               18m
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running   4 (4h50m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running   4 (4h50m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running   4 (4h50m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running   3 (4h50m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl port-forward svc/academia-service 8081:80
Forwarding from 127.0.0.1:8081 -> 8080
Forwarding from [::1]:8081 -> 8080
Handling connection for 8081
Handling connection for 8081
error: lost connection to pod

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
Handling connection for 8081
Handling connection for 8081


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -w
NAME                                   READY   STATUS    RESTARTS      AGE
academia-deployment-5bdc4654f9-fqfkl   1/1     Running   0             7m42s
backend-deployment-7cdf89b6fd-2fnjm    1/1     Running   5 (33m ago)   10d
backend-deployment-7cdf89b6fd-lgzql    1/1     Running   5 (33m ago)   10d
frontend-deployment-9f48f9d4d-pfbfs    1/1     Running   5 (33m ago)   10d
frontend-deployment-9f48f9d4d-wwhmb    1/1     Running   4 (33m ago)   10d


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -l app=academia-app
No resources found in default namespace.

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl get pods -l app=academia
NAME                                   READY   STATUS    RESTARTS   AGE
academia-deployment-5bdc4654f9-fqfkl   1/1     Running   0          11m

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl rollout history deployment/academia-deployment
deployment.apps/academia-deployment
REVISION  CHANGE-CAUSE
1         <none>
2         <none>
3         <none>
4         <none>
5         <none>
6         <none>
7         <none>
8         <none>


diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl rollout status deployment/academia-deployment
deployment "academia-deployment" successfully rolled out

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl describe pod -l app=academia
Name:             academia-deployment-5bdc4654f9-fqfkl
Namespace:        default
Priority:         0
Service Account:  default
Node:             k3d-mi-cluster-java-agent-1/172.24.0.2
Start Time:       Sat, 06 Dec 2025 13:49:48 +0100
Labels:           app=academia
                  pod-template-hash=5bdc4654f9
Annotations:      kubectl.kubernetes.io/restartedAt: 2025-12-06T13:49:48+01:00
Status:           Running
IP:               10.42.1.41
IPs:
  IP:           10.42.1.41
Controlled By:  ReplicaSet/academia-deployment-5bdc4654f9
Containers:
  academia:
    Container ID:   containerd://64e0d1b7f730051179db631f214e2893f22183249b5194bc21c8a13bca034843
    Image:          academia-multi-centro:latest
    Image ID:       sha256:64a555c014366bc04332641493a48b89a8b113c24129dc4c1e3eab508290bafc
    Port:           8080/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Sat, 06 Dec 2025 13:49:49 +0100
    Ready:          True
    Restart Count:  0
    Limits:
      cpu:     2
      memory:  2Gi
    Requests:
      cpu:      500m
      memory:   1Gi
    Liveness:   http-get http://:8080/actuator/health delay=20s timeout=5s period=15s #success=1 #failure=5
    Readiness:  http-get http://:8080/actuator/health delay=10s timeout=5s period=10s #success=1 #failure=3
    Environment:
      SPRING_PROFILES_ACTIVE:                 prod
      SPRING_BOOT_GRACEFUL_SHUTDOWN_ENABLED:  false
      DB_SUPABASE:                            jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres?sslmode=require
      DB_USERNAME:                            postgres.wjbbuiiskercelchtaqg
      DB_PASSWORD:                            Ac4d3m1a_1994!
      JWT_SECRET_KEY:                         404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
      JWT_EXPIRATION_TIME:                    86400000
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-sn2sl (ro)
Conditions:
  Type                        Status
  PodReadyToStartContainers   True
  Initialized                 True
  Ready                       True
  ContainersReady             True
  PodScheduled                True
Volumes:
  kube-api-access-sn2sl:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    Optional:                false
    DownwardAPI:             true
QoS Class:                   Burstable
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type    Reason     Age   From               Message
  ----    ------     ----  ----               -------
  Normal  Scheduled  14m   default-scheduler  Successfully assigned default/academia-deployment-5bdc4654f9-fqfkl to k3d-mi-cluster-java-agent-1
  Normal  Pulled     14m   kubelet            Container image "academia-multi-centro:latest" already present on machine
  Normal  Created    14m   kubelet            Created container academia
  Normal  Started    14m   kubelet            Started container academia

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment
$ kubectl logs -l app=academia
        Unmapped registrations: {}
        Unmapped arguments: {}
        Skipped types: []
2025-12-06T12:49:56.890Z  INFO 1 --- [academymanager] [           main] s.b.a.g.s.GraphQlWebMvcAutoConfiguration : GraphQL endpoint HTTP POST /graphql
2025-12-06T12:49:57.053Z  INFO 1 --- [academymanager] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoints beneath base path '/actuator'
2025-12-06T12:49:57.516Z  INFO 1 --- [academymanager] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-12-06T12:49:57.526Z  INFO 1 --- [academymanager] [           main] c.a.a.AcademymanagerApplication          : Started AcademymanagerApplication in 7.928 seconds (process running for 8.465)
2025-12-06T12:50:08.883Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-12-06T12:50:08.883Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-12-06T12:50:08.884Z  INFO 1 --- [academymanager] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms

diego@DESKTOP-GJ8H97B MINGW64 ~/k3d-deployment


Esto es para agregar el cluster en lens de manera manual por el fallo de que se queda pillado
una solución que ha funcionado 
Windows PowerShell
Copyright (C) Microsoft Corporation. Todos los derechos reservados.

Instale la versión más reciente de PowerShell para obtener nuevas características y mejoras. https://aka.ms/PSWindows

PS C:\Users\diego> kubectl config current-context
k3d-mi-cluster-java
PS C:\Users\diego> k3d cluster list
NAME              SERVERS   AGENTS   LOADBALANCER
mi-cluster-java   1/1       2/2      true
PS C:\Users\diego> k3d kubeconfig get mi-cluster-java > ~/mi-cluster-java-kubeconfig.yaml
PS C:\Users\diego> k3d kubeconfig get mi-cluster-java > ~/.kube/config
PS C:\Users\diego> k3d kubeconfig get mi-cluster-java > C:\Users\diego\mi-cluster-java-kubeconfig.yaml
PS C:\Users\diego> k3d kubeconfig get mi-cluster-java > $HOME\.kube\config
PS C:\Users\diego> k3d kubeconfig get mi-cluster-java
---
apiVersion: v1
clusters:
- cluster:
    certificate-authority-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUJkakNDQVIyZ0F3SUJBZ0lCQURBS0JnZ3Foa2pPUFFRREFqQWpNU0V3SHdZRFZRUUREQmhyTTNNdGMyVnkKZG1WeUxXTmhRREUzTmpRd056Y3dNREV3SGhjTk1qVXhNVEkxTVRNeU16SXhXaGNOTXpVeE1USXpNVE15TXpJeApXakFqTVNFd0h3WURWUVFEREJock0zTXRjMlZ5ZG1WeUxXTmhRREUzTmpRd056Y3dNREV3V1RBVEJnY3Foa2pPClBRSUJCZ2dxaGtqT1BRTUJCd05DQUFSZ1FZT1RpdGpCZktTUGFsOFBuRG9aUnlnbjcwTjdTTlZ1MitzOUZuTTQKa3BjdEd3MDA4emYrNDc2N3d3bEE2NWswckJxejYxVEp5TEtNTXRBUmRGNTdvMEl3UURBT0JnTlZIUThCQWY4RQpCQU1DQXFRd0R3WURWUjBUQVFIL0JBVXdBd0VCL3pBZEJnTlZIUTRFRmdRVXFOZWwraGM5M1FhQ3FoSWdHV1R3ClZ5MjVvaHN3Q2dZSUtvWkl6ajBFQXdJRFJ3QXdSQUlnQnFmbVRHelBsQkJiaWlnaVU3MU1KdGxQR2pjd3lneFgKTG93cXJxUWZhMmNDSUhteDdRMU9oSlIzeWxmMWxnb3pwZjFyRHJhY0NwZm9FUDlQVTY5aTIrc3UKLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=
    server: https://host.docker.internal:55524
  name: k3d-mi-cluster-java
contexts:
- context:
    cluster: k3d-mi-cluster-java
    user: admin@k3d-mi-cluster-java
  name: k3d-mi-cluster-java
current-context: k3d-mi-cluster-java
kind: Config
preferences: {}
users:
- name: admin@k3d-mi-cluster-java
  user:
    client-certificate-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUJrRENDQVRlZ0F3SUJBZ0lJRWE1TjBQMm13Mzh3Q2dZSUtvWkl6ajBFQXdJd0l6RWhNQjhHQTFVRUF3d1kKYXpOekxXTnNhV1Z1ZEMxallVQXhOelkwTURjM01EQXhNQjRYRFRJMU1URXlOVEV6TWpNeU1Wb1hEVEkyTVRFeQpOVEV6TWpNeU1Wb3dNREVYTUJVR0ExVUVDaE1PYzNsemRHVnRPbTFoYzNSbGNuTXhGVEFUQmdOVkJBTVRESE41CmMzUmxiVHBoWkcxcGJqQlpNQk1HQnlxR1NNNDlBZ0VHQ0NxR1NNNDlBd0VIQTBJQUJJamo0RGVDWWNORk45YWwKNjRVMzN4dERFcmZPOTIrSVFORGlTVW9GZjd4ZjNMbUdqbExyN2Y3MkNYcUU2SitOa1VNbjgzd1Nib3QyTnM3dQpDazhQU0Z1alNEQkdNQTRHQTFVZER3RUIvd1FFQXdJRm9EQVRCZ05WSFNVRUREQUtCZ2dyQmdFRkJRY0RBakFmCkJnTlZIU01FR0RBV2dCU3o5bjRzTWR3L3diTGo0NmxmaWlUVTI0MVZ6ekFLQmdncWhrak9QUVFEQWdOSEFEQkUKQWlBQTU4ZFJ6TTkyeTNjY1hyM2Z1a2xXTUVCNy9MNVA0TUNKOWtOZkdrUjVoZ0lnVE4zWTBXRWxVTjZTKzhCMwpyaUNsWFRPRkhRaS9nMUFjemxEUTBLU3VXV2s9Ci0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0KLS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUJkekNDQVIyZ0F3SUJBZ0lCQURBS0JnZ3Foa2pPUFFRREFqQWpNU0V3SHdZRFZRUUREQmhyTTNNdFkyeHAKWlc1MExXTmhRREUzTmpRd056Y3dNREV3SGhjTk1qVXhNVEkxTVRNeU16SXhXaGNOTXpVeE1USXpNVE15TXpJeApXakFqTVNFd0h3WURWUVFEREJock0zTXRZMnhwWlc1MExXTmhRREUzTmpRd056Y3dNREV3V1RBVEJnY3Foa2pPClBRSUJCZ2dxaGtqT1BRTUJCd05DQUFSZm0wRGlxdjk2R3BDUWoyYW13Sm5ZSTl2eEl0a1hUK2p1QkpNemp4aDkKbkg3azYwaHJESW5BQ2w4aTV5d1N6K2pYb1hHSlV3OHlieUVRcW1ZbG9sUGFvMEl3UURBT0JnTlZIUThCQWY4RQpCQU1DQXFRd0R3WURWUjBUQVFIL0JBVXdBd0VCL3pBZEJnTlZIUTRFRmdRVXMvWitMREhjUDhHeTQrT3BYNG9rCjFOdU5WYzh3Q2dZSUtvWkl6ajBFQXdJRFNBQXdSUUloQUxLL1ZqVkNMTHl0dllEbEo1cmY3SmdoN3FacGhJWC8KT1lkWEZEV2lVeDlKQWlCbHZPOXNSZGlkdVFlQmpndWpxRUoyRnFKa0IzdWwzTlV5MlVlTHNrS3VaQT09Ci0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K
    client-key-data: LS0tLS1CRUdJTiBFQyBQUklWQVRFIEtFWS0tLS0tCk1IY0NBUUVFSVBRZWtwVWJpeFo5bmUrbnBEOFFGRjlCTDh0SjV1ak1vSkVXbGw1YnJ0empvQW9HQ0NxR1NNNDkKQXdFSG9VUURRZ0FFaU9QZ040Smh3MFUzMXFYcmhUZmZHME1TdDg3M2I0aEEwT0pKU2dWL3ZGL2N1WWFPVXV2dAovdllKZW9Ub240MlJReWZ6ZkJKdWkzWTJ6dTRLVHc5SVd3PT0KLS0tLS1FTkQgRUMgUFJJVkFURSBLRVktLS0tLQo=
PS C:\Users\diego>

