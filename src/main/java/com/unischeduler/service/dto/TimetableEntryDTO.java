package com.unischeduler.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.unischeduler.domain.TimetableEntry} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TimetableEntryDTO implements Serializable {

    private Long id;

    @NotNull
    private TimetableVersionDTO timetableVersion;

    @NotNull
    private CourseEventDTO courseEvent;

    @NotNull
    private RoomDTO room;

    @NotNull
    private TimeslotDTO timeslot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TimetableVersionDTO getTimetableVersion() {
        return timetableVersion;
    }

    public void setTimetableVersion(TimetableVersionDTO timetableVersion) {
        this.timetableVersion = timetableVersion;
    }

    public CourseEventDTO getCourseEvent() {
        return courseEvent;
    }

    public void setCourseEvent(CourseEventDTO courseEvent) {
        this.courseEvent = courseEvent;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimetableEntryDTO)) {
            return false;
        }

        TimetableEntryDTO timetableEntryDTO = (TimetableEntryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, timetableEntryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimetableEntryDTO{" +
            "id=" + getId() +
            ", timetableVersion=" + getTimetableVersion() +
            ", courseEvent=" + getCourseEvent() +
            ", room=" + getRoom() +
            ", timeslot=" + getTimeslot() +
            "}";
    }
}
