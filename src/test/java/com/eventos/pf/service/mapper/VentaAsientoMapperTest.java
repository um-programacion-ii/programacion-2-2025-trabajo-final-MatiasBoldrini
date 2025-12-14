package com.eventos.pf.service.mapper;

import static com.eventos.pf.domain.VentaAsientoAsserts.*;
import static com.eventos.pf.domain.VentaAsientoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VentaAsientoMapperTest {

    private VentaAsientoMapper ventaAsientoMapper;

    @BeforeEach
    void setUp() {
        ventaAsientoMapper = new VentaAsientoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getVentaAsientoSample1();
        var actual = ventaAsientoMapper.toEntity(ventaAsientoMapper.toDto(expected));
        assertVentaAsientoAllPropertiesEquals(expected, actual);
    }
}
