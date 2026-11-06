package com.sukoon.dto;

import lombok.Builder;
import lombok.Data;
import com.sukoon.enums.JobStatus;
import java.time.LocalDateTime;

@Data
@Builder
public class JobResponse {
    private Long jobId;
    private String message;
    private JobStatus status;
    private LocalDateTime scheduledAt;
}