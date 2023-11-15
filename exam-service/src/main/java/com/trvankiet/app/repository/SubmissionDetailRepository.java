package com.trvankiet.app.repository;

import com.trvankiet.app.entity.SubmissionDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionDetailRepository extends MongoRepository<SubmissionDetail, String> {
    List<SubmissionDetail> findAllBySubmissionId(String submissionId);
}
