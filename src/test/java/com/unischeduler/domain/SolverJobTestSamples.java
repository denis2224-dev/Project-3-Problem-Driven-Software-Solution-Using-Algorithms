package com.unischeduler.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SolverJobTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SolverJob getSolverJobSample1() {
        return new SolverJob()
            .id(1L)
            .progressPercent(1)
            .message("message1")
            .hardConflictCount(1)
            .softPenaltyScore(1)
            .backtrackCount(1)
            .domainReductionCount(1)
            .runtimeMs(1L);
    }

    public static SolverJob getSolverJobSample2() {
        return new SolverJob()
            .id(2L)
            .progressPercent(2)
            .message("message2")
            .hardConflictCount(2)
            .softPenaltyScore(2)
            .backtrackCount(2)
            .domainReductionCount(2)
            .runtimeMs(2L);
    }

    public static SolverJob getSolverJobRandomSampleGenerator() {
        return new SolverJob()
            .id(longCount.incrementAndGet())
            .progressPercent(intCount.incrementAndGet())
            .message(UUID.randomUUID().toString())
            .hardConflictCount(intCount.incrementAndGet())
            .softPenaltyScore(intCount.incrementAndGet())
            .backtrackCount(intCount.incrementAndGet())
            .domainReductionCount(intCount.incrementAndGet())
            .runtimeMs(longCount.incrementAndGet());
    }
}
