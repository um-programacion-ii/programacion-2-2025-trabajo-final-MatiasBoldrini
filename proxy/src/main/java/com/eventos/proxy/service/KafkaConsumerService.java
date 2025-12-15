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
     * TODO: Ajustar el procesamiento según el formato real de los mensajes.
     * Actualmente recibe el mensaje como String y lo procesa de forma básica.
     * 
     * @param mensaje Mensaje recibido del tópico Kafka
     */
    @KafkaListener(topics = "${catedra.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumirEventoActualizacion(String mensaje) {
        log.info("Mensaje recibido de Kafka: {}", mensaje);

        try {
            // TODO: Parsear el mensaje JSON y extraer los datos relevantes
            // Por ahora, simplemente notificamos al backend con el mensaje raw

            log.debug("Procesando actualización de evento desde Kafka");
            notificacionService.notificarBackend(mensaje);

        } catch (Exception e) {
            log.error("Error al procesar mensaje de Kafka: {}", e.getMessage(), e);
            // TODO: Implementar estrategia de retry o dead letter queue
        }
    }

}
