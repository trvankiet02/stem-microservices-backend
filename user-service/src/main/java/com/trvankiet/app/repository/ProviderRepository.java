package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Provider;
import com.trvankiet.app.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, String> {
    Optional<Provider> findByCode(String code);
    Optional<Provider> findByName(String name);
}
