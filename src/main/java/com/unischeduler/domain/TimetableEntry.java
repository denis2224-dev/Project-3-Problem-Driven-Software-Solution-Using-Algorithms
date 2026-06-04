package com.unischeduler.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TimetableEntry.
 */
@Entity
@Table(name = "timetable_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TimetableEntry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "timetable", "solverJob" }, allowSetters = true)
    private TimetableVersion timetableVersion;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "course", "professor", "studentGroup" }, allowSetters = true)
    private CourseEvent courseEvent;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "building" }, allowSetters = true)
    private Room room;

    @ManyToOne(optional = false)
    @NotNull
    private Timeslot timeslot;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TimetableEntry id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TimetableVersion getTimetableVersion() {
        return this.timetableVersion;
    }

    public void setTimetableVersion(TimetableVersion timetableVersion) {
        this.timetableVersion = timetableVersion;
    }

    public TimetableEntry timetableVersion(TimetableVersion timetableVersion) {
        this.setTimetableVersion(timetableVersion);
        return this;
    }

    public CourseEvent getCourseEvent() {
        return this.courseEvent;
    }

    public void setCourseEvent(CourseEvent courseEvent) {
        this.courseEvent = courseEvent;
    }

    public TimetableEntry courseEvent(CourseEvent courseEvent) {
        this.setCourseEvent(courseEvent);
        return this;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public TimetableEntry room(Room room) {
        this.setRoom(room);
        return this;
    }

    public Timeslot getTimeslot() {
        return this.timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public TimetableEntry timeslot(Timeslot timeslot) {
        this.setTimeslot(timeslot);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimetableEntry)) {
            return false;
        }
        return getId() != null && getId().equals(((TimetableEntry) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimetableEntry{" +
            "id=" + getId() +
            "}";
    }
}
