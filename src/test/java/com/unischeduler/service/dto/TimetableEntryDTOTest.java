package com.unischeduler.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimetableEntryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimetableEntryDTO.class);
        TimetableEntryDTO timetableEntryDTO1 = new TimetableEntryDTO();
        timetableEntryDTO1.setId(1L);
        TimetableEntryDTO timetableEntryDTO2 = new TimetableEntryDTO();
        assertThat(timetableEntryDTO1).isNotEqualTo(timetableEntryDTO2);
        timetableEntryDTO2.setId(timetableEntryDTO1.getId());
        assertThat(timetableEntryDTO1).isEqualTo(timetableEntryDTO2);
        timetableEntryDTO2.setId(2L);
        assertThat(timetableEntryDTO1).isNotEqualTo(timetableEntryDTO2);
        timetableEntryDTO1.setId(null);
        assertThat(timetableEntryDTO1).isNotEqualTo(timetableEntryDTO2);
    }
}
