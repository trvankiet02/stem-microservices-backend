package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
}
