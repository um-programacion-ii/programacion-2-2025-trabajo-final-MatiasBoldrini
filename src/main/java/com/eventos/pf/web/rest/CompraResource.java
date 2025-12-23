package com.eventos.pf.web.rest;

import com.eventos.pf.security.SecurityUtils;
import com.eventos.pf.service.compra.CompraFlowService;
import com.eventos.pf.service.compra.dto.AsientoPersona;
import com.eventos.pf.service.compra.dto.AsientoSeleccion;
import com.eventos.pf.service.compra.session.CompraSesionState;
import com.eventos.pf.service.catedra.dto.BloquearAsientosResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller para el flujo de compra (sesión, pasos, bloqueo y confirmación).
 */
@RestController
@RequestMapping("/api/compra")
public class CompraResource {

    private final CompraFlowService compraFlowService;

    public CompraResource(CompraFlowService compraFlowService) {
        this.compraFlowService = compraFlowService;
    }

    @PostMapping("/iniciar/{eventoIdLocal}")
    public ResponseEntity<CompraSesionState> iniciar(@PathVariable("eventoIdLocal") Long eventoIdLocal) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        CompraSesionState state = compraFlowService.iniciar(eventoIdLocal, login);
        return ResponseEntity.ok(state);
    }

    @PostMapping("/sesion/{sesionId}/seleccionar-asientos")
    public ResponseEntity<CompraSesionState> seleccionarAsientos(
        @PathVariable("sesionId") Long sesionId,
        @Valid @RequestBody List<@Valid AsientoSeleccion> asientos
    ) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        String jwt = SecurityUtils.getCurrentUserJWT().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        CompraSesionState state = compraFlowService.seleccionarAsientos(sesionId, asientos, login, jwt);
        return ResponseEntity.ok(state);
    }

    @PostMapping("/sesion/{sesionId}/bloquear")
    public ResponseEntity<BloquearAsientosResponse> bloquear(@PathVariable("sesionId") Long sesionId) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        String jwt = SecurityUtils.getCurrentUserJWT().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        BloquearAsientosResponse resp = compraFlowService.bloquear(sesionId, login, jwt);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/sesion/{sesionId}/cargar-nombres")
    public ResponseEntity<CompraSesionState> cargarNombres(
        @PathVariable("sesionId") Long sesionId,
        @Valid @RequestBody List<@Valid AsientoPersona> asientosPersonas
    ) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        CompraSesionState state = compraFlowService.cargarNombres(sesionId, asientosPersonas, login);
        return ResponseEntity.ok(state);
    }

    @PostMapping("/sesion/{sesionId}/confirmar")
    public ResponseEntity<CompraFlowService.ConfirmarCompraResult> confirmar(@PathVariable("sesionId") Long sesionId) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        String jwt = SecurityUtils.getCurrentUserJWT().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return ResponseEntity.ok(compraFlowService.confirmar(sesionId, login, jwt));
    }

    @GetMapping("/sesion/actual")
    public ResponseEntity<CompraSesionState> getSesionActual() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Optional<CompraSesionState> state = compraFlowService.getSesionActual(login);
        return state.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/cerrar")
    public ResponseEntity<Void> cerrarSesionActual() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        compraFlowService.cerrarSesionActual(login);
        return ResponseEntity.noContent().build();
    }
}


