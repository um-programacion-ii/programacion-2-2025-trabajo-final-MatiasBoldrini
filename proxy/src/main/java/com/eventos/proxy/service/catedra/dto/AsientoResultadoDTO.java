package com.eventos.proxy.service.catedra.dto;

/**
 * Respuesta (payload 6/7) - resultado por asiento.
 */
public record AsientoResultadoDTO(
    Integer fila,
    Integer columna,
    String estado,
    String persona
) {}


