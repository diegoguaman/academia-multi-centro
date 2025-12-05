# Script para ejecutar la aplicación en Docker
# Uso: .\docker-run.ps1

# Detener y eliminar contenedor existente si existe
docker stop academia-app 2>$null
docker rm academia-app 2>$null

# Ejecutar contenedor con variables de entorno
docker run -d `
  --name academia-app `
  -p 8080:8080 `
  -e DB_SUPABASE="jdbc:postgresql://db.wjbbuiiskercelchtaqg.supabase.co:6543/postgres?sslmode=require" `
  -e DB_USERNAME="postgres" `
  -e DB_PASSWORD="Ac4d3m1a_1994!" `
  -e JWT_SECRET_KEY="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970" `
  -e JWT_EXPIRATION_TIME="86400000" `
  -e SPRING_PROFILES_ACTIVE="prod" `
  academia-multi-centro:latest

Write-Host "`n✅ Contenedor iniciado. Verificando logs..." -ForegroundColor Green
Start-Sleep -Seconds 3

# Mostrar logs
docker logs academia-app

Write-Host "`n📊 Para ver logs en tiempo real: docker logs -f academia-app" -ForegroundColor Cyan
Write-Host "🏥 Para verificar health: curl http://localhost:8080/actuator/health" -ForegroundColor Cyan
Write-Host "🛑 Para detener: docker stop academia-app" -ForegroundColor Yellow
