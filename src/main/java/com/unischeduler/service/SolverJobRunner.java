package com.unischeduler.service;

import com.unischeduler.domain.CourseEvent;
import com.unischeduler.domain.ProfessorPreference;
import com.unischeduler.domain.Room;
import com.unischeduler.domain.Timeslot;
import com.unischeduler.domain.Timetable;
import com.unischeduler.domain.TimetableEntry;
import com.unischeduler.domain.TimetableVersion;
import com.unischeduler.domain.enumeration.CourseEventType;
import com.unischeduler.domain.enumeration.ProfessorPreferenceType;
import com.unischeduler.domain.enumeration.RoomType;
import com.unischeduler.domain.enumeration.SolverJobStatus;
import com.unischeduler.domain.enumeration.TimetableStatus;
import com.unischeduler.repository.CourseEventRepository;
import com.unischeduler.repository.ProfessorPreferenceRepository;
import com.unischeduler.repository.RoomRepository;
import com.unischeduler.repository.SolverJobRepository;
import com.unischeduler.repository.TimeslotRepository;
import com.unischeduler.repository.TimetableEntryRepository;
import com.unischeduler.repository.TimetableRepository;
import com.unischeduler.repository.TimetableVersionRepository;
import com.unischeduler.solver.SolverResult;
import com.unischeduler.solver.SolverStatistics;
import com.unischeduler.solver.TimetableSolutionMapper;
import com.unischeduler.solver.backtracking.BacktrackingMacSolver;
import com.unischeduler.solver.csp.CSPConstraint;
import com.unischeduler.solver.csp.CSPModel;
import com.unischeduler.solver.csp.CSPValue;
import com.unischeduler.solver.csp.CSPVariable;
import com.unischeduler.solver.graph.ConflictGraph;
import com.unischeduler.solver.graph.ConflictGraphBuilder;
import com.unischeduler.solver.graph.GraphVertex;
import com.unischeduler.solver.graph.WelshPowellColoringSolver;
import com.unischeduler.solver.scoring.SoftConstraintScorer.ProfessorTimePreference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Asynchronous solver pipeline. Controllers only create jobs; this service performs the work.
 */
@Service
public class SolverJobRunner {

    private final CourseEventRepository courseEventRepository;
    private final RoomRepository roomRepository;
    private final TimeslotRepository timeslotRepository;
    private final ProfessorPreferenceRepository professorPreferenceRepository;
    private final TimetableRepository timetableRepository;
    private final TimetableVersionRepository timetableVersionRepository;
    private final TimetableEntryRepository timetableEntryRepository;
    private final SolverJobRepository solverJobRepository;
    private final SolverJobProgressService progressService;
    private final ConflictGraphBuilder conflictGraphBuilder = new ConflictGraphBuilder();
    private final WelshPowellColoringSolver coloringSolver = new WelshPowellColoringSolver();
    private final BacktrackingMacSolver backtrackingMacSolver = new BacktrackingMacSolver();
    private final TimetableSolutionMapper timetableSolutionMapper = new TimetableSolutionMapper();

    public SolverJobRunner(
        CourseEventRepository courseEventRepository,
        RoomRepository roomRepository,
        TimeslotRepository timeslotRepository,
        ProfessorPreferenceRepository professorPreferenceRepository,
        TimetableRepository timetableRepository,
        TimetableVersionRepository timetableVersionRepository,
        TimetableEntryRepository timetableEntryRepository,
        SolverJobRepository solverJobRepository,
        SolverJobProgressService progressService
    ) {
        this.courseEventRepository = courseEventRepository;
        this.roomRepository = roomRepository;
        this.timeslotRepository = timeslotRepository;
        this.professorPreferenceRepository = professorPreferenceRepository;
        this.timetableRepository = timetableRepository;
        this.timetableVersionRepository = timetableVersionRepository;
        this.timetableEntryRepository = timetableEntryRepository;
        this.solverJobRepository = solverJobRepository;
        this.progressService = progressService;
    }

