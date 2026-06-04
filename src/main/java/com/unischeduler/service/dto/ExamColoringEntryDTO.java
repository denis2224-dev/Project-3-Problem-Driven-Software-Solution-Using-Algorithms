package com.unischeduler.service.dto;

import java.io.Serializable;

/**
 * One colored exam in the graph-coloring exam schedule preview.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamColoringEntryDTO implements Serializable {

    private Long examId;
    private String examName;
    private String courseCode;
    private String courseName;
    private String studentGroupName;
    private Integer color;
    private String suggestedPeriod;

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStudentGroupName() {
        return studentGroupName;
    }

    public void setStudentGroupName(String studentGroupName) {
        this.studentGroupName = studentGroupName;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getSuggestedPeriod() {
        return suggestedPeriod;
    }

    public void setSuggestedPeriod(String suggestedPeriod) {
        this.suggestedPeriod = suggestedPeriod;
    }
}
