package com.unischeduler.service.mapper;

import com.unischeduler.domain.Course;
import com.unischeduler.domain.Department;
import com.unischeduler.service.dto.CourseDTO;
import com.unischeduler.service.dto.DepartmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Course} and its DTO {@link CourseDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseMapper extends EntityMapper<CourseDTO, Course> {
    @Mapping(target = "department", source = "department", qualifiedByName = "departmentName")
    CourseDTO toDto(Course s);

    @Named("departmentName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DepartmentDTO toDtoDepartmentName(Department department);
}
