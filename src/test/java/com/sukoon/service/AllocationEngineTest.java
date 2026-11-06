package com.sukoon.service;

import com.sukoon.dto.WorkerScore;
import com.sukoon.entity.Job;
import com.sukoon.entity.Worker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AllocationEngineTest {

    private final AllocationEngine engine = new AllocationEngine();

    @Test
    void testFairAllocation_InactivityAndQuotaShouldWin() {
        // 1. Setup Job
        Job job = Job.builder()
                .id(1L)
                .scheduledAt(LocalDateTime.of(2026, 11, 6, 10, 0))
                .build();

        // 2. Setup Worker A (Active recently, Low Quota, High Rating)
        Worker workerA = Worker.builder()
                .id(101L)
                .rating(new BigDecimal("5.00"))
                .quotaRemaining(2)
                .lastJobAt(LocalDateTime.of(2026, 11, 5, 10, 0)) // Kal hi kaam kiya
                .cancellations(0)
                .build();

        // 3. Setup Worker B (Inactive for 20 days, High Quota, Average Rating)
        Worker workerB = Worker.builder()
                .id(102L)
                .rating(new BigDecimal("4.00"))
                .quotaRemaining(10)
                .lastJobAt(LocalDateTime.of(2026, 10, 17, 10, 0)) // 20 din se khaali
                .cancellations(0)
                .build();

        // 4. Calculate Scores
        WorkerScore scoreA = engine.calculateScore(workerA, job);
        WorkerScore scoreB = engine.calculateScore(workerB, job);

        // 5. Assert (Check)
        System.out.println("Worker A Score: " + scoreA.getTotalScore());
        System.out.println("Worker B Score: " + scoreB.getTotalScore());

        // Worker B ko jeetna chahiye kyunki woh 20 din se inactive hai aur quota zyada hai
        assertTrue(scoreB.getTotalScore() > scoreA.getTotalScore(),
                "Inactive worker with high quota should have higher score");
    }
}