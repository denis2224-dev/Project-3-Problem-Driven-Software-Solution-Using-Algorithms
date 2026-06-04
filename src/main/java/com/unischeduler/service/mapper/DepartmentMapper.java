package com.unischeduler.service.mapper;

import com.unischeduler.domain.Department;
import com.unischeduler.domain.Faculty;
import com.unischeduler.service.dto.DepartmentDTO;
import com.unischeduler.service.dto.FacultyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Department} and its DTO {@link DepartmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface DepartmentMapper extends EntityMapper<DepartmentDTO, Department> {
    @Mapping(target = "faculty", source = "faculty", qualifiedByName = "facultyName")
    DepartmentDTO toDto(Department s);

    @Named("facultyName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    FacultyDTO toDtoFacultyName(Faculty faculty);
}
