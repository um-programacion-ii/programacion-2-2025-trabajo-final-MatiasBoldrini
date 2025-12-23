package com.eventos.pf.service.catedra;

import com.eventos.pf.service.catedra.dto.EventoCatedraDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CatedraClientService {

    private static final Logger LOG = LoggerFactory.getLogger(CatedraClientService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${catedra.api.url}")
    private String catedraUrl;

    @Value("${catedra.api.token:}")
    private String catedraToken;

    public List<EventoCatedraDTO> getEventos() {
        if (catedraToken == null || catedraToken.isBlank()) {
            throw new IllegalStateException("CATEDRA_TOKEN no configurado (catedra.api.token)");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(catedraToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = catedraUrl + "/api/endpoints/v1/eventos";
        LOG.debug("GET eventos cátedra: {}", url);

        ResponseEntity<List<EventoCatedraDTO>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<>() {}
        );

        return response.getBody() == null ? List.of() : response.getBody();
    }

    public EventoCatedraDTO getEvento(Long id) {
        if (catedraToken == null || catedraToken.isBlank()) {
            throw new IllegalStateException("CATEDRA_TOKEN no configurado (catedra.api.token)");
        }
        if (id == null) {
            throw new IllegalArgumentException("id requerido");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(catedraToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = catedraUrl + "/api/endpoints/v1/evento/" + id;
        LOG.debug("GET evento cátedra: {}", url);

        return restTemplate.exchange(url, HttpMethod.GET, entity, EventoCatedraDTO.class).getBody();
    }
}


