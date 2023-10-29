package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Optional<Comment> findByCommentId(String commentId);
}
