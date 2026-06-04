package com.unischeduler.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudentGroupDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudentGroupDTO.class);
        StudentGroupDTO studentGroupDTO1 = new StudentGroupDTO();
        studentGroupDTO1.setId(1L);
        StudentGroupDTO studentGroupDTO2 = new StudentGroupDTO();
        assertThat(studentGroupDTO1).isNotEqualTo(studentGroupDTO2);
        studentGroupDTO2.setId(studentGroupDTO1.getId());
        assertThat(studentGroupDTO1).isEqualTo(studentGroupDTO2);
        studentGroupDTO2.setId(2L);
        assertThat(studentGroupDTO1).isNotEqualTo(studentGroupDTO2);
        studentGroupDTO1.setId(null);
        assertThat(studentGroupDTO1).isNotEqualTo(studentGroupDTO2);
    }
}
