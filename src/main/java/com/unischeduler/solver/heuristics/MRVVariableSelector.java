package com.unischeduler.solver.heuristics;

import com.unischeduler.solver.csp.Assignment;
import com.unischeduler.solver.csp.CSPModel;
import com.unischeduler.solver.csp.CSPVariable;
import java.util.Comparator;
import java.util.Optional;

/**
 * Minimum Remaining Values heuristic for choosing the next CSP variable.
 */
public class MRVVariableSelector {

    public Optional<CSPVariable> selectVariable(CSPModel model, Assignment assignment) {
        return model
            .getVariables()
            .stream()
            .filter(variable -> !assignment.isAssigned(variable))
            // MRV chooses the unassigned variable with the smallest current domain after propagation.
            .sorted(
                Comparator.comparingInt((CSPVariable variable) -> model.getDomain(variable).size())
                    .thenComparing(Comparator.comparingInt((CSPVariable variable) -> model.neighborsOf(variable).size()).reversed())
                    .thenComparing(CSPVariable::getId)
            )
            .findFirst();
    }
}
