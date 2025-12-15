package com.eventos.proxy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un asiento en Redis (bloqueado o vendido).
 * 
 * IMPORTANTE: Si un asiento NO está en Redis, significa que está DISPONIBLE.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsientoRedis {

    /**
     * Número de fila del asiento.
     */
    private Integer fila;

    /**
     * Número de columna del asiento.
     */
    private Integer columna;

    /**
     * Estado del asiento: "Bloqueado" o "Vendido".
     */
    private String estado;

    /**
     * Fecha de expiración del bloqueo (formato ISO 8601).
     * Solo aplica para asientos bloqueados.
     * Si está vencido, el asiento puede ser bloqueado nuevamente.
     */
    @JsonProperty("expira")
    private String expira;

}
