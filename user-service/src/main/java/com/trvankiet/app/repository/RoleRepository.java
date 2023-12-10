package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByCode(String code);
    Optional<Role> findByName(String name);
}
