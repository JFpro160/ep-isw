package com.ep.isw.application.usecase;

import java.util.UUID;

public record CreateCharacterCommand(String name, String village, String rank, int powerLevel, UUID createdBy) {
}
