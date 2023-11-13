package com.trvankiet.app.repository;

import com.trvankiet.app.entity.ReactionType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionTypeRepository extends MongoRepository<ReactionType, String> {
    Optional<ReactionType> findByName(String reactionTypeName);
    Optional<ReactionType> findByCode(String reactionTypeName);
}
