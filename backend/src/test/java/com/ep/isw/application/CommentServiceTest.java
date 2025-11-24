package com.ep.isw.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ep.isw.application.dto.CommentDto;
import com.ep.isw.application.service.CommentService;
import com.ep.isw.application.usecase.AddCommentCommand;
import com.ep.isw.domain.exception.ResourceNotFoundException;
import com.ep.isw.domain.model.Character;
import com.ep.isw.domain.repository.CharacterRepository;
import com.ep.isw.domain.repository.CommentRepository;
import com.ep.isw.domain.service.CommentPolicy;
import com.ep.isw.infrastructure.persistence.InMemoryCharacterRepository;
import com.ep.isw.infrastructure.persistence.InMemoryCommentRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommentServiceTest {

    private CommentRepository commentRepository;
    private CharacterRepository characterRepository;
    private CommentService commentService;
    private Clock clock;
    private UUID characterId;
    private UUID authorId;

    @BeforeEach
    void setUp() {
        commentRepository = new InMemoryCommentRepository();
        characterRepository = new InMemoryCharacterRepository();
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        commentService = new CommentService(commentRepository, characterRepository, new CommentPolicy(), clock);

        Character character = Character.create("Alex", "Central City", "Mentor", 90, UUID.randomUUID(),
                Instant.now(clock));
        characterRepository.save(character);
        characterId = character.getId();
        authorId = UUID.randomUUID();
    }

    @Test
    void addsCommentWhenPolicyIsSatisfied() {
        CommentDto dto = commentService.add(new AddCommentCommand(characterId, authorId, "Alex", "Buen trabajo"));

        assertThat(dto.characterId()).isEqualTo(characterId);
        assertThat(commentService.findByCharacter(characterId)).hasSize(1);
    }

    @Test
    void rejectsDuplicateContent() {
        commentService.add(new AddCommentCommand(characterId, authorId, "Alex", "Duplicado"));

        assertThatThrownBy(() -> commentService.add(new AddCommentCommand(characterId, authorId, "Alex", "Duplicado")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Ya existe un comentario igual");
    }

    @Test
    void enforcesMinimumIntervalBetweenComments() {
        commentService.add(new AddCommentCommand(characterId, authorId, "Alex", "Primer comentario"));

        assertThatThrownBy(
                () -> commentService.add(new AddCommentCommand(characterId, authorId, "Alex", "Segundo comentario")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Espera unos segundos");
    }

    @Test
    void failsWhenCharacterDoesNotExist() {
        UUID missingCharacter = UUID.randomUUID();

        assertThatThrownBy(
                () -> commentService.add(new AddCommentCommand(missingCharacter, authorId, "Alex", "Hola mundo")))
                .isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("Personaje no encontrado");
    }
}
