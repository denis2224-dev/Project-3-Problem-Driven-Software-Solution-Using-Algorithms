package com.unischeduler.solver.graph;

import java.util.Objects;

/**
 * Undirected edge between two events that cannot share a timeslot.
 */
public class GraphEdge {

    private final GraphVertex source;
    private final GraphVertex target;
    private final String reason;

    public GraphEdge(GraphVertex source, GraphVertex target, String reason) {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(target, "target must not be null");
        if (source.equals(target)) {
            throw new IllegalArgumentException("A conflict edge cannot connect a vertex to itself");
        }

        if (source.getId().compareTo(target.getId()) <= 0) {
            this.source = source;
            this.target = target;
        } else {
            this.source = target;
            this.target = source;
        }
        this.reason = reason == null || reason.isBlank() ? "CONFLICT" : reason;
    }

    public GraphVertex getSource() {
        return source;
    }

    public GraphVertex getTarget() {
        return target;
    }

    public String getReason() {
        return reason;
    }

    public boolean connects(GraphVertex left, GraphVertex right) {
        return (source.equals(left) && target.equals(right)) || (source.equals(right) && target.equals(left));
    }

    public GraphVertex other(GraphVertex vertex) {
        if (source.equals(vertex)) {
            return target;
        }
        if (target.equals(vertex)) {
            return source;
        }
        throw new IllegalArgumentException("Vertex is not part of this edge");
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof GraphEdge graphEdge)) {
            return false;
        }
        return source.equals(graphEdge.source) && target.equals(graphEdge.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }

    @Override
    public String toString() {
        return "GraphEdge{" + "source=" + source + ", target=" + target + ", reason='" + reason + '\'' + '}';
    }
}
