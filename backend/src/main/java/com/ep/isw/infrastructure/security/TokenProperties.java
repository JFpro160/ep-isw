package com.ep.isw.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.token")
public record TokenProperties(String issuer, String secret, long ttlSeconds) {
}
