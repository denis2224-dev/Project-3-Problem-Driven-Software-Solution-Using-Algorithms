package com.unischeduler.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ScheduleConflictTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ScheduleConflict getScheduleConflictSample1() {
        return new ScheduleConflict().id(1L).description("description1");
    }

    public static ScheduleConflict getScheduleConflictSample2() {
        return new ScheduleConflict().id(2L).description("description2");
    }

    public static ScheduleConflict getScheduleConflictRandomSampleGenerator() {
        return new ScheduleConflict().id(longCount.incrementAndGet()).description(UUID.randomUUID().toString());
    }
}
