package com.eventos.pf.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * Sesión de compra del usuario
 *
 * Permite recuperar el estado de compra si el usuario cambia de dispositivo.
 * Los campos asientosSeleccionados y datosPersonas almacenan JSON.
 */
@Entity
@Table(name = "sesion_compra")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SesionCompra implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "paso_actual", nullable = false)
    private Integer pasoActual;

    @Lob
    @Column(name = "asientos_seleccionados")
    private String asientosSeleccionados;

    @Lob
    @Column(name = "datos_personas")
    private String datosPersonas;

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    @Column(name = "fecha_expiracion")
    private Instant fechaExpiracion;

    @NotNull
    @Column(name = "activa", nullable = false)
    private Boolean activa;

    /**
     * Cada sesión pertenece a un usuario
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User usuario;

    /**
     * Cada sesión está asociada a un evento
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "integrantes" }, allowSetters = true)
    private Evento evento;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SesionCompra id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPasoActual() {
        return this.pasoActual;
    }

    public SesionCompra pasoActual(Integer pasoActual) {
        this.setPasoActual(pasoActual);
        return this;
    }

    public void setPasoActual(Integer pasoActual) {
        this.pasoActual = pasoActual;
    }

    public String getAsientosSeleccionados() {
        return this.asientosSeleccionados;
    }

    public SesionCompra asientosSeleccionados(String asientosSeleccionados) {
        this.setAsientosSeleccionados(asientosSeleccionados);
        return this;
    }

    public void setAsientosSeleccionados(String asientosSeleccionados) {
        this.asientosSeleccionados = asientosSeleccionados;
    }

    public String getDatosPersonas() {
        return this.datosPersonas;
    }

    public SesionCompra datosPersonas(String datosPersonas) {
        this.setDatosPersonas(datosPersonas);
        return this;
    }

    public void setDatosPersonas(String datosPersonas) {
        this.datosPersonas = datosPersonas;
    }

    public Instant getFechaCreacion() {
        return this.fechaCreacion;
    }

    public SesionCompra fechaCreacion(Instant fechaCreacion) {
        this.setFechaCreacion(fechaCreacion);
        return this;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Instant getFechaExpiracion() {
        return this.fechaExpiracion;
    }

    public SesionCompra fechaExpiracion(Instant fechaExpiracion) {
        this.setFechaExpiracion(fechaExpiracion);
        return this;
    }

    public void setFechaExpiracion(Instant fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Boolean getActiva() {
        return this.activa;
    }

    public SesionCompra activa(Boolean activa) {
        this.setActiva(activa);
        return this;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public User getUsuario() {
        return this.usuario;
    }

    public void setUsuario(User user) {
        this.usuario = user;
    }

    public SesionCompra usuario(User user) {
        this.setUsuario(user);
        return this;
    }

    public Evento getEvento() {
        return this.evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public SesionCompra evento(Evento evento) {
        this.setEvento(evento);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SesionCompra)) {
            return false;
        }
        return getId() != null && getId().equals(((SesionCompra) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SesionCompra{" +
            "id=" + getId() +
            ", pasoActual=" + getPasoActual() +
            ", asientosSeleccionados='" + getAsientosSeleccionados() + "'" +
            ", datosPersonas='" + getDatosPersonas() + "'" +
            ", fechaCreacion='" + getFechaCreacion() + "'" +
            ", fechaExpiracion='" + getFechaExpiracion() + "'" +
            ", activa='" + getActiva() + "'" +
            "}";
    }
}



