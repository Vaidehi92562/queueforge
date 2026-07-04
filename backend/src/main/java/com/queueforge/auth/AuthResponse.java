package com.queueforge.auth;

import com.queueforge.user.UserRole;

import java.util.UUID;

public class AuthResponse {

    private String token;
    private UUID userId;
    private String name;
    private String email;
    private UserRole role;

    public AuthResponse(String token, UUID userId, String name, String email, UserRole role) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }
}
