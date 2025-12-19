package com.eventos.proxy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Servicio que consume mensajes del tópico Kafka "eventos-actualizacion".
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final NotificacionService notificacionService;

    @Value("${catedra.kafka.capture-samples:false}")
    private boolean captureSamples;

    @Value("${catedra.kafka.samples-dir:logs/kafka-samples}")
    private String samplesDir;

    private final AtomicLong messageCounter = new AtomicLong(0);

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")
            .withZone(ZoneId.systemDefault());

    /**
     * Escucha mensajes del tópico eventos-actualizacion.
     * 
     * Recibe el mensaje como String y, por ahora, lo reenvía sin parsear.
     * Ajustar el procesamiento cuando se confirme el formato definitivo del
     * mensaje.
     * 
     * @param record Record completo de Kafka con metadatos
     */
    @KafkaListener(topics = "${catedra.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumirEventoActualizacion(ConsumerRecord<String, String> record) {
        String mensaje = record.value();
        log.info("Mensaje recibido de Kafka [partition={}, offset={}]: {}",
                record.partition(), record.offset(), mensaje);

        // Capturar muestra para análisis si está habilitado
        if (captureSamples) {
            guardarMuestra(record);
        }

        try {
            // Parsear y mapear el JSON cuando se defina el esquema oficial.
            // Actualmente se notifica al backend con el payload original.

            log.debug("Procesando actualización de evento desde Kafka");
            notificacionService.notificarBackend(mensaje, record.offset());

        } catch (Exception e) {
            log.error("Error al procesar mensaje de Kafka: {}", e.getMessage(), e);
            // Agregar estrategia de reintentos o dead letter queue si el mensaje falla.
        }
    }

    /**
     * Guarda una muestra del mensaje Kafka para análisis del esquema.
     */
    private void guardarMuestra(ConsumerRecord<String, String> record) {
        try {
            Path dir = Path.of(samplesDir);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            long count = messageCounter.incrementAndGet();
            String timestamp = TIMESTAMP_FORMAT.format(Instant.now());
            String filename = String.format("msg_%03d_%s_p%d_o%d.json",
                    count, timestamp, record.partition(), record.offset());

            Path file = dir.resolve(filename);

            // Guardar con metadatos
            StringBuilder content = new StringBuilder();
            content.append("// Kafka Message Sample\n");
            content.append("// Topic: ").append(record.topic()).append("\n");
            content.append("// Partition: ").append(record.partition()).append("\n");
            content.append("// Offset: ").append(record.offset()).append("\n");
            content.append("// Key: ").append(record.key()).append("\n");
            content.append("// Timestamp: ").append(Instant.ofEpochMilli(record.timestamp())).append("\n");
            content.append("// Captured: ").append(Instant.now()).append("\n");
            content.append("// ---\n");
            content.append(record.value());

            Files.writeString(file, content.toString(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            log.info("Muestra de Kafka guardada en: {}", file);

        } catch (IOException e) {
            log.warn("No se pudo guardar muestra de Kafka: {}", e.getMessage());
        }
    }
}
