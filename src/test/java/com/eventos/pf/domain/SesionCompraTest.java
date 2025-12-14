package com.eventos.pf.domain;

import static com.eventos.pf.domain.EventoTestSamples.*;
import static com.eventos.pf.domain.SesionCompraTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.eventos.pf.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SesionCompraTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SesionCompra.class);
        SesionCompra sesionCompra1 = getSesionCompraSample1();
        SesionCompra sesionCompra2 = new SesionCompra();
        assertThat(sesionCompra1).isNotEqualTo(sesionCompra2);

        sesionCompra2.setId(sesionCompra1.getId());
        assertThat(sesionCompra1).isEqualTo(sesionCompra2);

        sesionCompra2 = getSesionCompraSample2();
        assertThat(sesionCompra1).isNotEqualTo(sesionCompra2);
    }

    @Test
    void eventoTest() {
        SesionCompra sesionCompra = getSesionCompraRandomSampleGenerator();
        Evento eventoBack = getEventoRandomSampleGenerator();

        sesionCompra.setEvento(eventoBack);
        assertThat(sesionCompra.getEvento()).isEqualTo(eventoBack);

        sesionCompra.evento(null);
        assertThat(sesionCompra.getEvento()).isNull();
    }
}
