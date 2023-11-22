package com.trvankiet.app.repository;

import com.trvankiet.app.entity.ChatNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatNotificationRepository extends MongoRepository<ChatNotification, String> {
}
