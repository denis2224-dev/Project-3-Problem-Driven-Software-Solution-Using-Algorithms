package com.unischeduler.solver.backtracking;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.solver.csp.Assignment;
import com.unischeduler.solver.csp.CSPConstraint;
import com.unischeduler.solver.csp.CSPModel;
import com.unischeduler.solver.csp.CSPValue;
import com.unischeduler.solver.csp.CSPVariable;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class BacktrackingMacSolverTest {

    private final BacktrackingMacSolver solver = new BacktrackingMacSolver();

    @Test
    void returnsValidAssignmentForSmallFeasibleDataset() {
        CSPVariable first = variable("E1", 10L, 20L);
        CSPVariable second = variable("E2", 10L, 21L);
        CSPModel model = new CSPModel();
        model.addVariable(first, List.of(value("s1-r1", 1L, 1L), value("s2-r1", 2L, 1L)));
        model.addVariable(second, List.of(value("s1-r2", 1L, 2L), value("s2-r2", 2L, 2L)));
        model.addConstraint(cannotShareTimeslot(first, second, "same professor"));

        Optional<Assignment> result = solver.solve(model);

        assertThat(result).isPresent();
        assertAllConstraintsSatisfied(model, result.orElseThrow());
    }

    @Test
    void returnsFailureForImpossibleDataset() {
        CSPVariable first = variable("E1", 10L, 20L);
        CSPVariable second = variable("E2", 10L, 21L);
        CSPModel model = new CSPModel();
        model.addVariable(first, List.of(value("s1-r1", 1L, 1L)));
        model.addVariable(second, List.of(value("s1-r2", 1L, 2L)));
        model.addConstraint(cannotShareTimeslot(first, second, "same professor"));

        Optional<Assignment> result = solver.solve(model);

        assertThat(result).isEmpty();
    }

    @Test
    void respectsProfessorClashConstraint() {
        CSPVariable first = variable("E1", 10L, 20L);
        CSPVariable second = variable("E2", 10L, 21L);
        CSPModel model = new CSPModel();
        model.addVariable(first, List.of(value("s1-r1", 1L, 1L), value("s2-r1", 2L, 1L)));
        model.addVariable(second, List.of(value("s1-r2", 1L, 2L), value("s2-r2", 2L, 2L)));
        model.addConstraint(cannotShareTimeslot(first, second, "same professor"));

        Assignment assignment = solver.solve(model).orElseThrow();

        assertThat(timeslotOf(assignment, first)).isNotEqualTo(timeslotOf(assignment, second));
    }

    @Test
    void respectsRoomClashConstraint() {
        CSPVariable first = variable("E1", 10L, 20L);
        CSPVariable second = variable("E2", 11L, 21L);
        CSPModel model = new CSPModel();
        model.addVariable(first, List.of(value("s1-r1-a", 1L, 1L), value("s2-r1-a", 2L, 1L)));
        model.addVariable(second, List.of(value("s1-r1-b", 1L, 1L), value("s1-r2-b", 1L, 2L)));
        model.addConstraint(cannotShareRoomAtSameTime(first, second));

        Assignment assignment = solver.solve(model).orElseThrow();

        assertThat(sameRoomAtSameTime(valueOf(assignment, first), valueOf(assignment, second))).isFalse();
    }

    @Test
    void respectsGroupClashConstraint() {
        CSPVariable first = variable("E1", 10L, 20L);
        CSPVariable second = variable("E2", 11L, 20L);
        CSPModel model = new CSPModel();
        model.addVariable(first, List.of(value("s1-r1", 1L, 1L), value("s2-r1", 2L, 1L)));
        model.addVariable(second, List.of(value("s1-r2", 1L, 2L), value("s2-r2", 2L, 2L)));
        model.addConstraint(cannotShareTimeslot(first, second, "same group"));

        Assignment assignment = solver.solve(model).orElseThrow();

        assertThat(timeslotOf(assignment, first)).isNotEqualTo(timeslotOf(assignment, second));
    }

    private CSPVariable variable(String id, Long professorId, Long groupId) {
        return new CSPVariable(id, "Event " + id, null, professorId, groupId, 30, null);
    }

    private CSPValue value(String id, Long timeslotId, Long roomId) {
        return new CSPValue(id, timeslotId, roomId, "MONDAY", "08:00", "09:30", 40, "projector", "A");
    }

    private CSPConstraint cannotShareTimeslot(CSPVariable first, CSPVariable second, String reason) {
        return new CSPConstraint(first, second, reason, (left, right) -> !sameTimeslot(left, right));
    }

    private CSPConstraint cannotShareRoomAtSameTime(CSPVariable first, CSPVariable second) {
        return new CSPConstraint(first, second, "same room and timeslot", (left, right) -> !sameRoomAtSameTime(left, right));
    }

    private void assertAllConstraintsSatisfied(CSPModel model, Assignment assignment) {
        for (CSPConstraint constraint : model.getConstraints()) {
            CSPVariable first = constraint.getFirstVariable();
            CSPVariable second = constraint.getSecondVariable();
            assertThat(constraint.isSatisfied(first, valueOf(assignment, first), second, valueOf(assignment, second))).isTrue();
        }
    }

    private CSPValue valueOf(Assignment assignment, CSPVariable variable) {
        return assignment.valueOf(variable).orElseThrow();
    }

    private Long timeslotOf(Assignment assignment, CSPVariable variable) {
        return valueOf(assignment, variable).getTimeslotId();
    }

    private boolean sameTimeslot(CSPValue left, CSPValue right) {
        return left.getTimeslotId().equals(right.getTimeslotId());
    }

    private boolean sameRoomAtSameTime(CSPValue left, CSPValue right) {
        return sameTimeslot(left, right) && left.getRoomId().equals(right.getRoomId());
    }
}
