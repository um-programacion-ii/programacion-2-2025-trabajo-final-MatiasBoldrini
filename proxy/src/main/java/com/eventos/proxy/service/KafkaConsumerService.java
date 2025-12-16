package com.eventos.proxy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Servicio que consume mensajes del tópico Kafka "eventos-actualizacion".
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final NotificacionService notificacionService;

    /**
     * Escucha mensajes del tópico eventos-actualizacion.
     * 
     * Recibe el mensaje como String y, por ahora, lo reenvía sin parsear.
     * Ajustar el procesamiento cuando se confirme el formato definitivo del mensaje.
     * 
     * @param mensaje Mensaje recibido del tópico Kafka
     */
    @KafkaListener(topics = "${catedra.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumirEventoActualizacion(String mensaje) {
        log.info("Mensaje recibido de Kafka: {}", mensaje);

        try {
            // Parsear y mapear el JSON cuando se defina el esquema oficial.
            // Actualmente se notifica al backend con el payload original.

            log.debug("Procesando actualización de evento desde Kafka");
            notificacionService.notificarBackend(mensaje);

        } catch (Exception e) {
            log.error("Error al procesar mensaje de Kafka: {}", e.getMessage(), e);
            // Agregar estrategia de reintentos o dead letter queue si el mensaje falla.
        }
    }

}




