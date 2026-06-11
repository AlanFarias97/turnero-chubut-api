package com.turnerochubut.api.auth;

import java.util.UUID;

record UserResponse(
    UUID id,
    String email,
    String displayName,
    UserRole role,
    AuthProvider authProvider
) {
    static UserResponse from(AppUser user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getRole(),
            user.getAuthProvider()
        );
    }
}
