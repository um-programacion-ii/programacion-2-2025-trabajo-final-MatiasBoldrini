package com.eventos.pf.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * Entidad que representa un asiento vendido en una venta.
 */
@Entity
@Table(name = "venta_asiento")
public class VentaAsiento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    @JsonIgnoreProperties(value = { "asientos", "usuario", "evento" }, allowSetters = true)
    private Venta venta;

    @NotNull
    @Column(name = "fila", nullable = false)
    private Integer fila;

    @NotNull
    @Column(name = "columna", nullable = false)
    private Integer columna;

    /**
     * Nombre de la persona asignada a este asiento.
     */
    @Size(max = 200)
    @Column(name = "persona", length = 200)
    private String persona;

    /**
     * Estado del asiento: VENDIDO, BLOQUEADO, etc.
     */
    @Size(max = 50)
    @Column(name = "estado", length = 50)
    private String estado;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public VentaAsiento venta(Venta venta) {
        this.setVenta(venta);
        return this;
    }

    public Integer getFila() {
        return fila;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public VentaAsiento fila(Integer fila) {
        this.setFila(fila);
        return this;
    }

    public Integer getColumna() {
        return columna;
    }

    public void setColumna(Integer columna) {
        this.columna = columna;
    }

    public VentaAsiento columna(Integer columna) {
        this.setColumna(columna);
        return this;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public VentaAsiento persona(String persona) {
        this.setPersona(persona);
        return this;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public VentaAsiento estado(String estado) {
        this.setEstado(estado);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VentaAsiento)) {
            return false;
        }
        return id != null && id.equals(((VentaAsiento) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "VentaAsiento{" +
            "id=" + getId() +
            ", fila=" + getFila() +
            ", columna=" + getColumna() +
            ", persona='" + getPersona() + "'" +
            ", estado='" + getEstado() + "'" +
            "}";
    }
}
















