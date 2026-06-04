package com.unischeduler.domain;

import static com.unischeduler.domain.DepartmentTestSamples.*;
import static com.unischeduler.domain.ProfessorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfessorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Professor.class);
        Professor professor1 = getProfessorSample1();
        Professor professor2 = new Professor();
        assertThat(professor1).isNotEqualTo(professor2);

        professor2.setId(professor1.getId());
        assertThat(professor1).isEqualTo(professor2);

        professor2 = getProfessorSample2();
        assertThat(professor1).isNotEqualTo(professor2);
    }

    @Test
    void departmentTest() {
        Professor professor = getProfessorRandomSampleGenerator();
        Department departmentBack = getDepartmentRandomSampleGenerator();

        professor.setDepartment(departmentBack);
        assertThat(professor.getDepartment()).isEqualTo(departmentBack);

        professor.department(null);
        assertThat(professor.getDepartment()).isNull();
    }
}
