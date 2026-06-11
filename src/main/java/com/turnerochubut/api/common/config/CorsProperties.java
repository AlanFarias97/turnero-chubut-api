package com.turnerochubut.api.common.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "turnero.cors")
public record CorsProperties(
    List<String> allowedOrigins
) {
}
