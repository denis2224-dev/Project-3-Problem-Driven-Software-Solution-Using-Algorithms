package com.unischeduler.domain;

import static com.unischeduler.domain.ExamScheduleEntryTestSamples.*;
import static com.unischeduler.domain.ExamTestSamples.*;
import static com.unischeduler.domain.RoomTestSamples.*;
import static com.unischeduler.domain.TimeslotTestSamples.*;
import static com.unischeduler.domain.TimetableVersionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamScheduleEntryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExamScheduleEntry.class);
        ExamScheduleEntry examScheduleEntry1 = getExamScheduleEntrySample1();
        ExamScheduleEntry examScheduleEntry2 = new ExamScheduleEntry();
        assertThat(examScheduleEntry1).isNotEqualTo(examScheduleEntry2);

        examScheduleEntry2.setId(examScheduleEntry1.getId());
        assertThat(examScheduleEntry1).isEqualTo(examScheduleEntry2);

        examScheduleEntry2 = getExamScheduleEntrySample2();
        assertThat(examScheduleEntry1).isNotEqualTo(examScheduleEntry2);
    }

    @Test
    void examTest() {
        ExamScheduleEntry examScheduleEntry = getExamScheduleEntryRandomSampleGenerator();
        Exam examBack = getExamRandomSampleGenerator();

        examScheduleEntry.setExam(examBack);
        assertThat(examScheduleEntry.getExam()).isEqualTo(examBack);

        examScheduleEntry.exam(null);
        assertThat(examScheduleEntry.getExam()).isNull();
    }

    @Test
    void roomTest() {
        ExamScheduleEntry examScheduleEntry = getExamScheduleEntryRandomSampleGenerator();
        Room roomBack = getRoomRandomSampleGenerator();

        examScheduleEntry.setRoom(roomBack);
        assertThat(examScheduleEntry.getRoom()).isEqualTo(roomBack);

        examScheduleEntry.room(null);
        assertThat(examScheduleEntry.getRoom()).isNull();
    }

    @Test
    void timeslotTest() {
        ExamScheduleEntry examScheduleEntry = getExamScheduleEntryRandomSampleGenerator();
        Timeslot timeslotBack = getTimeslotRandomSampleGenerator();

        examScheduleEntry.setTimeslot(timeslotBack);
        assertThat(examScheduleEntry.getTimeslot()).isEqualTo(timeslotBack);

        examScheduleEntry.timeslot(null);
        assertThat(examScheduleEntry.getTimeslot()).isNull();
    }

    @Test
    void timetableVersionTest() {
        ExamScheduleEntry examScheduleEntry = getExamScheduleEntryRandomSampleGenerator();
        TimetableVersion timetableVersionBack = getTimetableVersionRandomSampleGenerator();

        examScheduleEntry.setTimetableVersion(timetableVersionBack);
        assertThat(examScheduleEntry.getTimetableVersion()).isEqualTo(timetableVersionBack);

        examScheduleEntry.timetableVersion(null);
        assertThat(examScheduleEntry.getTimetableVersion()).isNull();
    }
}
