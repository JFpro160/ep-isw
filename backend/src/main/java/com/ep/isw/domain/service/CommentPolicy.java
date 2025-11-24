package com.ep.isw.domain.service;

import com.ep.isw.domain.model.Comment;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class CommentPolicy {

    private static final Duration MIN_INTERVAL = Duration.ofSeconds(10);

    public void validate(List<Comment> existing, String newContent, Instant now) {
        boolean duplicate = existing.stream().anyMatch(c -> c.getContent().equalsIgnoreCase(newContent.trim()));
        if (duplicate) {
            throw new IllegalArgumentException("Ya existe un comentario igual para este personaje");
        }
        boolean tooSoon = existing.stream()
                .anyMatch(c -> Duration.between(c.getCreatedAt(), now).compareTo(MIN_INTERVAL) < 0);
        if (tooSoon) {
            throw new IllegalArgumentException("Espera unos segundos antes de comentar nuevamente");
        }
    }
}
