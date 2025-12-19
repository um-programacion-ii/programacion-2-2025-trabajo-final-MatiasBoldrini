package com.eventos.pf.service.catedra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Payload 7 (enunciado) - asiento a vender.
 */
public class AsientoVentaDTO {

    @NotNull
    @Positive
    private Integer fila;

    @NotNull
    @Positive
    private Integer columna;

    @NotBlank
    private String persona;

    public AsientoVentaDTO() {}

    public AsientoVentaDTO(Integer fila, Integer columna, String persona) {
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


