package com.unischeduler.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RoomTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Room getRoomSample1() {
        return new Room().id(1L).name("name1").code("code1").capacity(1).equipment("equipment1");
    }

    public static Room getRoomSample2() {
        return new Room().id(2L).name("name2").code("code2").capacity(2).equipment("equipment2");
    }

    public static Room getRoomRandomSampleGenerator() {
        return new Room()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .capacity(intCount.incrementAndGet())
            .equipment(UUID.randomUUID().toString());
    }
}
