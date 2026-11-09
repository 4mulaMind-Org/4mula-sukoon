package com.sukoon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AcceptJobResponse {
    private Long jobId;
    private Long workerId;
    private String status;
    private String message;
}