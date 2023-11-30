package com.trvankiet.app.repository;

import com.trvankiet.app.entity.GroupMember;
import com.trvankiet.app.entity.GroupMemberRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends MongoRepository<GroupMember, String> {
    List<GroupMember> findByUserId(String userId);
    List<GroupMember> findAllByUserId(String userId);
    Optional<GroupMember> findByUserIdAndGroupId(String userId, String groupId);
    List<GroupMember> findByUserIdAndGroupMemberRole(String userId, GroupMemberRole groupMemberRole);
    List<GroupMember> findByGroupId(String groupId);
}
