package com.ep.isw.application.dto;

import com.ep.isw.domain.model.Character;
import java.time.Instant;
import java.util.UUID;

public record CharacterDto(UUID id, String name, String village, String rank, int powerLevel, Instant createdAt,
        UUID createdBy) {

    public static CharacterDto from(Character character) {
        return new CharacterDto(character.getId(), character.getName(), character.getVillage(), character.getRank(),
                character.getPowerLevel(), character.getCreatedAt(), character.getCreatedBy());
    }
}
