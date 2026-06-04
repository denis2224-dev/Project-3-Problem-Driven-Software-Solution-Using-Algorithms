package com.unischeduler.domain;

import static com.unischeduler.domain.SolverJobTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SolverJobTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolverJob.class);
        SolverJob solverJob1 = getSolverJobSample1();
        SolverJob solverJob2 = new SolverJob();
        assertThat(solverJob1).isNotEqualTo(solverJob2);

        solverJob2.setId(solverJob1.getId());
        assertThat(solverJob1).isEqualTo(solverJob2);

        solverJob2 = getSolverJobSample2();
        assertThat(solverJob1).isNotEqualTo(solverJob2);
    }
}
