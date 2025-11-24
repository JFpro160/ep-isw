package com.ep.isw.infrastructure.persistence;

import com.ep.isw.domain.model.Character;
import com.ep.isw.domain.repository.CharacterRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryCharacterRepository implements CharacterRepository {

    private final Map<UUID, Character> storage = new ConcurrentHashMap<>();

    @Override
    public Character save(Character character) {
        storage.put(character.getId(), character);
        return character;
    }

    @Override
    public Optional<Character> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Character> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return storage.values().stream().anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }
}
