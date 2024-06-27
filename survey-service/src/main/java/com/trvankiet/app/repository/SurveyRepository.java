package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends MongoRepository<Survey, String> {
    Page<Survey> findByGroupIdIn(List<String> groupIds, Pageable pageable);
    Page<Survey> findAllByGroupId(String groupId, Pageable pageable);
}
