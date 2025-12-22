package com.eventos.proxy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.eventos.proxy.model.EventoAsientosRedis;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

class RedisServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private ObjectMapper objectMapper;

    private RedisService redisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        redisService = new RedisService(redisTemplate, objectMapper);
        ReflectionTestUtils.setField(redisService, "keyPrefix", "evento_");
    }

    @Test
    void obtenerAsientos_devuelveVacio_siNoHayDatos() {
        when(valueOps.get("evento_10")).thenReturn(null);

        Optional<EventoAsientosRedis> result = redisService.obtenerAsientos(10L);

        assertThat(result).isEmpty();
    }

    @Test
    void obtenerAsientos_devuelveDatos_siJsonValido() throws Exception {
        when(valueOps.get("evento_10")).thenReturn("{\"asientos\":[]}");
        EventoAsientosRedis parsed = new EventoAsientosRedis(List.of());
        when(objectMapper.readValue(eq("{\"asientos\":[]}"), eq(EventoAsientosRedis.class))).thenReturn(parsed);

        Optional<EventoAsientosRedis> result = redisService.obtenerAsientos(10L);

        assertThat(result).isPresent();
        assertThat(result.get().getAsientos()).isEmpty();
    }

    @Test
    void obtenerAsientos_lanzaBadGateway_siJsonInvalido() throws Exception {
        when(valueOps.get("evento_10")).thenReturn("{not-json}");
        when(objectMapper.readValue(eq("{not-json}"), eq(EventoAsientosRedis.class)))
            .thenThrow(new JsonProcessingException("bad json") {});

        assertThatThrownBy(() -> redisService.obtenerAsientos(10L))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value()).isEqualTo(502));
    }

    @Test
    void obtenerAsientos_lanzaServiceUnavailable_siRedisNoResponde() {
        when(valueOps.get("evento_10")).thenThrow(new RuntimeException("timeout"));

        assertThatThrownBy(() -> redisService.obtenerAsientos(10L))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value()).isEqualTo(503));
    }
}


