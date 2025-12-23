package com.eventos.pf.service.proxy;

import com.eventos.pf.service.catedra.dto.BloquearAsientosRequest;
import com.eventos.pf.service.catedra.dto.BloquearAsientosResponse;
import com.eventos.pf.service.catedra.dto.RealizarVentaRequest;
import com.eventos.pf.service.catedra.dto.RealizarVentaResponse;
import com.eventos.pf.service.proxy.dto.EventoAsientosDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProxyClientService {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyClientService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${proxy.url}")
    private String proxyUrl;

    public EventoAsientosDTO obtenerAsientos(Long eventoIdCatedra, String jwt) {
        if (eventoIdCatedra == null || eventoIdCatedra <= 0) {
            throw new IllegalArgumentException("eventoIdCatedra inválido");
        }
        if (jwt == null || jwt.isBlank()) {
            throw new IllegalArgumentException("jwt requerido");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = proxyUrl + "/api/redis/evento/" + eventoIdCatedra + "/asientos";
        LOG.debug("GET asientos proxy: {}", url);

        try {
            ResponseEntity<EventoAsientosDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, EventoAsientosDTO.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                return new EventoAsientosDTO(List.of());
            }

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody() == null ? new EventoAsientosDTO(List.of()) : response.getBody();
            }

            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Respuesta inesperada del proxy consultando asientos: " + response.getStatusCode()
            );
        } catch (HttpStatusCodeException e) {
            // Errores HTTP del proxy (4xx/5xx)
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Error consultando proxy (status=" + e.getStatusCode() + ")",
                e
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error consultando proxy", e);
        }
    }

    public BloquearAsientosResponse bloquearAsientos(BloquearAsientosRequest request, String jwt) {
        if (request == null || request.getEventoId() == null || request.getEventoId() <= 0) {
            throw new IllegalArgumentException("request/eventoId inválido");
        }
        if (jwt == null || jwt.isBlank()) {
            throw new IllegalArgumentException("jwt requerido");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BloquearAsientosRequest> entity = new HttpEntity<>(request, headers);

        String url = proxyUrl + "/api/catedra/bloquear-asientos";
        LOG.debug("POST bloquear-asientos proxy: {}", url);

        try {
            ResponseEntity<BloquearAsientosResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                BloquearAsientosResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Respuesta inesperada del proxy bloqueando asientos: " + response.getStatusCode()
            );
        } catch (HttpStatusCodeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error bloqueando asientos via proxy (status=" + e.getStatusCode() + ")", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error bloqueando asientos via proxy", e);
        }
    }

    public RealizarVentaResponse realizarVenta(RealizarVentaRequest request, String jwt) {
        if (request == null || request.getEventoId() == null || request.getEventoId() <= 0) {
            throw new IllegalArgumentException("request/eventoId inválido");
        }
        if (jwt == null || jwt.isBlank()) {
            throw new IllegalArgumentException("jwt requerido");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RealizarVentaRequest> entity = new HttpEntity<>(request, headers);

        String url = proxyUrl + "/api/catedra/realizar-venta";
        LOG.debug("POST realizar-venta proxy: {}", url);

        try {
            ResponseEntity<RealizarVentaResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                RealizarVentaResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Respuesta inesperada del proxy realizando venta: " + response.getStatusCode()
            );
        } catch (HttpStatusCodeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error realizando venta via proxy (status=" + e.getStatusCode() + ")", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error realizando venta via proxy", e);
        }
    }
}


