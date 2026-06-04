package com.unischeduler.solver.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Undirected graph where vertices are events and edges are hard same-timeslot conflicts.
 */
public class ConflictGraph {

    private final Map<String, GraphVertex> verticesById = new LinkedHashMap<>();
    private final Set<GraphEdge> edges = new LinkedHashSet<>();
    private final Map<GraphVertex, Set<GraphVertex>> adjacency = new LinkedHashMap<>();

    public void addVertex(GraphVertex vertex) {
        addOrGetCanonicalVertex(vertex);
    }

    public void addEdge(GraphVertex source, GraphVertex target, String reason) {
        GraphVertex canonicalSource = addOrGetCanonicalVertex(source);
        GraphVertex canonicalTarget = addOrGetCanonicalVertex(target);
        GraphEdge edge = new GraphEdge(canonicalSource, canonicalTarget, reason);

        if (edges.add(edge)) {
            adjacency.get(canonicalSource).add(canonicalTarget);
            adjacency.get(canonicalTarget).add(canonicalSource);
        }
    }

    public Collection<GraphVertex> getVertices() {
        return Collections.unmodifiableCollection(verticesById.values());
    }

    public Set<GraphEdge> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    public Optional<GraphVertex> getVertex(String id) {
        return Optional.ofNullable(verticesById.get(id));
    }

    public Set<GraphVertex> neighborsOf(GraphVertex vertex) {
        GraphVertex canonicalVertex = verticesById.get(vertex.getId());
        if (canonicalVertex == null) {
            return Set.of();
        }
        return Collections.unmodifiableSet(adjacency.get(canonicalVertex));
    }

    public boolean hasEdge(GraphVertex source, GraphVertex target) {
        GraphVertex canonicalSource = verticesById.get(source.getId());
        GraphVertex canonicalTarget = verticesById.get(target.getId());
        if (canonicalSource == null || canonicalTarget == null) {
            return false;
        }
        return adjacency.get(canonicalSource).contains(canonicalTarget);
    }

    public int degreeOf(GraphVertex vertex) {
        return neighborsOf(vertex).size();
    }

    public int vertexCount() {
        return verticesById.size();
    }

    public int edgeCount() {
        return edges.size();
    }

    private GraphVertex addOrGetCanonicalVertex(GraphVertex vertex) {
        GraphVertex existing = verticesById.get(vertex.getId());
        if (existing != null) {
            return existing;
        }

        verticesById.put(vertex.getId(), vertex);
        adjacency.put(vertex, new LinkedHashSet<>());
        return vertex;
    }
}
