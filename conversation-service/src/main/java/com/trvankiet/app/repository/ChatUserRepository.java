package com.trvankiet.app.repository;

import com.trvankiet.app.constant.StatusEnum;
import com.trvankiet.app.entity.ChatUser;
import org.apache.catalina.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatUserRepository extends MongoRepository<ChatUser, String> {

    List<ChatUser> findAllByStatus(StatusEnum status);
}
