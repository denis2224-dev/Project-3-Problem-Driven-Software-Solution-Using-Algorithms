package com.unischeduler.domain;

import static com.unischeduler.domain.CourseTestSamples.*;
import static com.unischeduler.domain.DepartmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Course.class);
        Course course1 = getCourseSample1();
        Course course2 = new Course();
        assertThat(course1).isNotEqualTo(course2);

        course2.setId(course1.getId());
        assertThat(course1).isEqualTo(course2);

        course2 = getCourseSample2();
        assertThat(course1).isNotEqualTo(course2);
    }

    @Test
    void departmentTest() {
        Course course = getCourseRandomSampleGenerator();
        Department departmentBack = getDepartmentRandomSampleGenerator();

        course.setDepartment(departmentBack);
        assertThat(course.getDepartment()).isEqualTo(departmentBack);

        course.department(null);
        assertThat(course.getDepartment()).isNull();
    }
}
