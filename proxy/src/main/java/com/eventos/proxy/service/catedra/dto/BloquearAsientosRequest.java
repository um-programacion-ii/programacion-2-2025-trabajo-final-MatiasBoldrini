package com.eventos.proxy.service.catedra.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * Payload 6 (enunciado) - request de bloqueo.
 */
public record BloquearAsientosRequest(
    @NotNull @Positive Long eventoId,
    @NotEmpty List<@Valid AsientoBloqueoDTO> asientos
) {}


