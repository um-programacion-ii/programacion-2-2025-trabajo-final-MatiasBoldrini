package com.eventos.pf.service.catedra.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Payload 7 (enunciado) - response de venta.
 */
public class RealizarVentaResponse {

    private Long eventoId;
    private Long ventaId;
    private Instant fechaVenta;
    private List<AsientoResultadoDTO> asientos;
    private Boolean resultado;
    private String descripcion;
    private BigDecimal precioVenta;

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public Instant getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Instant fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public List<AsientoResultadoDTO> getAsientos() {
        return asientos;
    }

    public void setAsientos(List<AsientoResultadoDTO> asientos) {
        this.asientos = asientos;
    }

    public Boolean getResultado() {
        return resultado;
    }

    public void setResultado(Boolean resultado) {
        this.resultado = resultado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }
}


