package com.unischeduler.service.mapper;

import static com.unischeduler.domain.TimetableEntryAsserts.*;
import static com.unischeduler.domain.TimetableEntryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimetableEntryMapperTest {

    private TimetableEntryMapper timetableEntryMapper;

    @BeforeEach
    void setUp() {
        timetableEntryMapper = new TimetableEntryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTimetableEntrySample1();
        var actual = timetableEntryMapper.toEntity(timetableEntryMapper.toDto(expected));
        assertTimetableEntryAllPropertiesEquals(expected, actual);
    }
}
