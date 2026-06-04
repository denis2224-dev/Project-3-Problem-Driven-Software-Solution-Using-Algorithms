package com.unischeduler.service.mapper;

import static com.unischeduler.domain.ScheduleConflictAsserts.*;
import static com.unischeduler.domain.ScheduleConflictTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScheduleConflictMapperTest {

    private ScheduleConflictMapper scheduleConflictMapper;

    @BeforeEach
    void setUp() {
        scheduleConflictMapper = new ScheduleConflictMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getScheduleConflictSample1();
        var actual = scheduleConflictMapper.toEntity(scheduleConflictMapper.toDto(expected));
        assertScheduleConflictAllPropertiesEquals(expected, actual);
    }
}
