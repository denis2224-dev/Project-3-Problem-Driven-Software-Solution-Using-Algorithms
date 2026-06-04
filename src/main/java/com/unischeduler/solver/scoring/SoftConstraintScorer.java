package com.unischeduler.solver.scoring;

import com.unischeduler.domain.enumeration.ProfessorPreferenceType;
import com.unischeduler.solver.csp.Assignment;
import com.unischeduler.solver.csp.CSPValue;
import com.unischeduler.solver.csp.CSPVariable;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Scores soft constraints after a hard-valid timetable assignment is found.
 */
public class SoftConstraintScorer {

    private static final int STUDENT_GAP_WEIGHT = 2;
    private static final int PROFESSOR_GAP_WEIGHT = 1;
    private static final int UNAVAILABLE_PENALTY = 100;
    private static final int AVOID_PENALTY = 20;
    private static final int MISSED_PREFERRED_SLOT_PENALTY = 5;
    private static final int BUILDING_TRANSITION_PENALTY = 3;
    private static final int EDGE_TIME_PENALTY = 2;

    public int score(Assignment assignment) {
        return score(assignment, List.of());
    }

    public int score(Assignment assignment, List<ProfessorTimePreference> professorPreferences) {
        int score = 0;

        // Idle gaps penalize schedules that leave students or professors waiting between classes.
        score += idleGapPenalty(assignment, CSPVariable::getStudentGroupId, STUDENT_GAP_WEIGHT);
        score += idleGapPenalty(assignment, CSPVariable::getProfessorId, PROFESSOR_GAP_WEIGHT);

        // Preference penalties capture unavailable, avoid, and missed preferred professor slots.
        score += professorPreferencePenalty(assignment, professorPreferences);

        // Building transitions penalize consecutive events that force physical movement between buildings.
        score += buildingTransitionPenalty(assignment, CSPVariable::getStudentGroupId);
        score += buildingTransitionPenalty(assignment, CSPVariable::getProfessorId);

        score += edgeTimePenalty(assignment);
        return score;
    }

    private int idleGapPenalty(Assignment assignment, Function<CSPVariable, Long> keyExtractor, int weight) {
        int penalty = 0;
        Map<Long, Map<String, List<ScheduledEvent>>> eventsByOwnerAndDay = groupEventsByOwnerAndDay(assignment, keyExtractor);

        for (Map<String, List<ScheduledEvent>> eventsByDay : eventsByOwnerAndDay.values()) {
            for (List<ScheduledEvent> dayEvents : eventsByDay.values()) {
                dayEvents.sort(Comparator.comparingInt(ScheduledEvent::startMinute));
                for (int index = 1; index < dayEvents.size(); index++) {
                    int gapMinutes = dayEvents.get(index).startMinute() - dayEvents.get(index - 1).endMinute();
                    if (gapMinutes > 0) {
                        penalty += Math.max(1, gapMinutes / 30) * weight;
                    }
                }
            }
        }

        return penalty;
    }

    private int professorPreferencePenalty(Assignment assignment, List<ProfessorTimePreference> professorPreferences) {
        int penalty = 0;
        Map<PreferenceKey, ProfessorPreferenceType> preferenceByProfessorAndTimeslot = new HashMap<>();
        Map<Long, Set<Long>> preferredTimeslotsByProfessor = new HashMap<>();

        for (ProfessorTimePreference preference : professorPreferences) {
            preferenceByProfessorAndTimeslot.put(
                new PreferenceKey(preference.professorId(), preference.timeslotId()),
                preference.preferenceType()
            );
            if (preference.preferenceType() == ProfessorPreferenceType.PREFERRED) {
                preferredTimeslotsByProfessor.computeIfAbsent(preference.professorId(), ignored -> new HashSet<>()).add(preference.timeslotId());
            }
        }

        for (Map.Entry<CSPVariable, CSPValue> entry : assignment.asMap().entrySet()) {
            Long professorId = entry.getKey().getProfessorId();
            Long timeslotId = entry.getValue().getTimeslotId();
            if (professorId == null || timeslotId == null) {
                continue;
            }

            ProfessorPreferenceType exactPreference = preferenceByProfessorAndTimeslot.get(new PreferenceKey(professorId, timeslotId));
            if (exactPreference == ProfessorPreferenceType.UNAVAILABLE) {
                penalty += UNAVAILABLE_PENALTY;
            } else if (exactPreference == ProfessorPreferenceType.AVOID) {
                penalty += AVOID_PENALTY;
            }

            Set<Long> preferredSlots = preferredTimeslotsByProfessor.getOrDefault(professorId, Set.of());
            if (!preferredSlots.isEmpty() && !preferredSlots.contains(timeslotId)) {
                penalty += MISSED_PREFERRED_SLOT_PENALTY;
            }
        }

        return penalty;
    }

