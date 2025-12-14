package com.eventos.pf.domain;

import com.eventos.pf.domain.enumeration.EventoEstado;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa un evento (charla, curso, obra de teatro).
 */
@Entity
@Table(name = "evento")
public class Evento extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID del evento en el servidor de la cátedra.
     */
    @Column(name = "evento_id_catedra", unique = true)
    private Long eventoIdCatedra;

    @NotNull
    @Size(max = 255)
    @Column(name = "titulo", length = 255, nullable = false)
    private String titulo;

    @Size(max = 500)
    @Column(name = "resumen", length = 500)
    private String resumen;

    @Column(name = "descripcion", columnDefinition = "TEXT")
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
    private EventoEstado estado = EventoEstado.ACTIVO;

    @Column(name = "ultima_actualizacion")
    private Instant ultimaActualizacion;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Integrante> integrantes = new HashSet<>();

    // Getters y Setters

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

    public Evento eventoIdCatedra(Long eventoIdCatedra) {
        this.setEventoIdCatedra(eventoIdCatedra);
        return this;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Evento titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public Evento resumen(String resumen) {
        this.setResumen(resumen);
        return this;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Evento descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public Evento fecha(Instant fecha) {
        this.setFecha(fecha);
        return this;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Evento direccion(String direccion) {
        this.setDireccion(direccion);
        return this;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Evento imagen(String imagen) {
        this.setImagen(imagen);
        return this;
    }

    public Integer getFilaAsientos() {
        return filaAsientos;
    }

    public void setFilaAsientos(Integer filaAsientos) {
        this.filaAsientos = filaAsientos;
    }

    public Evento filaAsientos(Integer filaAsientos) {
        this.setFilaAsientos(filaAsientos);
        return this;
    }

    public Integer getColumnaAsientos() {
        return columnaAsientos;
    }

    public void setColumnaAsientos(Integer columnaAsientos) {
        this.columnaAsientos = columnaAsientos;
    }

    public Evento columnaAsientos(Integer columnaAsientos) {
        this.setColumnaAsientos(columnaAsientos);
        return this;
    }

    public BigDecimal getPrecioEntrada() {
        return precioEntrada;
    }

    public void setPrecioEntrada(BigDecimal precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public Evento precioEntrada(BigDecimal precioEntrada) {
        this.setPrecioEntrada(precioEntrada);
        return this;
    }

    public String getEventoTipo() {
        return eventoTipo;
    }

    public void setEventoTipo(String eventoTipo) {
        this.eventoTipo = eventoTipo;
    }

    public Evento eventoTipo(String eventoTipo) {
        this.setEventoTipo(eventoTipo);
        return this;
    }

    public String getOrganizadorNombre() {
        return organizadorNombre;
    }

    public void setOrganizadorNombre(String organizadorNombre) {
        this.organizadorNombre = organizadorNombre;
    }

    public Evento organizadorNombre(String organizadorNombre) {
        this.setOrganizadorNombre(organizadorNombre);
        return this;
    }

    public String getOrganizadorApellido() {
        return organizadorApellido;
    }

    public void setOrganizadorApellido(String organizadorApellido) {
        this.organizadorApellido = organizadorApellido;
    }

    public Evento organizadorApellido(String organizadorApellido) {
        this.setOrganizadorApellido(organizadorApellido);
        return this;
    }

    public EventoEstado getEstado() {
        return estado;
    }

    public void setEstado(EventoEstado estado) {
        this.estado = estado;
    }

    public Evento estado(EventoEstado estado) {
        this.setEstado(estado);
        return this;
    }

    public Instant getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(Instant ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public Evento ultimaActualizacion(Instant ultimaActualizacion) {
        this.setUltimaActualizacion(ultimaActualizacion);
        return this;
    }

    public Set<Integrante> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(Set<Integrante> integrantes) {
        if (this.integrantes != null) {
            this.integrantes.forEach(i -> i.setEvento(null));
        }
        if (integrantes != null) {
            integrantes.forEach(i -> i.setEvento(this));
        }
        this.integrantes = integrantes;
    }

    public Evento integrantes(Set<Integrante> integrantes) {
        this.setIntegrantes(integrantes);
        return this;
    }

    public Evento addIntegrante(Integrante integrante) {
        this.integrantes.add(integrante);
        integrante.setEvento(this);
        return this;
    }

    public Evento removeIntegrante(Integrante integrante) {
        this.integrantes.remove(integrante);
        integrante.setEvento(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Evento)) {
            return false;
        }
        return id != null && id.equals(((Evento) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Evento{" +
            "id=" + getId() +
            ", eventoIdCatedra=" + getEventoIdCatedra() +
            ", titulo='" + getTitulo() + "'" +
            ", resumen='" + getResumen() + "'" +
            ", fecha='" + getFecha() + "'" +
            ", direccion='" + getDireccion() + "'" +
            ", filaAsientos=" + getFilaAsientos() +
            ", columnaAsientos=" + getColumnaAsientos() +
            ", precioEntrada=" + getPrecioEntrada() +
            ", eventoTipo='" + getEventoTipo() + "'" +
            ", estado='" + getEstado() + "'" +
            "}";
    }
}
















