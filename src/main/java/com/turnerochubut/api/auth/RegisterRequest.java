package com.turnerochubut.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record RegisterRequest(
    @NotBlank
    @Email
    String email,

    @NotBlank
    @Size(max = 150)
    String displayName,

    @NotBlank
    @Size(min = 8, max = 100)
    String password,

    UserRole role
) {
}
