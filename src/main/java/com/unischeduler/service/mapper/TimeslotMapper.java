package com.unischeduler.service.mapper;

import com.unischeduler.domain.Timeslot;
import com.unischeduler.service.dto.TimeslotDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Timeslot} and its DTO {@link TimeslotDTO}.
 */
@Mapper(componentModel = "spring")
public interface TimeslotMapper extends EntityMapper<TimeslotDTO, Timeslot> {}
