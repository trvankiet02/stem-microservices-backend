package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends MongoRepository<Submission, String> {
}
