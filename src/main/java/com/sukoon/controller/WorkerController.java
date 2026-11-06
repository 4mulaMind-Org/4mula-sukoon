package com.sukoon.controller;

import com.sukoon.dto.WorkerRequest;
import com.sukoon.dto.WorkerResponse;
import com.sukoon.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;

    @PostMapping("/register")
    public ResponseEntity<WorkerResponse> registerWorker(@RequestBody WorkerRequest request) {
        return ResponseEntity.ok(workerService.registerWorker(request));
    }
}