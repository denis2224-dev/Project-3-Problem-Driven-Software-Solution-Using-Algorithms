package com.unischeduler.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TimetableEntryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static TimetableEntry getTimetableEntrySample1() {
        return new TimetableEntry().id(1L);
    }

    public static TimetableEntry getTimetableEntrySample2() {
        return new TimetableEntry().id(2L);
    }

    public static TimetableEntry getTimetableEntryRandomSampleGenerator() {
        return new TimetableEntry().id(longCount.incrementAndGet());
    }
}
