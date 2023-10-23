package com.trvankiet.app.repository;

import com.trvankiet.app.entity.PostType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostTypeRepository extends MongoRepository<PostType, String> {
    Optional<PostType> findByPostTypeName(String postTypeName);
}
