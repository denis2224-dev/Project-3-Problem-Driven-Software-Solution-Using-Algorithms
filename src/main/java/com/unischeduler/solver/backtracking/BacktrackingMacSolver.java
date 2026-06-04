package com.unischeduler.solver.backtracking;

import com.unischeduler.solver.ac3.AC3Propagator;
import com.unischeduler.solver.ac3.AC3Result;
import com.unischeduler.solver.csp.Assignment;
import com.unischeduler.solver.csp.CSPModel;
import com.unischeduler.solver.csp.CSPValue;
import com.unischeduler.solver.csp.CSPVariable;
import com.unischeduler.solver.heuristics.LCVValueOrdering;
import com.unischeduler.solver.heuristics.MRVVariableSelector;
import java.util.List;
import java.util.Optional;

/**
 * Backtracking search with Maintaining Arc Consistency (MAC).
 */
public class BacktrackingMacSolver {

    private final AC3Propagator ac3Propagator;
    private final MRVVariableSelector variableSelector;
    private final LCVValueOrdering valueOrdering;

    public BacktrackingMacSolver() {
        this(new AC3Propagator(), new MRVVariableSelector(), new LCVValueOrdering());
    }

    public BacktrackingMacSolver(
        AC3Propagator ac3Propagator,
        MRVVariableSelector variableSelector,
        LCVValueOrdering valueOrdering
    ) {
        this.ac3Propagator = ac3Propagator;
        this.variableSelector = variableSelector;
        this.valueOrdering = valueOrdering;
    }

    public Optional<Assignment> solve(CSPModel model) {
        CSPModel workingModel = model.copy();
        AC3Result initialPropagation = ac3Propagator.propagate(workingModel);

        if (!initialPropagation.isArcConsistent()) {
            return Optional.empty();
        }

        return backtrack(workingModel, new Assignment());
    }

    private Optional<Assignment> backtrack(CSPModel model, Assignment assignment) {
        if (assignment.isComplete(model)) {
            return Optional.of(assignment.copy());
        }

        Optional<CSPVariable> selectedVariable = variableSelector.selectVariable(model, assignment);
        if (selectedVariable.isEmpty()) {
            return Optional.of(assignment.copy());
        }

        CSPVariable variable = selectedVariable.orElseThrow();

        for (CSPValue value : valueOrdering.orderValues(model, variable, assignment)) {
            if (!model.isConsistentWith(variable, value, assignment)) {
                continue;
            }

            Assignment branchAssignment = assignment.copy();
            branchAssignment.assign(variable, value);

            CSPModel branchModel = model.copy();
            branchModel.restrictDomain(variable, List.of(value));

            // MAC step: after every tentative assignment, enforce AC-3 on the remaining domains.
            AC3Result propagationResult = ac3Propagator.propagate(branchModel);
            if (!propagationResult.isArcConsistent()) {
                continue;
            }

            Optional<Assignment> result = backtrack(branchModel, branchAssignment);
            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }
}
