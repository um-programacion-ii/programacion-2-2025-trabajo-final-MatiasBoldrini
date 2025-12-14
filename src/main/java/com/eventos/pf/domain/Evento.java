package com.eventos.pf.domain;

import com.eventos.pf.domain.enumeration.EventoEstado;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Evento principal
 *
 * Campos adicionales no estándar:
 * - eventoIdCatedra: ID del evento en el servidor de la cátedra
 * - eventoTipo: String (no entidad separada)
 * - organizadorNombre/organizadorApellido: campos directos
 * - ultimaActualizacion: timestamp de sincronización
 *
 * NOTA: JHipster agregará automáticamente campos de auditoría (createdBy, createdDate, etc.)
 */
@Entity
@Table(name = "evento")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Evento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "evento_id_catedra", unique = true)
    private Long eventoIdCatedra;

    @NotNull
    @Size(max = 255)
    @Column(name = "titulo", length = 255, nullable = false)
    private String titulo;

    @Size(max = 500)
    @Column(name = "resumen", length = 500)
    private String resumen;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha")
    private Instant fecha;

    @Size(max = 500)
    @Column(name = "direccion", length = 500)
    private String direccion;

    @Size(max = 500)
    @Column(name = "imagen", length = 500)
    private String imagen;

    @Column(name = "fila_asientos")
    private Integer filaAsientos;

    @Column(name = "columna_asientos")
    private Integer columnaAsientos;

    @Column(name = "precio_entrada", precision = 21, scale = 2)
    private BigDecimal precioEntrada;

    @Size(max = 100)
    @Column(name = "evento_tipo", length = 100)
    private String eventoTipo;

    @Size(max = 100)
    @Column(name = "organizador_nombre", length = 100)
    private String organizadorNombre;

    @Size(max = 100)
    @Column(name = "organizador_apellido", length = 100)
    private String organizadorApellido;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EventoEstado estado;

    @Column(name = "ultima_actualizacion")
    private Instant ultimaActualizacion;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_evento__integrantes",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "integrantes_id")
    )
    @JsonIgnoreProperties(value = { "eventos" }, allowSetters = true)
    private Set<Integrante> integrantes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Evento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventoIdCatedra() {
        return this.eventoIdCatedra;
    }

    public Evento eventoIdCatedra(Long eventoIdCatedra) {
        this.setEventoIdCatedra(eventoIdCatedra);
        return this;
    }

    public void setEventoIdCatedra(Long eventoIdCatedra) {
        this.eventoIdCatedra = eventoIdCatedra;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public Evento titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return this.resumen;
    }

    public Evento resumen(String resumen) {
        this.setResumen(resumen);
        return this;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Evento descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Instant getFecha() {
        return this.fecha;
    }

    public Evento fecha(Instant fecha) {
        this.setFecha(fecha);
        return this;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public Evento direccion(String direccion) {
        this.setDireccion(direccion);
        return this;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getImagen() {
        return this.imagen;
    }

    public Evento imagen(String imagen) {
        this.setImagen(imagen);
        return this;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getFilaAsientos() {
        return this.filaAsientos;
    }

    public Evento filaAsientos(Integer filaAsientos) {
        this.setFilaAsientos(filaAsientos);
        return this;
    }

    public void setFilaAsientos(Integer filaAsientos) {
        this.filaAsientos = filaAsientos;
    }

    public Integer getColumnaAsientos() {
        return this.columnaAsientos;
    }

    public Evento columnaAsientos(Integer columnaAsientos) {
        this.setColumnaAsientos(columnaAsientos);
        return this;
    }

    public void setColumnaAsientos(Integer columnaAsientos) {
        this.columnaAsientos = columnaAsientos;
    }

    public BigDecimal getPrecioEntrada() {
        return this.precioEntrada;
    }

    public Evento precioEntrada(BigDecimal precioEntrada) {
        this.setPrecioEntrada(precioEntrada);
        return this;
    }

    public void setPrecioEntrada(BigDecimal precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public String getEventoTipo() {
        return this.eventoTipo;
    }

    public Evento eventoTipo(String eventoTipo) {
        this.setEventoTipo(eventoTipo);
        return this;
    }

    public void setEventoTipo(String eventoTipo) {
        this.eventoTipo = eventoTipo;
    }

    public String getOrganizadorNombre() {
        return this.organizadorNombre;
    }

    public Evento organizadorNombre(String organizadorNombre) {
        this.setOrganizadorNombre(organizadorNombre);
        return this;
    }

    public void setOrganizadorNombre(String organizadorNombre) {
        this.organizadorNombre = organizadorNombre;
    }

    public String getOrganizadorApellido() {
        return this.organizadorApellido;
    }

    public Evento organizadorApellido(String organizadorApellido) {
        this.setOrganizadorApellido(organizadorApellido);
        return this;
    }

    public void setOrganizadorApellido(String organizadorApellido) {
        this.organizadorApellido = organizadorApellido;
    }

    public EventoEstado getEstado() {
        return this.estado;
    }

    public Evento estado(EventoEstado estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EventoEstado estado) {
        this.estado = estado;
    }

    public Instant getUltimaActualizacion() {
        return this.ultimaActualizacion;
    }

    public Evento ultimaActualizacion(Instant ultimaActualizacion) {
        this.setUltimaActualizacion(ultimaActualizacion);
        return this;
    }

    public void setUltimaActualizacion(Instant ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public Set<Integrante> getIntegrantes() {
        return this.integrantes;
    }

    public void setIntegrantes(Set<Integrante> integrantes) {
        this.integrantes = integrantes;
    }

    public Evento integrantes(Set<Integrante> integrantes) {
        this.setIntegrantes(integrantes);
        return this;
    }

    public Evento addIntegrantes(Integrante integrante) {
        this.integrantes.add(integrante);
        return this;
    }

    public Evento removeIntegrantes(Integrante integrante) {
        this.integrantes.remove(integrante);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Evento)) {
            return false;
        }
        return getId() != null && getId().equals(((Evento) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Evento{" +
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
            "}";
    }
}
