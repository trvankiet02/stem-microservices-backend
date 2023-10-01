package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Album;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends MongoRepository<Album, String> {
}
