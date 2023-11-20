package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

    List<Group> findAllByNameAndConfigTypeAndConfigAccessibilityAndGradeAndSubject(
            String query, String type, String accessibility, Integer grade, String subject
    );

    @Query("{'$and':[ {'$or':[ {'group_name': {$regex: ?0, $options: 'i'}}, {'group_description': {$regex: ?0, $options: 'i'}} ]}, {'$or':[ {'class_grade': ?1}, {'class_subject': ?2} ]} ]}")
    List<Group> customSearch(String query, Integer grade, String subject);

    @Query("{'$or': " +
            "[" +
            "{'group_name': {$regex: ?0, $options:'i'}}, " +
            "{'group_description': {$regex: ?0, $options:'i'}}" +
            "]" +
            "}"
    )
    List<Group> searchGroupByQuery(String query);

    @Query("{" +
            "'$and': [" +
            "{'$or': [" +
            "{'group_name': {$regex: ?0, $options:'i'}}, " +
            "{'group_description': {$regex: ?0, $options:'i'}}" +
            "]}, " +
            "{'$or': [" +
            "{'class_grade': ?1}, " +
            "{'class_grade': { $exists: false }}" +
            "]}" +
            "]" +
            "}"
    )
    List<Group> searchGroups(String query, Integer grade);

}
