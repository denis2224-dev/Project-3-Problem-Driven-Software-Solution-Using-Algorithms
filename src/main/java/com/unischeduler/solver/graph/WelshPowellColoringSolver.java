package com.unischeduler.solver.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manual implementation of the Welsh-Powell graph coloring heuristic.
 */
public class WelshPowellColoringSolver {

    public Map<GraphVertex, Integer> color(ConflictGraph graph) {
        List<GraphVertex> orderedVertices = orderVerticesByDescendingDegree(graph);
        Map<GraphVertex, Integer> coloring = new LinkedHashMap<>();
        int color = 0;

        for (GraphVertex seed : orderedVertices) {
            if (coloring.containsKey(seed)) {
                continue;
            }

            // Start a new color class with the highest-degree uncolored vertex.
            coloring.put(seed, color);

            for (GraphVertex candidate : orderedVertices) {
                // A vertex can reuse this color only if it is not adjacent to any vertex already colored with it.
                if (!coloring.containsKey(candidate) && canUseColor(candidate, color, coloring, graph)) {
                    coloring.put(candidate, color);
                }
            }

            color++;
        }

        return Collections.unmodifiableMap(coloring);
    }

    public List<GraphVertex> orderVerticesByDescendingDegree(ConflictGraph graph) {
        List<GraphVertex> orderedVertices = new ArrayList<>(graph.getVertices());

        // Welsh-Powell first sorts vertices by descending degree; deterministic tie-breakers keep demos reproducible.
        orderedVertices.sort(
            Comparator.comparingInt((GraphVertex vertex) -> graph.degreeOf(vertex))
                .reversed()
                .thenComparing(GraphVertex::getLabel)
                .thenComparing(GraphVertex::getId)
        );

        return orderedVertices;
    }

    public int colorCount(Map<GraphVertex, Integer> coloring) {
        return coloring.values().stream().mapToInt(Integer::intValue).max().orElse(-1) + 1;
    }

    private boolean canUseColor(GraphVertex candidate, int color, Map<GraphVertex, Integer> coloring, ConflictGraph graph) {
        for (Map.Entry<GraphVertex, Integer> assignment : coloring.entrySet()) {
            if (assignment.getValue() == color && graph.hasEdge(candidate, assignment.getKey())) {
                return false;
            }
        }
        return true;
    }
}
