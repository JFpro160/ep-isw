package com.ep.isw.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CommentRequest(@NotNull UUID characterId, @NotBlank String content) {
}
