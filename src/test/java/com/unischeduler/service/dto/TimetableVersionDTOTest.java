package com.unischeduler.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimetableVersionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimetableVersionDTO.class);
        TimetableVersionDTO timetableVersionDTO1 = new TimetableVersionDTO();
        timetableVersionDTO1.setId(1L);
        TimetableVersionDTO timetableVersionDTO2 = new TimetableVersionDTO();
        assertThat(timetableVersionDTO1).isNotEqualTo(timetableVersionDTO2);
        timetableVersionDTO2.setId(timetableVersionDTO1.getId());
        assertThat(timetableVersionDTO1).isEqualTo(timetableVersionDTO2);
        timetableVersionDTO2.setId(2L);
        assertThat(timetableVersionDTO1).isNotEqualTo(timetableVersionDTO2);
        timetableVersionDTO1.setId(null);
        assertThat(timetableVersionDTO1).isNotEqualTo(timetableVersionDTO2);
    }
}
