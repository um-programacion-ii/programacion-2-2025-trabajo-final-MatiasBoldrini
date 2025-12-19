package com.eventos.proxy.service.catedra.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Payload 7 (enunciado) - response de venta.
 */
public record RealizarVentaResponse(
    Long eventoId,
    Long ventaId,
    Instant fechaVenta,
    List<AsientoResultadoDTO> asientos,
    Boolean resultado,
    String descripcion,
    BigDecimal precioVenta
) {}


