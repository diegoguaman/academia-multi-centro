# 🚀 Quick Start: Secrets Management

## Setup en 2 Minutos

### Paso 1: Crear archivo .env

```bash
# Copiar template
cp .env.example .env

# Editar con tus valores
# (Usa tu editor favorito: nano, vscode, etc.)
```

### Paso 2: Completar valores en .env

```env
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=tu_password_real_aqui
JWT_SECRET_KEY=tu_jwt_secret_aqui
```

### Paso 3: Ejecutar aplicación

```powershell
# Windows PowerShell
.\start-dev.ps1

# O manualmente
mvn spring-boot:run
```

**¡Listo!** La aplicación carga automáticamente las variables desde `.env`

---

## ✅ Verificación

Si todo funciona, verás:

```
✓ .env file loaded successfully (development mode)
Tomcat started on port(s): 8080
```

---

## 🔒 Seguridad

- ✅ `.env` está en `.gitignore` (no se sube a Git)
- ✅ Solo se usa en desarrollo
- ✅ Production usa variables de entorno del sistema

---

## 📚 Documentación Completa

Ver: `docs/secrets-management-enterprise.md`

