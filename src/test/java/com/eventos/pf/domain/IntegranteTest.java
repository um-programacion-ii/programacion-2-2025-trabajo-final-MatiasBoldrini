package com.eventos.pf.domain;

import static com.eventos.pf.domain.EventoTestSamples.*;
import static com.eventos.pf.domain.IntegranteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.eventos.pf.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class IntegranteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Integrante.class);
        Integrante integrante1 = getIntegranteSample1();
        Integrante integrante2 = new Integrante();
        assertThat(integrante1).isNotEqualTo(integrante2);

        integrante2.setId(integrante1.getId());
        assertThat(integrante1).isEqualTo(integrante2);

        integrante2 = getIntegranteSample2();
        assertThat(integrante1).isNotEqualTo(integrante2);
    }

    @Test
    void eventosTest() {
        Integrante integrante = getIntegranteRandomSampleGenerator();
        Evento eventoBack = getEventoRandomSampleGenerator();

        integrante.addEventos(eventoBack);
        assertThat(integrante.getEventos()).containsOnly(eventoBack);
        assertThat(eventoBack.getIntegrantes()).containsOnly(integrante);

        integrante.removeEventos(eventoBack);
        assertThat(integrante.getEventos()).doesNotContain(eventoBack);
        assertThat(eventoBack.getIntegrantes()).doesNotContain(integrante);

        integrante.eventos(new HashSet<>(Set.of(eventoBack)));
        assertThat(integrante.getEventos()).containsOnly(eventoBack);
        assertThat(eventoBack.getIntegrantes()).containsOnly(integrante);

        integrante.setEventos(new HashSet<>());
        assertThat(integrante.getEventos()).doesNotContain(eventoBack);
        assertThat(eventoBack.getIntegrantes()).doesNotContain(integrante);
    }
}
