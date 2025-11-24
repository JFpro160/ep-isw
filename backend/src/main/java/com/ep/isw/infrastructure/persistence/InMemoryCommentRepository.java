package com.ep.isw.infrastructure.persistence;

import com.ep.isw.domain.model.Comment;
import com.ep.isw.domain.repository.CommentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryCommentRepository implements CommentRepository {

    private final Map<UUID, List<Comment>> comments = new ConcurrentHashMap<>();

    @Override
    public Comment save(Comment comment) {
        comments.computeIfAbsent(comment.getCharacterId(), key -> new ArrayList<>()).add(comment);
        return comment;
    }

    @Override
    public List<Comment> findByCharacterId(UUID characterId) {
        return new ArrayList<>(comments.getOrDefault(characterId, List.of()));
    }
}
