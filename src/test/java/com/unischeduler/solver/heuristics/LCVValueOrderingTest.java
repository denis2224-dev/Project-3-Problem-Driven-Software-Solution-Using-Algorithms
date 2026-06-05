package com.unischeduler.solver.heuristics;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.solver.csp.Assignment;
import com.unischeduler.solver.csp.CSPModel;
import com.unischeduler.solver.csp.CSPValue;
import com.unischeduler.solver.csp.CSPVariable;
import java.util.List;
import org.junit.jupiter.api.Test;

class LCVValueOrderingTest {

    private final LCVValueOrdering ordering = new LCVValueOrdering();

    @Test
    void preservesDomainWarmStartOrderWhenValuesAreEquallyConstraining() {
        CSPVariable variable = new CSPVariable("event-1", "Event 1");
        CSPValue preferredValue = new CSPValue("preferred-slot");
        CSPValue fallbackValue = new CSPValue("fallback-slot");
        CSPModel model = new CSPModel();
        model.addVariable(variable, List.of(preferredValue, fallbackValue));

        List<CSPValue> values = ordering.orderValues(model, variable, new Assignment());

        assertThat(values).containsExactly(preferredValue, fallbackValue);
    }
}
