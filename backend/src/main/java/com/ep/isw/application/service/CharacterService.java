package com.ep.isw.application.service;

import com.ep.isw.application.dto.CharacterDto;
import com.ep.isw.application.usecase.CreateCharacterCommand;
import com.ep.isw.domain.exception.DomainException;
import com.ep.isw.domain.exception.ResourceNotFoundException;
import com.ep.isw.domain.model.Character;
import com.ep.isw.domain.repository.CharacterRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CharacterService {

    private final CharacterRepository repository;
    private final Clock clock;

    public CharacterService(CharacterRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public CharacterDto create(CreateCharacterCommand command) {
        if (repository.existsByNameIgnoreCase(command.name())) {
            throw new DomainException("Ya existe un personaje con ese nombre");
        }
        Character character = Character.create(command.name(), command.village(), command.rank(), command.powerLevel(),
                command.createdBy(), Instant.now(clock));
        return CharacterDto.from(repository.save(character));
    }

    public CharacterDto find(UUID id) {
        Character character = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personaje no encontrado"));
        return CharacterDto.from(character);
    }

    public List<CharacterDto> list() {
        return repository.findAll().stream().map(CharacterDto::from).collect(Collectors.toList());
    }
}
