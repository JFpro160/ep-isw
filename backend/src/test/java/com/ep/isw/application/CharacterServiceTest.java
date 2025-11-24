package com.ep.isw.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.ep.isw.application.dto.CharacterDto;
import com.ep.isw.application.service.CharacterService;
import com.ep.isw.application.usecase.CreateCharacterCommand;
import com.ep.isw.infrastructure.persistence.InMemoryCharacterRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CharacterServiceTest {

    private CharacterService service;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        service = new CharacterService(new InMemoryCharacterRepository(), clock);
    }

    @Test
    void createsAndListsCharacters() {
        UUID creator = UUID.randomUUID();
        CharacterDto dto = service.create(new CreateCharacterCommand("Alex", "Central City", "Mentor", 95, creator));

        assertThat(dto.name()).isEqualTo("Alex");
        assertThat(dto.createdAt()).isEqualTo(Instant.now(clock));

        assertThat(service.list()).hasSize(1);
    }
}
