package com.unischeduler.service.dto;

import com.unischeduler.domain.enumeration.SolverJobStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Solver job result with stored timetable version and generated entries.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SolverJobResultDTO implements Serializable {

    private Long jobId;
    private SolverJobStatus status;
    private String message;
    private Long timetableId;
    private Long timetableVersionId;
    private String timetableName;
    private Integer totalHardConflicts;
    private Integer totalSoftPenalty;
    private List<SolverJobResultEntryDTO> entries = new ArrayList<>();

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(Long timetableId) {
        this.timetableId = timetableId;
    }

    public Long getTimetableVersionId() {
        return timetableVersionId;
    }

    public void setTimetableVersionId(Long timetableVersionId) {
        this.timetableVersionId = timetableVersionId;
    }

    public String getTimetableName() {
        return timetableName;
    }

    public void setTimetableName(String timetableName) {
        this.timetableName = timetableName;
    }

    public Integer getTotalHardConflicts() {
        return totalHardConflicts;
    }

    public void setTotalHardConflicts(Integer totalHardConflicts) {
        this.totalHardConflicts = totalHardConflicts;
    }

    public Integer getTotalSoftPenalty() {
        return totalSoftPenalty;
    }

    public void setTotalSoftPenalty(Integer totalSoftPenalty) {
        this.totalSoftPenalty = totalSoftPenalty;
    }

    public List<SolverJobResultEntryDTO> getEntries() {
        return entries;
    }

    public void setEntries(List<SolverJobResultEntryDTO> entries) {
        this.entries = entries;
    }
}
