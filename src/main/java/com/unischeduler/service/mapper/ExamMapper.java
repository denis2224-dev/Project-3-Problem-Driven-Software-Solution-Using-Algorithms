package com.unischeduler.service.mapper;

import com.unischeduler.domain.Course;
import com.unischeduler.domain.Exam;
import com.unischeduler.domain.StudentGroup;
import com.unischeduler.service.dto.CourseDTO;
import com.unischeduler.service.dto.ExamDTO;
import com.unischeduler.service.dto.StudentGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Exam} and its DTO {@link ExamDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExamMapper extends EntityMapper<ExamDTO, Exam> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseCode")
    @Mapping(target = "studentGroup", source = "studentGroup", qualifiedByName = "studentGroupName")
    ExamDTO toDto(Exam s);

    @Named("courseCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    CourseDTO toDtoCourseCode(Course course);

    @Named("studentGroupName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    StudentGroupDTO toDtoStudentGroupName(StudentGroup studentGroup);
}
