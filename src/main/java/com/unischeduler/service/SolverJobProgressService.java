package com.unischeduler.service;

import com.unischeduler.domain.SolverJob;
import com.unischeduler.domain.enumeration.SolverJobStatus;
import com.unischeduler.repository.SolverJobRepository;
import com.unischeduler.solver.SolverStatistics;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persists visible solver-job progress in independent transactions.
 */
@Service
public class SolverJobProgressService {

    private final SolverJobRepository solverJobRepository;

    public SolverJobProgressService(SolverJobRepository solverJobRepository) {
        this.solverJobRepository = solverJobRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(Long jobId, SolverJobStatus status, int progressPercent, String message) {
        solverJobRepository
            .findById(jobId)
            .ifPresent(job -> {
                if (job.getStatus() == SolverJobStatus.CANCELLED) {
                    return;
                }
                job.status(status).progressPercent(progressPercent).message(message);
                solverJobRepository.save(job);
            });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void complete(Long jobId, SolverStatistics statistics, String message) {
        solverJobRepository
            .findById(jobId)
            .ifPresent(job -> {
                if (job.getStatus() == SolverJobStatus.CANCELLED) {
                    return;
                }
                applyStatistics(job, statistics);
                job.status(SolverJobStatus.COMPLETED)
                    .progressPercent(100)
                    .hardConflictCount(0)
                    .finishedAt(Instant.now())
                    .message(message);
                solverJobRepository.save(job);
            });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void fail(Long jobId, SolverStatistics statistics, String message) {
        solverJobRepository
            .findById(jobId)
            .ifPresent(job -> {
                if (job.getStatus() == SolverJobStatus.CANCELLED) {
                    return;
                }
                applyStatistics(job, statistics);
                job.status(SolverJobStatus.FAILED)
                    .progressPercent(100)
                    .hardConflictCount(1)
                    .finishedAt(Instant.now())
                    .message(message);
                solverJobRepository.save(job);
            });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SolverJob cancel(Long jobId) {
        SolverJob job = solverJobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Solver job not found"));
        if (job.getStatus() != SolverJobStatus.COMPLETED && job.getStatus() != SolverJobStatus.FAILED) {
            job.status(SolverJobStatus.CANCELLED)
                .progressPercent(100)
                .finishedAt(Instant.now())
                .message("Solver job was cancelled by the user.");
            job = solverJobRepository.save(job);
        }
        return job;
    }

    @Transactional(readOnly = true)
    public boolean isCancelled(Long jobId) {
        return solverJobRepository.findById(jobId).map(job -> job.getStatus() == SolverJobStatus.CANCELLED).orElse(true);
    }

    private void applyStatistics(SolverJob job, SolverStatistics statistics) {
        if (statistics == null) {
            return;
        }
        job.softPenaltyScore(statistics.getSoftPenaltyScore())
            .backtrackCount(statistics.getBacktrackCount())
            .domainReductionCount(statistics.getDomainReductionCount())
            .runtimeMs(statistics.getRuntimeMs());
    }
}
