package com.sukoon.service;

import com.sukoon.dto.JobRequest;
import com.sukoon.dto.JobResponse;
import com.sukoon.entity.Job;
import com.sukoon.entity.User;
import com.sukoon.enums.JobStatus;
import com.sukoon.repository.JobRepository;
import com.sukoon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobResponse createJob(JobRequest request) {
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Job job = Job.builder()
                .customer(customer)
                .serviceType(request.getServiceType())
                .status(JobStatus.REQUESTED)
                .scheduledAt(request.getScheduledAt())
                .build();

        Job savedJob = jobRepository.save(job);

        return JobResponse.builder()
                .jobId(savedJob.getId())
                .message("Job created successfully!")
                .status(savedJob.getStatus())
                .scheduledAt(savedJob.getScheduledAt())
                .build();
    }
}