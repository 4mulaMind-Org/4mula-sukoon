package com.sukoon.service;

import com.sukoon.dto.WorkerRequest;
import com.sukoon.dto.WorkerResponse;
import com.sukoon.entity.User;
import com.sukoon.entity.Worker;
import com.sukoon.repository.UserRepository;
import com.sukoon.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;

    public WorkerResponse registerWorker(WorkerRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Worker worker = Worker.builder()
                .user(user)
                .primarySkill(request.getPrimarySkill())
                .skills(request.getSkills())
                .rating(new BigDecimal("5.00"))
                .quotaRemaining(10)
                .cancellations(0)
                .build();

        Worker savedWorker = workerRepository.save(worker);

        return WorkerResponse.builder()
                .id(savedWorker.getId())
                .userId(user.getId())
                .primarySkill(savedWorker.getPrimarySkill())
                .rating(savedWorker.getRating())
                .quotaRemaining(savedWorker.getQuotaRemaining())
                .build();
    }
}