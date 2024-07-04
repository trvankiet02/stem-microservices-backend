package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

    @Query("{'$or': " +
            "[" +
            "{'group_name': {$regex: ?0, $options:'i'}}, " +
            "{'group_description': {$regex: ?0, $options:'i'}}" +
            "]" +
            "}"
    )
    List<Group> searchGroupByQuery(String query);
    @Query("{'$and': " +
            "[" +
            "{'$or': " +
            "[" +
            "{'group_name': {$regex: ?0, $options:'i'}}, " +
            "{'group_description': {$regex: ?0, $options:'i'}}" +
            "]" +
            "}, " +
            "{'isClass': ?1}" +
            "]" +
            "}"
    )
    Page<Group> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsClass(String query, Boolean isClass, Pageable pageable);
    @Query("{'$and': " +
            "[" +
            "{'$or': " +
            "[" +
            "{'group_name': {$regex: ?0, $options:'i'}}, " +
            "{'group_description': {$regex: ?0, $options:'i'}}" +
            "]" +
            "}, " +
            "{'isClass': ?1}, " +
            "{'isCompetition': ?2}" +
            "]" +
            "}"
    )
    Page<Group> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsClassAndIsCompetition(String query, Boolean isClass, Boolean Competition, Pageable pageable);

    @Query("{" +
            "'$and': [" +
            "   {'$or': [ {'group_name': {$regex: ?0, $options:'i'}}, {'group_description': {$regex: ?0, $options:'i'}} ]}, " +
            "   {'isClass': ?1}, " +
            "   {'isPublic': ?2}, " +
            "   {'isAcceptAllRequest': ?3}, " +
            "   {'class_grade': ?4} " +
            "]" +
            "}")
    List<Group> searchGroupByQueryAndIsClassAndIsPublicAndIsAcceptAllRequest(String query, Boolean isClass, Boolean isPublic, Boolean isAcceptAllRequest, Integer grade);

    List<Group> findAllBySubjectAndIsPublic(String subject, Boolean isPublic);
    List<Group> findAllBySubjectAndIsPublicAndIsCompetition(String subject, Boolean isPublic, Boolean isCompetition);
    List<Group> findAllByGradeAndIsPublic(Integer grade, Boolean isPublic);
    List<Group> findAllByGradeAndIsPublicAndIsCompetition(Integer grade, Boolean isPublic, Boolean isCompetition);
    List<Group> findAllByIsClassAndIsPublic(Boolean isClass, Boolean isPublic);
    List<Group> findAllByIsClassAndIsPublicAndIsCompetition(Boolean isClass, Boolean isPublic, Boolean isCompetition);
    Page<Group> findAllByIsClass(Boolean isClass, Pageable pageable);
    Page<Group> findAllByIsClassAndIsPublic(Boolean isClass, Boolean isPublic, Pageable pageable);
    Page<Group> findAllByIsClassAndIsPublicAndIsCompetition(Boolean isClass, Boolean isPublic, Boolean isCompetition, Pageable pageable);

    Page<Group> findAllBySubjectAndIsPublic(String subject, Boolean isPublic, Pageable pageable);
    Page<Group> findAllBySubjectAndIsPublicAndIsCompetition(String subject, Boolean isPublic, Boolean isCompetition, Pageable pageable);
    Page<Group> findAllByGradeAndIsPublic(Integer grade, Boolean isPublic, Pageable pageable);
    Page<Group> findAllByGradeAndIsPublicAndIsCompetition(Integer grade, Boolean isPublic, Boolean isCompetition, Pageable pageable);
}
