package com.ep.isw.presentation.controller;

import com.ep.isw.application.dto.CommentDto;
import com.ep.isw.application.service.CommentService;
import com.ep.isw.application.usecase.AddCommentCommand;
import com.ep.isw.domain.model.UserAccount;
import com.ep.isw.presentation.dto.CommentRequest;
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
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDto> addComment(@Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        UserAccount principal = (UserAccount) authentication.getPrincipal();
        CommentDto dto = commentService.add(new AddCommentCommand(request.characterId(), principal.getId(),
                principal.getDisplayName(), request.content()));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{characterId}")
    public List<CommentDto> findByCharacter(@PathVariable UUID characterId) {
        return commentService.findByCharacter(characterId);
    }
}
