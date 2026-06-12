package com.turnerochubut.api.auth;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
class AdminUserController {

    private final AdminUserService adminUserService;

    AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    List<UserResponse> listUsers() {
        return adminUserService.listUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        return adminUserService.createUser(request);
    }

    @PutMapping("/{id}")
    UserResponse updateUser(
        @PathVariable UUID id,
        @Valid @RequestBody AdminUpdateUserRequest request,
        Principal principal
    ) {
        return adminUserService.updateUser(id, request, principal.getName());
    }

    @PatchMapping("/{id}/active")
    UserResponse setUserActive(
        @PathVariable UUID id,
        @RequestBody SetUserActiveRequest request,
        Principal principal
    ) {
        return adminUserService.setUserActive(id, request.active(), principal.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable UUID id, Principal principal) {
        adminUserService.deleteUser(id, principal.getName());
    }
}
