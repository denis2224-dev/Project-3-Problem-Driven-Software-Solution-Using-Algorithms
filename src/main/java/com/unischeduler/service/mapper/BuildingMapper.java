package com.unischeduler.service.mapper;

import com.unischeduler.domain.Building;
import com.unischeduler.service.dto.BuildingDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Building} and its DTO {@link BuildingDTO}.
 */
@Mapper(componentModel = "spring")
public interface BuildingMapper extends EntityMapper<BuildingDTO, Building> {}
