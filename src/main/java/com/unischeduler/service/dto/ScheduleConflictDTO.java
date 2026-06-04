package com.unischeduler.service.dto;

import com.unischeduler.domain.enumeration.ConflictSeverity;
import com.unischeduler.domain.enumeration.ConflictType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.unischeduler.domain.ScheduleConflict} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleConflictDTO implements Serializable {

    private Long id;

    @NotNull
    private ConflictType conflictType;

    @NotNull
    @Size(max = 2000)
    private String description;

    @NotNull
    private ConflictSeverity severity;

    @NotNull
    private TimetableVersionDTO timetableVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConflictType getConflictType() {
        return conflictType;
    }

    public void setConflictType(ConflictType conflictType) {
        this.conflictType = conflictType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ConflictSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(ConflictSeverity severity) {
        this.severity = severity;
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
        if (!(o instanceof ScheduleConflictDTO)) {
            return false;
        }

        ScheduleConflictDTO scheduleConflictDTO = (ScheduleConflictDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, scheduleConflictDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleConflictDTO{" +
            "id=" + getId() +
            ", conflictType='" + getConflictType() + "'" +
            ", description='" + getDescription() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", timetableVersion=" + getTimetableVersion() +
            "}";
    }
}
