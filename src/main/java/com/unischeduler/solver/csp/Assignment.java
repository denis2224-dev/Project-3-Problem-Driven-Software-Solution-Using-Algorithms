package com.unischeduler.solver.csp;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Partial or complete assignment produced by backtracking.
 */
public class Assignment {

    private final Map<CSPVariable, CSPValue> assignedValues = new LinkedHashMap<>();

    public void assign(CSPVariable variable, CSPValue value) {
        assignedValues.put(variable, value);
    }

    public void unassign(CSPVariable variable) {
        assignedValues.remove(variable);
    }

    public boolean isAssigned(CSPVariable variable) {
        return assignedValues.containsKey(variable);
    }

    public Optional<CSPValue> valueOf(CSPVariable variable) {
        return Optional.ofNullable(assignedValues.get(variable));
    }

    public Map<CSPVariable, CSPValue> asMap() {
        return Collections.unmodifiableMap(assignedValues);
    }

    public int size() {
        return assignedValues.size();
    }

    public boolean isComplete(CSPModel model) {
        return assignedValues.size() == model.getVariables().size();
    }

    public Assignment copy() {
        Assignment copy = new Assignment();
        assignedValues.forEach(copy::assign);
        return copy;
    }
}
