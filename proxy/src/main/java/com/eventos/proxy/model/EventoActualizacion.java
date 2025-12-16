package com.eventos.proxy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mensaje recibido del tópico Kafka "eventos-actualizacion".
 * 
 * Estructura de ejemplo; actualizar cuando se conozca el formato real de los
 * mensajes producidos en Kafka.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoActualizacion {

    /**
     * ID del evento que fue actualizado.
     */
    private Long eventoId;

    /**
     * Tipo de actualización (ej: "EVENTO_MODIFICADO", "ASIENTOS_ACTUALIZADOS",
     * etc.)
     */
    private String tipoActualizacion;

    /**
     * Timestamp de la actualización.
     */
    private String timestamp;

    /**
     * Datos adicionales del evento (opcional).
     */
    private Object datos;

}




