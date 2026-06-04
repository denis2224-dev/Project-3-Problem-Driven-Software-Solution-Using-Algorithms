package com.unischeduler.service.mapper;

import com.unischeduler.domain.ScheduleConflict;
import com.unischeduler.domain.TimetableVersion;
import com.unischeduler.service.dto.ScheduleConflictDTO;
import com.unischeduler.service.dto.TimetableVersionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ScheduleConflict} and its DTO {@link ScheduleConflictDTO}.
 */
@Mapper(componentModel = "spring")
public interface ScheduleConflictMapper extends EntityMapper<ScheduleConflictDTO, ScheduleConflict> {
    @Mapping(target = "timetableVersion", source = "timetableVersion", qualifiedByName = "timetableVersionVersionNumber")
    ScheduleConflictDTO toDto(ScheduleConflict s);

    @Named("timetableVersionVersionNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "versionNumber", source = "versionNumber")
    TimetableVersionDTO toDtoTimetableVersionVersionNumber(TimetableVersion timetableVersion);
}
