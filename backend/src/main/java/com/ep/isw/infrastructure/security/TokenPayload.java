package com.ep.isw.infrastructure.security;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record TokenPayload(UUID userId, String username, Set<String> roles, Instant expiresAt) {
}
