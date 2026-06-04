package com.unischeduler.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseEventDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseEventDTO.class);
        CourseEventDTO courseEventDTO1 = new CourseEventDTO();
        courseEventDTO1.setId(1L);
        CourseEventDTO courseEventDTO2 = new CourseEventDTO();
        assertThat(courseEventDTO1).isNotEqualTo(courseEventDTO2);
        courseEventDTO2.setId(courseEventDTO1.getId());
        assertThat(courseEventDTO1).isEqualTo(courseEventDTO2);
        courseEventDTO2.setId(2L);
        assertThat(courseEventDTO1).isNotEqualTo(courseEventDTO2);
        courseEventDTO1.setId(null);
        assertThat(courseEventDTO1).isNotEqualTo(courseEventDTO2);
    }
}
