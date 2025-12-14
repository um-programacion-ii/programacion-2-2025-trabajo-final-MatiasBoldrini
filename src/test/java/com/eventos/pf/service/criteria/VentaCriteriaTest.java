package com.eventos.pf.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class VentaCriteriaTest {

    @Test
    void newVentaCriteriaHasAllFiltersNullTest() {
        var ventaCriteria = new VentaCriteria();
        assertThat(ventaCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ventaCriteriaFluentMethodsCreatesFiltersTest() {
        var ventaCriteria = new VentaCriteria();

        setAllFilters(ventaCriteria);

        assertThat(ventaCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ventaCriteriaCopyCreatesNullFilterTest() {
        var ventaCriteria = new VentaCriteria();
        var copy = ventaCriteria.copy();

        assertThat(ventaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ventaCriteria)
        );
    }

    @Test
    void ventaCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ventaCriteria = new VentaCriteria();
        setAllFilters(ventaCriteria);

        var copy = ventaCriteria.copy();

        assertThat(ventaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ventaCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ventaCriteria = new VentaCriteria();

        assertThat(ventaCriteria).hasToString("VentaCriteria{}");
    }

    private static void setAllFilters(VentaCriteria ventaCriteria) {
        ventaCriteria.id();
        ventaCriteria.ventaIdCatedra();
        ventaCriteria.fechaVenta();
        ventaCriteria.precioVenta();
        ventaCriteria.resultado();
        ventaCriteria.descripcion();
        ventaCriteria.asientosId();
        ventaCriteria.usuarioId();
        ventaCriteria.eventoId();
        ventaCriteria.distinct();
    }

    private static Condition<VentaCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getVentaIdCatedra()) &&
                condition.apply(criteria.getFechaVenta()) &&
                condition.apply(criteria.getPrecioVenta()) &&
                condition.apply(criteria.getResultado()) &&
                condition.apply(criteria.getDescripcion()) &&
                condition.apply(criteria.getAsientosId()) &&
                condition.apply(criteria.getUsuarioId()) &&
                condition.apply(criteria.getEventoId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<VentaCriteria> copyFiltersAre(VentaCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getVentaIdCatedra(), copy.getVentaIdCatedra()) &&
                condition.apply(criteria.getFechaVenta(), copy.getFechaVenta()) &&
                condition.apply(criteria.getPrecioVenta(), copy.getPrecioVenta()) &&
                condition.apply(criteria.getResultado(), copy.getResultado()) &&
                condition.apply(criteria.getDescripcion(), copy.getDescripcion()) &&
                condition.apply(criteria.getAsientosId(), copy.getAsientosId()) &&
                condition.apply(criteria.getUsuarioId(), copy.getUsuarioId()) &&
                condition.apply(criteria.getEventoId(), copy.getEventoId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
