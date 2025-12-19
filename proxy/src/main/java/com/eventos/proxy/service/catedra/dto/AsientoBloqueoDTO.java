package com.eventos.proxy.service.catedra.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Payload 6 (enunciado) - asiento a bloquear.
 */
public record AsientoBloqueoDTO(
    @NotNull @Positive Integer fila,
    @NotNull @Positive Integer columna
) {}


