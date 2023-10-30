package com.trvankiet.app.repository;

import com.trvankiet.app.entity.FileType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileTypeRepository extends MongoRepository<FileType, String> {
    Optional<FileType> findByFileTypeName(String fileTypeName);

    Optional<FileType> findByFileTypeExtensionContaining(String extension);
}
