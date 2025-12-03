# ✅ Configuración Correcta del Archivo .env

## 🔍 Problema Identificado

**Warning en Supabase Dashboard:** "Not IPv4 compatible"

**Causa:** 
- Supabase usa IPv6 para conexiones directas (puerto 5432)
- Tu red es IPv4-only
- Por eso falla la resolución DNS: `UnknownHostException`

**Solución:** Usar **Session Pooler** (puerto 6543) que es compatible con IPv4.

---

## 📋 Variables Correctas para el Archivo .env

### ⚠️ IMPORTANTE: Formato de la URL

La imagen muestra que Supabase usa este formato:
```
jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres?user=postgres&password=[YOUR_PASSWORD]
```

**PERO** para Spring Boot debes **SEPARAR** user y password:

```env
# ❌ INCORRECTO (no poner user y password en la URL)
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres?user=postgres&password=xxx

# ✅ CORRECTO (separar en variables)
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=tu_password_aqui
```

---

## 🎯 Configuración Completa del .env

**Archivo:** `.env` (en la raíz del proyecto)

### Opción 1: Session Pooler (Recomendado - Soluciona IPv4)

```env
# Database Configuration (Supabase - Session Pooler)
# Puerto 6543 es compatible con IPv4 (gratis)
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=tu_password_real_aqui

# JWT Configuration
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_TIME=86400000
```

**Cambios importantes:**
- ✅ Puerto: `5432` → `6543` (Session Pooler)
- ✅ Agregado: `?sslmode=require` (requiere SSL)
- ✅ User y password separados (NO en la URL)

### Opción 2: Conexión Directa (Solo si tu red soporta IPv6)

```env
# Database Configuration (Supabase - Direct Connection)
# Solo funciona si tu red soporta IPv6
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=tu_password_real_aqui

# JWT Configuration
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_TIME=86400000
```

**Nota:** Esta opción probablemente NO funcionará por el warning IPv4.

---

## 🔧 Cómo Obtener la Configuración del Session Pooler

### Paso 1: En Supabase Dashboard

1. Ve a tu proyecto en Supabase Dashboard
2. Ve a **Settings** → **Database**
3. Busca la sección **Connection Pooling**
4. Activa el Session Pooler si no está activado
5. Copia la **Connection String** del pooler

### Paso 2: Convertir a Formato Spring Boot

Supabase muestra algo como:
```
postgresql://postgres:[YOUR_PASSWORD]@db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres
```

**Convierte a JDBC:**
- Cambia `postgresql://` por `jdbc:postgresql://`
- Quita `:postgres` de la URL (lo pones en `DB_USERNAME`)
- Quita `[YOUR_PASSWORD]` de la URL (lo pones en `DB_PASSWORD`)
- Agrega `?sslmode=require` al final

**Resultado:**
```env
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=tu_password
```

---

## 📝 Variables Explicadas

### DB_SUPABASE

**Formato correcto:**
```
jdbc:postgresql://HOST:PUERTO/DATABASE?sslmode=require
```

**Componentes:**
- `jdbc:postgresql://` - Prefijo JDBC (requerido para Spring Boot)
- `db.wjbbuiiskercelchtaqg.supabase.co` - Hostname de Supabase
- `6543` - Puerto del Session Pooler (o `5432` para directo)
- `postgres` - Nombre de la base de datos
- `?sslmode=require` - Requiere conexión SSL (recomendado)

**Ejemplos:**
```env
# Session Pooler (IPv4 compatible)
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require

# Direct Connection (IPv6 required)
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres?sslmode=require
```

### DB_USERNAME

**Valor:** Siempre `postgres` para Supabase

```env
DB_USERNAME=postgres
```

### DB_PASSWORD

**Valor:** Tu password real de Supabase (sin corchetes)

```env
DB_PASSWORD=tu_password_real_sin_corchetes
```

**⚠️ Importante:**
- NO pongas espacios alrededor del `=`
- NO uses comillas a menos que el password las tenga
- Si tu password tiene caracteres especiales, no necesitas escaparlos

### JWT_SECRET_KEY

