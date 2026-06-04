package com.unischeduler.service;

import com.unischeduler.domain.Course;
import com.unischeduler.domain.Exam;
import com.unischeduler.domain.StudentGroup;
import com.unischeduler.repository.ExamRepository;
import com.unischeduler.service.dto.ExamColoringEntryDTO;
import com.unischeduler.service.dto.ExamColoringResultDTO;
import com.unischeduler.solver.graph.ConflictGraph;
import com.unischeduler.solver.graph.ConflictGraphBuilder;
import com.unischeduler.solver.graph.GraphEdge;
import com.unischeduler.solver.graph.GraphVertex;
import com.unischeduler.solver.graph.WelshPowellColoringSolver;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Builds an exam conflict graph and exposes the Welsh-Powell coloring as a demo schedule.
 */
@Service
@Transactional(readOnly = true)
public class ExamColoringService {

    private final ExamRepository examRepository;
    private final ConflictGraphBuilder conflictGraphBuilder = new ConflictGraphBuilder();
    private final WelshPowellColoringSolver coloringSolver = new WelshPowellColoringSolver();

    public ExamColoringService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public ExamColoringResultDTO colorExams() {
        List<Exam> exams = examRepository.findAllWithEagerRelationships();
        Map<Long, Exam> examsById = exams
            .stream()
            .filter(exam -> exam.getId() != null)
            .collect(Collectors.toMap(Exam::getId, Function.identity()));
        ConflictGraph graph = conflictGraphBuilder.buildForExams(exams);
        Map<GraphVertex, Integer> coloring = coloringSolver.color(graph);

        ExamColoringResultDTO result = new ExamColoringResultDTO();
        result.setExamCount(exams.size());
        result.setColorCount(coloringSolver.colorCount(coloring));
        result.setConflictEdgeCount(graph.edgeCount());
        result.setConflictFree(isConflictFree(graph, coloring));
        result.setEntries(
            coloring
                .entrySet()
                .stream()
                .sorted(
                    Comparator.comparingInt(Map.Entry<GraphVertex, Integer>::getValue).thenComparing(entry -> entry.getKey().getLabel())
                )
                .map(entry -> toEntry(entry.getKey(), entry.getValue(), examsById))
                .toList()
        );
        return result;
    }

    private boolean isConflictFree(ConflictGraph graph, Map<GraphVertex, Integer> coloring) {
        for (GraphEdge edge : graph.getEdges()) {
            if (coloring.get(edge.getSource()).equals(coloring.get(edge.getTarget()))) {
                return false;
            }
        }
        return true;
    }

    private ExamColoringEntryDTO toEntry(GraphVertex vertex, Integer color, Map<Long, Exam> examsById) {
        Exam exam = examsById.get(vertex.getSourceId());
        Course course = exam == null ? null : exam.getCourse();
        StudentGroup studentGroup = exam == null ? null : exam.getStudentGroup();

        ExamColoringEntryDTO dto = new ExamColoringEntryDTO();
        dto.setExamId(vertex.getSourceId());
        dto.setExamName(exam == null ? vertex.getLabel() : exam.getName());
        dto.setCourseCode(course == null ? null : course.getCode());
        dto.setCourseName(course == null ? null : course.getName());
        dto.setStudentGroupName(studentGroup == null ? null : studentGroup.getName());
        dto.setColor(color);
        dto.setSuggestedPeriod("Exam period " + (color + 1));
        return dto;
    }
}
