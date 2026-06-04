package com.unischeduler.domain;

import static com.unischeduler.domain.CourseEventTestSamples.*;
import static com.unischeduler.domain.CourseTestSamples.*;
import static com.unischeduler.domain.ProfessorTestSamples.*;
import static com.unischeduler.domain.StudentGroupTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseEvent.class);
        CourseEvent courseEvent1 = getCourseEventSample1();
        CourseEvent courseEvent2 = new CourseEvent();
        assertThat(courseEvent1).isNotEqualTo(courseEvent2);

        courseEvent2.setId(courseEvent1.getId());
        assertThat(courseEvent1).isEqualTo(courseEvent2);

        courseEvent2 = getCourseEventSample2();
        assertThat(courseEvent1).isNotEqualTo(courseEvent2);
    }

    @Test
    void courseTest() {
        CourseEvent courseEvent = getCourseEventRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        courseEvent.setCourse(courseBack);
        assertThat(courseEvent.getCourse()).isEqualTo(courseBack);

        courseEvent.course(null);
        assertThat(courseEvent.getCourse()).isNull();
    }

    @Test
    void professorTest() {
        CourseEvent courseEvent = getCourseEventRandomSampleGenerator();
        Professor professorBack = getProfessorRandomSampleGenerator();

        courseEvent.setProfessor(professorBack);
        assertThat(courseEvent.getProfessor()).isEqualTo(professorBack);

        courseEvent.professor(null);
        assertThat(courseEvent.getProfessor()).isNull();
    }

    @Test
    void studentGroupTest() {
        CourseEvent courseEvent = getCourseEventRandomSampleGenerator();
        StudentGroup studentGroupBack = getStudentGroupRandomSampleGenerator();

        courseEvent.setStudentGroup(studentGroupBack);
        assertThat(courseEvent.getStudentGroup()).isEqualTo(studentGroupBack);

        courseEvent.studentGroup(null);
        assertThat(courseEvent.getStudentGroup()).isNull();
    }
}
