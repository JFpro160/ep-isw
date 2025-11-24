package com.ep.isw.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ep.isw.application.dto.UserDto;
import com.ep.isw.application.service.UserService;
import com.ep.isw.domain.model.UserAccount;
import com.ep.isw.domain.model.UserRole;
import com.ep.isw.infrastructure.security.TokenService;
import com.ep.isw.presentation.dto.AuthResponse;
import com.ep.isw.presentation.dto.LoginRequest;
import com.ep.isw.presentation.dto.RegisterRequest;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Captor
    private ArgumentCaptor<com.ep.isw.application.usecase.RegisterUserCommand> registerCaptor;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(userService, authenticationManager, tokenService);
    }

    @Test
    void registersUserThroughService() {
        UserDto dto = new UserDto(UUID.randomUUID(), "mentor", "Alex", Set.of(UserRole.USER), Instant.now());
        when(userService.register(any())).thenReturn(dto);

        ResponseEntity<UserDto> response = controller.register(new RegisterRequest("mentor", "secret123", "Alex"));

        verify(userService).register(registerCaptor.capture());
        assertThat(registerCaptor.getValue().username()).isEqualTo("mentor");
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void logsInUsersAndReturnsToken() {
        UserAccount account = UserAccount.create("mentor", "hashed", "Alex", Set.of(UserRole.USER));
        Authentication authentication = new UsernamePasswordAuthenticationToken(account, null,
                account.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.generate(account)).thenReturn("token-value");

        ResponseEntity<AuthResponse> response = controller.login(new LoginRequest("mentor", "secret123"));

        assertThat(response.getBody()).isEqualTo(new AuthResponse("token-value", "mentor"));
    }
}
