package com.ep.isw.domain.repository;

import com.ep.isw.domain.model.Comment;
import java.util.List;
import java.util.UUID;

public interface CommentRepository {

    Comment save(Comment comment);

    List<Comment> findByCharacterId(UUID characterId);
}
