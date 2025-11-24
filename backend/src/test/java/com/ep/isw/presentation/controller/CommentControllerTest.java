package com.ep.isw.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ep.isw.application.dto.CommentDto;
import com.ep.isw.application.service.CommentService;
import com.ep.isw.domain.model.UserAccount;
import com.ep.isw.domain.model.UserRole;
import com.ep.isw.presentation.dto.CommentRequest;
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
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Captor
    private ArgumentCaptor<com.ep.isw.application.usecase.AddCommentCommand> commandCaptor;

    private CommentController controller;
    private Authentication authentication;
    private UUID characterId;

    @BeforeEach
    void setUp() {
        controller = new CommentController(commentService);
        UserAccount account = UserAccount.create("mentor", "hashed", "Alex", Set.of(UserRole.USER));
        authentication = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
        characterId = UUID.randomUUID();
    }

    @Test
    void addsCommentsWithAuthenticatedPrincipal() {
        CommentDto dto = new CommentDto(UUID.randomUUID(), characterId, "Alex", "Hola", Instant.now());
        when(commentService.add(any())).thenReturn(dto);

        ResponseEntity<CommentDto> response = controller.addComment(new CommentRequest(characterId, "Hola"),
                authentication);

        verify(commentService).add(commandCaptor.capture());
        assertThat(commandCaptor.getValue().authorId())
                .isEqualTo(((UserAccount) authentication.getPrincipal()).getId());
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void listsCommentsByCharacter() {
        CommentDto dto = new CommentDto(UUID.randomUUID(), characterId, "Alex", "Hola", Instant.now());
        when(commentService.findByCharacter(characterId)).thenReturn(List.of(dto));

        assertThat(controller.findByCharacter(characterId)).containsExactly(dto);
    }
}
