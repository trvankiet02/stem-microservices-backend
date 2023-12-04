package com.trvankiet.app.repository;

import com.trvankiet.app.entity.GroupConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupConfigRepository extends MongoRepository<GroupConfig, String> {

    Optional<GroupConfig> findByCode(String code);
    List<GroupConfig> findAllByAccessibility(String accessibility);
    List<GroupConfig> findAllByMemberMode(String memberMode);
    List<GroupConfig> findAllByType(String type);
    List<GroupConfig> findAllByTypeAndAccessibility(String type, String accessibility);



    Optional<GroupConfig> findByTypeAndAccessibilityAndMemberMode(String type, String accessibility, String memberMode);
}