    @Async("taskExecutor")
    @Transactional
    public void runSolverJob(Long jobId) {
        SolverStatistics statistics = new SolverStatistics();

        try {
            progressService.updateStatus(jobId, SolverJobStatus.VALIDATING_INPUT, 5, "Validating academic input data.");
            List<CourseEvent> courseEvents = sortedCourseEvents(courseEventRepository.findAllWithEagerRelationships());
            List<Room> rooms = roomRepository.findAllWithEagerRelationships();
            List<Timeslot> timeslots = sortedTimeslots(timeslotRepository.findAll());
            List<ProfessorPreference> professorPreferences = professorPreferenceRepository.findAllWithEagerRelationships();
            validateInputs(courseEvents, rooms, timeslots);
            stopIfCancelled(jobId);

            progressService.updateStatus(jobId, SolverJobStatus.BUILDING_CONFLICT_GRAPH, 15, "Building conflict graph.");
            ConflictGraph graph = conflictGraphBuilder.buildForCourseEvents(courseEvents);
            statistics.setConflictGraphVertexCount(graph.vertexCount());
            statistics.setConflictGraphEdgeCount(graph.edgeCount());
            stopIfCancelled(jobId);

            progressService.updateStatus(jobId, SolverJobStatus.RUNNING_WELCH_POWELL, 25, "Running Welsh-Powell graph coloring.");
            Map<GraphVertex, Integer> coloring = coloringSolver.color(graph);
            statistics.setColorCount(coloringSolver.colorCount(coloring));
            stopIfCancelled(jobId);

            progressService.updateStatus(jobId, SolverJobStatus.RUNNING_AC3, 40, "Building CSP domains and preparing AC-3.");
            TimetableCspBuild cspBuild = buildCspModel(courseEvents, rooms, timeslots, professorPreferences, coloring);
            stopIfCancelled(jobId);

            progressService.updateStatus(jobId, SolverJobStatus.RUNNING_BACKTRACKING, 55, "Running MAC backtracking search.");
            SolverResult result = backtrackingMacSolver.solveWithStatistics(cspBuild.model(), cspBuild.professorTimePreferences());
            copyPipelineStatistics(statistics, result.getStatistics());

            if (!result.isSuccess()) {
                progressService.fail(jobId, result.getStatistics(), result.getMessage());
                return;
            }
            stopIfCancelled(jobId);

            progressService.updateStatus(jobId, SolverJobStatus.SCORING_SOFT_CONSTRAINTS, 85, "Persisting scored timetable solution.");
            TimetableVersion version = persistTimetable(jobId, result, cspBuild);
            progressService.complete(
                jobId,
                result.getStatistics(),
                "Timetable generated successfully as version " + version.getVersionNumber() + "."
            );
        } catch (CancellationException exception) {
            progressService.cancel(jobId);
        } catch (Exception exception) {
            progressService.fail(jobId, statistics, exception.getMessage());
        }
    }

    private TimetableVersion persistTimetable(Long jobId, SolverResult result, TimetableCspBuild cspBuild) {
        Timetable timetable = timetableRepository.save(
            new Timetable()
                .name("Generated FAF Timetable")
                .semester("Spring")
                .academicYear("2025-2026")
                .status(TimetableStatus.DRAFT)
                .createdAt(Instant.now())
        );
        TimetableVersion version = timetableVersionRepository.save(
            new TimetableVersion()
                .versionNumber(1)
                .createdAt(Instant.now())
                .totalHardConflicts(0)
                .totalSoftPenalty(result.getStatistics().getSoftPenaltyScore())
                .averageStudentGap(0.0)
                .roomUtilization(roomUtilization(cspBuild))
                .timetable(timetable)
                .solverJob(solverJobRepository.getReferenceById(jobId))
        );
        List<TimetableEntry> entries = timetableSolutionMapper.toEntries(
            version,
            result.getAssignment().orElseThrow(),
            cspBuild.courseEventsById(),
            cspBuild.roomsById(),
            cspBuild.timeslotsById()
        );
        timetableEntryRepository.saveAll(entries);
        return version;
    }

    private TimetableCspBuild buildCspModel(
        List<CourseEvent> courseEvents,
        List<Room> rooms,
        List<Timeslot> timeslots,
        List<ProfessorPreference> professorPreferences,
        Map<GraphVertex, Integer> coloring
    ) {
        CSPModel model = new CSPModel();
        Map<Long, Integer> colorByCourseEventId = colorByCourseEventId(coloring);
        Set<ProfessorTimeslotKey> unavailableTimeslots = unavailableTimeslots(professorPreferences);
        Map<Long, CourseEvent> courseEventsById = courseEvents.stream().collect(Collectors.toMap(CourseEvent::getId, Function.identity()));
        Map<Long, Room> roomsById = rooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
        Map<Long, Timeslot> timeslotsById = timeslots.stream().collect(Collectors.toMap(Timeslot::getId, Function.identity()));

        for (int eventIndex = 0; eventIndex < courseEvents.size(); eventIndex++) {
            CourseEvent courseEvent = courseEvents.get(eventIndex);
            CSPVariable variable = variableFor(courseEvent);
            Long preferredTimeslotId = preferredTimeslot(courseEvent, timeslots, colorByCourseEventId, eventIndex);
            List<CSPValue> domainValues = domainValues(courseEvent, rooms, timeslots, unavailableTimeslots, preferredTimeslotId);
            if (domainValues.isEmpty()) {
                throw new IllegalStateException("Empty CSP domain for course event " + courseEvent.getId());
            }
            model.addVariable(variable, domainValues);
        }

        addPairwiseHardConstraints(model, courseEvents);

        return new TimetableCspBuild(model, courseEventsById, roomsById, timeslotsById, professorTimePreferences(professorPreferences));
    }

