package com.ticketnest.auth.dto;
import com.ticketnest.auth.entity.Role;
public record AuthResponse(String token, Long userId, String email, Role role) {}