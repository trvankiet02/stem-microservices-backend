package com.trvankiet.app.repository;

import com.trvankiet.app.constant.GroupMemberRoleType;
import com.trvankiet.app.entity.GroupMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends MongoRepository<GroupMember, String> {
    List<GroupMember> findByUserId(String userId);
    List<GroupMember> findAllByUserId(String userId);
    Optional<GroupMember> findByUserIdAndGroupId(String userId, String groupId);
    List<GroupMember> findAllByGroupId(String groupId);

    Integer countByGroupId(String groupId);
    List<GroupMember> findAllByUserIdAndRole(String userId, GroupMemberRoleType role);
}
