package com.unischeduler.solver.ac3;

import com.unischeduler.solver.csp.CSPValue;
import com.unischeduler.solver.csp.CSPVariable;
import java.util.Collections;
import java.util.List;

/**
 * Result of one AC-3 REVISE(Xi, Xj) operation.
 */
public class ReviseResult {

    private final CSPVariable variable;
    private final CSPVariable neighbor;
    private final List<CSPValue> removedValues;
    private final boolean domainWipedOut;

    public ReviseResult(CSPVariable variable, CSPVariable neighbor, List<CSPValue> removedValues, boolean domainWipedOut) {
        this.variable = variable;
        this.neighbor = neighbor;
        this.removedValues = List.copyOf(removedValues);
        this.domainWipedOut = domainWipedOut;
    }

    public CSPVariable getVariable() {
        return variable;
    }

    public CSPVariable getNeighbor() {
        return neighbor;
    }

    public List<CSPValue> getRemovedValues() {
        return Collections.unmodifiableList(removedValues);
    }

    public boolean isRevised() {
        return !removedValues.isEmpty();
    }

    public boolean isDomainWipedOut() {
        return domainWipedOut;
    }
}
