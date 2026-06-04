package com.unischeduler.service.mapper;

import com.unischeduler.domain.Professor;
import com.unischeduler.domain.ProfessorPreference;
import com.unischeduler.domain.Timeslot;
import com.unischeduler.service.dto.ProfessorDTO;
import com.unischeduler.service.dto.ProfessorPreferenceDTO;
import com.unischeduler.service.dto.TimeslotDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProfessorPreference} and its DTO {@link ProfessorPreferenceDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProfessorPreferenceMapper extends EntityMapper<ProfessorPreferenceDTO, ProfessorPreference> {
    @Mapping(target = "professor", source = "professor", qualifiedByName = "professorLastName")
    @Mapping(target = "timeslot", source = "timeslot", qualifiedByName = "timeslotStartTime")
    ProfessorPreferenceDTO toDto(ProfessorPreference s);

    @Named("professorLastName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "lastName", source = "lastName")
    ProfessorDTO toDtoProfessorLastName(Professor professor);

    @Named("timeslotStartTime")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "startTime", source = "startTime")
    TimeslotDTO toDtoTimeslotStartTime(Timeslot timeslot);
}
