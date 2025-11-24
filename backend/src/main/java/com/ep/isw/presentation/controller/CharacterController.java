package com.ep.isw.presentation.controller;

import com.ep.isw.application.dto.CharacterDto;
import com.ep.isw.application.service.CharacterService;
import com.ep.isw.application.usecase.CreateCharacterCommand;
import com.ep.isw.domain.model.UserAccount;
import com.ep.isw.presentation.dto.CharacterRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/characters")
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @PostMapping
    public ResponseEntity<CharacterDto> create(@Valid @RequestBody CharacterRequest request,
            Authentication authentication) {
        UserAccount principal = (UserAccount) authentication.getPrincipal();
        CharacterDto dto = characterService.create(new CreateCharacterCommand(request.name(), request.village(),
                request.rank(), request.powerLevel(), principal.getId()));
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public List<CharacterDto> list() {
        return characterService.list();
    }

    @GetMapping("/{id}")
    public CharacterDto find(@PathVariable UUID id) {
        return characterService.find(id);
    }
}
