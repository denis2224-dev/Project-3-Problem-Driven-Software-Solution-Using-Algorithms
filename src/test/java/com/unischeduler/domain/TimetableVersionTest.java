package com.unischeduler.domain;

import static com.unischeduler.domain.SolverJobTestSamples.*;
import static com.unischeduler.domain.TimetableTestSamples.*;
import static com.unischeduler.domain.TimetableVersionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimetableVersionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimetableVersion.class);
        TimetableVersion timetableVersion1 = getTimetableVersionSample1();
        TimetableVersion timetableVersion2 = new TimetableVersion();
        assertThat(timetableVersion1).isNotEqualTo(timetableVersion2);

        timetableVersion2.setId(timetableVersion1.getId());
        assertThat(timetableVersion1).isEqualTo(timetableVersion2);

        timetableVersion2 = getTimetableVersionSample2();
        assertThat(timetableVersion1).isNotEqualTo(timetableVersion2);
    }

    @Test
    void timetableTest() {
        TimetableVersion timetableVersion = getTimetableVersionRandomSampleGenerator();
        Timetable timetableBack = getTimetableRandomSampleGenerator();

        timetableVersion.setTimetable(timetableBack);
        assertThat(timetableVersion.getTimetable()).isEqualTo(timetableBack);

        timetableVersion.timetable(null);
        assertThat(timetableVersion.getTimetable()).isNull();
    }

    @Test
    void solverJobTest() {
        TimetableVersion timetableVersion = getTimetableVersionRandomSampleGenerator();
        SolverJob solverJobBack = getSolverJobRandomSampleGenerator();

        timetableVersion.setSolverJob(solverJobBack);
        assertThat(timetableVersion.getSolverJob()).isEqualTo(solverJobBack);

        timetableVersion.solverJob(null);
        assertThat(timetableVersion.getSolverJob()).isNull();
    }
}
