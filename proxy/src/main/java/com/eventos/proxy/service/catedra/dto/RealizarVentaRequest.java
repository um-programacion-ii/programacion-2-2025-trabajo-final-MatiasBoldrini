package com.eventos.proxy.service.catedra.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Payload 7 (enunciado) - request de venta.
 */
public record RealizarVentaRequest(
    @NotNull @Positive Long eventoId,
    @NotNull Instant fecha,
    @NotNull BigDecimal precioVenta,
    @NotEmpty List<@Valid AsientoVentaDTO> asientos
) {}


