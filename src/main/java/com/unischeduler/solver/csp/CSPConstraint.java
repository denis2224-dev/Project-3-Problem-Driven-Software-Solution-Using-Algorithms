package com.unischeduler.solver.csp;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * Binary hard constraint between two CSP variables.
 */
public class CSPConstraint {

    private final CSPVariable firstVariable;
    private final CSPVariable secondVariable;
    private final String description;
    private final BiPredicate<CSPValue, CSPValue> predicate;

    public CSPConstraint(
        CSPVariable firstVariable,
        CSPVariable secondVariable,
        String description,
        BiPredicate<CSPValue, CSPValue> predicate
    ) {
        this.firstVariable = Objects.requireNonNull(firstVariable, "firstVariable must not be null");
        this.secondVariable = Objects.requireNonNull(secondVariable, "secondVariable must not be null");
        if (firstVariable.equals(secondVariable)) {
            throw new IllegalArgumentException("A binary CSP constraint must connect two different variables");
        }
        this.description = description == null || description.isBlank() ? "binary constraint" : description;
        this.predicate = Objects.requireNonNull(predicate, "predicate must not be null");
    }

    public CSPVariable getFirstVariable() {
        return firstVariable;
    }

    public CSPVariable getSecondVariable() {
        return secondVariable;
    }

    public String getDescription() {
        return description;
    }

    public boolean connects(CSPVariable left, CSPVariable right) {
        return (firstVariable.equals(left) && secondVariable.equals(right)) || (firstVariable.equals(right) && secondVariable.equals(left));
    }

    public boolean involves(CSPVariable variable) {
        return firstVariable.equals(variable) || secondVariable.equals(variable);
    }

    public CSPVariable other(CSPVariable variable) {
        if (firstVariable.equals(variable)) {
            return secondVariable;
        }
        if (secondVariable.equals(variable)) {
            return firstVariable;
        }
        throw new IllegalArgumentException("Variable is not part of this constraint");
    }

    public boolean isSatisfied(CSPVariable leftVariable, CSPValue leftValue, CSPVariable rightVariable, CSPValue rightValue) {
        if (firstVariable.equals(leftVariable) && secondVariable.equals(rightVariable)) {
            return predicate.test(leftValue, rightValue);
        }
        if (firstVariable.equals(rightVariable) && secondVariable.equals(leftVariable)) {
            return predicate.test(rightValue, leftValue);
        }
        throw new IllegalArgumentException("Values do not belong to this binary constraint");
    }
}
