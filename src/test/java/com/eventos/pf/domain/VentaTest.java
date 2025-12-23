package com.eventos.pf.domain;

import static com.eventos.pf.domain.EventoTestSamples.*;
import static com.eventos.pf.domain.VentaAsientoTestSamples.*;
import static com.eventos.pf.domain.VentaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.eventos.pf.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class VentaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Venta.class);
        Venta venta1 = getVentaSample1();
        Venta venta2 = new Venta();
        assertThat(venta1).isNotEqualTo(venta2);

        venta2.setId(venta1.getId());
        assertThat(venta1).isEqualTo(venta2);

        venta2 = getVentaSample2();
        assertThat(venta1).isNotEqualTo(venta2);
    }

    @Test
    void asientosTest() {
        Venta venta = getVentaRandomSampleGenerator();
        VentaAsiento ventaAsientoBack = getVentaAsientoRandomSampleGenerator();

        venta.addAsientos(ventaAsientoBack);
        assertThat(venta.getAsientos()).containsOnly(ventaAsientoBack);
        assertThat(ventaAsientoBack.getVenta()).isEqualTo(venta);

        venta.removeAsientos(ventaAsientoBack);
        assertThat(venta.getAsientos()).doesNotContain(ventaAsientoBack);
        assertThat(ventaAsientoBack.getVenta()).isNull();

        venta.asientos(new HashSet<>(Set.of(ventaAsientoBack)));
        assertThat(venta.getAsientos()).containsOnly(ventaAsientoBack);
        assertThat(ventaAsientoBack.getVenta()).isEqualTo(venta);

        venta.setAsientos(new HashSet<>());
        assertThat(venta.getAsientos()).doesNotContain(ventaAsientoBack);
        assertThat(ventaAsientoBack.getVenta()).isNull();
    }

    @Test
    void eventoTest() {
        Venta venta = getVentaRandomSampleGenerator();
        Evento eventoBack = getEventoRandomSampleGenerator();

        venta.setEvento(eventoBack);
        assertThat(venta.getEvento()).isEqualTo(eventoBack);

        venta.evento(null);
        assertThat(venta.getEvento()).isNull();
    }
}