    private void addPairwiseHardConstraints(CSPModel model, List<CourseEvent> courseEvents) {
        List<CSPVariable> variables = model.getVariables();

        for (int left = 0; left < courseEvents.size(); left++) {
            for (int right = left + 1; right < courseEvents.size(); right++) {
                CourseEvent leftEvent = courseEvents.get(left);
                CourseEvent rightEvent = courseEvents.get(right);
                CSPVariable leftVariable = variables.get(left);
                CSPVariable rightVariable = variables.get(right);
                boolean sameProfessor = sameId(leftEvent.getProfessor().getId(), rightEvent.getProfessor().getId());
                boolean sameGroup = sameId(leftEvent.getStudentGroup().getId(), rightEvent.getStudentGroup().getId());

                model.addConstraint(
                    new CSPConstraint(
                        leftVariable,
                        rightVariable,
                        "hard timetable clash",
                        (leftValue, rightValue) ->
                            !sameRoomAtSameTime(leftValue, rightValue) &&
                            !(sameProfessor && sameTimeslot(leftValue, rightValue)) &&
                            !(sameGroup && sameTimeslot(leftValue, rightValue))
                    )
                );
            }
        }
    }

    private List<CSPValue> domainValues(
        CourseEvent courseEvent,
        List<Room> rooms,
        List<Timeslot> timeslots,
        Set<ProfessorTimeslotKey> unavailableTimeslots,
        Long preferredTimeslotId
    ) {
        List<CSPValue> values = new ArrayList<>();

        for (Timeslot timeslot : timeslots) {
            if (unavailableTimeslots.contains(new ProfessorTimeslotKey(courseEvent.getProfessor().getId(), timeslot.getId()))) {
                continue;
            }
            for (Room room : rooms) {
                if (roomCanHost(courseEvent, room)) {
                    values.add(valueFor(courseEvent, timeslot, room));
                }
            }
        }

        values.sort(
            Comparator.comparing((CSPValue value) -> Objects.equals(value.getTimeslotId(), preferredTimeslotId) ? 0 : 1)
                .thenComparing(CSPValue::getDayOfWeek, Comparator.nullsLast(String::compareTo))
                .thenComparing(CSPValue::getStartTime, Comparator.nullsLast(String::compareTo))
                .thenComparing(CSPValue::getRoomId, Comparator.nullsLast(Long::compareTo))
        );
        return values;
    }

    private CSPVariable variableFor(CourseEvent event) {
        return new CSPVariable(
            "course-event-" + event.getId(),
            event.getCourse().getCode() + " " + event.getEventType(),
            event.getId(),
            event.getProfessor().getId(),
            event.getStudentGroup().getId(),
            event.getExpectedStudents(),
            event.getRequiredEquipment()
        );
    }

    private CSPValue valueFor(CourseEvent event, Timeslot timeslot, Room room) {
        String buildingCode = room.getBuilding() == null ? null : room.getBuilding().getCode();
        return new CSPValue(
            "event-" + event.getId() + "-timeslot-" + timeslot.getId() + "-room-" + room.getId(),
            timeslot.getId(),
            room.getId(),
            timeslot.getDayOfWeek().name(),
            timeslot.getStartTime(),
            timeslot.getEndTime(),
            room.getCapacity(),
            room.getEquipment(),
            buildingCode
        );
    }

    private boolean roomCanHost(CourseEvent event, Room room) {
        return (
            room.getCapacity() >= event.getExpectedStudents() &&
            roomTypeMatches(event.getEventType(), room.getRoomType()) &&
            hasEquipment(event, room)
        );
    }

    private boolean roomTypeMatches(CourseEventType eventType, RoomType roomType) {
        return switch (eventType) {
            case LABORATORY -> roomType == RoomType.LABORATORY;
            case SEMINAR -> roomType == RoomType.SEMINAR || roomType == RoomType.LECTURE;
            case LECTURE -> roomType == RoomType.LECTURE || roomType == RoomType.EXAM;
        };
    }

    private boolean hasEquipment(CourseEvent event, Room room) {
        String requiredEquipment = event.getRequiredEquipment();
        if (requiredEquipment == null || requiredEquipment.isBlank()) {
            return true;
        }

        String roomEquipment = room.getEquipment() == null ? "" : room.getEquipment().toLowerCase(Locale.ROOT);
        for (String token : requiredEquipment.toLowerCase(Locale.ROOT).split(",")) {
            String requiredToken = token.trim();
            if (!requiredToken.isEmpty() && !roomEquipment.contains(requiredToken)) {
                return false;
            }
        }
        return true;
    }

