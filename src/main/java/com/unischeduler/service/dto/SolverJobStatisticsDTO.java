package com.unischeduler.service.dto;

import com.unischeduler.domain.enumeration.SolverJobStatus;
import java.io.Serializable;
import java.time.Instant;

/**
 * Runtime statistics exposed for a solver job.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SolverJobStatisticsDTO implements Serializable {

    private Long jobId;
    private SolverJobStatus status;
    private Instant startedAt;
    private Instant finishedAt;
    private Integer progressPercent;
    private String message;
    private Integer hardConflictCount;
    private Integer softPenaltyScore;
    private Integer backtrackCount;
    private Integer domainReductionCount;
    private Long runtimeMs;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public SolverJobStatus getStatus() {
        return status;
    }

    public void setStatus(SolverJobStatus status) {
        this.status = status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Integer getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Integer progressPercent) {
        this.progressPercent = progressPercent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getHardConflictCount() {
        return hardConflictCount;
    }

    public void setHardConflictCount(Integer hardConflictCount) {
        this.hardConflictCount = hardConflictCount;
    }

    public Integer getSoftPenaltyScore() {
        return softPenaltyScore;
    }

    public void setSoftPenaltyScore(Integer softPenaltyScore) {
        this.softPenaltyScore = softPenaltyScore;
    }

    public Integer getBacktrackCount() {
        return backtrackCount;
    }

    public void setBacktrackCount(Integer backtrackCount) {
        this.backtrackCount = backtrackCount;
    }

    public Integer getDomainReductionCount() {
        return domainReductionCount;
    }

    public void setDomainReductionCount(Integer domainReductionCount) {
        this.domainReductionCount = domainReductionCount;
    }

    public Long getRuntimeMs() {
        return runtimeMs;
    }

    public void setRuntimeMs(Long runtimeMs) {
        this.runtimeMs = runtimeMs;
    }
}
