package com.turnerochubut.api.auth;

import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    AuthService(AppUserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya esta registrado");
        }

        UserRole role = request.role() == null ? UserRole.CAJERO : request.role();
        if (role == UserRole.ADMINISTRADOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se puede crear un administrador desde registro publico");
        }

        AppUser user = new AppUser(
            email,
            request.displayName().trim(),
            passwordEncoder.encode(request.password()),
            role,
            AuthProvider.EMAIL
        );

        AppUser savedUser = userRepository.save(user);
        return createAuthResponse(savedUser);
    }

    @Transactional(readOnly = true)
    AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas"));

        if (!user.isActive() || user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
        }

        return createAuthResponse(user);
    }

    @Transactional(readOnly = true)
    UserResponse getCurrentUser(String email) {
        AppUser user = userRepository.findByEmailIgnoreCase(normalizeEmail(email))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario desactivado");
        }

        return UserResponse.from(user);
    }

    private AuthResponse createAuthResponse(AppUser user) {
        return new AuthResponse(jwtService.createToken(user), UserResponse.from(user));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
