package com.unischeduler.domain;

import static com.unischeduler.domain.ScheduleConflictTestSamples.*;
import static com.unischeduler.domain.TimetableVersionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScheduleConflictTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduleConflict.class);
        ScheduleConflict scheduleConflict1 = getScheduleConflictSample1();
        ScheduleConflict scheduleConflict2 = new ScheduleConflict();
        assertThat(scheduleConflict1).isNotEqualTo(scheduleConflict2);

        scheduleConflict2.setId(scheduleConflict1.getId());
        assertThat(scheduleConflict1).isEqualTo(scheduleConflict2);

        scheduleConflict2 = getScheduleConflictSample2();
        assertThat(scheduleConflict1).isNotEqualTo(scheduleConflict2);
    }

    @Test
    void timetableVersionTest() {
        ScheduleConflict scheduleConflict = getScheduleConflictRandomSampleGenerator();
        TimetableVersion timetableVersionBack = getTimetableVersionRandomSampleGenerator();

        scheduleConflict.setTimetableVersion(timetableVersionBack);
        assertThat(scheduleConflict.getTimetableVersion()).isEqualTo(timetableVersionBack);

        scheduleConflict.timetableVersion(null);
        assertThat(scheduleConflict.getTimetableVersion()).isNull();
    }
}
