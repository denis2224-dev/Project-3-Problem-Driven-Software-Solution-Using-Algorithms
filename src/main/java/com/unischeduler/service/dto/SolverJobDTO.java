package com.unischeduler.service.dto;

import com.unischeduler.domain.enumeration.SolverJobStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.unischeduler.domain.SolverJob} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SolverJobDTO implements Serializable {

    private Long id;

    @NotNull
    private SolverJobStatus status;

    private Instant startedAt;

    private Instant finishedAt;

    @Min(value = 0)
    @Max(value = 100)
    private Integer progressPercent;

    @Size(max = 2000)
    private String message;

    @Min(value = 0)
    private Integer hardConflictCount;

    @Min(value = 0)
    private Integer softPenaltyScore;

    @Min(value = 0)
    private Integer backtrackCount;

    @Min(value = 0)
    private Integer domainReductionCount;

    @Min(value = 0L)
    private Long runtimeMs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolverJobDTO)) {
            return false;
        }

        SolverJobDTO solverJobDTO = (SolverJobDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, solverJobDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SolverJobDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", startedAt='" + getStartedAt() + "'" +
            ", finishedAt='" + getFinishedAt() + "'" +
            ", progressPercent=" + getProgressPercent() +
            ", message='" + getMessage() + "'" +
            ", hardConflictCount=" + getHardConflictCount() +
            ", softPenaltyScore=" + getSoftPenaltyScore() +
            ", backtrackCount=" + getBacktrackCount() +
            ", domainReductionCount=" + getDomainReductionCount() +
            ", runtimeMs=" + getRuntimeMs() +
            "}";
    }
}
