package com.eventos.proxy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Estructura de datos que se almacena en Redis para cada evento.
 * 
 * Clave en Redis: evento_{ID}
 * Valor: JSON con esta estructura
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoAsientosRedis {

    /**
     * Lista de asientos bloqueados o vendidos.
     * 
     * REGLA CRÍTICA: Si un asiento NO está en esta lista, está DISPONIBLE.
     */
    private List<AsientoRedis> asientos;

}



