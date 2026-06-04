package com.unischeduler.domain;

import static com.unischeduler.domain.BuildingTestSamples.*;
import static com.unischeduler.domain.RoomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.unischeduler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RoomTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Room.class);
        Room room1 = getRoomSample1();
        Room room2 = new Room();
        assertThat(room1).isNotEqualTo(room2);

        room2.setId(room1.getId());
        assertThat(room1).isEqualTo(room2);

        room2 = getRoomSample2();
        assertThat(room1).isNotEqualTo(room2);
    }

    @Test
    void buildingTest() {
        Room room = getRoomRandomSampleGenerator();
        Building buildingBack = getBuildingRandomSampleGenerator();

        room.setBuilding(buildingBack);
        assertThat(room.getBuilding()).isEqualTo(buildingBack);

        room.building(null);
        assertThat(room.getBuilding()).isNull();
    }
}
