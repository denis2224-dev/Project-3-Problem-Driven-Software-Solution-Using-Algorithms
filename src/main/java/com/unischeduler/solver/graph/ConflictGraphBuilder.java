package com.unischeduler.solver.graph;

import com.unischeduler.domain.CourseEvent;
import com.unischeduler.domain.Exam;
import com.unischeduler.domain.Professor;
import com.unischeduler.domain.StudentGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builds conflict graphs from persisted academic scheduling input.
 */
public class ConflictGraphBuilder {

    public ConflictGraph buildForCourseEvents(List<CourseEvent> courseEvents) {
        ConflictGraph graph = new ConflictGraph();
        List<GraphVertex> vertices = new ArrayList<>();

        for (int index = 0; index < courseEvents.size(); index++) {
            GraphVertex vertex = courseEventVertex(courseEvents.get(index), index);
            graph.addVertex(vertex);
            vertices.add(vertex);
        }

        // Graph construction is O(n^2): compare every pair of events and add an edge for each hard timeslot clash.
        for (int left = 0; left < courseEvents.size(); left++) {
            for (int right = left + 1; right < courseEvents.size(); right++) {
                List<String> reasons = courseEventConflictReasons(courseEvents.get(left), courseEvents.get(right));
                if (!reasons.isEmpty()) {
                    graph.addEdge(vertices.get(left), vertices.get(right), String.join(",", reasons));
                }
            }
        }

        return graph;
    }

    public ConflictGraph buildForExams(List<Exam> exams) {
        ConflictGraph graph = new ConflictGraph();
        List<GraphVertex> vertices = new ArrayList<>();

        for (int index = 0; index < exams.size(); index++) {
            GraphVertex vertex = examVertex(exams.get(index), index);
            graph.addVertex(vertex);
            vertices.add(vertex);
        }

        for (int left = 0; left < exams.size(); left++) {
            for (int right = left + 1; right < exams.size(); right++) {
                if (sameStudentGroup(exams.get(left).getStudentGroup(), exams.get(right).getStudentGroup())) {
                    graph.addEdge(vertices.get(left), vertices.get(right), "GROUP_CLASH");
                }
            }
        }

        return graph;
    }

    private List<String> courseEventConflictReasons(CourseEvent left, CourseEvent right) {
        List<String> reasons = new ArrayList<>();
        if (sameProfessor(left.getProfessor(), right.getProfessor())) {
            reasons.add("PROFESSOR_CLASH");
        }
        if (sameStudentGroup(left.getStudentGroup(), right.getStudentGroup())) {
            reasons.add("GROUP_CLASH");
        }
        return reasons;
    }

    private GraphVertex courseEventVertex(CourseEvent event, int index) {
        Long sourceId = event.getId();
        Long professorId = event.getProfessor() == null ? null : event.getProfessor().getId();
        Long groupId = event.getStudentGroup() == null ? null : event.getStudentGroup().getId();
        return new GraphVertex(
            sourceId == null ? "course-event-unpersisted-" + index : "course-event-" + sourceId,
            courseEventLabel(event, index),
            "COURSE_EVENT",
            sourceId,
            professorId,
            groupId
        );
    }

    private GraphVertex examVertex(Exam exam, int index) {
        Long sourceId = exam.getId();
        Long groupId = exam.getStudentGroup() == null ? null : exam.getStudentGroup().getId();
        return new GraphVertex(
            sourceId == null ? "exam-unpersisted-" + index : "exam-" + sourceId,
            exam.getName() == null ? "Exam " + index : exam.getName(),
            "EXAM",
            sourceId,
            null,
            groupId
        );
    }

    private String courseEventLabel(CourseEvent event, int index) {
        if (event.getCourse() == null) {
            return "Course event " + index;
        }
        return event.getCourse().getCode() + " " + event.getEventType();
    }

    private boolean sameProfessor(Professor left, Professor right) {
        if (left == null || right == null) {
            return false;
        }
        if (left.getId() != null && right.getId() != null) {
            return Objects.equals(left.getId(), right.getId());
        }
        return left == right;
    }

    private boolean sameStudentGroup(StudentGroup left, StudentGroup right) {
        if (left == null || right == null) {
            return false;
        }
        if (left.getId() != null && right.getId() != null) {
            return Objects.equals(left.getId(), right.getId());
        }
        return left == right;
    }
}
