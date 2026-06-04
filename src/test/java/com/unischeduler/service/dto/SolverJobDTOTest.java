package com.unischeduler.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SolverJobDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolverJobDTO.class);
        SolverJobDTO solverJobDTO1 = new SolverJobDTO();
        solverJobDTO1.setId(1L);
        SolverJobDTO solverJobDTO2 = new SolverJobDTO();
        assertThat(solverJobDTO1).isNotEqualTo(solverJobDTO2);
        solverJobDTO2.setId(solverJobDTO1.getId());
        assertThat(solverJobDTO1).isEqualTo(solverJobDTO2);
        solverJobDTO2.setId(2L);
        assertThat(solverJobDTO1).isNotEqualTo(solverJobDTO2);
        solverJobDTO1.setId(null);
        assertThat(solverJobDTO1).isNotEqualTo(solverJobDTO2);
    }
}
