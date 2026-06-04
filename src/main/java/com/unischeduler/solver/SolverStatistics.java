package com.unischeduler.solver;

/**
 * Mutable statistics collected during one solver run.
 */
public class SolverStatistics {

    private int conflictGraphVertexCount;
    private int conflictGraphEdgeCount;
    private int colorCount;
    private int ac3RevisionCount;
    private int domainReductionCount;
    private int backtrackCount;
    private int exploredNodeCount;
    private int softPenaltyScore;
    private long runtimeMs;

    public int getConflictGraphVertexCount() {
        return conflictGraphVertexCount;
    }

    public void setConflictGraphVertexCount(int conflictGraphVertexCount) {
        this.conflictGraphVertexCount = conflictGraphVertexCount;
    }

    public int getConflictGraphEdgeCount() {
        return conflictGraphEdgeCount;
    }

    public void setConflictGraphEdgeCount(int conflictGraphEdgeCount) {
        this.conflictGraphEdgeCount = conflictGraphEdgeCount;
    }

    public int getColorCount() {
        return colorCount;
    }

    public void setColorCount(int colorCount) {
        this.colorCount = colorCount;
    }

    public int getAc3RevisionCount() {
        return ac3RevisionCount;
    }

    public void addAc3RevisionCount(int ac3RevisionCount) {
        this.ac3RevisionCount += ac3RevisionCount;
    }

    public int getDomainReductionCount() {
        return domainReductionCount;
    }

    public void addDomainReductionCount(int domainReductionCount) {
        this.domainReductionCount += domainReductionCount;
    }

    public int getBacktrackCount() {
        return backtrackCount;
    }

    public void incrementBacktrackCount() {
        backtrackCount++;
    }

    public int getExploredNodeCount() {
        return exploredNodeCount;
    }

    public void incrementExploredNodeCount() {
        exploredNodeCount++;
    }

    public int getSoftPenaltyScore() {
        return softPenaltyScore;
    }

    public void setSoftPenaltyScore(int softPenaltyScore) {
        this.softPenaltyScore = softPenaltyScore;
    }

    public long getRuntimeMs() {
        return runtimeMs;
    }

    public void setRuntimeMs(long runtimeMs) {
        this.runtimeMs = runtimeMs;
    }
}
