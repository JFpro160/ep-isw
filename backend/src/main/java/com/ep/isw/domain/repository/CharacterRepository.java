package com.ep.isw.domain.repository;

import com.ep.isw.domain.model.Character;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CharacterRepository {

    Character save(Character character);

    Optional<Character> findById(UUID id);

    List<Character> findAll();

    boolean existsByNameIgnoreCase(String name);
}
