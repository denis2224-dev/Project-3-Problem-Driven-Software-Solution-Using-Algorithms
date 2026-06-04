package com.unischeduler.solver.csp;

import java.util.Objects;

/**
 * CSP variable representing one event that must receive a timeslot-room value.
 */
public class CSPVariable {

    private final String id;
    private final String label;
    private final Long sourceEventId;
    private final Long professorId;
    private final Long studentGroupId;
    private final Integer expectedStudents;
    private final String requiredEquipment;

    public CSPVariable(String id, String label) {
        this(id, label, null, null, null, null, null);
    }

    public CSPVariable(
        String id,
        String label,
        Long sourceEventId,
        Long professorId,
        Long studentGroupId,
        Integer expectedStudents,
        String requiredEquipment
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.label = Objects.requireNonNull(label, "label must not be null");
        this.sourceEventId = sourceEventId;
        this.professorId = professorId;
        this.studentGroupId = studentGroupId;
        this.expectedStudents = expectedStudents;
        this.requiredEquipment = requiredEquipment;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Long getSourceEventId() {
        return sourceEventId;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public Long getStudentGroupId() {
        return studentGroupId;
    }

    public Integer getExpectedStudents() {
        return expectedStudents;
    }

    public String getRequiredEquipment() {
        return requiredEquipment;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CSPVariable that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CSPVariable{" + "id='" + id + '\'' + ", label='" + label + '\'' + '}';
    }
}
