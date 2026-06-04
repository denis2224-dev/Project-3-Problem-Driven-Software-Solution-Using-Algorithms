package com.unischeduler.service.mapper;

import static com.unischeduler.domain.TimetableVersionAsserts.*;
import static com.unischeduler.domain.TimetableVersionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimetableVersionMapperTest {

    private TimetableVersionMapper timetableVersionMapper;

    @BeforeEach
    void setUp() {
        timetableVersionMapper = new TimetableVersionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTimetableVersionSample1();
        var actual = timetableVersionMapper.toEntity(timetableVersionMapper.toDto(expected));
        assertTimetableVersionAllPropertiesEquals(expected, actual);
    }
}