    private List<ProfessorTimePreference> professorTimePreferences(List<ProfessorPreference> professorPreferences) {
        return professorPreferences
            .stream()
            .map(preference ->
                new ProfessorTimePreference(
                    preference.getProfessor().getId(),
                    preference.getTimeslot().getId(),
                    preference.getPreferenceType()
                )
            )
            .toList();
    }

    private Set<ProfessorTimeslotKey> unavailableTimeslots(List<ProfessorPreference> professorPreferences) {
        Set<ProfessorTimeslotKey> unavailable = new HashSet<>();
        for (ProfessorPreference preference : professorPreferences) {
            if (preference.getPreferenceType() == ProfessorPreferenceType.UNAVAILABLE) {
                unavailable.add(new ProfessorTimeslotKey(preference.getProfessor().getId(), preference.getTimeslot().getId()));
            }
        }
        return unavailable;
    }

    private Map<Long, Integer> colorByCourseEventId(Map<GraphVertex, Integer> coloring) {
        Map<Long, Integer> colors = new HashMap<>();
        coloring.forEach((vertex, color) -> {
            if ("COURSE_EVENT".equals(vertex.getSourceType()) && vertex.getSourceId() != null) {
                colors.put(vertex.getSourceId(), color);
            }
        });
        return colors;
    }

    private Long preferredTimeslot(CourseEvent event, List<Timeslot> timeslots, Map<Long, Integer> colorByCourseEventId, int eventIndex) {
        Integer color = colorByCourseEventId.get(event.getId());
        if (timeslots.isEmpty()) {
            return null;
        }
        int colorOffset = color == null ? 0 : color;
        int slotIndex = Math.floorMod(eventIndex * 11 + colorOffset * 3, timeslots.size());
        return timeslots.get(slotIndex).getId();
    }

    private List<Timeslot> sortedTimeslots(List<Timeslot> timeslots) {
        return timeslots.stream().sorted(Comparator.comparing(Timeslot::getDayOfWeek).thenComparing(Timeslot::getStartTime)).toList();
    }

    private List<CourseEvent> sortedCourseEvents(List<CourseEvent> courseEvents) {
        return courseEvents
            .stream()
            .sorted(
                Comparator.comparing((CourseEvent event) -> studentGroupName(event), Comparator.nullsLast(String::compareTo)).thenComparing(
                    CourseEvent::getId,
                    Comparator.nullsLast(Long::compareTo)
                )
            )
            .toList();
    }

    private String studentGroupName(CourseEvent event) {
        return event.getStudentGroup() == null ? null : event.getStudentGroup().getName();
    }

    private void validateInputs(List<CourseEvent> courseEvents, List<Room> rooms, List<Timeslot> timeslots) {
        if (courseEvents.isEmpty()) {
            throw new IllegalStateException("No course events found. Load demo data or create academic data first.");
        }
        if (rooms.isEmpty()) {
            throw new IllegalStateException("No rooms found. Load demo data or create rooms first.");
        }
        if (timeslots.isEmpty()) {
            throw new IllegalStateException("No timeslots found. Load demo data or create timeslots first.");
        }
    }

    private void stopIfCancelled(Long jobId) {
        if (progressService.isCancelled(jobId)) {
            throw new CancellationException();
        }
    }

    private void copyPipelineStatistics(SolverStatistics pipelineStatistics, SolverStatistics solverStatistics) {
        solverStatistics.setConflictGraphVertexCount(pipelineStatistics.getConflictGraphVertexCount());
        solverStatistics.setConflictGraphEdgeCount(pipelineStatistics.getConflictGraphEdgeCount());
        solverStatistics.setColorCount(pipelineStatistics.getColorCount());
    }

    private boolean sameTimeslot(CSPValue left, CSPValue right) {
        return Objects.equals(left.getTimeslotId(), right.getTimeslotId());
    }

    private boolean sameRoomAtSameTime(CSPValue left, CSPValue right) {
        return sameTimeslot(left, right) && Objects.equals(left.getRoomId(), right.getRoomId());
    }

    private boolean sameId(Long left, Long right) {
        return Objects.equals(left, right);
    }

    private Double roomUtilization(TimetableCspBuild cspBuild) {
        int totalSlots = cspBuild.roomsById().size() * cspBuild.timeslotsById().size();
        if (totalSlots == 0) {
            return 0.0;
        }
        return (double) cspBuild.model().getVariables().size() / totalSlots;
    }

    private record ProfessorTimeslotKey(Long professorId, Long timeslotId) {}

    private record TimetableCspBuild(
        CSPModel model,
        Map<Long, CourseEvent> courseEventsById,
        Map<Long, Room> roomsById,
        Map<Long, Timeslot> timeslotsById,
        List<ProfessorTimePreference> professorTimePreferences
    ) {}

    private static class CancellationException extends RuntimeException {}
}
