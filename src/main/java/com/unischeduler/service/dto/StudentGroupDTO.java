package com.unischeduler.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.unischeduler.domain.StudentGroup} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudentGroupDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String name;

    @NotNull
    @Min(value = 1)
    @Max(value = 6)
    private Integer year;

    @NotNull
    @Min(value = 1)
    private Integer groupSize;

    @NotNull
    private DepartmentDTO department;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(Integer groupSize) {
        this.groupSize = groupSize;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentGroupDTO)) {
            return false;
        }

        StudentGroupDTO studentGroupDTO = (StudentGroupDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, studentGroupDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudentGroupDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", year=" + getYear() +
            ", groupSize=" + getGroupSize() +
            ", department=" + getDepartment() +
            "}";
    }
}
