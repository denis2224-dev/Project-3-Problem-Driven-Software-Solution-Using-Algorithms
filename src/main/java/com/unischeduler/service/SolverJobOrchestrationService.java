package com.unischeduler.service;

import com.unischeduler.domain.Building;
import com.unischeduler.domain.Course;
import com.unischeduler.domain.CourseEvent;
import com.unischeduler.domain.Professor;
import com.unischeduler.domain.Room;
import com.unischeduler.domain.SolverJob;
import com.unischeduler.domain.StudentGroup;
import com.unischeduler.domain.Timeslot;
import com.unischeduler.domain.Timetable;
import com.unischeduler.domain.TimetableEntry;
import com.unischeduler.domain.TimetableVersion;
import com.unischeduler.domain.enumeration.SolverJobStatus;
import com.unischeduler.repository.SolverJobRepository;
import com.unischeduler.repository.TimetableEntryRepository;
import com.unischeduler.repository.TimetableVersionRepository;
import com.unischeduler.service.dto.SolverJobDTO;
import com.unischeduler.service.dto.SolverJobResultDTO;
import com.unischeduler.service.dto.SolverJobResultEntryDTO;
import com.unischeduler.service.dto.SolverJobStatisticsDTO;
import com.unischeduler.service.mapper.SolverJobMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates solver jobs and exposes stored asynchronous solver results.
 */
@Service
@Transactional
public class SolverJobOrchestrationService {

    private final SolverJobRepository solverJobRepository;
    private final TimetableVersionRepository timetableVersionRepository;
    private final TimetableEntryRepository timetableEntryRepository;
    private final SolverJobMapper solverJobMapper;
    private final SolverJobRunner solverJobRunner;
    private final SolverJobProgressService progressService;

    public SolverJobOrchestrationService(
        SolverJobRepository solverJobRepository,
        TimetableVersionRepository timetableVersionRepository,
        TimetableEntryRepository timetableEntryRepository,
        SolverJobMapper solverJobMapper,
        SolverJobRunner solverJobRunner,
        SolverJobProgressService progressService
    ) {
        this.solverJobRepository = solverJobRepository;
        this.timetableVersionRepository = timetableVersionRepository;
        this.timetableEntryRepository = timetableEntryRepository;
        this.solverJobMapper = solverJobMapper;
        this.solverJobRunner = solverJobRunner;
        this.progressService = progressService;
    }

    public SolverJobDTO generateTimetable() {
        SolverJob job = solverJobRepository.save(
            new SolverJob()
                .status(SolverJobStatus.CREATED)
                .startedAt(Instant.now())
                .progressPercent(0)
                .message("Solver job created and queued.")
                .hardConflictCount(0)
                .softPenaltyScore(0)
                .backtrackCount(0)
                .domainReductionCount(0)
                .runtimeMs(0L)
        );
        solverJobRunner.runSolverJob(job.getId());
        return solverJobMapper.toDto(job);
    }

    @Transactional(readOnly = true)
    public Optional<SolverJobResultDTO> getResult(Long jobId) {
        return solverJobRepository.findById(jobId).map(job -> {
            SolverJobResultDTO result = baseResult(job);
            timetableVersionRepository
                .findFirstBySolverJobIdOrderByCreatedAtDesc(jobId)
                .ifPresent(version -> applyVersion(result, version));
            return result;
        });
    }

    @Transactional(readOnly = true)
    public Optional<SolverJobStatisticsDTO> getStatistics(Long jobId) {
        return solverJobRepository.findById(jobId).map(this::toStatistics);
    }

    public Optional<SolverJobDTO> cancel(Long jobId) {
        if (!solverJobRepository.existsById(jobId)) {
            return Optional.empty();
        }
        return Optional.of(solverJobMapper.toDto(progressService.cancel(jobId)));
    }

    private SolverJobResultDTO baseResult(SolverJob job) {
        SolverJobResultDTO result = new SolverJobResultDTO();
        result.setJobId(job.getId());
        result.setStatus(job.getStatus());
        result.setMessage(job.getMessage());
        result.setTotalHardConflicts(job.getHardConflictCount());
        result.setTotalSoftPenalty(job.getSoftPenaltyScore());
        return result;
    }

    private void applyVersion(SolverJobResultDTO result, TimetableVersion version) {
        Timetable timetable = version.getTimetable();
        result.setTimetableVersionId(version.getId());
        result.setTimetableId(timetable == null ? null : timetable.getId());
        result.setTimetableName(timetable == null ? null : timetable.getName());
        result.setTotalHardConflicts(version.getTotalHardConflicts());
        result.setTotalSoftPenalty(version.getTotalSoftPenalty());

        List<TimetableEntry> entries = timetableEntryRepository.findAllForVersionWithDetails(version.getId());
        result.setEntries(entries.stream().map(this::toResultEntry).toList());
    }

    private SolverJobResultEntryDTO toResultEntry(TimetableEntry entry) {
        CourseEvent courseEvent = entry.getCourseEvent();
        Course course = courseEvent.getCourse();
        Professor professor = courseEvent.getProfessor();
        StudentGroup studentGroup = courseEvent.getStudentGroup();
        Room room = entry.getRoom();
        Building building = room.getBuilding();
        Timeslot timeslot = entry.getTimeslot();

        SolverJobResultEntryDTO dto = new SolverJobResultEntryDTO();
        dto.setTimetableEntryId(entry.getId());
        dto.setCourseCode(course.getCode());
        dto.setCourseName(course.getName());
        dto.setEventType(courseEvent.getEventType().name());
        dto.setProfessorName(professor.getTitle() + " " + professor.getFirstName() + " " + professor.getLastName());
        dto.setStudentGroupName(studentGroup.getName());
        dto.setRoomCode(room.getCode());
        dto.setRoomName(room.getName());
        dto.setBuildingCode(building == null ? null : building.getCode());
        dto.setBuildingName(building == null ? null : building.getName());
        dto.setDayOfWeek(timeslot.getDayOfWeek().name());
        dto.setStartTime(timeslot.getStartTime());
        dto.setEndTime(timeslot.getEndTime());
        return dto;
    }

    private SolverJobStatisticsDTO toStatistics(SolverJob job) {
        SolverJobStatisticsDTO statistics = new SolverJobStatisticsDTO();
        statistics.setJobId(job.getId());
        statistics.setStatus(job.getStatus());
        statistics.setStartedAt(job.getStartedAt());
        statistics.setFinishedAt(job.getFinishedAt());
        statistics.setProgressPercent(job.getProgressPercent());
        statistics.setMessage(job.getMessage());
        statistics.setHardConflictCount(job.getHardConflictCount());
        statistics.setSoftPenaltyScore(job.getSoftPenaltyScore());
        statistics.setBacktrackCount(job.getBacktrackCount());
        statistics.setDomainReductionCount(job.getDomainReductionCount());
        statistics.setRuntimeMs(job.getRuntimeMs());
        return statistics;
    }
}
