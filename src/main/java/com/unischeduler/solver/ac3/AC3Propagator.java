package com.unischeduler.solver.ac3;

import com.unischeduler.solver.csp.CSPConstraint;
import com.unischeduler.solver.csp.CSPModel;
import com.unischeduler.solver.csp.CSPValue;
import com.unischeduler.solver.csp.CSPVariable;
import com.unischeduler.solver.csp.Domain;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * AC-3 arc-consistency propagator for binary CSP constraints.
 */
public class AC3Propagator {

    public AC3Result propagate(CSPModel model) {
        Deque<Arc> queue = new ArrayDeque<>();
        List<ReviseResult> revisions = new ArrayList<>();

        // AC-3 starts by enqueueing both directions of every binary constraint arc.
        for (CSPConstraint constraint : model.getConstraints()) {
            queue.add(new Arc(constraint.getFirstVariable(), constraint.getSecondVariable()));
            queue.add(new Arc(constraint.getSecondVariable(), constraint.getFirstVariable()));
        }

        while (!queue.isEmpty()) {
            Arc arc = queue.removeFirst();
            ReviseResult revision = revise(model, arc.variable(), arc.neighbor());

            if (!revision.isRevised()) {
                continue;
            }

            revisions.add(revision);
            if (revision.isDomainWipedOut()) {
                return new AC3Result(false, revisions, arc.variable());
            }

            // If D(Xi) changed, every predecessor Xk must be checked again against Xi.
            for (CSPVariable neighbor : model.neighborsOf(arc.variable())) {
                if (!neighbor.equals(arc.neighbor())) {
                    queue.add(new Arc(neighbor, arc.variable()));
                }
            }
        }

        return new AC3Result(true, revisions, null);
    }

    public ReviseResult revise(CSPModel model, CSPVariable variable, CSPVariable neighbor) {
        Domain domain = model.getDomain(variable);
        List<CSPValue> removedValues = new ArrayList<>();

        // REVISE removes each value in D(Xi) that has no supporting value in D(Xj).
        for (CSPValue value : new ArrayList<>(domain.getValues())) {
            if (!hasSupport(model, variable, value, neighbor)) {
                domain.remove(value);
                removedValues.add(value);
            }
        }

        return new ReviseResult(variable, neighbor, removedValues, domain.isEmpty());
    }

    private boolean hasSupport(CSPModel model, CSPVariable variable, CSPValue value, CSPVariable neighbor) {
        for (CSPValue neighborValue : model.getDomain(neighbor).getValues()) {
            if (satisfiesAllConstraints(model, variable, value, neighbor, neighborValue)) {
                return true;
            }
        }
        return false;
    }

    private boolean satisfiesAllConstraints(
        CSPModel model,
        CSPVariable variable,
        CSPValue value,
        CSPVariable neighbor,
        CSPValue neighborValue
    ) {
        for (CSPConstraint constraint : model.constraintsBetween(variable, neighbor)) {
            if (!constraint.isSatisfied(variable, value, neighbor, neighborValue)) {
                return false;
            }
        }
        return true;
    }

    private record Arc(CSPVariable variable, CSPVariable neighbor) {}
}
