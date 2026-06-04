package com.unischeduler.service.mapper;

import com.unischeduler.domain.Department;
import com.unischeduler.domain.Professor;
import com.unischeduler.service.dto.DepartmentDTO;
import com.unischeduler.service.dto.ProfessorDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Professor} and its DTO {@link ProfessorDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProfessorMapper extends EntityMapper<ProfessorDTO, Professor> {
    @Mapping(target = "department", source = "department", qualifiedByName = "departmentName")
    ProfessorDTO toDto(Professor s);

    @Named("departmentName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DepartmentDTO toDtoDepartmentName(Department department);
}
