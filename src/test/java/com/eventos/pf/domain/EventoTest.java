package com.eventos.pf.domain;

import static com.eventos.pf.domain.EventoTestSamples.*;
import static com.eventos.pf.domain.IntegranteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.eventos.pf.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EventoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Evento.class);
        Evento evento1 = getEventoSample1();
        Evento evento2 = new Evento();
        assertThat(evento1).isNotEqualTo(evento2);

        evento2.setId(evento1.getId());
        assertThat(evento1).isEqualTo(evento2);

        evento2 = getEventoSample2();
        assertThat(evento1).isNotEqualTo(evento2);
    }

    @Test
    void integrantesTest() {
        Evento evento = getEventoRandomSampleGenerator();
        Integrante integranteBack = getIntegranteRandomSampleGenerator();

        evento.addIntegrantes(integranteBack);
        assertThat(evento.getIntegrantes()).containsOnly(integranteBack);

        evento.removeIntegrantes(integranteBack);
        assertThat(evento.getIntegrantes()).doesNotContain(integranteBack);

        evento.integrantes(new HashSet<>(Set.of(integranteBack)));
        assertThat(evento.getIntegrantes()).containsOnly(integranteBack);

        evento.setIntegrantes(new HashSet<>());
        assertThat(evento.getIntegrantes()).doesNotContain(integranteBack);
    }
}
