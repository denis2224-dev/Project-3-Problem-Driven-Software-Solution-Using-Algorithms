package com.unischeduler.service.dto;

import com.unischeduler.domain.enumeration.AcademicDayOfWeek;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.unischeduler.domain.Timeslot} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TimeslotDTO implements Serializable {

    private Long id;

    @NotNull
    private AcademicDayOfWeek dayOfWeek;

    @NotNull
    @Size(min = 5, max = 5)
    private String startTime;

    @NotNull
    @Size(min = 5, max = 5)
    private String endTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AcademicDayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(AcademicDayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeslotDTO)) {
            return false;
        }

        TimeslotDTO timeslotDTO = (TimeslotDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, timeslotDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimeslotDTO{" +
            "id=" + getId() +
            ", dayOfWeek='" + getDayOfWeek() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }
}
