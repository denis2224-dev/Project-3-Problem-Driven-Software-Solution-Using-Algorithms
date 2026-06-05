package com.unischeduler.solver.heuristics;

import com.unischeduler.solver.csp.Assignment;
import com.unischeduler.solver.csp.CSPConstraint;
import com.unischeduler.solver.csp.CSPModel;
import com.unischeduler.solver.csp.CSPValue;
import com.unischeduler.solver.csp.CSPVariable;
import java.util.Comparator;
import java.util.List;

/**
 * Least Constraining Value heuristic for ordering values in backtracking.
 */
public class LCVValueOrdering {

    public List<CSPValue> orderValues(CSPModel model, CSPVariable variable, Assignment assignment) {
        return model
            .getDomain(variable)
            .getValues()
            .stream()
            // LCV tries values that eliminate the fewest values first. Java's stable sort preserves the domain warm-start order on ties.
            .sorted(Comparator.comparingInt((CSPValue value) -> eliminationScore(model, variable, value, assignment)))
            .toList();
    }

    private int eliminationScore(CSPModel model, CSPVariable variable, CSPValue value, Assignment assignment) {
        int score = 0;

        for (CSPVariable neighbor : model.neighborsOf(variable)) {
            if (assignment.isAssigned(neighbor)) {
                continue;
            }
            for (CSPValue neighborValue : model.getDomain(neighbor).getValues()) {
                if (!satisfiesConstraints(model, variable, value, neighbor, neighborValue)) {
                    score++;
                }
            }
        }

        return score;
    }

    private boolean satisfiesConstraints(
        CSPModel model,
        CSPVariable variable,
        CSPValue value,
        CSPVariable neighbor,
        CSPValue neighborValue
    ) {
        for (CSPConstraint constraint : model.constraintsBetween(variable, neighbor)) {
            if (!constraint.isSatisfied(variable, value, neighbor, neighborValue)) {
                return false;
            }
        }
        return true;
    }
}
