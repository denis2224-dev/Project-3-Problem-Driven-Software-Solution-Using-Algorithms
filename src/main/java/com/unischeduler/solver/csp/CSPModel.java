package com.unischeduler.solver.csp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Complete CSP definition P = (X, D, C).
 */
public class CSPModel {

    private final List<CSPVariable> variables = new ArrayList<>();
    private final Map<CSPVariable, Domain> domains = new LinkedHashMap<>();
    private final List<CSPConstraint> constraints = new ArrayList<>();
    private final Map<CSPVariable, Set<CSPVariable>> neighborsByVariable = new LinkedHashMap<>();
    private final Map<CSPVariable, Map<CSPVariable, List<CSPConstraint>>> constraintsByVariable = new LinkedHashMap<>();

    public void addVariable(CSPVariable variable, Collection<CSPValue> values) {
        if (!domains.containsKey(variable)) {
            variables.add(variable);
            neighborsByVariable.computeIfAbsent(variable, ignored -> new LinkedHashSet<>());
            constraintsByVariable.computeIfAbsent(variable, ignored -> new LinkedHashMap<>());
        }
        domains.put(variable, new Domain(variable, values));
    }

    public void addConstraint(CSPConstraint constraint) {
        constraints.add(constraint);
        indexConstraint(constraint.getFirstVariable(), constraint.getSecondVariable(), constraint);
        indexConstraint(constraint.getSecondVariable(), constraint.getFirstVariable(), constraint);
    }

    public List<CSPVariable> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    public Domain getDomain(CSPVariable variable) {
        Domain domain = domains.get(variable);
        if (domain == null) {
            throw new IllegalArgumentException("No domain registered for variable " + variable.getId());
        }
        return domain;
    }

    public void restrictDomain(CSPVariable variable, Collection<CSPValue> values) {
        getDomain(variable).replaceValues(values);
    }

    public List<CSPConstraint> getConstraints() {
        return Collections.unmodifiableList(constraints);
    }

    public List<CSPConstraint> constraintsBetween(CSPVariable left, CSPVariable right) {
        Map<CSPVariable, List<CSPConstraint>> byNeighbor = constraintsByVariable.get(left);
        if (byNeighbor == null) {
            return List.of();
        }
        return Collections.unmodifiableList(byNeighbor.getOrDefault(right, List.of()));
    }

    public Set<CSPVariable> neighborsOf(CSPVariable variable) {
        return Collections.unmodifiableSet(neighborsByVariable.getOrDefault(variable, Set.of()));
    }

    public boolean isConsistentWith(CSPVariable variable, CSPValue value, Assignment assignment) {
        for (Map.Entry<CSPVariable, CSPValue> assigned : assignment.asMap().entrySet()) {
            for (CSPConstraint constraint : constraintsBetween(variable, assigned.getKey())) {
                if (!constraint.isSatisfied(variable, value, assigned.getKey(), assigned.getValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    public CSPModel copy() {
        CSPModel copy = new CSPModel();
        for (CSPVariable variable : variables) {
            copy.addVariable(variable, getDomain(variable).getValues());
        }
        constraints.forEach(copy::addConstraint);
        return copy;
    }

    private void indexConstraint(CSPVariable variable, CSPVariable neighbor, CSPConstraint constraint) {
        neighborsByVariable.computeIfAbsent(variable, ignored -> new LinkedHashSet<>()).add(neighbor);
        constraintsByVariable
            .computeIfAbsent(variable, ignored -> new LinkedHashMap<>())
            .computeIfAbsent(neighbor, ignored -> new ArrayList<>())
            .add(constraint);
    }
}
