package com.eventos.pf.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.eventos.pf.domain.SesionCompra} entity.
 */
@Schema(
    description = "Sesión de compra del usuario\n\nPermite recuperar el estado de compra si el usuario cambia de dispositivo.\nLos campos asientosSeleccionados y datosPersonas almacenan JSON."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SesionCompraDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer pasoActual;

    @Lob
    private String asientosSeleccionados;

    @Lob
    private String datosPersonas;

    private Instant fechaCreacion;

    private Instant fechaExpiracion;

    @NotNull
    private Boolean activa;

    @Schema(description = "Cada sesión pertenece a un usuario")
    private UserDTO usuario;

    @Schema(description = "Cada sesión está asociada a un evento")
    private EventoDTO evento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPasoActual() {
        return pasoActual;
    }

    public void setPasoActual(Integer pasoActual) {
        this.pasoActual = pasoActual;
    }

    public String getAsientosSeleccionados() {
        return asientosSeleccionados;
    }

    public void setAsientosSeleccionados(String asientosSeleccionados) {
        this.asientosSeleccionados = asientosSeleccionados;
    }

    public String getDatosPersonas() {
        return datosPersonas;
    }

    public void setDatosPersonas(String datosPersonas) {
        this.datosPersonas = datosPersonas;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Instant getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(Instant fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public UserDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UserDTO usuario) {
        this.usuario = usuario;
    }

    public EventoDTO getEvento() {
        return evento;
    }

    public void setEvento(EventoDTO evento) {
        this.evento = evento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SesionCompraDTO)) {
            return false;
        }

        SesionCompraDTO sesionCompraDTO = (SesionCompraDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, sesionCompraDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SesionCompraDTO{" +
            "id=" + getId() +
            ", pasoActual=" + getPasoActual() +
            ", asientosSeleccionados='" + getAsientosSeleccionados() + "'" +
            ", datosPersonas='" + getDatosPersonas() + "'" +
            ", fechaCreacion='" + getFechaCreacion() + "'" +
            ", fechaExpiracion='" + getFechaExpiracion() + "'" +
            ", activa='" + getActiva() + "'" +
            ", usuario=" + getUsuario() +
            ", evento=" + getEvento() +
            "}";
    }
}
