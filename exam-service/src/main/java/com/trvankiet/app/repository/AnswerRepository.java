package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends MongoRepository<Answer, String> {
    List<Answer> findAllByQuestionId(String questionId);
    Optional<Answer> findByQuestionIdAndIsCorrectTrue(String questionId);
}
