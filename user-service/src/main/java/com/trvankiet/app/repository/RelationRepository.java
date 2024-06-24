package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationRepository extends JpaRepository<Relationship, String> {
    List<Relationship> findAllByChildId(String childId);
    List<Relationship> findAllByParentId(String parentId);
    Optional<Relationship> findByParentIdAndChildId(String parentId, String childId);
}
