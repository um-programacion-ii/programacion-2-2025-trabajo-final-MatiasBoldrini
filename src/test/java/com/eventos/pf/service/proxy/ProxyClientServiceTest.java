package com.eventos.pf.service.proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.eventos.pf.service.proxy.dto.EventoAsientosDTO;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

class ProxyClientServiceTest {

    @Test
    void obtenerAsientosCuandoProxyDevuelve204DevuelveListaVacia() {
        ProxyClientService service = new ProxyClientService();
        ReflectionTestUtils.setField(service, "proxyUrl", "http://proxy.test");

        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(service, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        server
            .expect(requestTo("http://proxy.test/api/redis/evento/1/asientos"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andRespond(withStatus(HttpStatus.NO_CONTENT));

        EventoAsientosDTO dto = service.obtenerAsientos(1L, "token");
        assertThat(dto.getAsientos()).isNotNull().isEmpty();
        server.verify();
    }

    @Test
    void obtenerAsientosCuandoProxyDevuelve200ParseaBody() {
        ProxyClientService service = new ProxyClientService();
        ReflectionTestUtils.setField(service, "proxyUrl", "http://proxy.test");

        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(service, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        String body =
            """
            {"asientos":[{"fila":2,"columna":3,"estado":"Bloqueado","expira":"2099-01-01T00:00:00Z"}]}
            """;

        server
            .expect(requestTo("http://proxy.test/api/redis/evento/1/asientos"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body, StandardCharsets.UTF_8));

        EventoAsientosDTO dto = service.obtenerAsientos(1L, "token");
        assertThat(dto.getAsientos()).isNotNull().hasSize(1);
        assertThat(dto.getAsientos().get(0).getFila()).isEqualTo(2);
        assertThat(dto.getAsientos().get(0).getColumna()).isEqualTo(3);
        assertThat(dto.getAsientos().get(0).getEstado()).isEqualTo("Bloqueado");
        assertThat(dto.getAsientos().get(0).getExpira()).isNotNull();
        server.verify();
    }
}


