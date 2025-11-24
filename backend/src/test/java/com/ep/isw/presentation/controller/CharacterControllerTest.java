package com.ep.isw.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ep.isw.application.dto.CharacterDto;
import com.ep.isw.application.service.CharacterService;
import com.ep.isw.domain.model.UserAccount;
import com.ep.isw.domain.model.UserRole;
import com.ep.isw.presentation.dto.CharacterRequest;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class CharacterControllerTest {

    @Mock
    private CharacterService characterService;

    @Captor
    private ArgumentCaptor<com.ep.isw.application.usecase.CreateCharacterCommand> commandCaptor;

    private CharacterController controller;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        controller = new CharacterController(characterService);
        UserAccount account = UserAccount.create("mentor", "hashed", "Alex", Set.of(UserRole.USER));
        authentication = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
    }

    @Test
    void createsCharactersUsingAuthenticatedUser() {
        CharacterDto dto = new CharacterDto(UUID.randomUUID(), "Alex", "Central", "Mentor", 90, Instant.now(),
                UUID.randomUUID());
        when(characterService.create(any())).thenReturn(dto);

        ResponseEntity<CharacterDto> response = controller.create(new CharacterRequest("Alex", "Central", "Mentor", 90),
                authentication);

        verify(characterService).create(commandCaptor.capture());
        assertThat(commandCaptor.getValue().createdBy())
                .isEqualTo(((UserAccount) authentication.getPrincipal()).getId());
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void listsAndFindsCharacters() {
        CharacterDto dto = new CharacterDto(UUID.randomUUID(), "Alex", "Central", "Mentor", 90, Instant.now(),
                UUID.randomUUID());
        when(characterService.list()).thenReturn(List.of(dto));
        when(characterService.find(dto.id())).thenReturn(dto);

        assertThat(controller.list()).hasSize(1);
        assertThat(controller.find(dto.id())).isEqualTo(dto);
    }
}
