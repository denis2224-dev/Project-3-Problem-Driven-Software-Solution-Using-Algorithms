package com.unischeduler.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StudentGroupTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static StudentGroup getStudentGroupSample1() {
        return new StudentGroup().id(1L).name("name1").year(1).groupSize(1);
    }

    public static StudentGroup getStudentGroupSample2() {
        return new StudentGroup().id(2L).name("name2").year(2).groupSize(2);
    }

    public static StudentGroup getStudentGroupRandomSampleGenerator() {
        return new StudentGroup()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .year(intCount.incrementAndGet())
            .groupSize(intCount.incrementAndGet());
    }
}
