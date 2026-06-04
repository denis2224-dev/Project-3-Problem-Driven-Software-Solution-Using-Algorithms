package com.unischeduler.service.mapper;

import com.unischeduler.domain.SolverJob;
import com.unischeduler.service.dto.SolverJobDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SolverJob} and its DTO {@link SolverJobDTO}.
 */
@Mapper(componentModel = "spring")
public interface SolverJobMapper extends EntityMapper<SolverJobDTO, SolverJob> {}
