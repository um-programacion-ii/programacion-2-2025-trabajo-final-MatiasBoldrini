package com.eventos.pf.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Registro de una venta
 */
@Entity
@Table(name = "venta")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Venta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "venta_id_catedra")
    private Long ventaIdCatedra;

    @NotNull
    @Column(name = "fecha_venta", nullable = false)
    private Instant fechaVenta;

    @Column(name = "precio_venta", precision = 21, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "resultado")
    private Boolean resultado;

    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    /**
     * Una venta tiene varios asientos
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "venta")
    @JsonIgnoreProperties(value = { "venta" }, allowSetters = true)
    private Set<VentaAsiento> asientos = new HashSet<>();

    /**
     * Cada venta pertenece a un usuario
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User usuario;

    /**
     * Cada venta está asociada a un evento
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "integrantes" }, allowSetters = true)
    private Evento evento;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Venta id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVentaIdCatedra() {
        return this.ventaIdCatedra;
    }

    public Venta ventaIdCatedra(Long ventaIdCatedra) {
        this.setVentaIdCatedra(ventaIdCatedra);
        return this;
    }

    public void setVentaIdCatedra(Long ventaIdCatedra) {
        this.ventaIdCatedra = ventaIdCatedra;
    }

    public Instant getFechaVenta() {
        return this.fechaVenta;
    }

    public Venta fechaVenta(Instant fechaVenta) {
        this.setFechaVenta(fechaVenta);
        return this;
    }

    public void setFechaVenta(Instant fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getPrecioVenta() {
        return this.precioVenta;
    }

    public Venta precioVenta(BigDecimal precioVenta) {
        this.setPrecioVenta(precioVenta);
        return this;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Boolean getResultado() {
        return this.resultado;
    }

    public Venta resultado(Boolean resultado) {
        this.setResultado(resultado);
        return this;
    }

    public void setResultado(Boolean resultado) {
        this.resultado = resultado;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Venta descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<VentaAsiento> getAsientos() {
        return this.asientos;
    }

    public void setAsientos(Set<VentaAsiento> ventaAsientos) {
        if (this.asientos != null) {
            this.asientos.forEach(i -> i.setVenta(null));
        }
        if (ventaAsientos != null) {
            ventaAsientos.forEach(i -> i.setVenta(this));
        }
        this.asientos = ventaAsientos;
    }

    public Venta asientos(Set<VentaAsiento> ventaAsientos) {
        this.setAsientos(ventaAsientos);
        return this;
    }

    public Venta addAsientos(VentaAsiento ventaAsiento) {
        this.asientos.add(ventaAsiento);
        ventaAsiento.setVenta(this);
        return this;
    }

    public Venta removeAsientos(VentaAsiento ventaAsiento) {
        this.asientos.remove(ventaAsiento);
        ventaAsiento.setVenta(null);
        return this;
    }

    public User getUsuario() {
        return this.usuario;
    }

    public void setUsuario(User user) {
        this.usuario = user;
    }

    public Venta usuario(User user) {
        this.setUsuario(user);
        return this;
    }

    public Evento getEvento() {
        return this.evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Venta evento(Evento evento) {
        this.setEvento(evento);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Venta)) {
            return false;
        }
        return getId() != null && getId().equals(((Venta) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Venta{" +
            "id=" + getId() +
            ", ventaIdCatedra=" + getVentaIdCatedra() +
            ", fechaVenta='" + getFechaVenta() + "'" +
            ", precioVenta=" + getPrecioVenta() +
            ", resultado='" + getResultado() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            "}";
    }
}