**Valor:** Clave secreta para firmar tokens JWT

```env
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```

**Para generar uno nuevo:**
```powershell
# Usando OpenSSL (si lo tienes)
openssl rand -hex 32

# O usar un generador online
```

### JWT_EXPIRATION_TIME

**Valor:** Tiempo de expiración en milisegundos

```env
JWT_EXPIRATION_TIME=86400000  # 24 horas en milisegundos
```

**Conversiones comunes:**
- 1 hora = `3600000`
- 24 horas = `86400000`
- 7 días = `604800000`

---

## ✅ Archivo .env Completo (Copia y Pega)

```env
# ============================================
# Database Configuration (Supabase)
# ============================================
# Usar Session Pooler (puerto 6543) para compatibilidad IPv4
# Si tu red soporta IPv6, puedes usar puerto 5432
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=TU_PASSWORD_REAL_AQUI

# ============================================
# JWT Configuration
# ============================================
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_TIME=86400000
```

---

## 🔍 Verificación de la Configuración

### 1. Verificar que el archivo existe

```powershell
# En la raíz del proyecto
Get-Content .env
```

### 2. Verificar formato

**✅ Correcto:**
```env
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=mipassword123
```

**❌ Incorrecto:**
```env
# ❌ User y password en la URL
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres?user=postgres&password=xxx

# ❌ Espacios alrededor del =
DB_SUPABASE = jdbc:postgresql://...

# ❌ Comillas innecesarias (solo si el password tiene espacios)
DB_PASSWORD="mipassword"  # Solo si el password tiene espacios

# ❌ Puerto incorrecto (5432 en vez de 6543 para IPv4)
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
```

### 3. Verificar que se carga al iniciar

Al iniciar la aplicación, deberías ver:
```
✓ .env file loaded successfully (development mode)
```

---

## 🎯 Configuración Recomendada (Basada en la Imagen)

Según la imagen que compartiste, la configuración correcta es:

```env
# Database Configuration
# IMPORTANTE: Usar puerto 6543 (Session Pooler) para IPv4 compatibility
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=TU_PASSWORD_DE_SUPABASE

# JWT Configuration
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_TIME=86400000
```

**Cambios clave vs conexión directa:**
- ✅ Puerto `6543` en vez de `5432`
- ✅ Agregado `?sslmode=require`
- ✅ User y password separados (no en la URL)

---

## 📊 Comparación: Direct vs Pooler

| Aspecto | Direct (5432) | Session Pooler (6543) |
|---------|--------------|----------------------|
| **IPv4 Compatible** | ❌ No | ✅ Sí |
| **IPv6 Required** | ✅ Sí | ❌ No |
| **Gratis** | ✅ Sí | ✅ Sí |
| **SSL Required** | Opcional | Recomendado |
| **Performance** | Mejor | Bueno |
| **Concurrent Connections** | Limitado | Mayor capacidad |

**Recomendación:** Usa Session Pooler (6543) si tienes problemas IPv4.

---

## ✅ Checklist Final

Antes de iniciar la aplicación, verifica:

- [ ] Archivo `.env` existe en la raíz del proyecto
- [ ] `DB_SUPABASE` usa puerto `6543` (Session Pooler)
- [ ] `DB_SUPABASE` incluye `?sslmode=require`
- [ ] `DB_USERNAME=postgres` (separado, no en la URL)
- [ ] `DB_PASSWORD` tiene tu password real (sin corchetes)
- [ ] No hay espacios alrededor del `=`
- [ ] Session Pooler está habilitado en Supabase Dashboard

---

## 🚀 Próximos Pasos

1. **Crea/Actualiza el archivo `.env`** con la configuración de arriba
2. **Reemplaza** `TU_PASSWORD_DE_SUPABASE` con tu password real
3. **Habilita Session Pooler** en Supabase Dashboard (Settings → Database → Connection Pooling)
4. **Reinicia** la aplicación
5. **Verifica** que veas: `✓ .env file loaded successfully`

---

**Última actualización:** Día 4 - Configuración corregida para IPv4
**Estado:** ✅ Listo para usar Session Pooler

