package com.sukoon.repository;

import com.sukoon.entity.WorkerSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerSlotRepository extends JpaRepository<WorkerSlot, Long> {
}