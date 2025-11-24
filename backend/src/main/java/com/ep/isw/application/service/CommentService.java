package com.ep.isw.application.service;

import com.ep.isw.application.dto.CommentDto;
import com.ep.isw.application.usecase.AddCommentCommand;
import com.ep.isw.domain.exception.ResourceNotFoundException;
import com.ep.isw.domain.model.Character;
import com.ep.isw.domain.model.Comment;
import com.ep.isw.domain.repository.CharacterRepository;
import com.ep.isw.domain.repository.CommentRepository;
import com.ep.isw.domain.service.CommentPolicy;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CharacterRepository characterRepository;
    private final CommentPolicy commentPolicy;
    private final Clock clock;

    public CommentService(CommentRepository commentRepository, CharacterRepository characterRepository,
            CommentPolicy commentPolicy, Clock clock) {
        this.commentRepository = commentRepository;
        this.characterRepository = characterRepository;
        this.commentPolicy = commentPolicy;
        this.clock = clock;
    }

    public CommentDto add(AddCommentCommand command) {
        Character character = characterRepository.findById(command.characterId())
                .orElseThrow(() -> new ResourceNotFoundException("Personaje no encontrado"));
        List<Comment> existing = commentRepository.findByCharacterId(character.getId());
        commentPolicy.validate(existing, command.content(), Instant.now(clock));
        Comment comment = Comment.create(character.getId(), command.authorId(), command.authorName(), command.content(),
                Instant.now(clock));
        return CommentDto.from(commentRepository.save(comment));
    }

    public List<CommentDto> findByCharacter(UUID characterId) {
        return commentRepository.findByCharacterId(characterId).stream().map(CommentDto::from)
                .collect(Collectors.toList());
    }
}
