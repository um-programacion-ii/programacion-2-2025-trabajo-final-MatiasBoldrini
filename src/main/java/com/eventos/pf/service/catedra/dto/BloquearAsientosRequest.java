package com.eventos.pf.service.catedra.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * Payload 6 (enunciado) - request de bloqueo.
 */
public class BloquearAsientosRequest {

    @NotNull
    @Positive
    private Long eventoId;

    @NotEmpty
    private List<@Valid AsientoBloqueoDTO> asientos;

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public List<AsientoBloqueoDTO> getAsientos() {
        return asientos;
    }

    public void setAsientos(List<AsientoBloqueoDTO> asientos) {
        this.asientos = asientos;
    }
}


