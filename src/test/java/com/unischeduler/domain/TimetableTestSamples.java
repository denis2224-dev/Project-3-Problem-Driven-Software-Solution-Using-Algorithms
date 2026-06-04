package com.unischeduler.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TimetableTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Timetable getTimetableSample1() {
        return new Timetable().id(1L).name("name1").semester("semester1").academicYear("academicYear1");
    }

    public static Timetable getTimetableSample2() {
        return new Timetable().id(2L).name("name2").semester("semester2").academicYear("academicYear2");
    }

    public static Timetable getTimetableRandomSampleGenerator() {
        return new Timetable()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .semester(UUID.randomUUID().toString())
            .academicYear(UUID.randomUUID().toString());
    }
}
