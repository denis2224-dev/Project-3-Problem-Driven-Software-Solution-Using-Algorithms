package com.unischeduler.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ProfessorPreferenceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ProfessorPreference getProfessorPreferenceSample1() {
        return new ProfessorPreference().id(1L);
    }

    public static ProfessorPreference getProfessorPreferenceSample2() {
        return new ProfessorPreference().id(2L);
    }

    public static ProfessorPreference getProfessorPreferenceRandomSampleGenerator() {
        return new ProfessorPreference().id(longCount.incrementAndGet());
    }
}
