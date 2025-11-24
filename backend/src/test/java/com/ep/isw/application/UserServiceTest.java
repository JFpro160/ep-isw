package com.ep.isw.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ep.isw.application.dto.UserDto;
import com.ep.isw.application.service.UserService;
import com.ep.isw.application.usecase.RegisterUserCommand;
import com.ep.isw.domain.exception.DomainException;
import com.ep.isw.infrastructure.persistence.InMemoryUserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class UserServiceTest {

    private UserService userService;
    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        userService = new UserService(userRepository, new BCryptPasswordEncoder());
    }

    @Test
    void registersUsersWithEncryptedPasswords() {
        UserDto dto = userService.register(new RegisterUserCommand("mentor", "secret123", "Alex Mentor"));

        Optional<?> stored = userRepository.findByUsername("mentor");
        assertThat(stored).isPresent();
        assertThat(dto.username()).isEqualTo("mentor");
        assertThat(dto.displayName()).isEqualTo("Alex Mentor");
    }

    @Test
    void preventsDuplicatedUsernames() {
        userService.register(new RegisterUserCommand("mentor", "secret123", "Alex Mentor"));

        assertThatThrownBy(() -> userService.register(new RegisterUserCommand("mentor", "other", "Dup")))
                .isInstanceOf(DomainException.class).hasMessageContaining("ya existe");
    }
}
