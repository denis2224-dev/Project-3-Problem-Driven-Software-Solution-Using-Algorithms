package com.unischeduler.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.unischeduler.domain.TimetableVersion} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TimetableVersionDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer versionNumber;

    @NotNull
    private Instant createdAt;

    @Min(value = 0)
    private Integer totalHardConflicts;

    @Min(value = 0)
    private Integer totalSoftPenalty;

    @DecimalMin(value = "0")
    private Double averageStudentGap;

    @DecimalMin(value = "0")
    private Double roomUtilization;

    @NotNull
    private TimetableDTO timetable;

    private SolverJobDTO solverJob;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTotalHardConflicts() {
        return totalHardConflicts;
    }

    public void setTotalHardConflicts(Integer totalHardConflicts) {
        this.totalHardConflicts = totalHardConflicts;
    }

    public Integer getTotalSoftPenalty() {
        return totalSoftPenalty;
    }

    public void setTotalSoftPenalty(Integer totalSoftPenalty) {
        this.totalSoftPenalty = totalSoftPenalty;
    }

    public Double getAverageStudentGap() {
        return averageStudentGap;
    }

    public void setAverageStudentGap(Double averageStudentGap) {
        this.averageStudentGap = averageStudentGap;
    }

    public Double getRoomUtilization() {
        return roomUtilization;
    }

    public void setRoomUtilization(Double roomUtilization) {
        this.roomUtilization = roomUtilization;
    }

    public TimetableDTO getTimetable() {
        return timetable;
    }

    public void setTimetable(TimetableDTO timetable) {
        this.timetable = timetable;
    }

    public SolverJobDTO getSolverJob() {
        return solverJob;
    }

    public void setSolverJob(SolverJobDTO solverJob) {
        this.solverJob = solverJob;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimetableVersionDTO)) {
            return false;
        }

        TimetableVersionDTO timetableVersionDTO = (TimetableVersionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, timetableVersionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimetableVersionDTO{" +
            "id=" + getId() +
            ", versionNumber=" + getVersionNumber() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", totalHardConflicts=" + getTotalHardConflicts() +
            ", totalSoftPenalty=" + getTotalSoftPenalty() +
            ", averageStudentGap=" + getAverageStudentGap() +
            ", roomUtilization=" + getRoomUtilization() +
            ", timetable=" + getTimetable() +
            ", solverJob=" + getSolverJob() +
            "}";
    }
}
