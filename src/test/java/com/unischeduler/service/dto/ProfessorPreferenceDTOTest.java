package com.unischeduler.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfessorPreferenceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfessorPreferenceDTO.class);
        ProfessorPreferenceDTO professorPreferenceDTO1 = new ProfessorPreferenceDTO();
        professorPreferenceDTO1.setId(1L);
        ProfessorPreferenceDTO professorPreferenceDTO2 = new ProfessorPreferenceDTO();
        assertThat(professorPreferenceDTO1).isNotEqualTo(professorPreferenceDTO2);
        professorPreferenceDTO2.setId(professorPreferenceDTO1.getId());
        assertThat(professorPreferenceDTO1).isEqualTo(professorPreferenceDTO2);
        professorPreferenceDTO2.setId(2L);
        assertThat(professorPreferenceDTO1).isNotEqualTo(professorPreferenceDTO2);
        professorPreferenceDTO1.setId(null);
        assertThat(professorPreferenceDTO1).isNotEqualTo(professorPreferenceDTO2);
    }
}
