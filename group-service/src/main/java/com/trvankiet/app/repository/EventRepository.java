package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Event;
import com.trvankiet.app.entity.Group;
import io.micrometer.core.instrument.Tags;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findAllByGroupId(String groupId);

    List<Event> findAllByGroupInOrderByCreatedAtDesc(List<Group> groups);
}
