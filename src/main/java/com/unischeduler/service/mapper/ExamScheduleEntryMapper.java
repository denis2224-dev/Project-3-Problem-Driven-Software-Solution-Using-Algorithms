package com.unischeduler.service.mapper;

import com.unischeduler.domain.Exam;
import com.unischeduler.domain.ExamScheduleEntry;
import com.unischeduler.domain.Room;
import com.unischeduler.domain.Timeslot;
import com.unischeduler.domain.TimetableVersion;
import com.unischeduler.service.dto.ExamDTO;
import com.unischeduler.service.dto.ExamScheduleEntryDTO;
import com.unischeduler.service.dto.RoomDTO;
import com.unischeduler.service.dto.TimeslotDTO;
import com.unischeduler.service.dto.TimetableVersionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExamScheduleEntry} and its DTO {@link ExamScheduleEntryDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExamScheduleEntryMapper extends EntityMapper<ExamScheduleEntryDTO, ExamScheduleEntry> {
    @Mapping(target = "exam", source = "exam", qualifiedByName = "examName")
    @Mapping(target = "room", source = "room", qualifiedByName = "roomCode")
    @Mapping(target = "timeslot", source = "timeslot", qualifiedByName = "timeslotStartTime")
    @Mapping(target = "timetableVersion", source = "timetableVersion", qualifiedByName = "timetableVersionVersionNumber")
    ExamScheduleEntryDTO toDto(ExamScheduleEntry s);

    @Named("examName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ExamDTO toDtoExamName(Exam exam);

    @Named("roomCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    RoomDTO toDtoRoomCode(Room room);

    @Named("timeslotStartTime")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "startTime", source = "startTime")
    TimeslotDTO toDtoTimeslotStartTime(Timeslot timeslot);

    @Named("timetableVersionVersionNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "versionNumber", source = "versionNumber")
    TimetableVersionDTO toDtoTimetableVersionVersionNumber(TimetableVersion timetableVersion);
}
