package com.trvankiet.app.repository;

import com.trvankiet.app.entity.QuestionType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionTypeRepository extends MongoRepository<QuestionType, String> {

    Optional<QuestionType> findByCode(String code);
    Optional<QuestionType> findByName(String name);
}
