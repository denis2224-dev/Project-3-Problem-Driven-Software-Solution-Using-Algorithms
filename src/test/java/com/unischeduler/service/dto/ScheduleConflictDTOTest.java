package com.unischeduler.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScheduleConflictDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduleConflictDTO.class);
        ScheduleConflictDTO scheduleConflictDTO1 = new ScheduleConflictDTO();
        scheduleConflictDTO1.setId(1L);
        ScheduleConflictDTO scheduleConflictDTO2 = new ScheduleConflictDTO();
        assertThat(scheduleConflictDTO1).isNotEqualTo(scheduleConflictDTO2);
        scheduleConflictDTO2.setId(scheduleConflictDTO1.getId());
        assertThat(scheduleConflictDTO1).isEqualTo(scheduleConflictDTO2);
        scheduleConflictDTO2.setId(2L);
        assertThat(scheduleConflictDTO1).isNotEqualTo(scheduleConflictDTO2);
        scheduleConflictDTO1.setId(null);
        assertThat(scheduleConflictDTO1).isNotEqualTo(scheduleConflictDTO2);
    }
}
