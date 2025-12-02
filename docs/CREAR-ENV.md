# 🚀 Crear archivo .env para Supabase

## Paso 1: Crear archivo `.env`

Crea un archivo llamado `.env` en la **raíz del proyecto** (mismo nivel que `pom.xml`).

## Paso 2: Copiar esta configuración

```env
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=Ac4d3m1a_1994!
JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_TIME=86400000
```

## Paso 3: Verificar formato

- ✅ Sin espacios antes/después del `=`
- ✅ Sin comillas alrededor de los valores
- ✅ Una variable por línea
- ✅ Archivo guardado como `.env` (no `.env.txt`)

## Paso 4: Ejecutar aplicación

```powershell
mvn spring-boot:run
```

## ✅ Verificación

Si todo está bien, verás en los logs:

```
✓ .env file loaded successfully
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Tomcat started on port(s): 8080
```

## ⚠️ Si hay problemas IPv4

Si ves errores de conexión, usa Session Pooler (puerto 6543):

```env
DB_SUPABASE=jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=Ac4d3m1a_1994!
```

---

**¡Listo!** La aplicación debería conectarse a Supabase correctamente. 🎉

