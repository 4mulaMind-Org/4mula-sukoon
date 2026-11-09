package com.sukoon.service;

import com.sukoon.dto.AcceptJobResponse;
import com.sukoon.dto.JobRequest;
import com.sukoon.dto.JobResponse;
import com.sukoon.dto.WorkerScore;
import com.sukoon.entity.Job;
import com.sukoon.entity.User;
import com.sukoon.entity.Worker;
import com.sukoon.entity.WorkerSlot;
import com.sukoon.enums.JobStatus;
import com.sukoon.repository.JobRepository;
import com.sukoon.repository.UserRepository;
import com.sukoon.repository.WorkerRepository;
import com.sukoon.repository.WorkerSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final AllocationEngine allocationEngine;
    private final WorkerSlotRepository workerSlotRepository;

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


        List<Worker> workers = workerRepository.findByPrimarySkill(request.getServiceType());


        List<WorkerScore> scoredWorkers = workers.stream()
                .map(worker -> allocationEngine.calculateScore(worker, savedJob))
                .collect(Collectors.toList());


        List<WorkerScore> top3Workers = scoredWorkers.stream()
                .sorted(Comparator.comparingDouble(WorkerScore::getTotalScore).reversed())
                .limit(3)
                .collect(Collectors.toList());


        System.out.println("--- FAIR ALLOCATION RESULTS for Job ID: " + savedJob.getId() + " ---");
        for (WorkerScore ws : top3Workers) {
            System.out.println("Worker ID: " + ws.getWorkerId() + " | Score: " + ws.getTotalScore());
        }


        return JobResponse.builder()
                .jobId(savedJob.getId())
                .message("Job created successfully! Top 3 workers allocated.")
                .status(savedJob.getStatus())
                .scheduledAt(savedJob.getScheduledAt())
                .build();
    }

    @Transactional
    public AcceptJobResponse acceptJob(Long jobId, Long workerId) {

        Job job = jobRepository.findByIdForUpdate(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));


        if (job.getWorker() != null) {
            throw new RuntimeException("Job already taken by another worker!");
        }


        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));


        job.setWorker(worker);
        job.setStatus(JobStatus.ASSIGNED);
        jobRepository.save(job);


        WorkerSlot slot = WorkerSlot.builder()
                .worker(worker)
                .job(job)
                .slotStart(job.getScheduledAt())
                .slotEnd(job.getScheduledAt().plusHours(2))
                .build();

        workerSlotRepository.save(slot);

        return AcceptJobResponse.builder()
                .jobId(job.getId())
                .workerId(worker.getId())
                .status("ASSIGNED")
                .message("Job accepted successfully!")
                .build();
    }
}
