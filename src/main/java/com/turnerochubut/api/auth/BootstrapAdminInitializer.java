package com.turnerochubut.api.auth;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
class BootstrapAdminInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapAdminInitializer.class);

    private final BootstrapAdminProperties properties;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    BootstrapAdminInitializer(
        BootstrapAdminProperties properties,
        AppUserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(properties.email()) || !StringUtils.hasText(properties.password())) {
            return;
        }

        String email = properties.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        AppUser admin = new AppUser(
            email,
            resolveDisplayName(),
            passwordEncoder.encode(properties.password()),
            UserRole.ADMINISTRADOR,
            AuthProvider.EMAIL
        );

        userRepository.save(admin);
        LOGGER.info("Bootstrap administrator created for email {}", email);
    }

    private String resolveDisplayName() {
        if (StringUtils.hasText(properties.displayName())) {
            return properties.displayName().trim();
        }
        return "Administrador";
    }
}
