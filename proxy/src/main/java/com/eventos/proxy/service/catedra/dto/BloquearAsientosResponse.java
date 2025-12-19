package com.eventos.proxy.service.catedra.dto;

import java.util.List;

/**
 * Payload 6 (enunciado) - response de bloqueo.
 */
public record BloquearAsientosResponse(
    Boolean resultado,
    String descripcion,
    Long eventoId,
    List<AsientoResultadoDTO> asientos
) {}


