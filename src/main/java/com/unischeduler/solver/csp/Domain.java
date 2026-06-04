package com.unischeduler.solver.csp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Mutable domain of allowed values for one CSP variable.
 */
public class Domain {

    private final CSPVariable variable;
    private final List<CSPValue> values = new ArrayList<>();

    public Domain(CSPVariable variable, Collection<CSPValue> values) {
        this.variable = Objects.requireNonNull(variable, "variable must not be null");
        this.values.addAll(Objects.requireNonNull(values, "values must not be null"));
    }

    public CSPVariable getVariable() {
        return variable;
    }

    public List<CSPValue> getValues() {
        return Collections.unmodifiableList(values);
    }

    public boolean remove(CSPValue value) {
        return values.remove(value);
    }

    public boolean contains(CSPValue value) {
        return values.contains(value);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public int size() {
        return values.size();
    }

    public Domain copy() {
        return new Domain(variable, values);
    }
}
