package com.ep.isw.domain.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ep.isw.domain.model.Comment;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CommentPolicyTest {

    private final CommentPolicy policy = new CommentPolicy();
    private final UUID characterId = UUID.randomUUID();
    private final UUID authorId = UUID.randomUUID();

    @Test
    void allowsDifferentCommentsSpacedInTime() {
        Comment existing = Comment.create(characterId, authorId, "Alex", "Saludos",
                Instant.parse("2025-01-01T00:00:00Z"));

        assertThatCode(() -> policy.validate(List.of(existing), "Nuevo", Instant.parse("2025-01-01T00:01:00Z")))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsDuplicateContent() {
        Comment existing = Comment.create(characterId, authorId, "Alex", "Saludos",
                Instant.parse("2025-01-01T00:00:00Z"));

        assertThatThrownBy(() -> policy.validate(List.of(existing), "Saludos", Instant.parse("2025-01-01T00:01:00Z")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("comentario igual");
    }

    @Test
    void rejectsCommentsThatArriveTooSoon() {
        Comment existing = Comment.create(characterId, authorId, "Alex", "Saludos",
                Instant.parse("2025-01-01T00:00:00Z"));

        assertThatThrownBy(() -> policy.validate(List.of(existing), "Otro", Instant.parse("2025-01-01T00:00:05Z")))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Espera unos segundos");
    }
}
