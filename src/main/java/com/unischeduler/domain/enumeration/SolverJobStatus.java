package com.unischeduler.domain.enumeration;

/**
 * The SolverJobStatus enumeration.
 */
public enum SolverJobStatus {
    CREATED,
    VALIDATING_INPUT,
    BUILDING_CONFLICT_GRAPH,
    RUNNING_WELCH_POWELL,
    RUNNING_AC3,
    RUNNING_BACKTRACKING,
    SCORING_SOFT_CONSTRAINTS,
    COMPLETED,
    FAILED,
    CANCELLED,
}
