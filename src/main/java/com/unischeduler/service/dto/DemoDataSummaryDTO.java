package com.unischeduler.service.dto;

import java.io.Serializable;

/**
 * Summary returned after loading or clearing the university demo scenario.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemoDataSummaryDTO implements Serializable {

    private long faculties;
    private long departments;
    private long buildings;
    private long rooms;
    private long professors;
    private long studentGroups;
    private long courses;
    private long courseEvents;
    private long timeslots;
    private long professorPreferences;
    private long exams;
    private String message;

    public DemoDataSummaryDTO() {}

    public DemoDataSummaryDTO(
        long faculties,
        long departments,
        long buildings,
        long rooms,
        long professors,
        long studentGroups,
        long courses,
        long courseEvents,
        long timeslots,
        long professorPreferences,
        long exams,
        String message
    ) {
        this.faculties = faculties;
        this.departments = departments;
        this.buildings = buildings;
        this.rooms = rooms;
        this.professors = professors;
        this.studentGroups = studentGroups;
        this.courses = courses;
        this.courseEvents = courseEvents;
        this.timeslots = timeslots;
        this.professorPreferences = professorPreferences;
        this.exams = exams;
        this.message = message;
    }

    public long getFaculties() {
        return faculties;
    }

    public void setFaculties(long faculties) {
        this.faculties = faculties;
    }

    public long getDepartments() {
        return departments;
    }

    public void setDepartments(long departments) {
        this.departments = departments;
    }

    public long getBuildings() {
        return buildings;
    }

    public void setBuildings(long buildings) {
        this.buildings = buildings;
    }

    public long getRooms() {
        return rooms;
    }

    public void setRooms(long rooms) {
        this.rooms = rooms;
    }

    public long getProfessors() {
        return professors;
    }

    public void setProfessors(long professors) {
        this.professors = professors;
    }

    public long getStudentGroups() {
        return studentGroups;
    }

    public void setStudentGroups(long studentGroups) {
        this.studentGroups = studentGroups;
    }

    public long getCourses() {
        return courses;
    }

    public void setCourses(long courses) {
        this.courses = courses;
    }

    public long getCourseEvents() {
        return courseEvents;
    }

    public void setCourseEvents(long courseEvents) {
        this.courseEvents = courseEvents;
    }

    public long getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(long timeslots) {
        this.timeslots = timeslots;
    }

    public long getProfessorPreferences() {
        return professorPreferences;
    }

    public void setProfessorPreferences(long professorPreferences) {
        this.professorPreferences = professorPreferences;
    }

    public long getExams() {
        return exams;
    }

    public void setExams(long exams) {
        this.exams = exams;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
