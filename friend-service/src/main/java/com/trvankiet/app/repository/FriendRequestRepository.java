package com.trvankiet.app.repository;

import com.trvankiet.app.entity.FriendRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends MongoRepository<FriendRequest, String> {

    List<FriendRequest> findAllByRecipientId(String recipientId);

    @Query("{'$or': [{'senderId': ?0, 'recipientId': ?1}, {'senderId': ?1, 'recipientId': ?0}]}")
    Optional<FriendRequest> findByTwoUserId(String senderId, String recipientId);
}
