package com.unischeduler.service.mapper;

import com.unischeduler.domain.Building;
import com.unischeduler.domain.Course;
import com.unischeduler.domain.CourseEvent;
import com.unischeduler.domain.Professor;
import com.unischeduler.domain.Room;
import com.unischeduler.domain.StudentGroup;
import com.unischeduler.domain.Timeslot;
import com.unischeduler.domain.TimetableEntry;
import com.unischeduler.service.dto.SolverJobResultEntryDTO;
import org.springframework.stereotype.Service;

/**
 * Converts stored timetable entries into the flattened shape needed by result and weekly-view screens.
 */
@Service
public class TimetableEntryResultMapper {

    public SolverJobResultEntryDTO toResultEntry(TimetableEntry entry) {
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
        dto.setProfessorName(formatProfessorName(professor));
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

    private String formatProfessorName(Professor professor) {
        String title = professor.getTitle() == null || professor.getTitle().isBlank() ? "" : professor.getTitle() + " ";
        return title + professor.getFirstName() + " " + professor.getLastName();
    }
}
