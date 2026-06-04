package com.unischeduler.service.mapper;

import static com.unischeduler.domain.FacultyAsserts.*;
import static com.unischeduler.domain.FacultyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FacultyMapperTest {

    private FacultyMapper facultyMapper;

    @BeforeEach
    void setUp() {
        facultyMapper = new FacultyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFacultySample1();
        var actual = facultyMapper.toEntity(facultyMapper.toDto(expected));
        assertFacultyAllPropertiesEquals(expected, actual);
    }
}
