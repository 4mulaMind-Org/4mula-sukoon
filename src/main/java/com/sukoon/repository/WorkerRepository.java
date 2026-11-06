package com.sukoon.repository;

import com.sukoon.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    // finding worker on skill bases
    List<Worker> findByPrimarySkill(String primarySkill);
}