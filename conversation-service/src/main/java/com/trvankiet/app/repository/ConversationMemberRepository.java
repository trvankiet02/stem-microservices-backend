package com.trvankiet.app.repository;

import com.trvankiet.app.entity.ConversationMember;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationMemberRepository extends MongoRepository<ConversationMember, String> {
}
