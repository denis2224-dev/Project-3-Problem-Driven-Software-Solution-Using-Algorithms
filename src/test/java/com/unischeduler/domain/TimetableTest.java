package com.unischeduler.domain;

import static com.unischeduler.domain.TimetableTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimetableTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Timetable.class);
        Timetable timetable1 = getTimetableSample1();
        Timetable timetable2 = new Timetable();
        assertThat(timetable1).isNotEqualTo(timetable2);

        timetable2.setId(timetable1.getId());
        assertThat(timetable1).isEqualTo(timetable2);

        timetable2 = getTimetableSample2();
        assertThat(timetable1).isNotEqualTo(timetable2);
    }
}
