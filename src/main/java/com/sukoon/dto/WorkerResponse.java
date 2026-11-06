package com.sukoon.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class WorkerResponse {
    private Long id;
    private Long userId;
    private String primarySkill;
    private BigDecimal rating;
    private Integer quotaRemaining;
}