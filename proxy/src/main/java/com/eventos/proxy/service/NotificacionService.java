package com.eventos.proxy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio para notificar al backend sobre actualizaciones de eventos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${backend.url}")
    private String backendUrl;

    /**
     * Notifica al backend sobre una actualización recibida de Kafka.
     * 
     * Actualmente usa un endpoint de ejemplo; ajustar la ruta definitiva cuando
     * el backend exponga el receptor de notificaciones.
     * 
     * @param mensaje Mensaje de actualización recibido de Kafka
     */
    public void notificarBackend(String mensaje) {
        String endpoint = backendUrl + "/api/eventos/notificar-actualizacion";

        try {
            log.debug("Notificando al backend: {}", endpoint);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Ajustar el cuerpo del request según el contrato que defina el backend.
            HttpEntity<String> request = new HttpEntity<>(mensaje, headers);

            // Por ahora solo se registra, el endpoint del backend no existe aún
            log.info("Notificación pendiente de envío al backend - Endpoint: {} - Mensaje: {}", endpoint, mensaje);

            // Descomentar cuando el endpoint del backend esté listo:
            // restTemplate.postForEntity(endpoint, request, String.class);
            // log.info("Backend notificado exitosamente");

        } catch (Exception e) {
            log.error("Error al notificar al backend: {}", e.getMessage(), e);
            // Agregar lógica de reintentos cuando el canal esté operativo.
        }
    }

}




