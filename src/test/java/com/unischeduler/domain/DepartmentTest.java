package com.unischeduler.domain;

import static com.unischeduler.domain.DepartmentTestSamples.*;
import static com.unischeduler.domain.FacultyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DepartmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Department.class);
        Department department1 = getDepartmentSample1();
        Department department2 = new Department();
        assertThat(department1).isNotEqualTo(department2);

        department2.setId(department1.getId());
        assertThat(department1).isEqualTo(department2);

        department2 = getDepartmentSample2();
        assertThat(department1).isNotEqualTo(department2);
    }

    @Test
    void facultyTest() {
        Department department = getDepartmentRandomSampleGenerator();
        Faculty facultyBack = getFacultyRandomSampleGenerator();

        department.setFaculty(facultyBack);
        assertThat(department.getFaculty()).isEqualTo(facultyBack);

        department.faculty(null);
        assertThat(department.getFaculty()).isNull();
    }
}