    private int buildingTransitionPenalty(Assignment assignment, Function<CSPVariable, Long> keyExtractor) {
        int penalty = 0;
        Map<Long, Map<String, List<ScheduledEvent>>> eventsByOwnerAndDay = groupEventsByOwnerAndDay(assignment, keyExtractor);

        for (Map<String, List<ScheduledEvent>> eventsByDay : eventsByOwnerAndDay.values()) {
            for (List<ScheduledEvent> dayEvents : eventsByDay.values()) {
                dayEvents.sort(Comparator.comparingInt(ScheduledEvent::startMinute));
                for (int index = 1; index < dayEvents.size(); index++) {
                    String previousBuilding = dayEvents.get(index - 1).value().getBuildingCode();
                    String currentBuilding = dayEvents.get(index).value().getBuildingCode();
                    if (previousBuilding != null && currentBuilding != null && !previousBuilding.equals(currentBuilding)) {
                        penalty += BUILDING_TRANSITION_PENALTY;
                    }
                }
            }
        }

        return penalty;
    }

    private int edgeTimePenalty(Assignment assignment) {
        int penalty = 0;
        for (CSPValue value : assignment.asMap().values()) {
            Integer startMinute = minuteOf(value.getStartTime());
            if (startMinute == null) {
                continue;
            }
            if (startMinute < 9 * 60 || startMinute >= 17 * 60) {
                penalty += EDGE_TIME_PENALTY;
            }
        }
        return penalty;
    }

    private Map<Long, Map<String, List<ScheduledEvent>>> groupEventsByOwnerAndDay(
        Assignment assignment,
        Function<CSPVariable, Long> keyExtractor
    ) {
        Map<Long, Map<String, List<ScheduledEvent>>> eventsByOwnerAndDay = new HashMap<>();

        for (Map.Entry<CSPVariable, CSPValue> entry : assignment.asMap().entrySet()) {
            Long key = keyExtractor.apply(entry.getKey());
            ScheduledEvent event = scheduledEvent(entry.getKey(), entry.getValue());
            if (key == null || event == null || event.value().getDayOfWeek() == null) {
                continue;
            }
            eventsByOwnerAndDay
                .computeIfAbsent(key, ignored -> new HashMap<>())
                .computeIfAbsent(event.value().getDayOfWeek(), ignored -> new ArrayList<>())
                .add(event);
        }

        return eventsByOwnerAndDay;
    }

    private ScheduledEvent scheduledEvent(CSPVariable variable, CSPValue value) {
        Integer startMinute = minuteOf(value.getStartTime());
        Integer endMinute = minuteOf(value.getEndTime());
        if (startMinute == null || endMinute == null) {
            return null;
        }
        return new ScheduledEvent(variable, value, startMinute, endMinute);
    }

    private Integer minuteOf(String value) {
        if (value == null) {
            return null;
        }
        try {
            LocalTime localTime = LocalTime.parse(value);
            return localTime.getHour() * 60 + localTime.getMinute();
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    public record ProfessorTimePreference(Long professorId, Long timeslotId, ProfessorPreferenceType preferenceType) {
        public ProfessorTimePreference {
            Objects.requireNonNull(professorId, "professorId must not be null");
            Objects.requireNonNull(timeslotId, "timeslotId must not be null");
            Objects.requireNonNull(preferenceType, "preferenceType must not be null");
        }
    }

    private record PreferenceKey(Long professorId, Long timeslotId) {}

    private record ScheduledEvent(CSPVariable variable, CSPValue value, int startMinute, int endMinute) {}
}
