package com.unischeduler.service.mapper;

import static com.unischeduler.domain.BuildingAsserts.*;
import static com.unischeduler.domain.BuildingTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BuildingMapperTest {

    private BuildingMapper buildingMapper;

    @BeforeEach
    void setUp() {
        buildingMapper = new BuildingMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBuildingSample1();
        var actual = buildingMapper.toEntity(buildingMapper.toDto(expected));
        assertBuildingAllPropertiesEquals(expected, actual);
    }
}
