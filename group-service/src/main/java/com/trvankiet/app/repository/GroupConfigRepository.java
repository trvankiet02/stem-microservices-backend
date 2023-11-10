package com.trvankiet.app.repository;

import com.trvankiet.app.entity.GroupConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupConfigRepository extends MongoRepository<GroupConfig, String> {

    Optional<GroupConfig> findByCode(String code);
    Optional<GroupConfig> findByAccessibility(String accessibility);
    Optional<GroupConfig> findByMemberMode(String memberMode);
    Optional<GroupConfig> findByType(String type);
    Optional<GroupConfig> findByTypeAndAccessibilityAndMemberMode(String type, String accessibility, String memberMode);
}
