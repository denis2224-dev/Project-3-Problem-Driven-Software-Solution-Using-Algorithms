package com.unischeduler.service.mapper;

import static com.unischeduler.domain.CourseEventAsserts.*;
import static com.unischeduler.domain.CourseEventTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseEventMapperTest {

    private CourseEventMapper courseEventMapper;

    @BeforeEach
    void setUp() {
        courseEventMapper = new CourseEventMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCourseEventSample1();
        var actual = courseEventMapper.toEntity(courseEventMapper.toDto(expected));
        assertCourseEventAllPropertiesEquals(expected, actual);
    }
}
