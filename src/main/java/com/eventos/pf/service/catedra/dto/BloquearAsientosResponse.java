package com.eventos.pf.service.catedra.dto;

import java.util.List;

/**
 * Payload 6 (enunciado) - response de bloqueo.
 */
public class BloquearAsientosResponse {

    private Boolean resultado;
    private String descripcion;
    private Long eventoId;
    private List<AsientoResultadoDTO> asientos;

    public Boolean getResultado() {
        return resultado;
    }

    public void setResultado(Boolean resultado) {
        this.resultado = resultado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public List<AsientoResultadoDTO> getAsientos() {
        return asientos;
    }

    public void setAsientos(List<AsientoResultadoDTO> asientos) {
        this.asientos = asientos;
    }
}


