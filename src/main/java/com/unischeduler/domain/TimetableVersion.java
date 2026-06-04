package com.unischeduler.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TimetableVersion.
 */
@Entity
@Table(name = "timetable_version")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TimetableVersion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Min(value = 0)
    @Column(name = "total_hard_conflicts")
    private Integer totalHardConflicts;

    @Min(value = 0)
    @Column(name = "total_soft_penalty")
    private Integer totalSoftPenalty;

    @DecimalMin(value = "0")
    @Column(name = "average_student_gap")
    private Double averageStudentGap;

    @DecimalMin(value = "0")
    @Column(name = "room_utilization")
    private Double roomUtilization;

    @ManyToOne(optional = false)
    @NotNull
    private Timetable timetable;

    @ManyToOne(fetch = FetchType.LAZY)
    private SolverJob solverJob;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TimetableVersion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersionNumber() {
        return this.versionNumber;
    }

    public TimetableVersion versionNumber(Integer versionNumber) {
        this.setVersionNumber(versionNumber);
        return this;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public TimetableVersion createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTotalHardConflicts() {
        return this.totalHardConflicts;
    }

    public TimetableVersion totalHardConflicts(Integer totalHardConflicts) {
        this.setTotalHardConflicts(totalHardConflicts);
        return this;
    }

    public void setTotalHardConflicts(Integer totalHardConflicts) {
        this.totalHardConflicts = totalHardConflicts;
    }

    public Integer getTotalSoftPenalty() {
        return this.totalSoftPenalty;
    }

    public TimetableVersion totalSoftPenalty(Integer totalSoftPenalty) {
        this.setTotalSoftPenalty(totalSoftPenalty);
        return this;
    }

    public void setTotalSoftPenalty(Integer totalSoftPenalty) {
        this.totalSoftPenalty = totalSoftPenalty;
    }

    public Double getAverageStudentGap() {
        return this.averageStudentGap;
    }

    public TimetableVersion averageStudentGap(Double averageStudentGap) {
        this.setAverageStudentGap(averageStudentGap);
        return this;
    }

    public void setAverageStudentGap(Double averageStudentGap) {
        this.averageStudentGap = averageStudentGap;
    }

    public Double getRoomUtilization() {
        return this.roomUtilization;
    }

    public TimetableVersion roomUtilization(Double roomUtilization) {
        this.setRoomUtilization(roomUtilization);
        return this;
    }

    public void setRoomUtilization(Double roomUtilization) {
        this.roomUtilization = roomUtilization;
    }

    public Timetable getTimetable() {
        return this.timetable;
    }

    public void setTimetable(Timetable timetable) {
        this.timetable = timetable;
    }

    public TimetableVersion timetable(Timetable timetable) {
        this.setTimetable(timetable);
        return this;
    }

    public SolverJob getSolverJob() {
        return this.solverJob;
    }

    public void setSolverJob(SolverJob solverJob) {
        this.solverJob = solverJob;
    }

    public TimetableVersion solverJob(SolverJob solverJob) {
        this.setSolverJob(solverJob);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimetableVersion)) {
            return false;
        }
        return getId() != null && getId().equals(((TimetableVersion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimetableVersion{" +
            "id=" + getId() +
            ", versionNumber=" + getVersionNumber() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", totalHardConflicts=" + getTotalHardConflicts() +
            ", totalSoftPenalty=" + getTotalSoftPenalty() +
            ", averageStudentGap=" + getAverageStudentGap() +
            ", roomUtilization=" + getRoomUtilization() +
            "}";
    }
}
