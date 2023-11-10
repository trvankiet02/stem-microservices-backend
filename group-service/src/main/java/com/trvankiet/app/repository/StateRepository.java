package com.trvankiet.app.repository;

import com.trvankiet.app.entity.State;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends MongoRepository<State, String> {

    Optional<State> findByCode(String code);
    Optional<State> findByName(String name);
    Optional<State> findByDescription(String description);

}
