#!/bin/bash

# Script de parada para Java 21 Features Testing

set -e

echo "=========================================="
echo "Java 21 Features Testing - Detener Aplicación"
echo "=========================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Buscar proceso Java de la aplicación
PID=$(ps aux | grep java | grep java21test | grep -v grep | awk '{print $2}')

if [ -z "$PID" ]; then
    echo -e "${YELLOW}⚠${NC} No hay ninguna aplicación Java 21 corriendo"
    exit 0
fi

echo -e "${YELLOW}Deteniendo aplicación...${NC}"
echo "PID encontrado: $PID"

# Intentar detener gracefully
kill $PID 2>/dev/null

# Esperar hasta 10 segundos para que se detenga
for i in {1..10}; do
    if ! ps -p $PID > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} Aplicación detenida correctamente"
        exit 0
    fi
    sleep 1
done

# Si aún está corriendo después de 10 segundos, forzar
if ps -p $PID > /dev/null 2>&1; then
    echo -e "${YELLOW}Forzando detención...${NC}"
    kill -9 $PID 2>/dev/null
    sleep 1

    if ! ps -p $PID > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} Aplicación detenida forzosamente"
    else
        echo -e "${RED}✗${NC} No se pudo detener la aplicación"
        exit 1
    fi
fi
