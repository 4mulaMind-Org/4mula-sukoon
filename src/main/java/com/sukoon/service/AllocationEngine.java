package com.sukoon.service;

import com.sukoon.dto.WorkerScore;
import com.sukoon.entity.Job;
import com.sukoon.entity.Worker;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
public class AllocationEngine {

    // Weights (Formula constants)
    private static final double W_INACTIVITY = 10.0;
    private static final double W_QUOTA      = 5.0;
    private static final double W_RATING     = 2.0;
    private static final double W_PROXIMITY  = 6.0;
    private static final double W_CANCEL     = 20.0;

    public WorkerScore calculateScore(Worker worker, Job job) {
        // 1. Inactivity (Days since last job, max 30)
        long days = 30;
        if (worker.getLastJobAt() != null) {
            days = ChronoUnit.DAYS.between(worker.getLastJobAt(), job.getScheduledAt());
            days = Math.min(days, 30); // Cap at 30 days
        }
        double inactivity = days * W_INACTIVITY;

        // 2. Quota (Remaining jobs worker can take)
        int quota = worker.getQuotaRemaining() != null ? worker.getQuotaRemaining() : 0;
        double quotaScore = quota * W_QUOTA;

        // 3. Rating
        double rating = (worker.getRating() != null ? worker.getRating().doubleValue() : 5.0) * W_RATING;

        // 4. Proximity (Mock logic for now)
        double distanceKm = 2.5; // Dummy value for now
        double proximity = Math.max(0, 6.0 - distanceKm) * W_PROXIMITY / 6.0;

        // 5. Cancellations (Penalty)
        int cancels = worker.getCancellations() != null ? worker.getCancellations() : 0;
        double cancelPenalty = cancels * W_CANCEL;

        // Total Score
        double total = inactivity + quotaScore + rating + proximity - cancelPenalty;

        return WorkerScore.builder()
                .workerId(worker.getId())
                .totalScore(total)
                .inactivityScore(inactivity)
                .quotaScore(quotaScore)
                .ratingScore(rating)
                .proximityScore(proximity)
                .cancellationPenalty(cancelPenalty)
                .build();
    }
}