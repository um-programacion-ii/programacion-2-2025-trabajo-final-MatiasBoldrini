package com.eventos.pf.service.catedra.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Payload 6 (enunciado) - asiento a bloquear.
 */
public class AsientoBloqueoDTO {

    @NotNull
    @Positive
    private Integer fila;

    @NotNull
    @Positive
    private Integer columna;

    public AsientoBloqueoDTO() {}

    public AsientoBloqueoDTO(Integer fila, Integer columna) {
        this.fila = fila;
        this.columna = columna;
    }

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
}


