package com.trvankiet.app.repository;

import com.trvankiet.app.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

    // find chat room that member contain id
    Page<ChatRoom> findAllByMembersId(String id, Pageable pageable);
    Page<ChatRoom> findAllByMembersIdIsContaining(String id, Pageable pageable);
}
