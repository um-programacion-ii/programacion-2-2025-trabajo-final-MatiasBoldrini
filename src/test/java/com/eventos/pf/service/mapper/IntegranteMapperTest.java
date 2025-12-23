package com.eventos.pf.service.mapper;

import static com.eventos.pf.domain.IntegranteAsserts.*;
import static com.eventos.pf.domain.IntegranteTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntegranteMapperTest {

    private IntegranteMapper integranteMapper;

    @BeforeEach
    void setUp() {
        integranteMapper = new IntegranteMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getIntegranteSample1();
        var actual = integranteMapper.toEntity(integranteMapper.toDto(expected));
        assertIntegranteAllPropertiesEquals(expected, actual);
    }
}
