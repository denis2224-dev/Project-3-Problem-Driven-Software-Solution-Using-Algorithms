package com.unischeduler.service.mapper;

import static com.unischeduler.domain.StudentGroupAsserts.*;
import static com.unischeduler.domain.StudentGroupTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentGroupMapperTest {

    private StudentGroupMapper studentGroupMapper;

    @BeforeEach
    void setUp() {
        studentGroupMapper = new StudentGroupMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStudentGroupSample1();
        var actual = studentGroupMapper.toEntity(studentGroupMapper.toDto(expected));
        assertStudentGroupAllPropertiesEquals(expected, actual);
    }
}
