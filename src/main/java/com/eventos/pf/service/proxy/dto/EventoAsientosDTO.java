package com.eventos.pf.service.proxy.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Contenedor de asientos bloqueados/vendidos según Redis (vía Proxy).
 *
 * Regla crítica: si un asiento NO aparece en {@link #asientos}, está DISPONIBLE.
 */
public class EventoAsientosDTO implements Serializable {

    private List<AsientoRedisDTO> asientos = new ArrayList<>();

    public EventoAsientosDTO() {}

    public EventoAsientosDTO(List<AsientoRedisDTO> asientos) {
        this.asientos = asientos == null ? new ArrayList<>() : asientos;
    }

    public List<AsientoRedisDTO> getAsientos() {
        return asientos;
    }

    public void setAsientos(List<AsientoRedisDTO> asientos) {
        this.asientos = asientos == null ? new ArrayList<>() : asientos;
    }

    /**
     * Determina si un asiento debe considerarse ocupado para selección.
     *
     * - Si estado es vendido/ocupado => ocupado.
     * - Si estado es bloqueo y NO expiró => ocupado.
     * - Si el bloqueo expiró => disponible.
     * - Si no está en Redis => disponible (se maneja fuera de este método).
     */
    public boolean estaOcupado(int fila, int columna) {
        if (asientos == null || asientos.isEmpty()) {
            return false;
        }

        Instant ahora = Instant.now();
        for (AsientoRedisDTO a : asientos) {
            if (a == null || a.getFila() == null || a.getColumna() == null) {
                continue;
            }
            if (a.getFila() == fila && a.getColumna() == columna) {
                String estado = a.getEstado() == null ? "" : a.getEstado().trim().toLowerCase();

                // Vendido / Ocupado (la consigna/payloads usan a veces "Ocupado" como estado de vendido)
                if (estado.equals("vendido") || estado.equals("ocupado")) {
                    return true;
                }

                // Bloqueo (tolerante: "bloqueado" o strings que empiezan por "bloqueo")
                boolean esBloqueo = estado.equals("bloqueado") || estado.startsWith("bloqueo");
                if (esBloqueo) {
                    Instant expira = a.getExpira();
                    // Si no hay expiración, asumimos que el bloqueo está vigente para ser conservadores.
                    if (expira == null) {
                        return true;
                    }
                    return expira.isAfter(ahora);
                }

                // Estado desconocido: ser conservadores y tratarlo como ocupado.
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "EventoAsientosDTO{" + "asientos=" + asientos + '}';
    }
}


