package com.sukoon.service;

import com.sukoon.dto.JobRequest;
import com.sukoon.dto.JobResponse;
import com.sukoon.dto.WorkerScore;
import com.sukoon.entity.Job;
import com.sukoon.entity.User;
import com.sukoon.entity.Worker;
import com.sukoon.enums.JobStatus;
import com.sukoon.repository.JobRepository;
import com.sukoon.repository.UserRepository;
import com.sukoon.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final AllocationEngine allocationEngine; // Engine inject kiya

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

        // --- FAIR ALLOCATION LOGIC START ---
        // 1. Us skill ke saare available workers fetch karo
        List<Worker> workers = workerRepository.findByPrimarySkill(request.getServiceType());

        // 2. Un sabhi workers ka score calculate karo
        List<WorkerScore> scoredWorkers = workers.stream()
                .map(worker -> allocationEngine.calculateScore(worker, savedJob))
                .collect(Collectors.toList());

        // 3. Score ke basis pe sort karo (Highest score first) aur top 3 select karo
        List<WorkerScore> top3Workers = scoredWorkers.stream()
                .sorted(Comparator.comparingDouble(WorkerScore::getTotalScore).reversed())
                .limit(3)
                .collect(Collectors.toList());

        // 4. Abhi ke liye hum bas log print kar rahe hain (Baad mein isko DB mein offer table mein save karenge)
        System.out.println("--- FAIR ALLOCATION RESULTS for Job ID: " + savedJob.getId() + " ---");
        for (WorkerScore ws : top3Workers) {
            System.out.println("Worker ID: " + ws.getWorkerId() + " | Score: " + ws.getTotalScore());
        }
        // --- FAIR ALLOCATION LOGIC END ---

        return JobResponse.builder()
                .jobId(savedJob.getId())
                .message("Job created successfully! Top 3 workers allocated.")
                .status(savedJob.getStatus())
                .scheduledAt(savedJob.getScheduledAt())
                .build();
    }
}