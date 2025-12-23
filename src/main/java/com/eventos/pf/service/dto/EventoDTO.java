package com.eventos.pf.service.dto;

import com.eventos.pf.domain.enumeration.EventoEstado;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.eventos.pf.domain.Evento} entity.
 */
@Schema(
    description = "Evento principal\n\nCampos adicionales no estándar:\n- eventoIdCatedra: ID del evento en el servidor de la cátedra\n- eventoTipo: String (no entidad separada)\n- organizadorNombre/organizadorApellido: campos directos\n- ultimaActualizacion: timestamp de sincronización\n\nNOTA: JHipster agregará automáticamente campos de auditoría (createdBy, createdDate, etc.)"
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventoDTO implements Serializable {

    private Long id;

    private Long eventoIdCatedra;

    @NotNull
    @Size(max = 255)
    private String titulo;

    @Size(max = 500)
    private String resumen;

    @Lob
    private String descripcion;

    private Instant fecha;

    @Size(max = 500)
    private String direccion;

    @Size(max = 500)
    private String imagen;

    private Integer filaAsientos;

    private Integer columnaAsientos;

    private BigDecimal precioEntrada;

    @Size(max = 100)
    private String eventoTipo;

    @Size(max = 100)
    private String organizadorNombre;

    @Size(max = 100)
    private String organizadorApellido;

    @NotNull
    private EventoEstado estado;

    private Instant ultimaActualizacion;

    private Set<IntegranteDTO> integrantes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventoIdCatedra() {
        return eventoIdCatedra;
    }

    public void setEventoIdCatedra(Long eventoIdCatedra) {
        this.eventoIdCatedra = eventoIdCatedra;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getFilaAsientos() {
        return filaAsientos;
    }

    public void setFilaAsientos(Integer filaAsientos) {
        this.filaAsientos = filaAsientos;
    }

    public Integer getColumnaAsientos() {
        return columnaAsientos;
    }

    public void setColumnaAsientos(Integer columnaAsientos) {
        this.columnaAsientos = columnaAsientos;
    }

    public BigDecimal getPrecioEntrada() {
        return precioEntrada;
    }

    public void setPrecioEntrada(BigDecimal precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public String getEventoTipo() {
        return eventoTipo;
    }

    public void setEventoTipo(String eventoTipo) {
        this.eventoTipo = eventoTipo;
    }

    public String getOrganizadorNombre() {
        return organizadorNombre;
    }

    public void setOrganizadorNombre(String organizadorNombre) {
        this.organizadorNombre = organizadorNombre;
    }

    public String getOrganizadorApellido() {
        return organizadorApellido;
    }

    public void setOrganizadorApellido(String organizadorApellido) {
        this.organizadorApellido = organizadorApellido;
    }

    public EventoEstado getEstado() {
        return estado;
    }

    public void setEstado(EventoEstado estado) {
        this.estado = estado;
    }

    public Instant getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(Instant ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public Set<IntegranteDTO> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(Set<IntegranteDTO> integrantes) {
        this.integrantes = integrantes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventoDTO)) {
            return false;
        }

        EventoDTO eventoDTO = (EventoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventoDTO{" +
            "id=" + getId() +
            ", eventoIdCatedra=" + getEventoIdCatedra() +
            ", titulo='" + getTitulo() + "'" +
            ", resumen='" + getResumen() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", fecha='" + getFecha() + "'" +
            ", direccion='" + getDireccion() + "'" +
            ", imagen='" + getImagen() + "'" +
            ", filaAsientos=" + getFilaAsientos() +
            ", columnaAsientos=" + getColumnaAsientos() +
            ", precioEntrada=" + getPrecioEntrada() +
            ", eventoTipo='" + getEventoTipo() + "'" +
            ", organizadorNombre='" + getOrganizadorNombre() + "'" +
            ", organizadorApellido='" + getOrganizadorApellido() + "'" +
            ", estado='" + getEstado() + "'" +
            ", ultimaActualizacion='" + getUltimaActualizacion() + "'" +
            ", integrantes=" + getIntegrantes() +
            "}";
    }
}
