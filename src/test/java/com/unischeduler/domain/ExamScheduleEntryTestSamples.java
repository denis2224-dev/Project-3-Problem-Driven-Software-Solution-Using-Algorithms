package com.unischeduler.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ExamScheduleEntryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ExamScheduleEntry getExamScheduleEntrySample1() {
        return new ExamScheduleEntry().id(1L);
    }

    public static ExamScheduleEntry getExamScheduleEntrySample2() {
        return new ExamScheduleEntry().id(2L);
    }

    public static ExamScheduleEntry getExamScheduleEntryRandomSampleGenerator() {
        return new ExamScheduleEntry().id(longCount.incrementAndGet());
    }
}
