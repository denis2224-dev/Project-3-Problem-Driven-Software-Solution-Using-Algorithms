package com.unischeduler.service.mapper;

import static com.unischeduler.domain.TimeslotAsserts.*;
import static com.unischeduler.domain.TimeslotTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimeslotMapperTest {

    private TimeslotMapper timeslotMapper;

    @BeforeEach
    void setUp() {
        timeslotMapper = new TimeslotMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTimeslotSample1();
        var actual = timeslotMapper.toEntity(timeslotMapper.toDto(expected));
        assertTimeslotAllPropertiesEquals(expected, actual);
    }
}
