package com.ep.isw.application.dto;

import com.ep.isw.domain.model.Comment;
import java.time.Instant;
import java.util.UUID;

public record CommentDto(UUID id, UUID characterId, String authorName, String content, Instant createdAt) {

    public static CommentDto from(Comment comment) {
        return new CommentDto(comment.getId(), comment.getCharacterId(), comment.getAuthorName(), comment.getContent(),
                comment.getCreatedAt());
    }
}
