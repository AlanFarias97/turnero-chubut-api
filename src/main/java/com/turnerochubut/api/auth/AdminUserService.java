package com.turnerochubut.api.auth;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
class AdminUserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    AdminUserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    List<UserResponse> listUsers() {
        return userRepository
            .findAll(Sort.by(Sort.Direction.ASC, "email"))
            .stream()
            .map(UserResponse::from)
            .toList();
    }

    @Transactional
    UserResponse createUser(AdminCreateUserRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya esta registrado");
        }

        AppUser user = new AppUser(
            email,
            request.firstName().trim(),
            request.lastName().trim(),
            request.phoneNumber().trim(),
            request.address().trim(),
            passwordEncoder.encode(request.password()),
            request.role(),
            AuthProvider.EMAIL
        );
        user.setActive(request.active());

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    UserResponse updateUser(UUID id, AdminUpdateUserRequest request, String currentEmail) {
        AppUser user = findUser(id);
        String email = normalizeEmail(request.email());

        userRepository
            .findByEmailIgnoreCase(email)
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(_existing -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya esta registrado");
            });

        boolean isSelf = user.getEmail().equalsIgnoreCase(normalizeEmail(currentEmail));
        if (isSelf && !request.active()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No podes desactivar tu propio usuario");
        }

        user.updateProfile(
            email,
            request.firstName().trim(),
            request.lastName().trim(),
            request.phoneNumber().trim(),
            request.address().trim(),
            request.role()
        );

        if (StringUtils.hasText(request.password())) {
            user.updatePasswordHash(passwordEncoder.encode(request.password()));
        }

        user.setActive(request.active());
        return UserResponse.from(user);
    }

    @Transactional
    UserResponse setUserActive(UUID id, boolean active, String currentEmail) {
        AppUser user = findUser(id);
        if (!active && user.getEmail().equalsIgnoreCase(normalizeEmail(currentEmail))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No podes desactivar tu propio usuario");
        }

        user.setActive(active);
        return UserResponse.from(user);
    }

    @Transactional
    void deleteUser(UUID id, String currentEmail) {
        AppUser user = findUser(id);
        if (user.getEmail().equalsIgnoreCase(normalizeEmail(currentEmail))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No podes borrar tu propio usuario");
        }

        userRepository.delete(user);
    }

    private AppUser findUser(UUID id) {
        return userRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
