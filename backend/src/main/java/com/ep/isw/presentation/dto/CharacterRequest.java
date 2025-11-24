package com.ep.isw.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CharacterRequest(@NotBlank String name, @NotBlank String village, @NotBlank String rank,
        @Min(1) @Max(100) int powerLevel) {
}
