package com.unischeduler.service.mapper;

import com.unischeduler.domain.Timetable;
import com.unischeduler.service.dto.TimetableDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Timetable} and its DTO {@link TimetableDTO}.
 */
@Mapper(componentModel = "spring")
public interface TimetableMapper extends EntityMapper<TimetableDTO, Timetable> {}
