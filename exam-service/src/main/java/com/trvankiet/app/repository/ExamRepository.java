package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Exam;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends MongoRepository<Exam, String> {
}
