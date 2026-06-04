package com.unischeduler.service.mapper;

import static com.unischeduler.domain.ExamScheduleEntryAsserts.*;
import static com.unischeduler.domain.ExamScheduleEntryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExamScheduleEntryMapperTest {

    private ExamScheduleEntryMapper examScheduleEntryMapper;

    @BeforeEach
    void setUp() {
        examScheduleEntryMapper = new ExamScheduleEntryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getExamScheduleEntrySample1();
        var actual = examScheduleEntryMapper.toEntity(examScheduleEntryMapper.toDto(expected));
        assertExamScheduleEntryAllPropertiesEquals(expected, actual);
    }
}
