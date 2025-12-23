package com.eventos.proxy.web.rest;

import com.eventos.proxy.model.EventoAsientosRedis;
import com.eventos.proxy.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST Controller para exponer endpoints del Proxy.
 * 
 * Endpoints disponibles:
 * - GET /api/redis/evento/{id}/asientos - Consultar estado de asientos desde
 * Redis
 * - POST /api/notificar-cambio - Recibir notificaciones (uso interno)
 * - GET /api/health - Health check
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ProxyResource {

    private final RedisService redisService;

    /**
     * Obtiene el estado de los asientos de un evento consultando Redis de la
     * cátedra.
     * 
     * IMPORTANTE: Si no se encuentran datos en Redis, significa que todos los
     * asientos
     * están disponibles.
     * 
     * @param eventoId ID del evento
     * @return EventoAsientosRedis con los asientos bloqueados/vendidos, o 204 si
     *         todos disponibles
     */
    @GetMapping("/redis/evento/{eventoId}/asientos")
    public ResponseEntity<EventoAsientosRedis> obtenerAsientos(@PathVariable Long eventoId) {
        log.info("Solicitud para obtener asientos del evento {}", eventoId);

        if (eventoId == null || eventoId <= 0) {
            log.warn("ID de evento inválido: {}", eventoId);
            return ResponseEntity.badRequest().build();
        }

        Optional<EventoAsientosRedis> asientos = redisService.obtenerAsientos(eventoId);

        if (asientos.isPresent()) {
            log.info("Asientos encontrados para evento {}: {} asientos", eventoId,
                    asientos.get().getAsientos() != null ? asientos.get().getAsientos().size() : 0);
            return ResponseEntity.ok(asientos.get());
        } else {
            // No hay datos en Redis = todos los asientos disponibles
            log.info("No hay datos en Redis para evento {}. Todos los asientos disponibles.", eventoId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    /**
     * Endpoint auxiliar para recibir notificaciones enviadas directamente al proxy.
     * El flujo habitual sigue siendo Kafka → Consumer → Backend.
     *
     * @param mensaje Mensaje de notificación
     * @return Confirmación de recepción
     */
    @PostMapping("/notificar-cambio")
    public ResponseEntity<String> notificarCambio(@RequestBody String mensaje) {
        log.info("Notificación recibida manualmente: {}", mensaje);

        try {
            // Por ahora solo logueamos
            log.debug("Procesando notificación manual: {}", mensaje);
            return ResponseEntity.ok("Notificación recibida");

        } catch (Exception e) {
            log.error("Error al procesar notificación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar notificación");
        }
    }

    /**
     * Health check del servicio Proxy.
     * Verifica la conexión con Redis.
     * 
     * @return Estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        boolean redisOk = redisService.verificarConexion();

        if (redisOk) {
            return ResponseEntity.ok("Proxy service OK - Redis connected");
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Proxy service degraded - Redis connection failed");
        }
    }

}
