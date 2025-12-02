# 🔗 Guía Completa: Conexión a Supabase

## Conversión de Formatos

### Formato URI (Supabase Dashboard)
```
postgresql://postgres:[YOUR_PASSWORD]@db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
```

### Formato JDBC (Spring Boot)
```
jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
```

**Diferencia:**
- URI: `postgresql://`
- JDBC: `jdbc:postgresql://`

## Configuración Completa

### 1. Archivo `.env` (Recomendado)

```env
# Database Configuration
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=tu_password_real

# JWT Configuration
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_TIME=86400000
```

### 2. application-dev.properties (Alternativa)

```properties
spring.datasource.url=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=tu_password_real
```

## ⚠️ Problema IPv4

Supabase muestra: **"Not IPv4 compatible"**

### ¿Qué significa?

- Supabase usa IPv6 por defecto
- Algunas redes/ISPs solo soportan IPv4
- Puede causar: `Connection timeout` o `Connection refused`

### Solución: Session Pooler (Gratis)

**URL con Pooler:**
```
jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
```

**Cambios:**
- Puerto: `5432` → `6543`
- Agregar: `?sslmode=require`

**Configuración:**
```env
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
```

### Habilitar Pooler en Supabase

1. Ve a Supabase Dashboard
2. Settings → Database
3. Connection Pooling → Enable
4. Copia la URL del pooler

## 🔒 Configuración SSL

### Opciones de SSL

```properties
# Sin SSL (NO recomendado para producción)
spring.datasource.url=jdbc:postgresql://...?sslmode=disable

# Requiere SSL (Recomendado)
spring.datasource.url=jdbc:postgresql://...?sslmode=require

# SSL + Verificación de certificado (Más seguro)
spring.datasource.url=jdbc:postgresql://...?sslmode=verify-full
```

### Spring Boot con SSL

Spring Boot maneja SSL automáticamente, pero puedes forzarlo:

```properties
spring.datasource.url=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres?sslmode=require
```

## 🧪 Test de Conexión

### Opción 1: Desde la Aplicación

```powershell
mvn spring-boot:run
```

**Buscar en logs:**
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

Si ves esto, la conexión es exitosa.

### Opción 2: Con psql (si está instalado)

```powershell
psql "postgresql://postgres:tu_password@db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres"
```

### Opción 3: Test de Conectividad

```powershell
# Verificar si el puerto está abierto
Test-NetConnection -ComputerName db.wjbbuiiskercelchtaqg.supabase.co -Port 5432

# O con telnet
telnet db.wjbbuiiskercelchtaqg.supabase.co 5432
```

## 🔧 Troubleshooting

### Error: "Connection timeout"

**Causas:**
1. Firewall bloqueando puerto 5432
2. Problema IPv4/IPv6
3. Red corporativa con restricciones

**Soluciones:**
1. Usar Session Pooler (puerto 6543)
2. Verificar firewall
3. Probar desde otra red

### Error: "Connection refused"

**Causas:**
1. URL incorrecta
2. Puerto incorrecto
3. Supabase project pausado

**Soluciones:**
1. Verificar URL en Supabase Dashboard
2. Verificar que el proyecto esté activo
3. Probar con Pooler

### Error: "Authentication failed"

**Causas:**
1. Password incorrecto
2. Username incorrecto

**Soluciones:**
1. Resetear password en Supabase Dashboard
2. Verificar username (debe ser `postgres`)

### Error: "SSL required"

**Solución:**
Agregar `?sslmode=require` a la URL:
```
jdbc:postgresql://...?sslmode=require
```

## 📊 Comparación: Direct vs Pooler

| Característica | Direct (5432) | Pooler (6543) |
|----------------|---------------|---------------|
| IPv4 compatible | ❌ | ✅ |
| Conexiones persistentes | ✅ | ✅ |
| Connection pooling | Manual | Automático |
| SSL | Opcional | Requerido |
| Uso recomendado | Apps con conexiones largas | Apps web, APIs |

**Recomendación:** Usar Pooler para aplicaciones web/APIs.

## ✅ Checklist de Configuración

- [ ] Archivo `.env` creado en raíz del proyecto
- [ ] URL en formato JDBC: `jdbc:postgresql://...`
- [ ] Password correcto configurado
- [ ] Si hay problemas IPv4: usar Pooler (puerto 6543)
- [ ] SSL configurado: `?sslmode=require`
- [ ] Aplicación inicia sin errores de conexión

## 🎯 Configuración Final Recomendada

```env
# Para desarrollo local (con Pooler para evitar problemas IPv4)
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=tu_password_real
```

**O si tu red soporta IPv6:**

```env
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=tu_password_real
```

---

**¡Configuración completa!** 🚀

