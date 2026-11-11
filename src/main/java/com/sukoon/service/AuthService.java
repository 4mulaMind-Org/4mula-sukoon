package com.sukoon.service;

import com.sukoon.dto.AuthRequest;
import com.sukoon.dto.AuthResponse;
import com.sukoon.entity.User;
import com.sukoon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthResponse authenticate(AuthRequest request) {
        // 1. Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhone(),
                        request.getPassword()
                )
        );

        // 2. Fetch User & Generate Token
        User user = userRepository.findByPhone(request.getPhone()).orElseThrow();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getPhone());
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder().token(jwtToken).build();
    }
}