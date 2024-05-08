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
    List<Group> findAllByGradeAndIsPublic(Integer grade, Boolean isPublic);
    List<Group> findAllByIsClassAndIsPublic(Boolean isClass, Boolean isPublic);
    Page<Group> findAllByIsClass(Boolean isClass, Pageable pageable);
    Page<Group> findAllByIsClassAndIsPublic(Boolean isClass, Boolean isPublic, Pageable pageable);

    Page<Group> findAllBySubjectAndIsPublic(String subject, Boolean isPublic, Pageable pageable);
    Page<Group> findAllByGradeAndIsPublic(Integer grade, Boolean isPublic, Pageable pageable);
}
