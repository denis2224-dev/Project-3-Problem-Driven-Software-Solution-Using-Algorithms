package com.unischeduler.domain;

import com.unischeduler.domain.enumeration.SolverJobStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SolverJob.
 */
@Entity
@Table(name = "solver_job")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SolverJob implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SolverJobStatus status;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Min(value = 0)
    @Max(value = 100)
    @Column(name = "progress_percent")
    private Integer progressPercent;

    @Size(max = 2000)
    @Column(name = "message", length = 2000)
    private String message;

    @Min(value = 0)
    @Column(name = "hard_conflict_count")
    private Integer hardConflictCount;

    @Min(value = 0)
    @Column(name = "soft_penalty_score")
    private Integer softPenaltyScore;

    @Min(value = 0)
    @Column(name = "backtrack_count")
    private Integer backtrackCount;

    @Min(value = 0)
    @Column(name = "domain_reduction_count")
    private Integer domainReductionCount;

    @Min(value = 0L)
    @Column(name = "runtime_ms")
    private Long runtimeMs;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SolverJob id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolverJobStatus getStatus() {
        return this.status;
    }

    public SolverJob status(SolverJobStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SolverJobStatus status) {
        this.status = status;
    }

    public Instant getStartedAt() {
        return this.startedAt;
    }

    public SolverJob startedAt(Instant startedAt) {
        this.setStartedAt(startedAt);
        return this;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return this.finishedAt;
    }

    public SolverJob finishedAt(Instant finishedAt) {
        this.setFinishedAt(finishedAt);
        return this;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Integer getProgressPercent() {
        return this.progressPercent;
    }

    public SolverJob progressPercent(Integer progressPercent) {
        this.setProgressPercent(progressPercent);
        return this;
    }

    public void setProgressPercent(Integer progressPercent) {
        this.progressPercent = progressPercent;
    }

    public String getMessage() {
        return this.message;
    }

    public SolverJob message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getHardConflictCount() {
        return this.hardConflictCount;
    }

    public SolverJob hardConflictCount(Integer hardConflictCount) {
        this.setHardConflictCount(hardConflictCount);
        return this;
    }

    public void setHardConflictCount(Integer hardConflictCount) {
        this.hardConflictCount = hardConflictCount;
    }

    public Integer getSoftPenaltyScore() {
        return this.softPenaltyScore;
    }

    public SolverJob softPenaltyScore(Integer softPenaltyScore) {
        this.setSoftPenaltyScore(softPenaltyScore);
        return this;
    }

    public void setSoftPenaltyScore(Integer softPenaltyScore) {
        this.softPenaltyScore = softPenaltyScore;
    }

    public Integer getBacktrackCount() {
        return this.backtrackCount;
    }

    public SolverJob backtrackCount(Integer backtrackCount) {
        this.setBacktrackCount(backtrackCount);
        return this;
    }

    public void setBacktrackCount(Integer backtrackCount) {
        this.backtrackCount = backtrackCount;
    }

    public Integer getDomainReductionCount() {
        return this.domainReductionCount;
    }

    public SolverJob domainReductionCount(Integer domainReductionCount) {
        this.setDomainReductionCount(domainReductionCount);
        return this;
    }

    public void setDomainReductionCount(Integer domainReductionCount) {
        this.domainReductionCount = domainReductionCount;
    }

    public Long getRuntimeMs() {
        return this.runtimeMs;
    }

    public SolverJob runtimeMs(Long runtimeMs) {
        this.setRuntimeMs(runtimeMs);
        return this;
    }

    public void setRuntimeMs(Long runtimeMs) {
        this.runtimeMs = runtimeMs;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolverJob)) {
            return false;
        }
        return getId() != null && getId().equals(((SolverJob) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SolverJob{" +
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
