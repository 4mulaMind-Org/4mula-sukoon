package com.sukoon.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobRequest {
    private Long customerId;
    private String serviceType;
    private LocalDateTime scheduledAt;
}