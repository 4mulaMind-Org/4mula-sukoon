package com.sukoon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkerScore {
    private Long workerId;
    private double totalScore;
    private double inactivityScore;
    private double quotaScore;
    private double ratingScore;
    private double proximityScore;
    private double cancellationPenalty;
}