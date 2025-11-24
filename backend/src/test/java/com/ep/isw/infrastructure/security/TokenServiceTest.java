package com.ep.isw.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.ep.isw.domain.model.UserAccount;
import com.ep.isw.domain.model.UserRole;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TokenServiceTest {

    @Test
    void generatesAndVerifiesToken() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        TokenProperties props = new TokenProperties("issuer", "secret-key", 60);
        TokenService tokenService = new TokenService(props, clock);
        UserAccount account = UserAccount.create("mentor", "hashed", "Alex Mentor", Set.of(UserRole.USER));

        String token = tokenService.generate(account);
        TokenPayload payload = tokenService.verify(token);

        assertThat(payload.username()).isEqualTo("mentor");
        assertThat(payload.expiresAt()).isEqualTo(Instant.parse("2025-01-01T00:01:00Z"));
    }
}
