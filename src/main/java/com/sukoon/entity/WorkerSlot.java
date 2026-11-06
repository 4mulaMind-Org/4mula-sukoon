package com.sukoon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "worker_slots", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"worker_id", "slot_start"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    private LocalDateTime slotStart;
    private LocalDateTime slotEnd;

    @OneToOne
    @JoinColumn(name = "job_id")
    private Job job;
}