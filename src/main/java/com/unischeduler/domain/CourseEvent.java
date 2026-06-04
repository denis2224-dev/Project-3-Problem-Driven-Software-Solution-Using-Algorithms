package com.unischeduler.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unischeduler.domain.enumeration.CourseEventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CourseEvent.
 */
@Entity
@Table(name = "course_event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private CourseEventType eventType;

    @NotNull
    @Min(value = 30)
    @Max(value = 240)
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @NotNull
    @Min(value = 1)
    @Column(name = "expected_students", nullable = false)
    private Integer expectedStudents;

    @Size(max = 1000)
    @Column(name = "required_equipment", length = 1000)
    private String requiredEquipment;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "department" }, allowSetters = true)
    private Course course;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "department" }, allowSetters = true)
    private Professor professor;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "department" }, allowSetters = true)
    private StudentGroup studentGroup;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CourseEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseEventType getEventType() {
        return this.eventType;
    }

    public CourseEvent eventType(CourseEventType eventType) {
        this.setEventType(eventType);
        return this;
    }

    public void setEventType(CourseEventType eventType) {
        this.eventType = eventType;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public CourseEvent durationMinutes(Integer durationMinutes) {
        this.setDurationMinutes(durationMinutes);
        return this;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getExpectedStudents() {
        return this.expectedStudents;
    }

    public CourseEvent expectedStudents(Integer expectedStudents) {
        this.setExpectedStudents(expectedStudents);
        return this;
    }

    public void setExpectedStudents(Integer expectedStudents) {
        this.expectedStudents = expectedStudents;
    }

    public String getRequiredEquipment() {
        return this.requiredEquipment;
    }

    public CourseEvent requiredEquipment(String requiredEquipment) {
        this.setRequiredEquipment(requiredEquipment);
        return this;
    }

    public void setRequiredEquipment(String requiredEquipment) {
        this.requiredEquipment = requiredEquipment;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CourseEvent course(Course course) {
        this.setCourse(course);
        return this;
    }

    public Professor getProfessor() {
        return this.professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public CourseEvent professor(Professor professor) {
        this.setProfessor(professor);
        return this;
    }

    public StudentGroup getStudentGroup() {
        return this.studentGroup;
    }

    public void setStudentGroup(StudentGroup studentGroup) {
        this.studentGroup = studentGroup;
    }

    public CourseEvent studentGroup(StudentGroup studentGroup) {
        this.setStudentGroup(studentGroup);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseEvent)) {
            return false;
        }
        return getId() != null && getId().equals(((CourseEvent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseEvent{" +
            "id=" + getId() +
            ", eventType='" + getEventType() + "'" +
            ", durationMinutes=" + getDurationMinutes() +
            ", expectedStudents=" + getExpectedStudents() +
            ", requiredEquipment='" + getRequiredEquipment() + "'" +
            "}";
    }
}
