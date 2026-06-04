package com.unischeduler.solver.scoring;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.domain.enumeration.ProfessorPreferenceType;
import com.unischeduler.solver.csp.Assignment;
import com.unischeduler.solver.csp.CSPValue;
import com.unischeduler.solver.csp.CSPVariable;
import com.unischeduler.solver.scoring.SoftConstraintScorer.ProfessorTimePreference;
import java.util.List;
import org.junit.jupiter.api.Test;

class SoftConstraintScorerTest {

    private final SoftConstraintScorer scorer = new SoftConstraintScorer();

    @Test
    void penalizesIdleGaps() {
        Assignment assignment = new Assignment();
        assignment.assign(variable("E1", 10L, 20L), value("V1", 1L, "MONDAY", "09:00", "10:00", "A"));
        assignment.assign(variable("E2", 11L, 20L), value("V2", 2L, "MONDAY", "13:00", "14:00", "A"));

        int score = scorer.score(assignment);

        assertThat(score).isPositive();
    }

    @Test
    void penalizesProfessorPreferenceViolations() {
        Assignment assignment = new Assignment();
        assignment.assign(variable("E1", 10L, 20L), value("V1", 2L, "MONDAY", "10:00", "11:00", "A"));

        int score = scorer.score(
            assignment,
            List.of(
                new ProfessorTimePreference(10L, 1L, ProfessorPreferenceType.PREFERRED),
                new ProfessorTimePreference(10L, 2L, ProfessorPreferenceType.AVOID)
            )
        );

        assertThat(score).isGreaterThanOrEqualTo(25);
    }

    @Test
    void penalizesBuildingTransitions() {
        Assignment sameBuilding = new Assignment();
        sameBuilding.assign(variable("E1", 10L, 20L), value("V1", 1L, "MONDAY", "09:00", "10:00", "A"));
        sameBuilding.assign(variable("E2", 11L, 20L), value("V2", 2L, "MONDAY", "10:00", "11:00", "A"));

        Assignment differentBuilding = new Assignment();
        differentBuilding.assign(variable("E1", 10L, 20L), value("V1", 1L, "MONDAY", "09:00", "10:00", "A"));
        differentBuilding.assign(variable("E2", 11L, 20L), value("V2", 2L, "MONDAY", "10:00", "11:00", "B"));

        assertThat(scorer.score(differentBuilding)).isGreaterThan(scorer.score(sameBuilding));
    }

    private CSPVariable variable(String id, Long professorId, Long groupId) {
        return new CSPVariable(id, "Event " + id, null, professorId, groupId, 30, null);
    }

    private CSPValue value(String id, Long timeslotId, String day, String start, String end, String buildingCode) {
        return new CSPValue(id, timeslotId, 1L, day, start, end, 40, "projector", buildingCode);
    }
}
