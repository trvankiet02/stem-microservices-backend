package com.trvankiet.app.repository;

import com.trvankiet.app.entity.GroupMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends MongoRepository<GroupMember, String> {
}
