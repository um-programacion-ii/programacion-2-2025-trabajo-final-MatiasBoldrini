package com.eventos.proxy.service;

import com.eventos.proxy.model.EventoAsientosRedis;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para consultar el estado de asientos en Redis de la cátedra.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${catedra.redis.key-prefix}")
    private String keyPrefix;

    /**
     * Obtiene el estado de los asientos de un evento desde Redis.
     * 
     * @param eventoId ID del evento
     * @return EventoAsientosRedis con la lista de asientos bloqueados/vendidos,
     *         o vacío si no hay asientos en Redis (todos disponibles)
     */
    public Optional<EventoAsientosRedis> obtenerAsientos(Long eventoId) {
        String key = keyPrefix + eventoId;
        log.debug("Consultando Redis con clave: {}", key);

        try {
            String jsonData = redisTemplate.opsForValue().get(key);

            if (jsonData == null || jsonData.isEmpty()) {
                log.debug("No se encontraron datos en Redis para evento {}. Todos los asientos están disponibles.",
                        eventoId);
                return Optional.empty();
            }

            EventoAsientosRedis asientos = objectMapper.readValue(jsonData, EventoAsientosRedis.class);
            log.debug("Asientos obtenidos de Redis para evento {}: {} asientos", eventoId,
                    asientos.getAsientos() != null ? asientos.getAsientos().size() : 0);

            return Optional.of(asientos);

        } catch (JsonProcessingException e) {
            log.error("Error al parsear JSON de Redis para evento {}: {}", eventoId, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error al consultar Redis para evento {}: {}", eventoId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Verifica la conexión con Redis.
     * 
     * @return true si la conexión es exitosa
     */
    public boolean verificarConexion() {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            log.info("Conexión con Redis de cátedra exitosa");
            return true;
        } catch (Exception e) {
            log.error("Error al conectar con Redis: {}", e.getMessage());
            return false;
        }
    }

}



