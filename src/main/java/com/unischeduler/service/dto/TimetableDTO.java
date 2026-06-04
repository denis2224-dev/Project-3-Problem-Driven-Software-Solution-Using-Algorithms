package com.unischeduler.service.dto;

import com.unischeduler.domain.enumeration.TimetableStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.unischeduler.domain.Timetable} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TimetableDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 160)
    private String name;

    @NotNull
    @Size(max = 40)
    private String semester;

    @NotNull
    @Size(max = 20)
    private String academicYear;

    @NotNull
    private TimetableStatus status;

    @NotNull
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public TimetableStatus getStatus() {
        return status;
    }

    public void setStatus(TimetableStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimetableDTO)) {
            return false;
        }

        TimetableDTO timetableDTO = (TimetableDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, timetableDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimetableDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", semester='" + getSemester() + "'" +
            ", academicYear='" + getAcademicYear() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
