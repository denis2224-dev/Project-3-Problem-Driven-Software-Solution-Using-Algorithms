package com.unischeduler.service.dto;

import com.unischeduler.domain.enumeration.CourseEventType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.unischeduler.domain.CourseEvent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseEventDTO implements Serializable {

    private Long id;

    @NotNull
    private CourseEventType eventType;

    @NotNull
    @Min(value = 30)
    @Max(value = 240)
    private Integer durationMinutes;

    @NotNull
    @Min(value = 1)
    private Integer expectedStudents;

    @Size(max = 1000)
    private String requiredEquipment;

    @NotNull
    private CourseDTO course;

    @NotNull
    private ProfessorDTO professor;

    @NotNull
    private StudentGroupDTO studentGroup;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseEventType getEventType() {
        return eventType;
    }

    public void setEventType(CourseEventType eventType) {
        this.eventType = eventType;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getExpectedStudents() {
        return expectedStudents;
    }

    public void setExpectedStudents(Integer expectedStudents) {
        this.expectedStudents = expectedStudents;
    }

    public String getRequiredEquipment() {
        return requiredEquipment;
    }

    public void setRequiredEquipment(String requiredEquipment) {
        this.requiredEquipment = requiredEquipment;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public ProfessorDTO getProfessor() {
        return professor;
    }

    public void setProfessor(ProfessorDTO professor) {
        this.professor = professor;
    }

    public StudentGroupDTO getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(StudentGroupDTO studentGroup) {
        this.studentGroup = studentGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseEventDTO)) {
            return false;
        }

        CourseEventDTO courseEventDTO = (CourseEventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, courseEventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseEventDTO{" +
            "id=" + getId() +
            ", eventType='" + getEventType() + "'" +
            ", durationMinutes=" + getDurationMinutes() +
            ", expectedStudents=" + getExpectedStudents() +
            ", requiredEquipment='" + getRequiredEquipment() + "'" +
            ", course=" + getCourse() +
            ", professor=" + getProfessor() +
            ", studentGroup=" + getStudentGroup() +
            "}";
    }
}
