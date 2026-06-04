package com.unischeduler.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BuildingDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BuildingDTO.class);
        BuildingDTO buildingDTO1 = new BuildingDTO();
        buildingDTO1.setId(1L);
        BuildingDTO buildingDTO2 = new BuildingDTO();
        assertThat(buildingDTO1).isNotEqualTo(buildingDTO2);
        buildingDTO2.setId(buildingDTO1.getId());
        assertThat(buildingDTO1).isEqualTo(buildingDTO2);
        buildingDTO2.setId(2L);
        assertThat(buildingDTO1).isNotEqualTo(buildingDTO2);
        buildingDTO1.setId(null);
        assertThat(buildingDTO1).isNotEqualTo(buildingDTO2);
    }
}
