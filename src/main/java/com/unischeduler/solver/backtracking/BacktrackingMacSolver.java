package com.unischeduler.solver.backtracking;

import com.unischeduler.solver.SolverResult;
import com.unischeduler.solver.SolverStatistics;
import com.unischeduler.solver.ac3.AC3Propagator;
import com.unischeduler.solver.ac3.AC3Result;
import com.unischeduler.solver.csp.Assignment;
import com.unischeduler.solver.csp.CSPModel;
import com.unischeduler.solver.csp.CSPValue;
import com.unischeduler.solver.csp.CSPVariable;
import com.unischeduler.solver.heuristics.LCVValueOrdering;
import com.unischeduler.solver.heuristics.MRVVariableSelector;
import com.unischeduler.solver.scoring.SoftConstraintScorer;
import com.unischeduler.solver.scoring.SoftConstraintScorer.ProfessorTimePreference;
import java.util.List;
import java.util.Optional;

/**
 * Backtracking search with Maintaining Arc Consistency (MAC).
 */
public class BacktrackingMacSolver {

    private final AC3Propagator ac3Propagator;
    private final MRVVariableSelector variableSelector;
    private final LCVValueOrdering valueOrdering;
    private final SoftConstraintScorer softConstraintScorer;

    public BacktrackingMacSolver() {
        this(new AC3Propagator(), new MRVVariableSelector(), new LCVValueOrdering(), new SoftConstraintScorer());
    }

    public BacktrackingMacSolver(
        AC3Propagator ac3Propagator,
        MRVVariableSelector variableSelector,
        LCVValueOrdering valueOrdering,
        SoftConstraintScorer softConstraintScorer
    ) {
        this.ac3Propagator = ac3Propagator;
        this.variableSelector = variableSelector;
        this.valueOrdering = valueOrdering;
        this.softConstraintScorer = softConstraintScorer;
    }

    public Optional<Assignment> solve(CSPModel model) {
        return solveWithStatistics(model).getAssignment();
    }

    public SolverResult solveWithStatistics(CSPModel model) {
        return solveWithStatistics(model, List.of());
    }

    public SolverResult solveWithStatistics(CSPModel model, List<ProfessorTimePreference> professorTimePreferences) {
        long startedAt = System.nanoTime();
        SolverStatistics statistics = new SolverStatistics();
        CSPModel workingModel = model.copy();
        AC3Result initialPropagation = ac3Propagator.propagate(workingModel);
        statistics.addAc3RevisionCount(initialPropagation.getRevisions().size());
        statistics.addDomainReductionCount(initialPropagation.getDomainReductionCount());

        if (!initialPropagation.isArcConsistent()) {
            statistics.setRuntimeMs(elapsedMs(startedAt));
            return SolverResult.failure("Initial AC-3 propagation detected infeasible domains.", statistics);
        }

        Optional<Assignment> assignment = backtrack(workingModel, new Assignment(), statistics);
        statistics.setRuntimeMs(elapsedMs(startedAt));

        if (assignment.isEmpty()) {
            return SolverResult.failure("No hard-valid timetable assignment exists for the provided CSP model.", statistics);
        }

        statistics.setSoftPenaltyScore(softConstraintScorer.score(assignment.orElseThrow(), professorTimePreferences));
        return SolverResult.success(assignment.orElseThrow(), statistics);
    }

    private Optional<Assignment> backtrack(CSPModel model, Assignment assignment, SolverStatistics statistics) {
        statistics.incrementExploredNodeCount();
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
            statistics.addAc3RevisionCount(propagationResult.getRevisions().size());
            statistics.addDomainReductionCount(propagationResult.getDomainReductionCount());
            if (!propagationResult.isArcConsistent()) {
                continue;
            }

            Optional<Assignment> result = backtrack(branchModel, branchAssignment, statistics);
            if (result.isPresent()) {
                return result;
            }
        }

        statistics.incrementBacktrackCount();
        return Optional.empty();
    }

    private long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
