package com.unischeduler.service.dto;

import com.unischeduler.domain.enumeration.ProfessorPreferenceType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.unischeduler.domain.ProfessorPreference} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProfessorPreferenceDTO implements Serializable {

    private Long id;

    @NotNull
    private ProfessorPreferenceType preferenceType;

    @NotNull
    private ProfessorDTO professor;

    @NotNull
    private TimeslotDTO timeslot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProfessorPreferenceType getPreferenceType() {
        return preferenceType;
    }

    public void setPreferenceType(ProfessorPreferenceType preferenceType) {
        this.preferenceType = preferenceType;
    }

    public ProfessorDTO getProfessor() {
        return professor;
    }

    public void setProfessor(ProfessorDTO professor) {
        this.professor = professor;
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
        if (!(o instanceof ProfessorPreferenceDTO)) {
            return false;
        }

        ProfessorPreferenceDTO professorPreferenceDTO = (ProfessorPreferenceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, professorPreferenceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfessorPreferenceDTO{" +
            "id=" + getId() +
            ", preferenceType='" + getPreferenceType() + "'" +
            ", professor=" + getProfessor() +
            ", timeslot=" + getTimeslot() +
            "}";
    }
}
