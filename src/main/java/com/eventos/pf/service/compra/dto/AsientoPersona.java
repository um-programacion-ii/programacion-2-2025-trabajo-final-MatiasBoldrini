package com.eventos.pf.service.compra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;

/**
 * DTO interno del flujo: asiento + persona.
 */
public class AsientoPersona implements Serializable {

    @NotNull
    @Positive
    private Integer fila;

    @NotNull
    @Positive
    private Integer columna;

    @NotBlank
    private String persona;

    public AsientoPersona() {}

    public AsientoPersona(Integer fila, Integer columna, String persona) {
        this.fila = fila;
        this.columna = columna;
        this.persona = persona;
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

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }
}





