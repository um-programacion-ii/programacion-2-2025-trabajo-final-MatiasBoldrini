package com.eventos.pf.service.compra.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;

/**
 * DTO interno del flujo: un asiento seleccionado (sin persona).
 */
public class AsientoSeleccion implements Serializable {

    @NotNull
    @Positive
    private Integer fila;

    @NotNull
    @Positive
    private Integer columna;

    public AsientoSeleccion() {}

    public AsientoSeleccion(Integer fila, Integer columna) {
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





