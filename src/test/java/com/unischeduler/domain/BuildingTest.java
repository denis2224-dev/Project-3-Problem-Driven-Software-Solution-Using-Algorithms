package com.unischeduler.domain;

import static com.unischeduler.domain.BuildingTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BuildingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Building.class);
        Building building1 = getBuildingSample1();
        Building building2 = new Building();
        assertThat(building1).isNotEqualTo(building2);

        building2.setId(building1.getId());
        assertThat(building1).isEqualTo(building2);

        building2 = getBuildingSample2();
        assertThat(building1).isNotEqualTo(building2);
    }
}
