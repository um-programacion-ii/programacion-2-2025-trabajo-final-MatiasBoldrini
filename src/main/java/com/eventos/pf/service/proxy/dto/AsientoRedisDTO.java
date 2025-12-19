package com.eventos.pf.service.proxy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;

/**
 * Asiento proveniente del Proxy (Redis de cátedra).
 *
 * Regla crítica: si un asiento NO aparece en Redis, está DISPONIBLE.
 *
 * Nota: en el proxy, el campo "expira" llega como string ISO-8601. Jackson puede mapearlo a Instant.
 */
public class AsientoRedisDTO implements Serializable {

    private Integer fila;
    private Integer columna;
    private String estado;

    @JsonProperty("expira")
    private Instant expira;

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

    public Instant getExpira() {
        return expira;
    }

    public void setExpira(Instant expira) {
        this.expira = expira;
    }

    @Override
    public String toString() {
        return (
            "AsientoRedisDTO{" +
            "fila=" +
            fila +
            ", columna=" +
            columna +
            ", estado='" +
            estado +
            '\'' +
            ", expira=" +
            expira +
            '}'
        );
    }
}


