package com.unischeduler.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimetableDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimetableDTO.class);
        TimetableDTO timetableDTO1 = new TimetableDTO();
        timetableDTO1.setId(1L);
        TimetableDTO timetableDTO2 = new TimetableDTO();
        assertThat(timetableDTO1).isNotEqualTo(timetableDTO2);
        timetableDTO2.setId(timetableDTO1.getId());
        assertThat(timetableDTO1).isEqualTo(timetableDTO2);
        timetableDTO2.setId(2L);
        assertThat(timetableDTO1).isNotEqualTo(timetableDTO2);
        timetableDTO1.setId(null);
        assertThat(timetableDTO1).isNotEqualTo(timetableDTO2);
    }
}
