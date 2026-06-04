package com.unischeduler.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CourseEventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CourseEvent getCourseEventSample1() {
        return new CourseEvent().id(1L).durationMinutes(1).expectedStudents(1).requiredEquipment("requiredEquipment1");
    }

    public static CourseEvent getCourseEventSample2() {
        return new CourseEvent().id(2L).durationMinutes(2).expectedStudents(2).requiredEquipment("requiredEquipment2");
    }

    public static CourseEvent getCourseEventRandomSampleGenerator() {
        return new CourseEvent()
            .id(longCount.incrementAndGet())
            .durationMinutes(intCount.incrementAndGet())
            .expectedStudents(intCount.incrementAndGet())
            .requiredEquipment(UUID.randomUUID().toString());
    }
}
