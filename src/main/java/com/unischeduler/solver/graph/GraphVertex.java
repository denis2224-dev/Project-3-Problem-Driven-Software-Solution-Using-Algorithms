package com.unischeduler.solver.graph;

import java.util.Objects;

/**
 * Vertex in the timetabling conflict graph.
 */
public class GraphVertex {

    private final String id;
    private final String label;
    private final String sourceType;
    private final Long sourceId;
    private final Long professorId;
    private final Long studentGroupId;

    public GraphVertex(String id, String label) {
        this(id, label, null, null, null, null);
    }

    public GraphVertex(String id, String label, String sourceType, Long sourceId, Long professorId, Long studentGroupId) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.label = Objects.requireNonNull(label, "label must not be null");
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.professorId = professorId;
        this.studentGroupId = studentGroupId;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getSourceType() {
        return sourceType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public Long getStudentGroupId() {
        return studentGroupId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof GraphVertex graphVertex)) {
            return false;
        }
        return id.equals(graphVertex.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "GraphVertex{" + "id='" + id + '\'' + ", label='" + label + '\'' + '}';
    }
}
