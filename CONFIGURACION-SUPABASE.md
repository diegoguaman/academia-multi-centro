# 🔗 Configuración de Supabase

## URL de Conexión

**Formato URI (Supabase Dashboard):**
```
postgresql://postgres:[YOUR_PASSWORD]@db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
```

**Formato JDBC (Spring Boot):**
```
jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
```

## ✅ Configuración Correcta

### Opción 1: Usar archivo `.env` (Recomendado)

Crear archivo `.env` en la raíz del proyecto:

```env
# Database Configuration (Supabase)
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=tu_password_aqui

# JWT Configuration
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_TIME=86400000
```

### Opción 2: Usar `application-dev.properties`

Ya está configurado en: `src/main/resources/application-dev.properties`

```properties
spring.datasource.url=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=tu_password_aqui
```

## ⚠️ Advertencia IPv4

Supabase muestra: **"Not IPv4 compatible"**

### ¿Qué significa?

- Supabase usa IPv6 por defecto
- Algunas redes/ISPs solo soportan IPv4
- Esto puede causar problemas de conexión

### Soluciones:

#### Opción A: Usar Session Pooler (Recomendado - Gratis)

**URL con Pooler:**
```
jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
```

**Nota:** Puerto cambia de `5432` a `6543` (pooler)

**Configuración en `.env`:**
```env
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
```

#### Opción B: IPv4 Add-on (Pago)

Si necesitas IPv4 directo, puedes comprar el add-on en Supabase Dashboard.

#### Opción C: Verificar si tu red soporta IPv6

```powershell
# Verificar conectividad IPv6
Test-NetConnection -ComputerName db.wjbbuiiskercelchtaqg.supabase.co -Port 5432
```

## 🔧 Configuración SSL

Spring Boot maneja SSL automáticamente, pero puedes forzarlo:

```properties
spring.datasource.url=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres?sslmode=require
```

**Opciones de sslmode:**
- `disable` - Sin SSL (no recomendado)
- `require` - Requiere SSL (recomendado)
- `verify-full` - SSL + verificación de certificado (más seguro)

## ✅ Verificación

### Test de Conexión

```powershell
# Con PowerShell (si tienes psql instalado)
psql "postgresql://postgres:tu_password@db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres"

# O con Maven
mvn spring-boot:run
```

### Si hay problemas de conexión:

1. **Verificar password:** Asegúrate de usar el password correcto
2. **Verificar firewall:** Algunos firewalls bloquean puerto 5432
3. **Probar con Pooler:** Usa puerto 6543 en vez de 5432
4. **Verificar SSL:** Agrega `?sslmode=require` a la URL

## 📝 Resumen

**URL para Spring Boot:**
```
jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
```

**Con Pooler (si hay problemas IPv4):**
```
jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
```

**Configurar en:**
- `.env` file (recomendado)
- O `application-dev.properties`

¡Listo para conectar! 🚀

