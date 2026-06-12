package com.turnerochubut.api.auth;

import java.util.UUID;

record UserResponse(
    UUID id,
    String email,
    String displayName,
    String firstName,
    String lastName,
    String phoneNumber,
    String address,
    UserRole role,
    AuthProvider authProvider,
    boolean active
) {
    static UserResponse from(AppUser user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhoneNumber(),
            user.getAddress(),
            user.getRole(),
            user.getAuthProvider(),
            user.isActive()
        );
    }
}
