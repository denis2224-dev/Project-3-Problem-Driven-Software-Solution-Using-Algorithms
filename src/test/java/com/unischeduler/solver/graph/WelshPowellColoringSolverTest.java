package com.unischeduler.solver.graph;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WelshPowellColoringSolverTest {

    private final WelshPowellColoringSolver solver = new WelshPowellColoringSolver();

    @Test
    void noAdjacentVerticesHaveSameColor() {
        ConflictGraph graph = triangleGraph();

        Map<GraphVertex, Integer> coloring = solver.color(graph);

        for (GraphEdge edge : graph.getEdges()) {
            assertThat(coloring.get(edge.getSource())).isNotEqualTo(coloring.get(edge.getTarget()));
        }
    }

    @Test
    void highDegreeVerticesAreProcessedFirst() {
        GraphVertex center = vertex("A");
        GraphVertex leafB = vertex("B");
        GraphVertex leafC = vertex("C");
        GraphVertex leafD = vertex("D");
        ConflictGraph graph = new ConflictGraph();
        graph.addEdge(center, leafB, "GROUP_CLASH");
        graph.addEdge(center, leafC, "GROUP_CLASH");
        graph.addEdge(center, leafD, "GROUP_CLASH");

        List<GraphVertex> orderedVertices = solver.orderVerticesByDescendingDegree(graph);

        assertThat(orderedVertices).first().isEqualTo(center);
    }

    @Test
    void simpleGraphProducesExpectedColorCount() {
        ConflictGraph graph = triangleGraph();

        Map<GraphVertex, Integer> coloring = solver.color(graph);

        assertThat(solver.colorCount(coloring)).isEqualTo(3);
    }

    private ConflictGraph triangleGraph() {
        GraphVertex first = vertex("A");
        GraphVertex second = vertex("B");
        GraphVertex third = vertex("C");
        ConflictGraph graph = new ConflictGraph();
        graph.addEdge(first, second, "GROUP_CLASH");
        graph.addEdge(second, third, "PROFESSOR_CLASH");
        graph.addEdge(first, third, "ROOM_CLASH");
        return graph;
    }

    private GraphVertex vertex(String id) {
        return new GraphVertex(id, "Event " + id);
    }
}
