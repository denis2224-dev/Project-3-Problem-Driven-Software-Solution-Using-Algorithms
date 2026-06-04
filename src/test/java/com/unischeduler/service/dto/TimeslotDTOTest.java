package com.unischeduler.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimeslotDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeslotDTO.class);
        TimeslotDTO timeslotDTO1 = new TimeslotDTO();
        timeslotDTO1.setId(1L);
        TimeslotDTO timeslotDTO2 = new TimeslotDTO();
        assertThat(timeslotDTO1).isNotEqualTo(timeslotDTO2);
        timeslotDTO2.setId(timeslotDTO1.getId());
        assertThat(timeslotDTO1).isEqualTo(timeslotDTO2);
        timeslotDTO2.setId(2L);
        assertThat(timeslotDTO1).isNotEqualTo(timeslotDTO2);
        timeslotDTO1.setId(null);
        assertThat(timeslotDTO1).isNotEqualTo(timeslotDTO2);
    }
}
