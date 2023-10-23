package com.trvankiet.app.repository;

import com.trvankiet.app.entity.ReactionType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionTypeRepository extends MongoRepository<ReactionType, String> {
}
