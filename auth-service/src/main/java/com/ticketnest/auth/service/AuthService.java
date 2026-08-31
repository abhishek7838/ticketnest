package com.ticketnest.auth.service;

import com.ticketnest.auth.dto.*;
import com.ticketnest.auth.entity.Role;
import com.ticketnest.auth.entity.User;
import com.ticketnest.auth.repository.UserRepository;
import com.ticketnest.auth.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new RuntimeException("Email already registered: " + req.email());  // TICK-10: return 409
        }
        Role role = req.role() != null ? req.role() : Role.CUSTOMER;   // TICK-9: lock this down later
        User user = userRepository.save(User.builder()
                .name(req.name())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))       // hash, never store raw
                .role(role)
                .build());
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole());
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(                             // checks password vs stored hash
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole());
    }
}