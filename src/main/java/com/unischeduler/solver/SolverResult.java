package com.unischeduler.solver;

import com.unischeduler.solver.csp.Assignment;
import java.util.Optional;

/**
 * Result of a solver run, including success/failure and measured statistics.
 */
public class SolverResult {

    private final boolean success;
    private final Assignment assignment;
    private final String message;
    private final SolverStatistics statistics;

    private SolverResult(boolean success, Assignment assignment, String message, SolverStatistics statistics) {
        this.success = success;
        this.assignment = assignment;
        this.message = message;
        this.statistics = statistics;
    }

    public static SolverResult success(Assignment assignment, SolverStatistics statistics) {
        return new SolverResult(true, assignment.copy(), "Valid timetable assignment found.", statistics);
    }

    public static SolverResult failure(String message, SolverStatistics statistics) {
        return new SolverResult(false, null, message, statistics);
    }

    public boolean isSuccess() {
        return success;
    }

    public Optional<Assignment> getAssignment() {
        return Optional.ofNullable(assignment).map(Assignment::copy);
    }

    public String getMessage() {
        return message;
    }

    public SolverStatistics getStatistics() {
        return statistics;
    }
}
