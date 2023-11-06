package com.trvankiet.app.repository;

import com.trvankiet.app.entity.File;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<File, String> {
    Optional<File> findByRefUrl(String refUrl);
}
