package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.TokenRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Token;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface TokenService {
    <S extends Token> S save(S entity);

    <S extends Token> Optional<S> findOne(Example<S> example);

    List<Token> findAll(Sort sort);

    List<Token> findAll();

    Optional<Token> findById(String id);

    Optional<Token> findByToken(String token);

    boolean existsById(String id);

    <S extends Token> long count(Example<S> example);

    <S extends Token> boolean exists(Example<S> example);

    long count();

    void deleteById(String id);

    void delete(Token entity);

    void revokeRefreshToken(String credentialId);

    ResponseEntity<GenericResponse> refreshAccessToken(TokenRequest tokenRequest);
}
