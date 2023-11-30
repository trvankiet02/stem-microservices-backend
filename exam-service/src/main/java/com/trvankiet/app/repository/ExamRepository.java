package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Exam;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends MongoRepository<Exam, String> {
    @Query("{$or: [" +
            "{exam_name: {$regex: ?0, $options: 'i'}}, " +
            "{exam_description: {$regex: ?0, $options: 'i'}}]}")
    List<Exam> searchExam(String query);
    List<Exam> findAllByGroupId(String groupId);

}
