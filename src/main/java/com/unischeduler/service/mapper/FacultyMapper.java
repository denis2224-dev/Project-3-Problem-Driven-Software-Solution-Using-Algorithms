package com.unischeduler.service.mapper;

import com.unischeduler.domain.Faculty;
import com.unischeduler.service.dto.FacultyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Faculty} and its DTO {@link FacultyDTO}.
 */
@Mapper(componentModel = "spring")
public interface FacultyMapper extends EntityMapper<FacultyDTO, Faculty> {}
