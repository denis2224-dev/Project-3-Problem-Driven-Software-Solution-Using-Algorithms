package com.unischeduler.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamScheduleEntryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExamScheduleEntryDTO.class);
        ExamScheduleEntryDTO examScheduleEntryDTO1 = new ExamScheduleEntryDTO();
        examScheduleEntryDTO1.setId(1L);
        ExamScheduleEntryDTO examScheduleEntryDTO2 = new ExamScheduleEntryDTO();
        assertThat(examScheduleEntryDTO1).isNotEqualTo(examScheduleEntryDTO2);
        examScheduleEntryDTO2.setId(examScheduleEntryDTO1.getId());
        assertThat(examScheduleEntryDTO1).isEqualTo(examScheduleEntryDTO2);
        examScheduleEntryDTO2.setId(2L);
        assertThat(examScheduleEntryDTO1).isNotEqualTo(examScheduleEntryDTO2);
        examScheduleEntryDTO1.setId(null);
        assertThat(examScheduleEntryDTO1).isNotEqualTo(examScheduleEntryDTO2);
    }
}
