# 🚀 Solución Rápida: Problemas de Inicio

## ✅ SOLUCIÓN IMPLEMENTADA

He creado los archivos necesarios para resolver el problema. Sigue estos pasos:

---

## 📝 PASO 1: Configurar Password de Supabase

1. **Abre el archivo:** `src/main/resources/application-dev.properties`
2. **Reemplaza** `YOUR_PASSWORD_HERE` con tu password real de Supabase:

```properties
spring.datasource.password=TU_PASSWORD_REAL_AQUI
```

**⚠️ IMPORTANTE:** Este archivo ya está en `.gitignore`, no se subirá a GitHub.

---

## 🚀 PASO 2: Iniciar la Aplicación

### Opción A: Script Automático (Recomendado)

```powershell
.\start-dev.ps1
```

El script:
- ✅ Verifica que el archivo de configuración existe
- ✅ Te avisa si falta configurar el password
- ✅ Inicia la aplicación con el perfil `dev`

### Opción B: Comando Manual

```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## ✅ VERIFICACIÓN

Si todo está bien, deberías ver:

```
Tomcat started on port(s): 8080 (http)
Started AcademymanagerApplication in X.XXX seconds
```

**Probar que funciona:**
```powershell
# En otra terminal
curl http://localhost:8080/actuator/health
```

---

## 🔍 SI AÚN HAY ERRORES

### Error: "Connection refused" o "Connection timeout"

**Causa:** Password incorrecto o URL de Supabase incorrecta

**Solución:**
1. Verifica tu password en Supabase Dashboard
2. Verifica la URL en `application-dev.properties`
3. Asegúrate de que la URL no tenga `?sslmode=require` al final

### Error: "Class not found" en IDE

**Solución:**
1. En VS Code/Cursor: `Ctrl+Shift+P` → `Java: Clean Java Language Server Workspace`
2. Reiniciar el IDE
3. O simplemente ignorar (la app funciona con `mvn`)

### Error: "Many errors in target/"

**Solución:**
- ✅ **IGNORAR** - Es normal
- Los archivos en `target/` son generados automáticamente
- El IDE puede mostrar errores, pero no afectan la ejecución

---

## 📊 RESUMEN DE CAMBIOS

### Archivos Creados:
- ✅ `src/main/resources/application-dev.properties` - Configuración de desarrollo
- ✅ `start-dev.ps1` - Script de inicio automático
- ✅ `.vscode/settings.json` - Configuración del IDE
- ✅ `docs/diagnostico-problemas-inicio.md` - Diagnóstico completo

### Archivos Modificados:
- ✅ `src/main/resources/application.properties` - Ahora con valores por defecto
- ✅ `.gitignore` - Excluye archivos sensibles

---

## 🎯 PRÓXIMOS PASOS

1. ✅ Configurar password en `application-dev.properties`
2. ✅ Ejecutar `.\start-dev.ps1`
3. ✅ Verificar que la app inicia correctamente
4. ✅ Probar endpoints de autenticación

---

## 💡 ALTERNATIVAS

### Si prefieres usar variables de entorno:

```powershell
$env:DB_SUPABASE = "jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:5432/postgres"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "tu_password"
mvn spring-boot:run
```

### Si prefieres usar archivo .env:

Ver documentación completa en: `docs/diagnostico-problemas-inicio.md`

---

**¿Todo funcionando?** 🎉 Continúa con el testing de autenticación JWT!


