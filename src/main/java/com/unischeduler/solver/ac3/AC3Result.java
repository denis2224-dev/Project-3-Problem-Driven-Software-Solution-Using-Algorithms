package com.unischeduler.solver.ac3;

import com.unischeduler.solver.csp.CSPVariable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Aggregate result of enforcing AC-3 over a CSP model.
 */
public class AC3Result {

    private final boolean arcConsistent;
    private final List<ReviseResult> revisions;
    private final CSPVariable emptyDomainVariable;

    public AC3Result(boolean arcConsistent, List<ReviseResult> revisions, CSPVariable emptyDomainVariable) {
        this.arcConsistent = arcConsistent;
        this.revisions = List.copyOf(revisions);
        this.emptyDomainVariable = emptyDomainVariable;
    }

    public boolean isArcConsistent() {
        return arcConsistent;
    }

    public List<ReviseResult> getRevisions() {
        return Collections.unmodifiableList(revisions);
    }

    public Optional<CSPVariable> getEmptyDomainVariable() {
        return Optional.ofNullable(emptyDomainVariable);
    }

    public int getDomainReductionCount() {
        return revisions.stream().mapToInt(revision -> revision.getRemovedValues().size()).sum();
    }
}
