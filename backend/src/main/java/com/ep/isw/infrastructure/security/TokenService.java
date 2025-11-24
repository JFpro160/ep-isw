package com.ep.isw.infrastructure.security;

import com.ep.isw.domain.model.UserAccount;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class TokenService {

    private static final String HMAC = "HmacSHA256";
    private final TokenProperties properties;
    private final Clock clock;

    public TokenService(TokenProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    public String generate(UserAccount account) {
        Instant expires = Instant.now(clock).plusSeconds(properties.ttlSeconds());
        String payload = String.format(
                "%s|%s|%s|%d", account.getId(), account.getUsername(), account.getAuthorities().stream()
                        .map(granted -> granted.getAuthority()).collect(Collectors.joining(",")),
                expires.getEpochSecond());
        String signature = sign(payload);
        String token = payload + "." + signature;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    public TokenPayload verify(String token) {
        String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
        String[] parts = decoded.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Token inválido");
        }
        String payload = parts[0];
        String signature = parts[1];
        String expected = sign(payload);
        if (!expected.equals(signature)) {
            throw new IllegalArgumentException("Firma inválida");
        }
        String[] fields = payload.split("\\|");
        if (fields.length != 4) {
            throw new IllegalArgumentException("Payload inválido");
        }
        UUID userId = UUID.fromString(fields[0]);
        String username = fields[1];
        Set<String> roles = fields[2].isBlank() ? Set.of() : Set.of(fields[2].split(","));
        Instant expires = Instant.ofEpochSecond(Long.parseLong(fields[3]));
        if (Instant.now(clock).isAfter(expires)) {
            throw new IllegalArgumentException("Token expirado");
        }
        return new TokenPayload(userId, username, roles, expires);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC);
            mac.init(new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), HMAC));
            byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo firmar el token", ex);
        }
    }
}
