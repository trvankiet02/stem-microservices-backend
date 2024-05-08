package com.trvankiet.app.repository;

import com.trvankiet.app.constant.StateType;
import com.trvankiet.app.entity.GroupMemberInvitation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberInvitationRepository extends MongoRepository<GroupMemberInvitation, String> {
    List<GroupMemberInvitation> findAllByToUserId(String userId);
    List<GroupMemberInvitation> findAllByToUserIdAndState(String userId, StateType state);
}
