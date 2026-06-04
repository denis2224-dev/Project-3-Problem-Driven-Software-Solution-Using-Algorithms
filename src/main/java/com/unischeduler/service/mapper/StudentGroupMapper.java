package com.unischeduler.service.mapper;

import com.unischeduler.domain.Department;
import com.unischeduler.domain.StudentGroup;
import com.unischeduler.service.dto.DepartmentDTO;
import com.unischeduler.service.dto.StudentGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StudentGroup} and its DTO {@link StudentGroupDTO}.
 */
@Mapper(componentModel = "spring")
public interface StudentGroupMapper extends EntityMapper<StudentGroupDTO, StudentGroup> {
    @Mapping(target = "department", source = "department", qualifiedByName = "departmentName")
    StudentGroupDTO toDto(StudentGroup s);

    @Named("departmentName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DepartmentDTO toDtoDepartmentName(Department department);
}
