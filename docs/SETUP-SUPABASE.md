# 🚀 Setup Rápido: Configurar Supabase

## Paso 1: Crear archivo `.env`

Crea un archivo llamado `.env` en la raíz del proyecto (mismo nivel que `pom.xml`).

## Paso 2: Copiar esta configuración

```env
# Database Configuration (Supabase)
# URL de Supabase convertida a formato JDBC para Spring Boot
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=tu_password_aqui

# JWT Configuration
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_TIME=86400000
```

## Paso 3: Reemplazar valores

1. **DB_PASSWORD:** Reemplaza `tu_password_aqui` con tu password real de Supabase
2. **JWT_SECRET_KEY:** Puedes generar uno nuevo con:
   ```powershell
   openssl rand -base64 32
   ```

## ⚠️ Si tienes problemas de conexión (IPv4)

Si ves errores de conexión, prueba con **Session Pooler**:

```env
# Usar Pooler (puerto 6543 en vez de 5432)
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=tu_password_aqui
```

## ✅ Verificar

```powershell
# Ejecutar aplicación
.\start-dev.ps1

# O manualmente
mvn spring-boot:run
```

Si todo está bien, verás:
```
✓ .env file loaded successfully (development mode)
Tomcat started on port(s): 8080
```

## 📝 Notas

- ✅ El archivo `.env` está en `.gitignore` (no se sube a Git)
- ✅ Solo se usa en desarrollo
- ✅ Production usa variables de entorno del sistema

---

**¡Listo!** Tu aplicación debería conectarse a Supabase correctamente. 🎉

