package com.eventos.pf.service.compra.session;

import com.eventos.pf.config.ApplicationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementación Redis del store de sesión activa (TTL por inactividad).
 */
@Service
public class RedisCompraSesionStore implements CompraSesionStore {

    private static final Logger LOG = LoggerFactory.getLogger(RedisCompraSesionStore.class);

    private static final String KEY_PREFIX = "compra:sesion:";

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final ApplicationProperties applicationProperties;

    public RedisCompraSesionStore(StringRedisTemplate redis, ObjectMapper objectMapper, ApplicationProperties applicationProperties) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public Optional<CompraSesionState> get(String userLogin) {
        if (userLogin == null || userLogin.isBlank()) {
            return Optional.empty();
        }
        String key = key(userLogin);
        String json = redis.opsForValue().get(key);
        if (json == null || json.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, CompraSesionState.class));
        } catch (Exception e) {
            LOG.warn("No se pudo deserializar CompraSesionState desde Redis (key={})", key, e);
            return Optional.empty();
        }
    }

    @Override
    public void save(String userLogin, CompraSesionState state) {
        if (userLogin == null || userLogin.isBlank()) {
            throw new IllegalArgumentException("userLogin requerido");
        }
        if (state == null) {
            throw new IllegalArgumentException("state requerido");
        }
        state.setUltimaActividad(Instant.now());
        String key = key(userLogin);
        try {
            String json = objectMapper.writeValueAsString(state);
            redis.opsForValue().set(key, json, applicationProperties.getCompra().getSesionTtl());
        } catch (Exception e) {
            throw new IllegalStateException("Error guardando sesión de compra en Redis", e);
        }
    }

    @Override
    public void delete(String userLogin) {
        if (userLogin == null || userLogin.isBlank()) {
            return;
        }
        redis.delete(key(userLogin));
    }

    private String key(String userLogin) {
        return KEY_PREFIX + userLogin;
    }
}





