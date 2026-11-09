package com.sukoon;

import com.sukoon.entity.Job;
import com.sukoon.entity.User;
import com.sukoon.entity.Worker;
import com.sukoon.enums.JobStatus;
import com.sukoon.enums.Role;
import com.sukoon.repository.JobRepository;
import com.sukoon.repository.UserRepository;
import com.sukoon.repository.WorkerRepository;
import com.sukoon.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class JobConcurrencyTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    // Redis Container add kiya gaya hai
    @Container
    @ServiceConnection(name = "redis")
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Autowired
    private JobService jobService;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private WorkerRepository workerRepository;
    @Autowired
    private UserRepository userRepository;

    private Job testJob;
    private List<Worker> workers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 1. Create 1 Customer
        User customer = User.builder()
                .phone("9999999999").name("Test Customer").role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now()).build();
        userRepository.save(customer);

        // 2. Create 10 Workers
        for (int i = 0; i < 10; i++) {
            User wUser = User.builder()
                    .phone("888888880" + i).name("Worker " + i).role(Role.WORKER)
                    .createdAt(LocalDateTime.now()).build();
            userRepository.save(wUser);

            Worker worker = Worker.builder()
                    .user(wUser).primarySkill("PLUMBING")
                    .rating(new BigDecimal("5.0")).quotaRemaining(10)
                    .cancellations(0).build();
            workers.add(workerRepository.save(worker));
        }

        // 3. Create 1 Job
        testJob = Job.builder()
                .customer(customer).serviceType("PLUMBING")
                .status(JobStatus.REQUESTED).scheduledAt(LocalDateTime.now().plusDays(1)).build();
        jobRepository.save(testJob);
    }

    @Test
    void tenWorkersAcceptSameJob_OnlyOneShouldSucceed() throws InterruptedException, ExecutionException {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);

        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            Long workerId = workers.get(i).getId();
            futures.add(executorService.submit(() -> {
                latch.await();
                try {
                    jobService.acceptJob(testJob.getId(), workerId);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }));
        }

        latch.countDown();

        int successCount = 0;
        int failureCount = 0;

        for (Future<Boolean> future : futures) {
            if (future.get()) successCount++;
            else failureCount++;
        }

        executorService.shutdown();

        System.out.println("--- CONCURRENCY TEST RESULTS ---");
        System.out.println("Successes: " + successCount);
        System.out.println("Failures: " + failureCount);

        assertEquals(1, successCount, "Only ONE worker should win the race condition.");
        assertEquals(9, failureCount, "9 workers should fail and rollback.");
    }
}