#!/bin/bash

# Script de gestión para Java 21 Features Testing

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

show_usage() {
    echo "=========================================="
    echo "Java 21 Features Testing - Gestión"
    echo "=========================================="
    echo ""
    echo "Uso: ./manage.sh [comando]"
    echo ""
    echo "Comandos disponibles:"
    echo "  start     - Iniciar la aplicación"
    echo "  stop      - Detener la aplicación"
    echo "  restart   - Reiniciar la aplicación"
    echo "  status    - Ver estado de la aplicación"
    echo "  logs      - Ver logs en tiempo real"
    echo "  test      - Ejecutar tests"
    echo "  compile   - Compilar el proyecto"
    echo ""
    echo "Ejemplos:"
    echo "  ./manage.sh start"
    echo "  ./manage.sh status"
    echo "  ./manage.sh logs"
}

check_status() {
    PID=$(ps aux | grep java | grep java21test | grep -v grep | awk '{print $2}')

    if [ -z "$PID" ]; then
        echo -e "${RED}●${NC} Aplicación DETENIDA"
        return 1
    else
        echo -e "${GREEN}●${NC} Aplicación CORRIENDO (PID: $PID)"

        # Verificar puerto
        if curl -s http://localhost:8080/api/actuator/health > /dev/null 2>&1; then
            echo -e "${GREEN}✓${NC} Puerto 8080: ACTIVO"
            echo -e "${GREEN}✓${NC} Swagger UI: http://localhost:8080/api/swagger-ui.html"
        else
            echo -e "${YELLOW}⚠${NC} Puerto 8080: No responde"
        fi
        return 0
    fi
}

case "$1" in
    start)
        echo "Iniciando aplicación..."
        ./start.sh
        ;;

    stop)
        echo "Deteniendo aplicación..."
        ./stop.sh
        ;;

    restart)
        echo "Reiniciando aplicación..."
        ./stop.sh
        sleep 2
        ./start.sh
        ;;

    status)
        check_status
        ;;

    logs)
        # Buscar el archivo de logs más reciente
        LATEST_LOG=$(ls -t /tmp/claude/-home-josemaria-gonzalez-Git-personal-monghithub-java21Test/tasks/*.output 2>/dev/null | head -n 1)

        if [ -z "$LATEST_LOG" ]; then
            echo -e "${YELLOW}No se encontraron logs recientes${NC}"
        else
            echo -e "${GREEN}Mostrando logs:${NC} $LATEST_LOG"
            echo "Presiona Ctrl+C para salir"
            echo ""
            tail -f "$LATEST_LOG"
        fi
        ;;

    test)
        echo "Ejecutando tests..."
        source ~/.sdkman/bin/sdkman-init.sh
        sdk env
        mvn test
        ;;

    compile)
        echo "Compilando proyecto..."
        source ~/.sdkman/bin/sdkman-init.sh
        sdk env
        mvn clean compile
        ;;

    *)
        show_usage
        exit 1
        ;;
esac
