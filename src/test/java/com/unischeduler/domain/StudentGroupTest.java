package com.unischeduler.domain;

import static com.unischeduler.domain.DepartmentTestSamples.*;
import static com.unischeduler.domain.StudentGroupTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudentGroupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudentGroup.class);
        StudentGroup studentGroup1 = getStudentGroupSample1();
        StudentGroup studentGroup2 = new StudentGroup();
        assertThat(studentGroup1).isNotEqualTo(studentGroup2);

        studentGroup2.setId(studentGroup1.getId());
        assertThat(studentGroup1).isEqualTo(studentGroup2);

        studentGroup2 = getStudentGroupSample2();
        assertThat(studentGroup1).isNotEqualTo(studentGroup2);
    }

    @Test
    void departmentTest() {
        StudentGroup studentGroup = getStudentGroupRandomSampleGenerator();
        Department departmentBack = getDepartmentRandomSampleGenerator();

        studentGroup.setDepartment(departmentBack);
        assertThat(studentGroup.getDepartment()).isEqualTo(departmentBack);

        studentGroup.department(null);
        assertThat(studentGroup.getDepartment()).isNull();
    }
}
