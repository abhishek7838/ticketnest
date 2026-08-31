package com.ticketnest.auth.dto;
import com.ticketnest.auth.entity.Role;
public record RegisterRequest(String name, String email, String password, Role role) {}