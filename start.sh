#!/bin/bash

# Script de arranque para Java 21 Features Testing
# Este script configura Java 21 y ejecuta la aplicación Spring Boot

set -e  # Salir si hay algún error

echo "=========================================="
echo "Java 21 Features Testing - Script de Arranque"
echo "=========================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Cargar SDKMAN
if [ -f "$HOME/.sdkman/bin/sdkman-init.sh" ]; then
    echo -e "${GREEN}✓${NC} Cargando SDKMAN..."
    source "$HOME/.sdkman/bin/sdkman-init.sh"
else
    echo -e "${RED}✗${NC} SDKMAN no está instalado."
    echo "Instálalo con: curl -s 'https://get.sdkman.io' | bash"
    exit 1
fi

# Configurar Java 21
echo -e "${GREEN}✓${NC} Configurando Java 21..."
sdk env

# Verificar versión de Java
echo ""
echo -e "${YELLOW}Versión de Java:${NC}"
java -version
echo ""

# Compilar el proyecto
echo -e "${GREEN}✓${NC} Compilando el proyecto..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo -e "${RED}✗${NC} Error al compilar el proyecto"
    exit 1
fi

echo ""
echo -e "${GREEN}✓${NC} Compilación exitosa!"
echo ""

# Ejecutar la aplicación
echo "=========================================="
echo "Iniciando aplicación Spring Boot..."
echo "=========================================="
echo ""
echo -e "${YELLOW}Endpoints disponibles:${NC}"
echo "  • Swagger UI: http://localhost:8080/api/swagger-ui.html"
echo "  • Health: http://localhost:8080/api/actuator/health"
echo "  • API Base: http://localhost:8080/api"
echo ""
echo -e "${YELLOW}Para detener la aplicación: Ctrl+C${NC}"
echo ""

# Ejecutar Spring Boot con preview features habilitadas
mvn spring-boot:run -Dspring-boot.run.jvmArguments="--enable-preview"
