package com.sukoon.dto;

import lombok.Data;
import java.util.List;

@Data
public class WorkerRequest {
    private Long userId;
    private String primarySkill;
    private List<String> skills;
}