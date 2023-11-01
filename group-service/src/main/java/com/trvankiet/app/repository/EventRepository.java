package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findAllByGroupGroupId(String groupId);
    Optional<Event> findByEventId(String eventId);
}
