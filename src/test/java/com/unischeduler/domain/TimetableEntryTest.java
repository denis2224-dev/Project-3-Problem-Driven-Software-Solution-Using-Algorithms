package com.unischeduler.domain;

import static com.unischeduler.domain.CourseEventTestSamples.*;
import static com.unischeduler.domain.RoomTestSamples.*;
import static com.unischeduler.domain.TimeslotTestSamples.*;
import static com.unischeduler.domain.TimetableEntryTestSamples.*;
import static com.unischeduler.domain.TimetableVersionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimetableEntryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimetableEntry.class);
        TimetableEntry timetableEntry1 = getTimetableEntrySample1();
        TimetableEntry timetableEntry2 = new TimetableEntry();
        assertThat(timetableEntry1).isNotEqualTo(timetableEntry2);

        timetableEntry2.setId(timetableEntry1.getId());
        assertThat(timetableEntry1).isEqualTo(timetableEntry2);

        timetableEntry2 = getTimetableEntrySample2();
        assertThat(timetableEntry1).isNotEqualTo(timetableEntry2);
    }

    @Test
    void timetableVersionTest() {
        TimetableEntry timetableEntry = getTimetableEntryRandomSampleGenerator();
        TimetableVersion timetableVersionBack = getTimetableVersionRandomSampleGenerator();

        timetableEntry.setTimetableVersion(timetableVersionBack);
        assertThat(timetableEntry.getTimetableVersion()).isEqualTo(timetableVersionBack);

        timetableEntry.timetableVersion(null);
        assertThat(timetableEntry.getTimetableVersion()).isNull();
    }

    @Test
    void courseEventTest() {
        TimetableEntry timetableEntry = getTimetableEntryRandomSampleGenerator();
        CourseEvent courseEventBack = getCourseEventRandomSampleGenerator();

        timetableEntry.setCourseEvent(courseEventBack);
        assertThat(timetableEntry.getCourseEvent()).isEqualTo(courseEventBack);

        timetableEntry.courseEvent(null);
        assertThat(timetableEntry.getCourseEvent()).isNull();
    }

    @Test
    void roomTest() {
        TimetableEntry timetableEntry = getTimetableEntryRandomSampleGenerator();
        Room roomBack = getRoomRandomSampleGenerator();

        timetableEntry.setRoom(roomBack);
        assertThat(timetableEntry.getRoom()).isEqualTo(roomBack);

        timetableEntry.room(null);
        assertThat(timetableEntry.getRoom()).isNull();
    }

    @Test
    void timeslotTest() {
        TimetableEntry timetableEntry = getTimetableEntryRandomSampleGenerator();
        Timeslot timeslotBack = getTimeslotRandomSampleGenerator();

        timetableEntry.setTimeslot(timeslotBack);
        assertThat(timetableEntry.getTimeslot()).isEqualTo(timeslotBack);

        timetableEntry.timeslot(null);
        assertThat(timetableEntry.getTimeslot()).isNull();
    }
}
