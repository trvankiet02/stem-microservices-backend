package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.dto.request.TokenRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Token;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.repository.CredentialRepository;
import com.trvankiet.app.repository.TokenRepository;
import com.trvankiet.app.service.EmailService;
import com.trvankiet.app.service.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final CredentialRepository credentialRepository;
    private final JwtService jwtService;
    private final EmailService emailService;


    @Override
    public <S extends Token> S save(S entity) {
        return tokenRepository.save(entity);
    }

    @Override
    public <S extends Token> Optional<S> findOne(Example<S> example) {
        return tokenRepository.findOne(example);
    }

    @Override
    public List<Token> findAll(Sort sort) {
        return tokenRepository.findAll(sort);
    }

    @Override
    public List<Token> findAll() {
        return tokenRepository.findAll();
    }

    @Override
    public Optional<Token> findById(String id) {
        return tokenRepository.findById(id);
    }

    @Override
    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public boolean existsById(String id) {
        return tokenRepository.existsById(id);
    }

    @Override
    public <S extends Token> long count(Example<S> example) {
        return tokenRepository.count(example);
    }

    @Override
    public <S extends Token> boolean exists(Example<S> example) {
        return tokenRepository.exists(example);
    }

    @Override
    public long count() {
        return tokenRepository.count();
    }

    @Override
    public void deleteById(String id) {
        tokenRepository.deleteById(id);
    }

    @Override
    public void delete(Token entity) {
        tokenRepository.delete(entity);
    }

    @Override
    public void revokeRefreshToken(String credentialId) {
        Optional<Credential> optionalCredential = credentialRepository.findById(credentialId);
        if (optionalCredential.isPresent() && optionalCredential.get().getIsEnabled()) {
            List<Token> refreshTokens = tokenRepository.findAll().stream()
                    .filter(token -> token.getCredential().getCredentialId().equals(credentialId) &&
                            token.getType().equals(TokenType.REFRESH_TOKEN) &&
                            !token.getRevoked() && !token.getExpired())
                    .toList();
            if(refreshTokens.isEmpty()){
                return;
            }
            refreshTokens.forEach(token -> {
                token.setRevoked(true);
                token.setExpired(true);
            });
            tokenRepository.saveAll(refreshTokens);
        }
    }

    @Override
    public ResponseEntity<GenericResponse> refreshAccessToken(TokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRefreshToken();
        String userId = jwtService.extractCredentialId(refreshToken);
        Optional<Credential> optionalCredential = credentialRepository.findById(userId);
        if (optionalCredential.isPresent() && optionalCredential.get().getIsEnabled()) {
            if (jwtService.validateToken(refreshToken, optionalCredential.get())) {
                String newAccessToken = jwtService.generateAccessToken(optionalCredential.get());
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("accessToken", newAccessToken);
                resultMap.put("refreshToken", refreshToken);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(GenericResponse.builder()
                                .success(true)
                                .message("Refresh access token successfully!")
                                .result(resultMap)
                                .statusCode(HttpStatus.OK.value())
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(GenericResponse.builder()
                                .success(false)
                                .message("Refresh token is expired!")
                                .result(null)
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .build());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GenericResponse.builder()
                        .success(false)
                        .message("Unauthorized. Please login again!")
                        .result(null)
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .build());
    }

    @Override
    public ResponseEntity<GenericResponse> resetPassword(String email) {
        Optional<Credential> optionalCredential = credentialRepository.findByUsername(email);
        if (optionalCredential.isPresent() && optionalCredential.get().getIsEnabled()) {
            Credential credential = optionalCredential.get();
            emailService.sendResetPasswordEmail(credential);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponse.builder()
                            .success(true)
                            .message("Reset password email sent successfully!")
                            .result(null)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(GenericResponse.builder()
                        .success(false)
                        .message("Invalid user!")
                        .result(null)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .build());
    }
}
