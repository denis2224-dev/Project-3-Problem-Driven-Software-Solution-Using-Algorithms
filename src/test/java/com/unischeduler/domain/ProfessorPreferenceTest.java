package com.unischeduler.domain;

import static com.unischeduler.domain.ProfessorPreferenceTestSamples.*;
import static com.unischeduler.domain.ProfessorTestSamples.*;
import static com.unischeduler.domain.TimeslotTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfessorPreferenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfessorPreference.class);
        ProfessorPreference professorPreference1 = getProfessorPreferenceSample1();
        ProfessorPreference professorPreference2 = new ProfessorPreference();
        assertThat(professorPreference1).isNotEqualTo(professorPreference2);

        professorPreference2.setId(professorPreference1.getId());
        assertThat(professorPreference1).isEqualTo(professorPreference2);

        professorPreference2 = getProfessorPreferenceSample2();
        assertThat(professorPreference1).isNotEqualTo(professorPreference2);
    }

    @Test
    void professorTest() {
        ProfessorPreference professorPreference = getProfessorPreferenceRandomSampleGenerator();
        Professor professorBack = getProfessorRandomSampleGenerator();

        professorPreference.setProfessor(professorBack);
        assertThat(professorPreference.getProfessor()).isEqualTo(professorBack);

        professorPreference.professor(null);
        assertThat(professorPreference.getProfessor()).isNull();
    }

    @Test
    void timeslotTest() {
        ProfessorPreference professorPreference = getProfessorPreferenceRandomSampleGenerator();
        Timeslot timeslotBack = getTimeslotRandomSampleGenerator();

        professorPreference.setTimeslot(timeslotBack);
        assertThat(professorPreference.getTimeslot()).isEqualTo(timeslotBack);

        professorPreference.timeslot(null);
        assertThat(professorPreference.getTimeslot()).isNull();
    }
}
