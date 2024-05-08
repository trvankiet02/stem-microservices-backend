package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.EmailRequest;
import com.trvankiet.app.dto.request.TokenRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Token;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface TokenService {
    <S extends Token> S save(S entity);

    List<Token> findAll();

    Optional<Token> findById(String id);

    Optional<Token> findByToken(String token);

    boolean existsById(String id);

    long count();

    void deleteById(String id);

    void delete(Token entity);

    void revokeRefreshToken(String credentialId);

    ResponseEntity<GenericResponse> refreshAccessToken(TokenRequest tokenRequest);

    ResponseEntity<GenericResponse> resetPassword(EmailRequest email);
}
