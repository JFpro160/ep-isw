package com.ep.isw.presentation.controller;

import com.ep.isw.application.dto.UserDto;
import com.ep.isw.application.service.UserService;
import com.ep.isw.application.usecase.RegisterUserCommand;
import com.ep.isw.domain.model.UserAccount;
import com.ep.isw.infrastructure.security.TokenService;
import com.ep.isw.presentation.dto.AuthResponse;
import com.ep.isw.presentation.dto.LoginRequest;
import com.ep.isw.presentation.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager,
            TokenService tokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        UserDto dto = userService
                .register(new RegisterUserCommand(request.username(), request.password(), request.displayName()));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserAccount principal = (UserAccount) authentication.getPrincipal();
        String token = tokenService.generate(principal);
        return ResponseEntity.ok(new AuthResponse(token, principal.getUsername()));
    }
}
