package com.unischeduler.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProfessorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Professor getProfessorSample1() {
        return new Professor().id(1L).firstName("firstName1").lastName("lastName1").email("email1").title("title1");
    }

    public static Professor getProfessorSample2() {
        return new Professor().id(2L).firstName("firstName2").lastName("lastName2").email("email2").title("title2");
    }

    public static Professor getProfessorRandomSampleGenerator() {
        return new Professor()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString());
    }
}
