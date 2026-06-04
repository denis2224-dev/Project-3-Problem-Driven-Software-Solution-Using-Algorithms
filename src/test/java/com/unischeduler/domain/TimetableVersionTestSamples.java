package com.unischeduler.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TimetableVersionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TimetableVersion getTimetableVersionSample1() {
        return new TimetableVersion().id(1L).versionNumber(1).totalHardConflicts(1).totalSoftPenalty(1);
    }

    public static TimetableVersion getTimetableVersionSample2() {
        return new TimetableVersion().id(2L).versionNumber(2).totalHardConflicts(2).totalSoftPenalty(2);
    }

    public static TimetableVersion getTimetableVersionRandomSampleGenerator() {
        return new TimetableVersion()
            .id(longCount.incrementAndGet())
            .versionNumber(intCount.incrementAndGet())
            .totalHardConflicts(intCount.incrementAndGet())
            .totalSoftPenalty(intCount.incrementAndGet());
    }
}
