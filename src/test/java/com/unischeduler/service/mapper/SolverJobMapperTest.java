package com.unischeduler.service.mapper;

import static com.unischeduler.domain.SolverJobAsserts.*;
import static com.unischeduler.domain.SolverJobTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SolverJobMapperTest {

    private SolverJobMapper solverJobMapper;

    @BeforeEach
    void setUp() {
        solverJobMapper = new SolverJobMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSolverJobSample1();
        var actual = solverJobMapper.toEntity(solverJobMapper.toDto(expected));
        assertSolverJobAllPropertiesEquals(expected, actual);
    }
}
