package com.eventos.proxy.service.catedra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Payload 7 (enunciado) - asiento a vender.
 */
public record AsientoVentaDTO(
    @NotNull @Positive Integer fila,
    @NotNull @Positive Integer columna,
    @NotBlank String persona
) {}


