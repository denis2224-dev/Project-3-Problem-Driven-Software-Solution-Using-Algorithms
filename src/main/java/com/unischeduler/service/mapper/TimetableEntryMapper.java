package com.unischeduler.service.mapper;

import com.unischeduler.domain.CourseEvent;
import com.unischeduler.domain.Room;
import com.unischeduler.domain.Timeslot;
import com.unischeduler.domain.TimetableEntry;
import com.unischeduler.domain.TimetableVersion;
import com.unischeduler.service.dto.CourseEventDTO;
import com.unischeduler.service.dto.RoomDTO;
import com.unischeduler.service.dto.TimeslotDTO;
import com.unischeduler.service.dto.TimetableEntryDTO;
import com.unischeduler.service.dto.TimetableVersionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TimetableEntry} and its DTO {@link TimetableEntryDTO}.
 */
@Mapper(componentModel = "spring")
public interface TimetableEntryMapper extends EntityMapper<TimetableEntryDTO, TimetableEntry> {
    @Mapping(target = "timetableVersion", source = "timetableVersion", qualifiedByName = "timetableVersionVersionNumber")
    @Mapping(target = "courseEvent", source = "courseEvent", qualifiedByName = "courseEventId")
    @Mapping(target = "room", source = "room", qualifiedByName = "roomCode")
    @Mapping(target = "timeslot", source = "timeslot", qualifiedByName = "timeslotStartTime")
    TimetableEntryDTO toDto(TimetableEntry s);

    @Named("timetableVersionVersionNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "versionNumber", source = "versionNumber")
    TimetableVersionDTO toDtoTimetableVersionVersionNumber(TimetableVersion timetableVersion);

    @Named("courseEventId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseEventDTO toDtoCourseEventId(CourseEvent courseEvent);

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
}
