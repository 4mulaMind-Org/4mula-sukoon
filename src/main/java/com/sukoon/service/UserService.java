package com.sukoon.service;

import com.sukoon.dto.UserRequest;
import com.sukoon.dto.UserResponse;
import com.sukoon.entity.User;
import com.sukoon.enums.Role;
import com.sukoon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse registerUser(UserRequest request) {
        // Check if user exists
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("User with this phone already exists!");
        }

        // Build and save user
        User user = User.builder()
                .phone(request.getPhone())
                .name(request.getName())
                .role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        // Return response
        return UserResponse.builder()
                .id(savedUser.getId())
                .phone(savedUser.getPhone())
                .name(savedUser.getName())
                .message("User registered successfully!")
                .createdAt(savedUser.getCreatedAt())
                .build();
    }
}