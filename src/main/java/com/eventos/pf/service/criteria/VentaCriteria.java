package com.eventos.pf.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.eventos.pf.domain.Venta} entity. This class is used
 * in {@link com.eventos.pf.web.rest.VentaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ventas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VentaCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter ventaIdCatedra;

    private InstantFilter fechaVenta;

    private BigDecimalFilter precioVenta;

    private BooleanFilter resultado;

    private StringFilter descripcion;

    private LongFilter asientosId;

    private LongFilter usuarioId;

    private LongFilter eventoId;

    private Boolean distinct;

    public VentaCriteria() {}

    public VentaCriteria(VentaCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.ventaIdCatedra = other.optionalVentaIdCatedra().map(LongFilter::copy).orElse(null);
        this.fechaVenta = other.optionalFechaVenta().map(InstantFilter::copy).orElse(null);
        this.precioVenta = other.optionalPrecioVenta().map(BigDecimalFilter::copy).orElse(null);
        this.resultado = other.optionalResultado().map(BooleanFilter::copy).orElse(null);
        this.descripcion = other.optionalDescripcion().map(StringFilter::copy).orElse(null);
        this.asientosId = other.optionalAsientosId().map(LongFilter::copy).orElse(null);
        this.usuarioId = other.optionalUsuarioId().map(LongFilter::copy).orElse(null);
        this.eventoId = other.optionalEventoId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VentaCriteria copy() {
        return new VentaCriteria(this);
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

    public LongFilter getVentaIdCatedra() {
        return ventaIdCatedra;
    }

    public Optional<LongFilter> optionalVentaIdCatedra() {
        return Optional.ofNullable(ventaIdCatedra);
    }

    public LongFilter ventaIdCatedra() {
        if (ventaIdCatedra == null) {
            setVentaIdCatedra(new LongFilter());
        }
        return ventaIdCatedra;
    }

    public void setVentaIdCatedra(LongFilter ventaIdCatedra) {
        this.ventaIdCatedra = ventaIdCatedra;
    }

    public InstantFilter getFechaVenta() {
        return fechaVenta;
    }

    public Optional<InstantFilter> optionalFechaVenta() {
        return Optional.ofNullable(fechaVenta);
    }

    public InstantFilter fechaVenta() {
        if (fechaVenta == null) {
            setFechaVenta(new InstantFilter());
        }
        return fechaVenta;
    }

    public void setFechaVenta(InstantFilter fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimalFilter getPrecioVenta() {
        return precioVenta;
    }

    public Optional<BigDecimalFilter> optionalPrecioVenta() {
        return Optional.ofNullable(precioVenta);
    }

    public BigDecimalFilter precioVenta() {
        if (precioVenta == null) {
            setPrecioVenta(new BigDecimalFilter());
        }
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimalFilter precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BooleanFilter getResultado() {
        return resultado;
    }

    public Optional<BooleanFilter> optionalResultado() {
        return Optional.ofNullable(resultado);
    }

    public BooleanFilter resultado() {
        if (resultado == null) {
            setResultado(new BooleanFilter());
        }
        return resultado;
    }

    public void setResultado(BooleanFilter resultado) {
        this.resultado = resultado;
    }

    public StringFilter getDescripcion() {
        return descripcion;
    }

    public Optional<StringFilter> optionalDescripcion() {
        return Optional.ofNullable(descripcion);
    }

    public StringFilter descripcion() {
        if (descripcion == null) {
            setDescripcion(new StringFilter());
        }
        return descripcion;
    }

    public void setDescripcion(StringFilter descripcion) {
        this.descripcion = descripcion;
    }

    public LongFilter getAsientosId() {
        return asientosId;
    }

    public Optional<LongFilter> optionalAsientosId() {
        return Optional.ofNullable(asientosId);
    }

    public LongFilter asientosId() {
        if (asientosId == null) {
            setAsientosId(new LongFilter());
        }
        return asientosId;
    }

    public void setAsientosId(LongFilter asientosId) {
        this.asientosId = asientosId;
    }

    public LongFilter getUsuarioId() {
        return usuarioId;
    }

    public Optional<LongFilter> optionalUsuarioId() {
        return Optional.ofNullable(usuarioId);
    }

    public LongFilter usuarioId() {
        if (usuarioId == null) {
            setUsuarioId(new LongFilter());
        }
        return usuarioId;
    }

    public void setUsuarioId(LongFilter usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LongFilter getEventoId() {
        return eventoId;
    }

    public Optional<LongFilter> optionalEventoId() {
        return Optional.ofNullable(eventoId);
    }

    public LongFilter eventoId() {
        if (eventoId == null) {
            setEventoId(new LongFilter());
        }
        return eventoId;
    }

    public void setEventoId(LongFilter eventoId) {
        this.eventoId = eventoId;
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
        final VentaCriteria that = (VentaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(ventaIdCatedra, that.ventaIdCatedra) &&
            Objects.equals(fechaVenta, that.fechaVenta) &&
            Objects.equals(precioVenta, that.precioVenta) &&
            Objects.equals(resultado, that.resultado) &&
            Objects.equals(descripcion, that.descripcion) &&
            Objects.equals(asientosId, that.asientosId) &&
            Objects.equals(usuarioId, that.usuarioId) &&
            Objects.equals(eventoId, that.eventoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ventaIdCatedra, fechaVenta, precioVenta, resultado, descripcion, asientosId, usuarioId, eventoId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VentaCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalVentaIdCatedra().map(f -> "ventaIdCatedra=" + f + ", ").orElse("") +
            optionalFechaVenta().map(f -> "fechaVenta=" + f + ", ").orElse("") +
            optionalPrecioVenta().map(f -> "precioVenta=" + f + ", ").orElse("") +
            optionalResultado().map(f -> "resultado=" + f + ", ").orElse("") +
            optionalDescripcion().map(f -> "descripcion=" + f + ", ").orElse("") +
            optionalAsientosId().map(f -> "asientosId=" + f + ", ").orElse("") +
            optionalUsuarioId().map(f -> "usuarioId=" + f + ", ").orElse("") +
            optionalEventoId().map(f -> "eventoId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
