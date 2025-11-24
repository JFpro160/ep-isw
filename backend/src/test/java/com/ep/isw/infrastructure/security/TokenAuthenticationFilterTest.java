package com.ep.isw.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ep.isw.application.service.UserService;
import com.ep.isw.domain.model.UserAccount;
import com.ep.isw.domain.model.UserRole;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class TokenAuthenticationFilterTest {

    private TokenAuthenticationFilter filter;
    private UserService userService;
    private TokenService tokenService;
    private Clock clock;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        tokenService = new TokenService(new TokenProperties("issuer", "secret-key", 60), clock);
        filter = new TokenAuthenticationFilter(tokenService, userService);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void setsAuthenticationWhenTokenIsValid() throws ServletException, IOException {
        UserAccount user = UserAccount.create("mentor", "hashed", "Alex Mentor", Set.of(UserRole.USER));
        when(userService.findByUsername("mentor")).thenReturn(Optional.of(user));

        String token = tokenService.generate(user);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(user);
    }

    @Test
    void ignoresInvalidTokens() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
