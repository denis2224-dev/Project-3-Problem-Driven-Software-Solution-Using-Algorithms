package com.unischeduler.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TimeslotTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Timeslot getTimeslotSample1() {
        return new Timeslot().id(1L).startTime("startTime1").endTime("endTime1");
    }

    public static Timeslot getTimeslotSample2() {
        return new Timeslot().id(2L).startTime("startTime2").endTime("endTime2");
    }

    public static Timeslot getTimeslotRandomSampleGenerator() {
        return new Timeslot().id(longCount.incrementAndGet()).startTime(UUID.randomUUID().toString()).endTime(UUID.randomUUID().toString());
    }
}
