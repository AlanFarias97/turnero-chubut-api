package com.turnerochubut.api.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "turnero.bootstrap.admin")
public record BootstrapAdminProperties(
    String email,
    String password,
    String displayName
) {
}
