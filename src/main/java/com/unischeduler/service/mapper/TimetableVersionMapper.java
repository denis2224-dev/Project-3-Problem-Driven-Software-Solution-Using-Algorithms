package com.unischeduler.service.mapper;

import com.unischeduler.domain.SolverJob;
import com.unischeduler.domain.Timetable;
import com.unischeduler.domain.TimetableVersion;
import com.unischeduler.service.dto.SolverJobDTO;
import com.unischeduler.service.dto.TimetableDTO;
import com.unischeduler.service.dto.TimetableVersionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TimetableVersion} and its DTO {@link TimetableVersionDTO}.
 */
@Mapper(componentModel = "spring")
public interface TimetableVersionMapper extends EntityMapper<TimetableVersionDTO, TimetableVersion> {
    @Mapping(target = "timetable", source = "timetable", qualifiedByName = "timetableName")
    @Mapping(target = "solverJob", source = "solverJob", qualifiedByName = "solverJobId")
    TimetableVersionDTO toDto(TimetableVersion s);

    @Named("timetableName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TimetableDTO toDtoTimetableName(Timetable timetable);

    @Named("solverJobId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SolverJobDTO toDtoSolverJobId(SolverJob solverJob);
}
