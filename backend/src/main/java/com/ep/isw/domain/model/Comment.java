package com.ep.isw.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Comment {

    private final UUID id;
    private final UUID characterId;
    private final UUID authorId;
    private final String authorName;
    private final String content;
    private final Instant createdAt;

    private Comment(UUID id, UUID characterId, UUID authorId, String authorName, String content, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.characterId = Objects.requireNonNull(characterId, "characterId");
        this.authorId = Objects.requireNonNull(authorId, "authorId");
        this.authorName = Objects.requireNonNull(authorName, "authorName");
        this.content = Objects.requireNonNull(content, "content");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static Comment create(UUID characterId, UUID authorId, String authorName, String content,
            Instant createdAt) {
        String trimmed = content == null ? "" : content.trim();
        if (trimmed.length() < 5) {
            throw new IllegalArgumentException("El comentario debe tener al menos 5 caracteres");
        }
        return new Comment(UUID.randomUUID(), characterId, authorId, authorName.trim(), trimmed, createdAt);
    }

    public UUID getId() {
        return id;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
