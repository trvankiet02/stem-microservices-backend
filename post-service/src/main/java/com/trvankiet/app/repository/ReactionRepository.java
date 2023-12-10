package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Reaction;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends MongoRepository<Reaction, String> {
    Optional<Reaction> findByAuthorIdAndPostId(String authorId, String postId);
    List<Reaction> findAllByPostId(String postId);
    long countByPostId(String postId);
}
