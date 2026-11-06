package com.sukoon.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String phone;
    private String name;
    private String message;
    private LocalDateTime createdAt;
}