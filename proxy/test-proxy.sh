#!/bin/bash

# Script de prueba para el servicio Proxy
# Facilita el testing de los endpoints y conexiones

set -e

PROXY_URL="http://localhost:8081"
REDIS_HOST="192.168.194.250"
REDIS_PORT="6379"
KAFKA_BROKERS="192.168.194.250:9092"
KAFKA_TOPIC="eventos-actualizacion"
KAFKA_GROUP="alumno-matias-boldrini"

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "========================================="
echo "  Proxy Service - Script de Pruebas"
echo "========================================="
echo ""

# Función para verificar si el servicio está corriendo
check_proxy_running() {
    echo -n "🔍 Verificando si el Proxy está corriendo... "
    if curl -s -o /dev/null -w "%{http_code}" "$PROXY_URL/api/health" | grep -q "200\|503"; then
        echo -e "${GREEN}✓ Proxy está corriendo${NC}"
        return 0
    else
        echo -e "${RED}✗ Proxy NO está corriendo${NC}"
        echo "   Ejecuta: ./mvnw spring-boot:run"
        return 1
    fi
}

# Función de health check
health_check() {
    echo ""
    echo "========================================="
    echo "  1. Health Check"
    echo "========================================="
    echo ""
    echo "Consultando: $PROXY_URL/api/health"
    echo ""
    curl -s "$PROXY_URL/api/health" | jq '.' 2>/dev/null || curl -s "$PROXY_URL/api/health"
    echo ""
}

# Función para obtener asientos de un evento
get_asientos() {
    local evento_id=${1:-1}
    echo ""
    echo "========================================="
    echo "  2. Obtener Asientos del Evento $evento_id"
    echo "========================================="
    echo ""
    echo "Consultando: $PROXY_URL/api/redis/evento/$evento_id/asientos"
    echo ""
    
    response=$(curl -s -w "\n%{http_code}" "$PROXY_URL/api/redis/evento/$evento_id/asientos")
    http_code=$(echo "$response" | tail -n 1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" == "200" ]; then
        echo -e "${GREEN}✓ Asientos encontrados (HTTP $http_code)${NC}"
        echo ""
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    elif [ "$http_code" == "204" ]; then
        echo -e "${YELLOW}⚠ No hay asientos en Redis (HTTP $http_code)${NC}"
        echo "  Esto significa que todos los asientos están DISPONIBLES"
    else
        echo -e "${RED}✗ Error (HTTP $http_code)${NC}"
        echo "$body"
    fi
    echo ""
}

# Función para probar notificación manual
test_notificacion() {
    echo ""
    echo "========================================="
    echo "  3. Probar Notificación Manual"
    echo "========================================="
    echo ""
    echo "Enviando notificación de prueba..."
    echo ""
    
    curl -X POST "$PROXY_URL/api/notificar-cambio" \
        -H "Content-Type: application/json" \
        -d '{"eventoId":1,"tipo":"TEST","mensaje":"Prueba de notificación"}' \
        -w "\nHTTP Code: %{http_code}\n"
    echo ""
}

# Función para verificar conexión a Redis (requiere redis-cli)
test_redis_connection() {
    echo ""
    echo "========================================="
    echo "  4. Verificar Conexión a Redis"
    echo "========================================="
    echo ""
    
    if ! command -v redis-cli &> /dev/null; then
        echo -e "${YELLOW}⚠ redis-cli no está instalado${NC}"
        echo "   Para instalar: brew install redis"
        return 1
    fi
    
    echo "Probando conexión a Redis..."
    echo ""
    
    if redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" PING > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Conexión a Redis exitosa${NC}"
        echo ""
        echo "Consultando evento_1:"
        redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" GET evento_1 | jq '.' 2>/dev/null || \
        redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" GET evento_1
    else
        echo -e "${RED}✗ No se pudo conectar a Redis${NC}"
    fi
    echo ""
}

# Función para verificar Kafka (requiere kcat)
test_kafka_consumer() {
    echo ""
    echo "========================================="
    echo "  5. Verificar Consumer de Kafka"
    echo "========================================="
    echo ""
    
    if ! command -v kcat &> /dev/null; then
        echo -e "${YELLOW}⚠ kcat no está instalado${NC}"
        echo "   Para instalar: brew install kcat"
        return 1
    fi
    
    echo "Iniciando consumer de Kafka..."
    echo "Tópico: $KAFKA_TOPIC"
    echo "Group: $KAFKA_GROUP"
    echo ""
    echo -e "${YELLOW}Presiona Ctrl+C para detener${NC}"
    echo ""
    
    kcat -b "$KAFKA_BROKERS" -t "$KAFKA_TOPIC" -G "$KAFKA_GROUP"
}

# Función para mostrar logs del Proxy
show_logs() {
    echo ""
    echo "========================================="
    echo "  6. Ver Logs del Proxy"
    echo "========================================="
    echo ""
    
    if [ -f "target/spring-boot-logs.log" ]; then
        tail -n 50 target/spring-boot-logs.log
    else
        echo -e "${YELLOW}⚠ No se encontró archivo de logs${NC}"
        echo "   Ejecuta el Proxy con: ./mvnw spring-boot:run"
    fi
}

# Menu principal
show_menu() {
    echo ""
    echo "========================================="
    echo "  Opciones de Prueba"
    echo "========================================="
    echo ""
    echo "1. Health Check"
    echo "2. Obtener asientos de evento"
    echo "3. Probar notificación manual"
    echo "4. Verificar conexión a Redis"
    echo "5. Iniciar consumer de Kafka"
    echo "6. Ver logs del Proxy"
    echo "7. Ejecutar todas las pruebas"
    echo "8. Salir"
    echo ""
    read -p "Selecciona una opción: " option
    
    case $option in
        1) health_check ;;
        2) 
            read -p "ID del evento [1]: " evento_id
            evento_id=${evento_id:-1}
            get_asientos "$evento_id"
            ;;
        3) test_notificacion ;;
        4) test_redis_connection ;;
        5) test_kafka_consumer ;;
        6) show_logs ;;
        7)
            health_check
            get_asientos 1
            get_asientos 2
            test_notificacion
            test_redis_connection
            ;;
        8) exit 0 ;;
        *) echo -e "${RED}Opción inválida${NC}" ;;
    esac
}

# Verificar si se pasó un parámetro
if [ $# -eq 0 ]; then
    # Verificar que el proxy esté corriendo
    if ! check_proxy_running; then
        exit 1
    fi
    
    # Modo interactivo
    while true; do
        show_menu
        echo ""
        read -p "¿Continuar? (s/n): " continue
        if [ "$continue" != "s" ] && [ "$continue" != "S" ]; then
            break
        fi
    done
else
    # Modo de comando directo
    case $1 in
        health) health_check ;;
        asientos) get_asientos "${2:-1}" ;;
        notify) test_notificacion ;;
        redis) test_redis_connection ;;
        kafka) test_kafka_consumer ;;
        logs) show_logs ;;
        all)
            check_proxy_running && \
            health_check && \
            get_asientos 1 && \
            test_notificacion && \
            test_redis_connection
            ;;
        *)
            echo "Uso: $0 [health|asientos|notify|redis|kafka|logs|all]"
            exit 1
            ;;
    esac
fi

echo ""
echo -e "${GREEN}✓ Script finalizado${NC}"
