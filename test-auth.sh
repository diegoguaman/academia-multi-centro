#!/bin/bash

# Script para probar autenticación JWT
# Uso: ./test-auth.sh

set -e

BASE_URL="http://localhost:8080"
BOLD='\033[1m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BOLD}=== Test de Autenticación JWT ===${NC}\n"

# Verificar que jq está instalado
if ! command -v jq &> /dev/null; then
    echo -e "${RED}Error: jq no está instalado${NC}"
    echo "Instala con: sudo apt install jq  (Ubuntu/Debian)"
    echo "O con: brew install jq  (macOS)"
    exit 1
fi

# Verificar que el servidor está corriendo
echo -e "${YELLOW}1. Verificando que el servidor está corriendo...${NC}"
if ! curl -s -f "$BASE_URL/actuator/health" > /dev/null; then
    echo -e "${RED}Error: Servidor no está corriendo en $BASE_URL${NC}"
    echo "Inicia el servidor con: mvn spring-boot:run"
    exit 1
fi
echo -e "${GREEN}✓ Servidor corriendo${NC}\n"

# Test 1: Registrar usuario
echo -e "${YELLOW}2. Registrando nuevo usuario...${NC}"
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test-'$(date +%s)'@test.com",
    "password": "Test123!",
    "rol": "ADMIN",
    "nombre": "Test",
    "apellidos": "Usuario"
  }')

if echo "$REGISTER_RESPONSE" | jq -e '.token' > /dev/null; then
    REGISTER_TOKEN=$(echo "$REGISTER_RESPONSE" | jq -r '.token')
    REGISTER_EMAIL=$(echo "$REGISTER_RESPONSE" | jq -r '.email')
    echo -e "${GREEN}✓ Usuario registrado: $REGISTER_EMAIL${NC}"
    echo -e "Token (primeros 50 chars): ${REGISTER_TOKEN:0:50}...\n"
else
    echo -e "${RED}✗ Error en registro:${NC}"
    echo "$REGISTER_RESPONSE" | jq
    exit 1
fi

# Test 2: Login
echo -e "${YELLOW}3. Haciendo login con usuario registrado...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$REGISTER_EMAIL\",
    \"password\": \"Test123!\"
  }")

if echo "$LOGIN_RESPONSE" | jq -e '.token' > /dev/null; then
    LOGIN_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token')
    echo -e "${GREEN}✓ Login exitoso${NC}"
    echo -e "Token type: $(echo "$LOGIN_RESPONSE" | jq -r '.tokenType')"
    echo -e "Expires in: $(echo "$LOGIN_RESPONSE" | jq -r '.expiresIn') ms\n"
else
    echo -e "${RED}✗ Error en login:${NC}"
    echo "$LOGIN_RESPONSE" | jq
    exit 1
fi

# Test 3: Acceder a endpoint protegido CON token
echo -e "${YELLOW}4. Accediendo a endpoint protegido CON token...${NC}"
PROTECTED_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X GET "$BASE_URL/api/cursos" \
  -H "Authorization: Bearer $LOGIN_TOKEN")

HTTP_STATUS=$(echo "$PROTECTED_RESPONSE" | grep "HTTP_STATUS" | cut -d: -f2)
RESPONSE_BODY=$(echo "$PROTECTED_RESPONSE" | sed '/HTTP_STATUS/d')

if [ "$HTTP_STATUS" = "200" ]; then
    echo -e "${GREEN}✓ Acceso autorizado (200 OK)${NC}\n"
else
    echo -e "${RED}✗ Error: Status $HTTP_STATUS (esperado: 200)${NC}"
    echo "$RESPONSE_BODY"
    exit 1
fi

# Test 4: Acceder a endpoint protegido SIN token
echo -e "${YELLOW}5. Accediendo a endpoint protegido SIN token (debe fallar)...${NC}"
NO_TOKEN_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X GET "$BASE_URL/api/cursos")

NO_TOKEN_STATUS=$(echo "$NO_TOKEN_RESPONSE" | grep "HTTP_STATUS" | cut -d: -f2)

if [ "$NO_TOKEN_STATUS" = "401" ]; then
    echo -e "${GREEN}✓ Acceso denegado correctamente (401 Unauthorized)${NC}\n"
else
    echo -e "${RED}✗ Error: Status $NO_TOKEN_STATUS (esperado: 401)${NC}"
    exit 1
fi

# Test 5: Acceder con token inválido
echo -e "${YELLOW}6. Accediendo con token inválido (debe fallar)...${NC}"
INVALID_TOKEN_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X GET "$BASE_URL/api/cursos" \
  -H "Authorization: Bearer invalid_token_12345")

INVALID_TOKEN_STATUS=$(echo "$INVALID_TOKEN_RESPONSE" | grep "HTTP_STATUS" | cut -d: -f2)

if [ "$INVALID_TOKEN_STATUS" = "401" ]; then
    echo -e "${GREEN}✓ Token inválido rechazado correctamente (401 Unauthorized)${NC}\n"
else
    echo -e "${RED}✗ Error: Status $INVALID_TOKEN_STATUS (esperado: 401)${NC}"
    exit 1
fi

# Test 6: Login con credenciales incorrectas
echo -e "${YELLOW}7. Intentando login con password incorrecta (debe fallar)...${NC}"
BAD_LOGIN_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$REGISTER_EMAIL\",
    \"password\": \"WrongPassword123!\"
  }")

BAD_LOGIN_STATUS=$(echo "$BAD_LOGIN_RESPONSE" | grep "HTTP_STATUS" | cut -d: -f2)

if [ "$BAD_LOGIN_STATUS" = "401" ]; then
    echo -e "${GREEN}✓ Credenciales incorrectas rechazadas (401 Unauthorized)${NC}\n"
else
    echo -e "${RED}✗ Error: Status $BAD_LOGIN_STATUS (esperado: 401)${NC}"
    exit 1
fi

# Resumen
echo -e "${BOLD}${GREEN}=== Todos los tests pasaron ✓ ===${NC}"
echo -e "\n${BOLD}Resumen:${NC}"
echo -e "✓ Servidor corriendo"
echo -e "✓ Registro de usuario funciona"
echo -e "✓ Login funciona y retorna JWT"
echo -e "✓ Token válido permite acceso"
echo -e "✓ Sin token → 401 Unauthorized"
echo -e "✓ Token inválido → 401 Unauthorized"
echo -e "✓ Credenciales incorrectas → 401 Unauthorized"

echo -e "\n${BOLD}Credenciales de prueba:${NC}"
echo -e "Email: ${GREEN}$REGISTER_EMAIL${NC}"
echo -e "Password: ${GREEN}Test123!${NC}"
echo -e "Token: ${GREEN}${LOGIN_TOKEN:0:50}...${NC}"

echo -e "\n${BOLD}Ejemplo de uso:${NC}"
echo -e "curl -X GET $BASE_URL/api/cursos \\"
echo -e "  -H \"Authorization: Bearer $LOGIN_TOKEN\""

