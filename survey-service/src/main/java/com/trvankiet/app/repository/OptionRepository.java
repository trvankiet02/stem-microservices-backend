package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Option;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends MongoRepository<Option, String> {
    List<Option> findAllBySurveyId(String surveyId);
}
