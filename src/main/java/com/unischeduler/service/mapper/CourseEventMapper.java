package com.unischeduler.service.mapper;

import com.unischeduler.domain.Course;
import com.unischeduler.domain.CourseEvent;
import com.unischeduler.domain.Professor;
import com.unischeduler.domain.StudentGroup;
import com.unischeduler.service.dto.CourseDTO;
import com.unischeduler.service.dto.CourseEventDTO;
import com.unischeduler.service.dto.ProfessorDTO;
import com.unischeduler.service.dto.StudentGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CourseEvent} and its DTO {@link CourseEventDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseEventMapper extends EntityMapper<CourseEventDTO, CourseEvent> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseName")
    @Mapping(target = "professor", source = "professor", qualifiedByName = "professorLastName")
    @Mapping(target = "studentGroup", source = "studentGroup", qualifiedByName = "studentGroupName")
    CourseEventDTO toDto(CourseEvent s);

    @Named("courseName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CourseDTO toDtoCourseName(Course course);

    @Named("professorLastName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "lastName", source = "lastName")
    ProfessorDTO toDtoProfessorLastName(Professor professor);

    @Named("studentGroupName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    StudentGroupDTO toDtoStudentGroupName(StudentGroup studentGroup);
}
