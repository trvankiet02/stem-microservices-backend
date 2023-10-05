package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findByCredential(Credential credential);
    Optional<Token> findByToken(String token);
}
