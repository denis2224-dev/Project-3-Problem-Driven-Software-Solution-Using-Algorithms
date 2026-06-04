package com.unischeduler.service.mapper;

import static com.unischeduler.domain.ProfessorPreferenceAsserts.*;
import static com.unischeduler.domain.ProfessorPreferenceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfessorPreferenceMapperTest {

    private ProfessorPreferenceMapper professorPreferenceMapper;

    @BeforeEach
    void setUp() {
        professorPreferenceMapper = new ProfessorPreferenceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProfessorPreferenceSample1();
        var actual = professorPreferenceMapper.toEntity(professorPreferenceMapper.toDto(expected));
        assertProfessorPreferenceAllPropertiesEquals(expected, actual);
    }
}
