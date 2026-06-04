package com.unischeduler.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unischeduler.domain.enumeration.ProfessorPreferenceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ProfessorPreference.
 */
@Entity
@Table(name = "professor_preference")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProfessorPreference implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "preference_type", nullable = false)
    private ProfessorPreferenceType preferenceType;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "department" }, allowSetters = true)
    private Professor professor;

    @ManyToOne(optional = false)
    @NotNull
    private Timeslot timeslot;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProfessorPreference id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProfessorPreferenceType getPreferenceType() {
        return this.preferenceType;
    }

    public ProfessorPreference preferenceType(ProfessorPreferenceType preferenceType) {
        this.setPreferenceType(preferenceType);
        return this;
    }

    public void setPreferenceType(ProfessorPreferenceType preferenceType) {
        this.preferenceType = preferenceType;
    }

    public Professor getProfessor() {
        return this.professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public ProfessorPreference professor(Professor professor) {
        this.setProfessor(professor);
        return this;
    }

    public Timeslot getTimeslot() {
        return this.timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public ProfessorPreference timeslot(Timeslot timeslot) {
        this.setTimeslot(timeslot);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfessorPreference)) {
            return false;
        }
        return getId() != null && getId().equals(((ProfessorPreference) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfessorPreference{" +
            "id=" + getId() +
            ", preferenceType='" + getPreferenceType() + "'" +
            "}";
    }
}
