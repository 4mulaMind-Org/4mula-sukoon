package com.sukoon.controller;

import com.sukoon.dto.AcceptJobResponse;
import com.sukoon.dto.JobRequest;
import com.sukoon.dto.JobResponse;
import com.sukoon.service.IdempotencyService;
import com.sukoon.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<?> createJob(
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
            @RequestBody JobRequest request) throws Exception {

        // 1. Check if key is provided
        if (idemKey == null || idemKey.isEmpty()) {
            return ResponseEntity.badRequest().body("Idempotency-Key header is required!");
        }

        // 2. Try to acquire lock in Redis
        boolean isNewRequest = idempotencyService.acquireLock(idemKey);
        if (!isNewRequest) {
            return ResponseEntity.status(409).body("Duplicate request! Job is already being processed or created.");
        }

        // 3. Process the job creation
        JobResponse response = jobService.createJob(request);

        // 4. Save the response in Redis for future retries (Optional for now, but good practice)
        idempotencyService.saveResponse(idemKey, objectMapper.writeValueAsString(response));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{jobId}/accept")
    public ResponseEntity<?> acceptJob(
            @PathVariable Long jobId,
            @RequestParam Long workerId) {
        try {
            AcceptJobResponse response = jobService.acceptJob(jobId, workerId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage()); // 409 Conflict
        }
    }
}