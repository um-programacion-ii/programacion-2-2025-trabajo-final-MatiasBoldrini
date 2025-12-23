package com.eventos.proxy.web.rest;

import com.eventos.proxy.service.catedra.CatedraClientService;
import com.eventos.proxy.service.catedra.dto.BloquearAsientosRequest;
import com.eventos.proxy.service.catedra.dto.BloquearAsientosResponse;
import com.eventos.proxy.service.catedra.dto.RealizarVentaRequest;
import com.eventos.proxy.service.catedra.dto.RealizarVentaResponse;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints passthrough a la API de cátedra.
 *
 * Requisito: el backend NO debe llamar a cátedra directo.
 */
@RestController
@RequestMapping("/api/catedra")
@RequiredArgsConstructor
@Slf4j
public class CatedraPassthroughResource {

    private final CatedraClientService catedraClientService;

    @PostMapping("/bloquear-asientos")
    public ResponseEntity<BloquearAsientosResponse> bloquearAsientos(@Valid @RequestBody BloquearAsientosRequest request) {
        log.info("Passthrough bloquear-asientos eventoId={}", request.eventoId());
        return ResponseEntity.ok(catedraClientService.bloquearAsientos(request));
    }

    @PostMapping("/realizar-venta")
    public ResponseEntity<RealizarVentaResponse> realizarVenta(@Valid @RequestBody RealizarVentaRequest request) {
        log.info("Passthrough realizar-venta eventoId={} asientos={}", request.eventoId(), request.asientos() != null ? request.asientos().size() : 0);
        return ResponseEntity.ok(catedraClientService.realizarVenta(request));
    }

    @GetMapping("/listar-ventas")
    public ResponseEntity<JsonNode> listarVentas() {
        log.info("Passthrough listar-ventas");
        return ResponseEntity.ok(catedraClientService.listarVentas());
    }

    @GetMapping("/listar-venta/{id}")
    public ResponseEntity<JsonNode> listarVenta(@PathVariable("id") @NotNull @Positive Long id) {
        log.info("Passthrough listar-venta id={}", id);
        return ResponseEntity.ok(catedraClientService.listarVenta(id));
    }

    @PostMapping("/forzar-actualizacion")
    public ResponseEntity<JsonNode> forzarActualizacion() {
        log.info("Passthrough forzar-actualizacion");
        return ResponseEntity.ok(catedraClientService.forzarActualizacion());
    }
}


