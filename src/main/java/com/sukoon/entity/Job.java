package com.sukoon.entity;

import com.sukoon.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    private String serviceType;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private LocalDateTime scheduledAt;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;
}