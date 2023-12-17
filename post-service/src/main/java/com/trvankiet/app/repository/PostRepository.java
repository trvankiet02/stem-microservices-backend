package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findAllByGroupId(String groupId);

    @Query("{post_content: {$regex: ?0, $options: 'i'}}")
    List<Post> searchPost(String query);

    //findAllBy PostId and Pageable
    Page<Post> findAllByGroupId(String groupId, Pageable pageable);

    //findAllBy PostIds and Pageable
    Page<Post> findAllByGroupIdIn(List<String> groupIds, Pageable pageable);

    long countByGroupId(String groupId);
}
