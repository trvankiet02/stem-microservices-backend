package com.trvankiet.app.repository;

import com.trvankiet.app.entity.GroupMemberRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GroupMemberRoleRepository extends MongoRepository<GroupMemberRole, String> {
    Optional<GroupMemberRole> findByRoleName(String roleName);
}
