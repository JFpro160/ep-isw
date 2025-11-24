package com.ep.isw.application.service;

import com.ep.isw.application.dto.UserDto;
import com.ep.isw.application.usecase.RegisterUserCommand;
import com.ep.isw.domain.exception.DomainException;
import com.ep.isw.domain.model.UserAccount;
import com.ep.isw.domain.model.UserRole;
import com.ep.isw.domain.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto register(RegisterUserCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            throw new DomainException("El usuario ya existe");
        }
        String encoded = passwordEncoder.encode(command.password());
        UserAccount account = UserAccount.create(command.username(), encoded, command.displayName(),
                Set.of(UserRole.USER));
        return UserDto.from(userRepository.save(account));
    }

    public Optional<UserAccount> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
