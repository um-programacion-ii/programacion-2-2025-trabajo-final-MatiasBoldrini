package com.eventos.pf.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.eventos.pf.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SesionCompraDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SesionCompraDTO.class);
        SesionCompraDTO sesionCompraDTO1 = new SesionCompraDTO();
        sesionCompraDTO1.setId(1L);
        SesionCompraDTO sesionCompraDTO2 = new SesionCompraDTO();
        assertThat(sesionCompraDTO1).isNotEqualTo(sesionCompraDTO2);
        sesionCompraDTO2.setId(sesionCompraDTO1.getId());
        assertThat(sesionCompraDTO1).isEqualTo(sesionCompraDTO2);
        sesionCompraDTO2.setId(2L);
        assertThat(sesionCompraDTO1).isNotEqualTo(sesionCompraDTO2);
        sesionCompraDTO1.setId(null);
        assertThat(sesionCompraDTO1).isNotEqualTo(sesionCompraDTO2);
    }
}
