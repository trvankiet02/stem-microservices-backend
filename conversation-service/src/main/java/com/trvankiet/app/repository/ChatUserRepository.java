package com.trvankiet.app.repository;

import com.trvankiet.app.entity.ChatUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserRepository extends MongoRepository<ChatUser, String> {
}
