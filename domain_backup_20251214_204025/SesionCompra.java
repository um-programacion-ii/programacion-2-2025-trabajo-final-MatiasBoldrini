package com.eventos.pf.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

/**
 * Entidad que representa la sesión de compra de un usuario.
 * Permite recuperar el estado de compra si el usuario cambia de dispositivo.
 */
@Entity
@Table(name = "sesion_compra")
public class SesionCompra implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties(value = { "authorities" }, allowSetters = true)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    @JsonIgnoreProperties(value = { "integrantes" }, allowSetters = true)
    private Evento evento;

    /**
     * Paso actual en el flujo de compra:
     * 1 = Selección de evento
     * 2 = Selección de asientos
     * 3 = Ingreso de datos de personas
     * 4 = Confirmación de compra
     */
    @NotNull
    @Column(name = "paso_actual", nullable = false)
    private Integer pasoActual = 1;

    /**
     * Asientos seleccionados en formato JSON.
     * Ejemplo: [{"fila": 1, "columna": 2}, {"fila": 1, "columna": 3}]
     */
    @Column(name = "asientos_seleccionados", columnDefinition = "TEXT")
    private String asientosSeleccionados;

    /**
     * Datos de las personas para cada asiento en formato JSON.
     * Ejemplo: [{"nombre": "Juan", "apellido": "Perez"}, ...]
     */
    @Column(name = "datos_personas", columnDefinition = "TEXT")
    private String datosPersonas;

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    @Column(name = "fecha_expiracion")
    private Instant fechaExpiracion;

    @NotNull
    @Column(name = "activa", nullable = false)
    private Boolean activa = true;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public SesionCompra usuario(User usuario) {
        this.setUsuario(usuario);
        return this;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public SesionCompra evento(Evento evento) {
        this.setEvento(evento);
        return this;
    }

    public Integer getPasoActual() {
        return pasoActual;
    }

    public void setPasoActual(Integer pasoActual) {
        this.pasoActual = pasoActual;
    }

    public SesionCompra pasoActual(Integer pasoActual) {
        this.setPasoActual(pasoActual);
        return this;
    }

    public String getAsientosSeleccionados() {
        return asientosSeleccionados;
    }

    public void setAsientosSeleccionados(String asientosSeleccionados) {
        this.asientosSeleccionados = asientosSeleccionados;
    }

    public SesionCompra asientosSeleccionados(String asientosSeleccionados) {
        this.setAsientosSeleccionados(asientosSeleccionados);
        return this;
    }

    public String getDatosPersonas() {
        return datosPersonas;
    }

    public void setDatosPersonas(String datosPersonas) {
        this.datosPersonas = datosPersonas;
    }

    public SesionCompra datosPersonas(String datosPersonas) {
        this.setDatosPersonas(datosPersonas);
        return this;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public SesionCompra fechaCreacion(Instant fechaCreacion) {
        this.setFechaCreacion(fechaCreacion);
        return this;
    }

    public Instant getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(Instant fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public SesionCompra fechaExpiracion(Instant fechaExpiracion) {
        this.setFechaExpiracion(fechaExpiracion);
        return this;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public SesionCompra activa(Boolean activa) {
        this.setActiva(activa);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SesionCompra)) {
            return false;
        }
        return id != null && id.equals(((SesionCompra) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SesionCompra{" +
            "id=" + getId() +
            ", pasoActual=" + getPasoActual() +
            ", fechaCreacion='" + getFechaCreacion() + "'" +
            ", fechaExpiracion='" + getFechaExpiracion() + "'" +
            ", activa='" + getActiva() + "'" +
            "}";
    }
}
















