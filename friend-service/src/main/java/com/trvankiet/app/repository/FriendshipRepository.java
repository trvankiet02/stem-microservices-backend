package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Friendship;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendshipRepository extends MongoRepository<Friendship, String> {

    Optional<Friendship> findByAuthorIdAndFriendIdsContaining(String authorId, String userId);

    Optional<Friendship> findByAuthorId(String authorId);
}
