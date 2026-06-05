package com.unischeduler.solver.ac3;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.solver.csp.CSPConstraint;
import com.unischeduler.solver.csp.CSPModel;
import com.unischeduler.solver.csp.CSPValue;
import com.unischeduler.solver.csp.CSPVariable;
import java.util.List;
import org.junit.jupiter.api.Test;

class AC3PropagatorTest {

    private final AC3Propagator propagator = new AC3Propagator();

    @Test
    void inconsistentDomainValuesAreRemoved() {
        CSPVariable first = variable("X1");
        CSPVariable second = variable("X2");
        CSPValue value1 = value("room-a-slot-1");
        CSPValue value2 = value("room-b-slot-1");
        CSPModel model = new CSPModel();
        model.addVariable(first, List.of(value1, value2));
        model.addVariable(second, List.of(value2));
        model.addConstraint(notEqual(first, second));

        AC3Result result = propagator.propagate(model);

        assertThat(result.isArcConsistent()).isTrue();
        assertThat(result.getDomainReductionCount()).isEqualTo(1);
        assertThat(model.getDomain(first).getValues()).containsExactly(value1);
    }

    @Test
    void emptyDomainCausesInfeasibility() {
        CSPVariable first = variable("X1");
        CSPVariable second = variable("X2");
        CSPValue onlyValue = value("room-a-slot-1");
        CSPModel model = new CSPModel();
        model.addVariable(first, List.of(onlyValue));
        model.addVariable(second, List.of(onlyValue));
        model.addConstraint(notEqual(first, second));

        AC3Result result = propagator.propagate(model);

        assertThat(result.isArcConsistent()).isFalse();
        assertThat(result.getEmptyDomainVariable()).contains(first);
        assertThat(model.getDomain(first).isEmpty()).isTrue();
    }

    @Test
    void consistentCspRemainsValid() {
        CSPVariable first = variable("X1");
        CSPVariable second = variable("X2");
        CSPValue value1 = value("room-a-slot-1");
        CSPValue value2 = value("room-b-slot-1");
        CSPModel model = new CSPModel();
        model.addVariable(first, List.of(value1));
        model.addVariable(second, List.of(value2));
        model.addConstraint(notEqual(first, second));

        AC3Result result = propagator.propagate(model);

        assertThat(result.isArcConsistent()).isTrue();
        assertThat(result.getDomainReductionCount()).isZero();
        assertThat(model.getDomain(first).getValues()).containsExactly(value1);
        assertThat(model.getDomain(second).getValues()).containsExactly(value2);
    }

    @Test
    void propagationFromAssignedVariableCascadesToAffectedNeighbors() {
        CSPVariable first = variable("X1");
        CSPVariable second = variable("X2");
        CSPVariable third = variable("X3");
        CSPValue value1 = value("room-a-slot-1");
        CSPValue value2 = value("room-b-slot-1");
        CSPValue value3 = value("room-c-slot-1");
        CSPModel model = new CSPModel();
        model.addVariable(first, List.of(value1));
        model.addVariable(second, List.of(value1, value2));
        model.addVariable(third, List.of(value2, value3));
        model.addConstraint(notEqual(first, second));
        model.addConstraint(notEqual(second, third));

        AC3Result result = propagator.propagateFrom(model, first);

        assertThat(result.isArcConsistent()).isTrue();
        assertThat(model.getDomain(second).getValues()).containsExactly(value2);
        assertThat(model.getDomain(third).getValues()).containsExactly(value3);
    }

    private CSPConstraint notEqual(CSPVariable first, CSPVariable second) {
        return new CSPConstraint(first, second, "values must differ", (left, right) -> !left.equals(right));
    }

    private CSPVariable variable(String id) {
        return new CSPVariable(id, "Variable " + id);
    }

    private CSPValue value(String id) {
        return new CSPValue(id);
    }
}
