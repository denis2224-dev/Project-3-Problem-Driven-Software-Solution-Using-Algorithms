package com.unischeduler.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ExamScheduleEntry.
 */
@Entity
@Table(name = "exam_schedule_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamScheduleEntry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "course", "studentGroup" }, allowSetters = true)
    private Exam exam;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "building" }, allowSetters = true)
    private Room room;

    @ManyToOne(optional = false)
    @NotNull
    private Timeslot timeslot;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "timetable", "solverJob" }, allowSetters = true)
    private TimetableVersion timetableVersion;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ExamScheduleEntry id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exam getExam() {
        return this.exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public ExamScheduleEntry exam(Exam exam) {
        this.setExam(exam);
        return this;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ExamScheduleEntry room(Room room) {
        this.setRoom(room);
        return this;
    }

    public Timeslot getTimeslot() {
        return this.timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public ExamScheduleEntry timeslot(Timeslot timeslot) {
        this.setTimeslot(timeslot);
        return this;
    }

    public TimetableVersion getTimetableVersion() {
        return this.timetableVersion;
    }

    public void setTimetableVersion(TimetableVersion timetableVersion) {
        this.timetableVersion = timetableVersion;
    }

    public ExamScheduleEntry timetableVersion(TimetableVersion timetableVersion) {
        this.setTimetableVersion(timetableVersion);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamScheduleEntry)) {
            return false;
        }
        return getId() != null && getId().equals(((ExamScheduleEntry) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamScheduleEntry{" +
            "id=" + getId() +
            "}";
    }
}
