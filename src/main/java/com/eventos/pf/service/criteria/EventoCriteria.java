package com.eventos.pf.service.criteria;

import com.eventos.pf.domain.enumeration.EventoEstado;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.eventos.pf.domain.Evento} entity. This class is used
 * in {@link com.eventos.pf.web.rest.EventoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /eventos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventoCriteria implements Serializable, Criteria {

    /**
     * Class for filtering EventoEstado
     */
    public static class EventoEstadoFilter extends Filter<EventoEstado> {

        public EventoEstadoFilter() {}

        public EventoEstadoFilter(EventoEstadoFilter filter) {
            super(filter);
        }

        @Override
        public EventoEstadoFilter copy() {
            return new EventoEstadoFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter eventoIdCatedra;

    private StringFilter titulo;

    private StringFilter resumen;

    private InstantFilter fecha;

    private StringFilter direccion;

    private StringFilter imagen;

    private IntegerFilter filaAsientos;

    private IntegerFilter columnaAsientos;

    private BigDecimalFilter precioEntrada;

    private StringFilter eventoTipo;

    private StringFilter organizadorNombre;

    private StringFilter organizadorApellido;

    private EventoEstadoFilter estado;

    private InstantFilter ultimaActualizacion;

    private LongFilter integrantesId;

    private Boolean distinct;

    public EventoCriteria() {}

    public EventoCriteria(EventoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.eventoIdCatedra = other.optionalEventoIdCatedra().map(LongFilter::copy).orElse(null);
        this.titulo = other.optionalTitulo().map(StringFilter::copy).orElse(null);
        this.resumen = other.optionalResumen().map(StringFilter::copy).orElse(null);
        this.fecha = other.optionalFecha().map(InstantFilter::copy).orElse(null);
        this.direccion = other.optionalDireccion().map(StringFilter::copy).orElse(null);
        this.imagen = other.optionalImagen().map(StringFilter::copy).orElse(null);
        this.filaAsientos = other.optionalFilaAsientos().map(IntegerFilter::copy).orElse(null);
        this.columnaAsientos = other.optionalColumnaAsientos().map(IntegerFilter::copy).orElse(null);
        this.precioEntrada = other.optionalPrecioEntrada().map(BigDecimalFilter::copy).orElse(null);
        this.eventoTipo = other.optionalEventoTipo().map(StringFilter::copy).orElse(null);
        this.organizadorNombre = other.optionalOrganizadorNombre().map(StringFilter::copy).orElse(null);
        this.organizadorApellido = other.optionalOrganizadorApellido().map(StringFilter::copy).orElse(null);
        this.estado = other.optionalEstado().map(EventoEstadoFilter::copy).orElse(null);
        this.ultimaActualizacion = other.optionalUltimaActualizacion().map(InstantFilter::copy).orElse(null);
        this.integrantesId = other.optionalIntegrantesId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EventoCriteria copy() {
        return new EventoCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getEventoIdCatedra() {
        return eventoIdCatedra;
    }

    public Optional<LongFilter> optionalEventoIdCatedra() {
        return Optional.ofNullable(eventoIdCatedra);
    }

    public LongFilter eventoIdCatedra() {
        if (eventoIdCatedra == null) {
            setEventoIdCatedra(new LongFilter());
        }
        return eventoIdCatedra;
    }

    public void setEventoIdCatedra(LongFilter eventoIdCatedra) {
        this.eventoIdCatedra = eventoIdCatedra;
    }

    public StringFilter getTitulo() {
        return titulo;
    }

    public Optional<StringFilter> optionalTitulo() {
        return Optional.ofNullable(titulo);
    }

    public StringFilter titulo() {
        if (titulo == null) {
            setTitulo(new StringFilter());
        }
        return titulo;
    }

    public void setTitulo(StringFilter titulo) {
        this.titulo = titulo;
    }

    public StringFilter getResumen() {
        return resumen;
    }

    public Optional<StringFilter> optionalResumen() {
        return Optional.ofNullable(resumen);
    }

    public StringFilter resumen() {
        if (resumen == null) {
            setResumen(new StringFilter());
        }
        return resumen;
    }

    public void setResumen(StringFilter resumen) {
        this.resumen = resumen;
    }

    public InstantFilter getFecha() {
        return fecha;
    }

    public Optional<InstantFilter> optionalFecha() {
        return Optional.ofNullable(fecha);
    }

    public InstantFilter fecha() {
        if (fecha == null) {
            setFecha(new InstantFilter());
        }
        return fecha;
    }

    public void setFecha(InstantFilter fecha) {
        this.fecha = fecha;
    }

    public StringFilter getDireccion() {
        return direccion;
    }

    public Optional<StringFilter> optionalDireccion() {
        return Optional.ofNullable(direccion);
    }

    public StringFilter direccion() {
        if (direccion == null) {
            setDireccion(new StringFilter());
        }
        return direccion;
    }

    public void setDireccion(StringFilter direccion) {
        this.direccion = direccion;
    }

    public StringFilter getImagen() {
        return imagen;
    }

    public Optional<StringFilter> optionalImagen() {
        return Optional.ofNullable(imagen);
    }

    public StringFilter imagen() {
        if (imagen == null) {
            setImagen(new StringFilter());
        }
        return imagen;
    }

    public void setImagen(StringFilter imagen) {
        this.imagen = imagen;
    }

    public IntegerFilter getFilaAsientos() {
        return filaAsientos;
    }

    public Optional<IntegerFilter> optionalFilaAsientos() {
        return Optional.ofNullable(filaAsientos);
    }

    public IntegerFilter filaAsientos() {
        if (filaAsientos == null) {
            setFilaAsientos(new IntegerFilter());
        }
        return filaAsientos;
    }

    public void setFilaAsientos(IntegerFilter filaAsientos) {
        this.filaAsientos = filaAsientos;
    }

    public IntegerFilter getColumnaAsientos() {
        return columnaAsientos;
    }

    public Optional<IntegerFilter> optionalColumnaAsientos() {
        return Optional.ofNullable(columnaAsientos);
    }

    public IntegerFilter columnaAsientos() {
        if (columnaAsientos == null) {
            setColumnaAsientos(new IntegerFilter());
        }
        return columnaAsientos;
    }

    public void setColumnaAsientos(IntegerFilter columnaAsientos) {
        this.columnaAsientos = columnaAsientos;
    }

    public BigDecimalFilter getPrecioEntrada() {
        return precioEntrada;
    }

    public Optional<BigDecimalFilter> optionalPrecioEntrada() {
        return Optional.ofNullable(precioEntrada);
    }

    public BigDecimalFilter precioEntrada() {
        if (precioEntrada == null) {
            setPrecioEntrada(new BigDecimalFilter());
        }
        return precioEntrada;
    }

    public void setPrecioEntrada(BigDecimalFilter precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public StringFilter getEventoTipo() {
        return eventoTipo;
    }

    public Optional<StringFilter> optionalEventoTipo() {
        return Optional.ofNullable(eventoTipo);
    }

    public StringFilter eventoTipo() {
        if (eventoTipo == null) {
            setEventoTipo(new StringFilter());
        }
        return eventoTipo;
    }

    public void setEventoTipo(StringFilter eventoTipo) {
        this.eventoTipo = eventoTipo;
    }

    public StringFilter getOrganizadorNombre() {
        return organizadorNombre;
    }

    public Optional<StringFilter> optionalOrganizadorNombre() {
        return Optional.ofNullable(organizadorNombre);
    }

    public StringFilter organizadorNombre() {
        if (organizadorNombre == null) {
            setOrganizadorNombre(new StringFilter());
        }
        return organizadorNombre;
    }

    public void setOrganizadorNombre(StringFilter organizadorNombre) {
        this.organizadorNombre = organizadorNombre;
    }

    public StringFilter getOrganizadorApellido() {
        return organizadorApellido;
    }

    public Optional<StringFilter> optionalOrganizadorApellido() {
        return Optional.ofNullable(organizadorApellido);
    }

    public StringFilter organizadorApellido() {
        if (organizadorApellido == null) {
            setOrganizadorApellido(new StringFilter());
        }
        return organizadorApellido;
    }

    public void setOrganizadorApellido(StringFilter organizadorApellido) {
        this.organizadorApellido = organizadorApellido;
    }

    public EventoEstadoFilter getEstado() {
        return estado;
    }

    public Optional<EventoEstadoFilter> optionalEstado() {
        return Optional.ofNullable(estado);
    }

    public EventoEstadoFilter estado() {
        if (estado == null) {
            setEstado(new EventoEstadoFilter());
        }
        return estado;
    }

    public void setEstado(EventoEstadoFilter estado) {
        this.estado = estado;
    }

    public InstantFilter getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public Optional<InstantFilter> optionalUltimaActualizacion() {
        return Optional.ofNullable(ultimaActualizacion);
    }

    public InstantFilter ultimaActualizacion() {
        if (ultimaActualizacion == null) {
            setUltimaActualizacion(new InstantFilter());
        }
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(InstantFilter ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public LongFilter getIntegrantesId() {
        return integrantesId;
    }

    public Optional<LongFilter> optionalIntegrantesId() {
        return Optional.ofNullable(integrantesId);
    }

    public LongFilter integrantesId() {
        if (integrantesId == null) {
            setIntegrantesId(new LongFilter());
        }
        return integrantesId;
    }

    public void setIntegrantesId(LongFilter integrantesId) {
        this.integrantesId = integrantesId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EventoCriteria that = (EventoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(eventoIdCatedra, that.eventoIdCatedra) &&
            Objects.equals(titulo, that.titulo) &&
            Objects.equals(resumen, that.resumen) &&
            Objects.equals(fecha, that.fecha) &&
            Objects.equals(direccion, that.direccion) &&
            Objects.equals(imagen, that.imagen) &&
            Objects.equals(filaAsientos, that.filaAsientos) &&
            Objects.equals(columnaAsientos, that.columnaAsientos) &&
            Objects.equals(precioEntrada, that.precioEntrada) &&
            Objects.equals(eventoTipo, that.eventoTipo) &&
            Objects.equals(organizadorNombre, that.organizadorNombre) &&
            Objects.equals(organizadorApellido, that.organizadorApellido) &&
            Objects.equals(estado, that.estado) &&
            Objects.equals(ultimaActualizacion, that.ultimaActualizacion) &&
            Objects.equals(integrantesId, that.integrantesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            eventoIdCatedra,
            titulo,
            resumen,
            fecha,
            direccion,
            imagen,
            filaAsientos,
            columnaAsientos,
            precioEntrada,
            eventoTipo,
            organizadorNombre,
            organizadorApellido,
            estado,
            ultimaActualizacion,
            integrantesId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalEventoIdCatedra().map(f -> "eventoIdCatedra=" + f + ", ").orElse("") +
            optionalTitulo().map(f -> "titulo=" + f + ", ").orElse("") +
            optionalResumen().map(f -> "resumen=" + f + ", ").orElse("") +
            optionalFecha().map(f -> "fecha=" + f + ", ").orElse("") +
            optionalDireccion().map(f -> "direccion=" + f + ", ").orElse("") +
            optionalImagen().map(f -> "imagen=" + f + ", ").orElse("") +
            optionalFilaAsientos().map(f -> "filaAsientos=" + f + ", ").orElse("") +
            optionalColumnaAsientos().map(f -> "columnaAsientos=" + f + ", ").orElse("") +
            optionalPrecioEntrada().map(f -> "precioEntrada=" + f + ", ").orElse("") +
            optionalEventoTipo().map(f -> "eventoTipo=" + f + ", ").orElse("") +
            optionalOrganizadorNombre().map(f -> "organizadorNombre=" + f + ", ").orElse("") +
            optionalOrganizadorApellido().map(f -> "organizadorApellido=" + f + ", ").orElse("") +
            optionalEstado().map(f -> "estado=" + f + ", ").orElse("") +
            optionalUltimaActualizacion().map(f -> "ultimaActualizacion=" + f + ", ").orElse("") +
            optionalIntegrantesId().map(f -> "integrantesId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
