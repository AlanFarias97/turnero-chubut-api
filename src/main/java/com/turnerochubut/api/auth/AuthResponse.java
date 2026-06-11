package com.turnerochubut.api.auth;

record AuthResponse(
    String token,
    UserResponse user
) {
}
