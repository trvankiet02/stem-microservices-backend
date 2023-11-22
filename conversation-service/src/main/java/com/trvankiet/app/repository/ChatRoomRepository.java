package com.trvankiet.app.repository;

import com.trvankiet.app.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
}
