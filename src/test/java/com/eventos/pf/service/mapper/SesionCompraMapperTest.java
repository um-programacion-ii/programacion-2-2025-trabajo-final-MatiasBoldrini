package com.eventos.pf.service.mapper;

import static com.eventos.pf.domain.SesionCompraAsserts.*;
import static com.eventos.pf.domain.SesionCompraTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SesionCompraMapperTest {

    private SesionCompraMapper sesionCompraMapper;

    @BeforeEach
    void setUp() {
        sesionCompraMapper = new SesionCompraMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSesionCompraSample1();
        var actual = sesionCompraMapper.toEntity(sesionCompraMapper.toDto(expected));
        assertSesionCompraAllPropertiesEquals(expected, actual);
    }
}
