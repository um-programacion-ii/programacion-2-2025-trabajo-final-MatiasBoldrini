package com.eventos.pf.service.proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.eventos.pf.service.catedra.dto.AsientoBloqueoDTO;
import com.eventos.pf.service.catedra.dto.AsientoVentaDTO;
import com.eventos.pf.service.catedra.dto.BloquearAsientosRequest;
import com.eventos.pf.service.catedra.dto.BloquearAsientosResponse;
import com.eventos.pf.service.catedra.dto.RealizarVentaRequest;
import com.eventos.pf.service.catedra.dto.RealizarVentaResponse;
import com.eventos.pf.service.proxy.dto.EventoAsientosDTO;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
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

    @Test
    void bloquearAsientosCuandoProxyDevuelve200ParseaBody() {
        ProxyClientService service = new ProxyClientService();
        ReflectionTestUtils.setField(service, "proxyUrl", "http://proxy.test");

        BloquearAsientosRequest req = new BloquearAsientosRequest();
        req.setEventoId(1L);
        req.setAsientos(List.of(new AsientoBloqueoDTO(2, 3)));

        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(service, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        String body =
            """
            {"resultado":true,"descripcion":"Asientos bloqueados con exito","eventoId":1,"asientos":[{"estado":"Bloqueo exitoso","fila":2,"columna":3}]}
            """;

        server
            .expect(requestTo("http://proxy.test/api/catedra/bloquear-asientos"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body, StandardCharsets.UTF_8));

        BloquearAsientosResponse resp = service.bloquearAsientos(req, "token");
        assertThat(resp).isNotNull();
        assertThat(resp.getResultado()).isTrue();
        assertThat(resp.getEventoId()).isEqualTo(1L);
        assertThat(resp.getAsientos()).isNotNull().hasSize(1);
        assertThat(resp.getAsientos().get(0).getFila()).isEqualTo(2);
        assertThat(resp.getAsientos().get(0).getColumna()).isEqualTo(3);
        server.verify();
    }

    @Test
    void realizarVentaCuandoProxyDevuelve200ParseaBody() {
        ProxyClientService service = new ProxyClientService();
        ReflectionTestUtils.setField(service, "proxyUrl", "http://proxy.test");

        RealizarVentaRequest req = new RealizarVentaRequest();
        req.setEventoId(1L);
        req.setFecha(Instant.parse("2025-08-17T20:00:00.000Z"));
        req.setPrecioVenta(java.math.BigDecimal.valueOf(1400.10));
        req.setAsientos(List.of(new AsientoVentaDTO(2, 3, "Fernando Galvez")));

        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(service, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        String body =
            """
            {"eventoId":1,"ventaId":1506,"fechaVenta":"2025-08-24T23:18:41.974720Z","asientos":[{"fila":2,"columna":3,"persona":"Fernando Galvez","estado":"Vendido"}],"resultado":true,"descripcion":"Venta realizada con exito","precioVenta":1400.0}
            """;

        server
            .expect(requestTo("http://proxy.test/api/catedra/realizar-venta"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(body, StandardCharsets.UTF_8));

        RealizarVentaResponse resp = service.realizarVenta(req, "token");
        assertThat(resp).isNotNull();
        assertThat(resp.getResultado()).isTrue();
        assertThat(resp.getVentaId()).isEqualTo(1506L);
        assertThat(resp.getAsientos()).isNotNull().hasSize(1);
        server.verify();
    }
}


