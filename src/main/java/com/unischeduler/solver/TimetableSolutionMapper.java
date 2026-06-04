package com.unischeduler.solver;

import com.unischeduler.domain.CourseEvent;
import com.unischeduler.domain.Room;
import com.unischeduler.domain.Timeslot;
import com.unischeduler.domain.TimetableEntry;
import com.unischeduler.domain.TimetableVersion;
import com.unischeduler.solver.csp.Assignment;
import com.unischeduler.solver.csp.CSPValue;
import com.unischeduler.solver.csp.CSPVariable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Maps a CSP assignment into persisted timetable entries.
 */
public class TimetableSolutionMapper {

    public List<TimetableEntry> toEntries(
        TimetableVersion timetableVersion,
        Assignment assignment,
        Map<Long, CourseEvent> courseEventsById,
        Map<Long, Room> roomsById,
        Map<Long, Timeslot> timeslotsById
    ) {
        return assignment
            .asMap()
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(entry -> entry.getKey().getId()))
            .map(entry -> toEntry(timetableVersion, entry.getKey(), entry.getValue(), courseEventsById, roomsById, timeslotsById))
            .toList();
    }

    private TimetableEntry toEntry(
        TimetableVersion timetableVersion,
        CSPVariable variable,
        CSPValue value,
        Map<Long, CourseEvent> courseEventsById,
        Map<Long, Room> roomsById,
        Map<Long, Timeslot> timeslotsById
    ) {
        CourseEvent courseEvent = courseEventsById.get(variable.getSourceEventId());
        Room room = roomsById.get(value.getRoomId());
        Timeslot timeslot = timeslotsById.get(value.getTimeslotId());

        if (courseEvent == null || room == null || timeslot == null) {
            throw new IllegalStateException("Solver assignment references missing persisted entities");
        }

        return new TimetableEntry().timetableVersion(timetableVersion).courseEvent(courseEvent).room(room).timeslot(timeslot);
    }
}
