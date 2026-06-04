# Algorithms

The scheduling problem is represented as a Constraint Satisfaction Problem:

```text
P = (X, D, C)
```

- `X`: events to schedule.
- `D`: possible values for each event, implemented as timeslot-room tuples.
- `C`: hard and soft constraints.

## Welsh-Powell Graph Coloring

Implementation:

- `ConflictGraph`
- `ConflictGraphBuilder`
- `GraphVertex`
- `GraphEdge`
- `WelshPowellColoringSolver`

Purpose:

1. Build a graph where each course event or exam is a vertex.
2. Add edges for events that cannot share a timeslot.
3. Sort vertices by descending degree.
4. Greedily assign the lowest valid color.
5. Use colors as a warm start for CSP domain ordering.

Complexity:

```text
O(V^2 + E)
```

The pairwise graph construction compares event pairs. Coloring sorts by degree and scans compatibility within each color class.

## Backtracking MAC With AC-3

Implementation:

- `CSPModel`
- `CSPVariable`
- `CSPValue`
- `CSPConstraint`
- `Domain`
- `Assignment`
- `AC3Propagator`
- `BacktrackingMacSolver`
- `MRVVariableSelector`
- `LCVValueOrdering`

Purpose:

1. Build domains from valid room-timeslot combinations.
2. Remove values violating unary constraints such as room capacity or professor unavailability.
3. Run AC-3 to remove unsupported values from binary constraints.
4. Select variables with MRV.
5. Order values with LCV.
6. Recursively assign values and maintain arc consistency after each assignment.

Worst-case complexity:

```text
O(d^n)
```

AC-3 and heuristics reduce practical search space but do not remove the theoretical exponential worst case.

## Hard Constraints

The solver enforces:

- Professor cannot teach two events at the same time.
- Student group cannot attend two mandatory events at the same time.
- Room cannot host two events at the same time.
- Room capacity must satisfy expected students.
- Required equipment must exist in the assigned room.
- Professor unavailable times are excluded from domains.

## Soft Constraint Scoring

Implementation:

- `SoftConstraintScorer`
- `SolverStatistics`
- `SolverResult`

Soft penalties include:

- Student idle gaps.
- Professor idle gaps.
- Professor preference violations.
- Building transitions.
- Early or late edge-hour pressure.

The result is valid if hard conflicts are zero. Soft score is used to explain schedule quality.

## Exam Coloring

The exam page calls:

```http
GET /api/exams/coloring
```

This endpoint builds an exam conflict graph where shared student groups create edges, then colors exams with `WelshPowellColoringSolver`. Each color is shown as an exam period.
