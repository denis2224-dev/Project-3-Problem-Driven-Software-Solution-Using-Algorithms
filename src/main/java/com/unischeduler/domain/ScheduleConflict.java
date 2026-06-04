package com.unischeduler.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unischeduler.domain.enumeration.ConflictSeverity;
import com.unischeduler.domain.enumeration.ConflictType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ScheduleConflict.
 */
@Entity
@Table(name = "schedule_conflict")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleConflict implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "conflict_type", nullable = false)
    private ConflictType conflictType;

    @NotNull
    @Size(max = 2000)
    @Column(name = "description", length = 2000, nullable = false)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private ConflictSeverity severity;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "timetable", "solverJob" }, allowSetters = true)
    private TimetableVersion timetableVersion;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ScheduleConflict id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConflictType getConflictType() {
        return this.conflictType;
    }

    public ScheduleConflict conflictType(ConflictType conflictType) {
        this.setConflictType(conflictType);
        return this;
    }

    public void setConflictType(ConflictType conflictType) {
        this.conflictType = conflictType;
    }

    public String getDescription() {
        return this.description;
    }

    public ScheduleConflict description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ConflictSeverity getSeverity() {
        return this.severity;
    }

    public ScheduleConflict severity(ConflictSeverity severity) {
        this.setSeverity(severity);
        return this;
    }

    public void setSeverity(ConflictSeverity severity) {
        this.severity = severity;
    }

    public TimetableVersion getTimetableVersion() {
        return this.timetableVersion;
    }

    public void setTimetableVersion(TimetableVersion timetableVersion) {
        this.timetableVersion = timetableVersion;
    }

    public ScheduleConflict timetableVersion(TimetableVersion timetableVersion) {
        this.setTimetableVersion(timetableVersion);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduleConflict)) {
            return false;
        }
        return getId() != null && getId().equals(((ScheduleConflict) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleConflict{" +
            "id=" + getId() +
            ", conflictType='" + getConflictType() + "'" +
            ", description='" + getDescription() + "'" +
            ", severity='" + getSeverity() + "'" +
            "}";
    }
}
