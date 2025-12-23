package com.eventos.proxy.service;

import com.eventos.proxy.model.BackendAuthRequest;
import com.eventos.proxy.model.BackendAuthResponse;
import com.eventos.proxy.model.SyncTriggerRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio para notificar al backend sobre actualizaciones de eventos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${backend.url}")
    private String backendUrl;

    @Value("${backend.auth.username}")
    private String backendUsername;

    @Value("${backend.auth.password}")
    private String backendPassword;

    @Value("${catedra.kafka.debounce-seconds:10}")
    private long debounceSeconds;

    private volatile Instant ultimaNotificacion = Instant.EPOCH;

    private volatile CachedJwt cachedJwt;

    /**
     * Notifica al backend sobre una actualización recibida de Kafka.
     *
     * @param mensaje Mensaje de actualización recibido de Kafka
     */
    public void notificarBackend(String mensaje, long offset) {
        Instant ahora = Instant.now();

        // Debounce para evitar flood de triggers
        if (Duration.between(ultimaNotificacion, ahora).compareTo(Duration.ofSeconds(debounceSeconds)) < 0) {
            log.debug("Debounce: ignorando notificación (última hace {}s). offset={}",
                    Duration.between(ultimaNotificacion, ahora).getSeconds(), offset);
            return;
        }

        String authEndpoint = backendUrl + "/api/authenticate";
        String triggerEndpoint = backendUrl + "/api/sync/trigger";

        try {
            String jwt = getOrCreateBackendJwt(authEndpoint);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(jwt);

            SyncTriggerRequest body = new SyncTriggerRequest("kafka", offset, ahora);
            HttpEntity<SyncTriggerRequest> request = new HttpEntity<>(body, headers);

            postWithRetry(triggerEndpoint, request, 3);
            ultimaNotificacion = ahora;

            log.info("Backend notificado: trigger enviado. offset={}, kafkaMsgLen={}", offset,
                    mensaje != null ? mensaje.length() : 0);

        } catch (Exception e) {
            log.error("Error al notificar al backend: {}", e.getMessage(), e);
        }
    }

    /**
     * Conserva el JWT del backend hasta su expiración.
     */
    private String getOrCreateBackendJwt(String authEndpoint) {
        CachedJwt current = cachedJwt;
        Instant now = Instant.now();
        if (current != null && current.isValid(now)) {
            return current.token;
        }

        BackendAuthRequest login = new BackendAuthRequest(backendUsername, backendPassword, false);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BackendAuthRequest> request = new HttpEntity<>(login, headers);

        ResponseEntity<BackendAuthResponse> response = restTemplate.postForEntity(authEndpoint, request,
                BackendAuthResponse.class);
        BackendAuthResponse body = response.getBody();
        if (body == null || body.getIdToken() == null || body.getIdToken().isBlank()) {
            throw new IllegalStateException("Backend /api/authenticate devolvió un token vacío");
        }

        Instant exp = parseJwtExp(body.getIdToken());
        cachedJwt = new CachedJwt(body.getIdToken(), exp);
        return body.getIdToken();
    }

    private void postWithRetry(String endpoint, HttpEntity<?> request, int maxAttempts) {
        long baseDelayMs = 250L;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                restTemplate.postForEntity(endpoint, request, String.class);
                return;
            } catch (RestClientException e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                long delay = baseDelayMs * (1L << (attempt - 1));
                log.warn("Fallo notificando backend (attempt {}/{}). Reintentando en {}ms. err={}",
                        attempt, maxAttempts, delay, e.getMessage());
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrumpido durante backoff", ie);
                }
            }
        }
    }

    /**
     * Lee claim 'exp' del JWT (epoch seconds). Si no existe, asume validez corta.
     */
    private Instant parseJwtExp(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) {
                return Instant.now().plusSeconds(60);
            }
            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            String json = new String(decoded, StandardCharsets.UTF_8);
            JsonNode node = objectMapper.readTree(json);
            JsonNode expNode = node.get("exp");
            if (expNode != null && expNode.canConvertToLong()) {
                return Instant.ofEpochSecond(expNode.asLong());
            }
            return Instant.now().plusSeconds(60);
        } catch (Exception e) {
            log.debug("No se pudo parsear exp del JWT: {}", e.getMessage());
            return Instant.now().plusSeconds(60);
        }
    }

    private static final class CachedJwt {
        private static final Duration SKEW = Duration.ofSeconds(30);

        private final String token;
        private final Instant expiresAt;

        private CachedJwt(String token, Instant expiresAt) {
            this.token = token;
            this.expiresAt = expiresAt;
        }

        private boolean isValid(Instant now) {
            return expiresAt != null && expiresAt.isAfter(now.plus(SKEW));
        }
    }
}
