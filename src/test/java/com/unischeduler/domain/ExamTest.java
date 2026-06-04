package com.unischeduler.domain;

import static com.unischeduler.domain.CourseTestSamples.*;
import static com.unischeduler.domain.ExamTestSamples.*;
import static com.unischeduler.domain.StudentGroupTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Exam.class);
        Exam exam1 = getExamSample1();
        Exam exam2 = new Exam();
        assertThat(exam1).isNotEqualTo(exam2);

        exam2.setId(exam1.getId());
        assertThat(exam1).isEqualTo(exam2);

        exam2 = getExamSample2();
        assertThat(exam1).isNotEqualTo(exam2);
    }

    @Test
    void courseTest() {
        Exam exam = getExamRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        exam.setCourse(courseBack);
        assertThat(exam.getCourse()).isEqualTo(courseBack);

        exam.course(null);
        assertThat(exam.getCourse()).isNull();
    }

    @Test
    void studentGroupTest() {
        Exam exam = getExamRandomSampleGenerator();
        StudentGroup studentGroupBack = getStudentGroupRandomSampleGenerator();

        exam.setStudentGroup(studentGroupBack);
        assertThat(exam.getStudentGroup()).isEqualTo(studentGroupBack);

        exam.studentGroup(null);
        assertThat(exam.getStudentGroup()).isNull();
    }
}
