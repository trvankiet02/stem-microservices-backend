package com.trvankiet.app.repository;

import com.trvankiet.app.entity.GroupMemberRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberRequestRepository extends MongoRepository<GroupMemberRequest, String> {
    List<GroupMemberRequest> findAllByGroupIdAndStateCode(String groupId, String state);
}
