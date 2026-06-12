package com.turnerochubut.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

record AdminUpdateUserRequest(
    @NotBlank
    @Email
    String email,

    @NotBlank
    @Size(max = 80)
    String firstName,

    @NotBlank
    @Size(max = 80)
    String lastName,

    @NotBlank
    @Size(max = 40)
    String phoneNumber,

    @NotBlank
    @Size(max = 180)
    String address,

    @Size(max = 100)
    @Pattern(
        regexp = "^$|^(?=.*[A-Z])(?=.*[^A-Za-z0-9]).+$",
        message = "La contrasena debe tener al menos una mayuscula y un caracter especial"
    )
    String password,

    @NotNull
    UserRole role,

    boolean active
) {
}
