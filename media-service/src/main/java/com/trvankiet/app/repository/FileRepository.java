package com.trvankiet.app.repository;

import com.trvankiet.app.entity.File;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends MongoRepository<File, String> {
}
