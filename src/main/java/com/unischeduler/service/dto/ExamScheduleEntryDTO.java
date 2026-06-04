package com.unischeduler.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.unischeduler.domain.ExamScheduleEntry} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamScheduleEntryDTO implements Serializable {

    private Long id;

    @NotNull
    private ExamDTO exam;

    @NotNull
    private RoomDTO room;

    @NotNull
    private TimeslotDTO timeslot;

    @NotNull
    private TimetableVersionDTO timetableVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExamDTO getExam() {
        return exam;
    }

    public void setExam(ExamDTO exam) {
        this.exam = exam;
    }

    public RoomDTO getRoom() {
        return room;
    }

    public void setRoom(RoomDTO room) {
        this.room = room;
    }

    public TimeslotDTO getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(TimeslotDTO timeslot) {
        this.timeslot = timeslot;
    }

    public TimetableVersionDTO getTimetableVersion() {
        return timetableVersion;
    }

    public void setTimetableVersion(TimetableVersionDTO timetableVersion) {
        this.timetableVersion = timetableVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamScheduleEntryDTO)) {
            return false;
        }

        ExamScheduleEntryDTO examScheduleEntryDTO = (ExamScheduleEntryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, examScheduleEntryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamScheduleEntryDTO{" +
            "id=" + getId() +
            ", exam=" + getExam() +
            ", room=" + getRoom() +
            ", timeslot=" + getTimeslot() +
            ", timetableVersion=" + getTimetableVersion() +
            "}";
    }
}
