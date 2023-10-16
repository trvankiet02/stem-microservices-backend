package com.trvankiet.app.repository;

import com.trvankiet.app.entity.GroupMemberRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRequestRepository extends MongoRepository<GroupMemberRequest, String> {
}
