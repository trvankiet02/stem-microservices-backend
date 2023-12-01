package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends MongoRepository<Submission, String> {
    Optional<Submission> findByExamIdAndAuthorId(String examId, String userId);

    List<Submission> findAllByExamId(String examId);

    Optional<Submission> findAllByExamIdAndAuthorId(String examId, String authorId);
}
