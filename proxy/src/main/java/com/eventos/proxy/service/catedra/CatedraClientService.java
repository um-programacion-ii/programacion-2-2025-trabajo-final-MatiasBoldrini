package com.eventos.proxy.service.catedra;

import com.eventos.proxy.service.catedra.dto.BloquearAsientosRequest;
import com.eventos.proxy.service.catedra.dto.BloquearAsientosResponse;
import com.eventos.proxy.service.catedra.dto.RealizarVentaRequest;
import com.eventos.proxy.service.catedra.dto.RealizarVentaResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

/**
 * Cliente HTTP del Proxy hacia la API de la cátedra.
 *
 * El proxy es el único componente con acceso directo a cátedra (según consigna).
 */
@Service
public class CatedraClientService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${catedra.api.url:http://192.168.194.250:8080}")
    private String catedraApiUrl;

    @Value("${catedra.api.token:}")
    private String catedraToken;

    public BloquearAsientosResponse bloquearAsientos(BloquearAsientosRequest request) {
        return post("/api/endpoints/v1/bloquear-asientos", request, BloquearAsientosResponse.class);
    }

    public RealizarVentaResponse realizarVenta(RealizarVentaRequest request) {
        return post("/api/endpoints/v1/realizar-venta", request, RealizarVentaResponse.class);
    }

    public JsonNode listarVentas() {
        return get("/api/endpoints/v1/listar-ventas", JsonNode.class);
    }

    public JsonNode listarVenta(Long ventaId) {
        return get("/api/endpoints/v1/listar-venta/" + ventaId, JsonNode.class);
    }

    public JsonNode forzarActualizacion() {
        return post("/api/endpoints/v1/forzar-actualizacion", null, JsonNode.class);
    }

    private <T> T get(String path, Class<T> responseType) {
        String url = catedraApiUrl + path;
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers()), responseType);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            throw new ResponseStatusException(BAD_GATEWAY, "Error llamando a cátedra (status=" + e.getStatusCode() + ")", e);
        } catch (Exception e) {
            throw new ResponseStatusException(BAD_GATEWAY, "Error llamando a cátedra", e);
        }
    }

    private <B, T> T post(String path, B body, Class<T> responseType) {
        String url = catedraApiUrl + path;
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers()), responseType);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            throw new ResponseStatusException(BAD_GATEWAY, "Error llamando a cátedra (status=" + e.getStatusCode() + ")", e);
        } catch (Exception e) {
            throw new ResponseStatusException(BAD_GATEWAY, "Error llamando a cátedra", e);
        }
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (catedraToken != null && !catedraToken.isBlank()) {
            headers.setBearerAuth(catedraToken);
        }
        return headers;
    }
}


