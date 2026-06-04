package com.unischeduler.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BuildingTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Building getBuildingSample1() {
        return new Building().id(1L).name("name1").code("code1").address("address1");
    }

    public static Building getBuildingSample2() {
        return new Building().id(2L).name("name2").code("code2").address("address2");
    }

    public static Building getBuildingRandomSampleGenerator() {
        return new Building()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString());
    }
}
