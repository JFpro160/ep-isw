package com.ep.isw.application.usecase;

import java.util.UUID;

public record AddCommentCommand(UUID characterId, UUID authorId, String authorName, String content) {
}
