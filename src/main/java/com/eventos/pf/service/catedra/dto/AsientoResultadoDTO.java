package com.eventos.pf.service.catedra.dto;

/**
 * Respuesta (payload 6/7) - resultado por asiento.
 */
public class AsientoResultadoDTO {

    private Integer fila;
    private Integer columna;
    private String estado;
    private String persona;

    public Integer getFila() {
        return fila;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public Integer getColumna() {
        return columna;
    }

    public void setColumna(Integer columna) {
        this.columna = columna;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }
}


