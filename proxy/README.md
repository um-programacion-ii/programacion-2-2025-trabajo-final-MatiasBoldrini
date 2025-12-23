# Proxy Service - Sistema de Venta de Entradas

Servicio intermediario entre el backend y los servicios externos de la cátedra (Kafka y Redis).

## 🎯 Propósito

Este servicio actúa como proxy para:
- **Redis**: Consultar el estado de asientos bloqueados y vendidos
- **Kafka**: Recibir notificaciones de actualización de eventos

## 🏗️ Arquitectura

```
Backend (JHipster) ←→ Proxy Service ←→ Servicios Cátedra (Kafka/Redis)
```

## 📋 Requisitos

- Java 21
- Maven 3.8+
- Acceso a la red de la cátedra (192.168.194.250)

## 🚀 Configuración

### Variables de entorno

```bash
# Servicios de cátedra
KAFKA_BOOTSTRAP_SERVERS=192.168.194.250:9092
REDIS_HOST=192.168.194.250
REDIS_PORT=6379

# Backend
BACKEND_URL=http://localhost:8080
```

### application.yml

El archivo `src/main/resources/application.yml` contiene la configuración completa.

**Importante**: El `group-id` de Kafka es único: `alumno-matias-boldrini`

## 🔧 Uso

### Compilar

```bash
mvn clean package
```

### Ejecutar

```bash
mvn spring-boot:run
```

El servicio estará disponible en `http://localhost:8081`

### Verificar

```bash
curl http://localhost:8081/api/health
```

## 📡 Endpoints

### GET /api/redis/evento/{eventoId}/asientos

Obtiene el estado de los asientos de un evento desde Redis.

**Respuestas**:
- `200 OK`: Asientos bloqueados/vendidos encontrados
- `204 No Content`: No hay datos en Redis (todos los asientos disponibles)
- `400 Bad Request`: ID de evento inválido

**Ejemplo**:
```bash
curl http://localhost:8081/api/redis/evento/1/asientos
```

**Respuesta**:
```json
{
  "asientos": [
    {
      "fila": 2,
      "columna": 1,
      "estado": "Bloqueado",
      "expira": "2025-08-24T23:25:00Z"
    },
    {
      "fila": 3,
      "columna": 5,
      "estado": "Vendido"
    }
  ]
}
```

### POST /api/notificar-cambio

Endpoint para notificaciones manuales (uso directo).

### GET /api/health

Health check del servicio.

## 🔍 Kafka Consumer

El servicio consume mensajes del tópico `eventos-actualizacion` con el group-id `alumno-matias-boldrini`.

### Estructura del mensaje (en revisión)

⚠️ **Pendiente**: Ajustar la estructura `EventoActualizacion.java` según el formato real de los mensajes de Kafka.

## 📝 Reglas de Negocio

### Redis
- **Clave**: `evento_{ID}`
- **Contenido**: JSON con asientos bloqueados o vendidos
- **Regla crítica**: Si un asiento NO está en Redis → está DISPONIBLE
- **Bloqueos**: Tienen campo `expira` (duración: 5 minutos)

### Kafka
- **Tópico**: `eventos-actualizacion`
- **Group ID**: `alumno-matias-boldrini` (único por alumno)
- Al recibir mensaje → procesar y notificar al backend

## 🔨 Pendientes

### Alta prioridad
- [ ] Verificar estructura real de mensajes Kafka
- [ ] Implementar endpoint en el backend para recibir notificaciones
- [ ] Descomentar llamada HTTP al backend en `NotificacionService`

### Media prioridad
- [ ] Agregar autenticación JWT entre Proxy y Backend
- [ ] Implementar retry logic para notificaciones fallidas
- [ ] Agregar dead letter queue para mensajes Kafka no procesables
- [ ] Tests de integración con Testcontainers

### Baja prioridad
- [ ] Métricas de Kafka (offset lag, etc.)
- [ ] Cache local de asientos para reducir consultas a Redis
- [ ] Circuit breaker para llamadas al backend

## 🧪 Testing

### Probar conexión a Redis

```bash
redis-cli -h 192.168.194.250 -p 6379 GET evento_1
```

### Probar consumo de Kafka

```bash
kcat -b 192.168.194.250:9092 -t eventos-actualizacion -G alumno-matias-boldrini
```

## 📚 Dependencias principales

- Spring Boot 3.2.0
- Spring Kafka
- Spring Data Redis
- Lettuce (cliente Redis)
- Jackson (JSON)
- Lombok

## 🐛 Troubleshooting

### Error: No se puede conectar a Redis
- Verificar conectividad: `ping 192.168.194.250`
- Verificar puerto: `nc -zv 192.168.194.250 6379`

### Error: No se reciben mensajes de Kafka
- Verificar que el tópico existe
- Verificar que el group-id es único
- Revisar logs: `logging.level.org.springframework.kafka=DEBUG`

## 📖 Referencias

- Documentación completa de API: `/proyect/API_CATEDRA.md`
- Conexión a servicios: `/proyect/CONEXION_CATEDRA.md`
- Reglas de desarrollo: `/.cursor/rules/rule.mdc`










