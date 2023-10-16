package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.dto.request.EmailRequest;
import com.trvankiet.app.dto.request.TokenRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Token;
import com.trvankiet.app.entity.User;
import com.trvankiet.app.exception.wrapper.TokenException;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.repository.CredentialRepository;
import com.trvankiet.app.repository.TokenRepository;
import com.trvankiet.app.repository.UserRepository;
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
    private final UserRepository userRepository;


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
        log.info("TokenServiceImpl, void, revokeRefreshToken");
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
        Optional<Credential> optionalCredential = getValidCredentialFromRefreshToken(refreshToken);

        if (optionalCredential.isPresent()) {
            String newAccessToken = jwtService.generateAccessToken(optionalCredential.get());
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("accessToken", newAccessToken);
            resultMap.put("refreshToken", refreshToken);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponse.builder()
                            .success(true)
                            .message("Làm mới token thành công!")
                            .result(resultMap)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GenericResponse.builder()
                        .success(false)
                        .message("Không xác thực! Vui lòng đăng nhập lại!")
                        .result(null)
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .build());
    }

    private Optional<Credential> getValidCredentialFromRefreshToken(String refreshToken) {
        String userId = jwtService.extractUserId(refreshToken);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new TokenException("Không tìm thấy người dùng!");
        }
        Credential credential = optionalUser.get().getCredential();
        if (credential.getIsEnabled()) {
            if (jwtService.validateToken(refreshToken)) {
                return Optional.of(credential);
            } else {
                throw new TokenException("Refresh token đã hết hạn hoặc không chính xác!");
            }
        }

        return Optional.empty();
    }


    @Override
    public ResponseEntity<GenericResponse> resetPassword(EmailRequest emailRequest) {
        String email = emailRequest.getEmail();
        Optional<Credential> optionalCredential = credentialRepository.findByUsername(email);
        if (optionalCredential.isPresent() && optionalCredential.get().getIsEnabled()) {
            Credential credential = optionalCredential.get();
            emailService.sendResetPasswordEmail(credential);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponse.builder()
                            .success(true)
                            .message("Vui lòng kiểm tra email để đặt lại mật khẩu!")
                            .result(null)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(GenericResponse.builder()
                        .success(false)
                        .message("Tài khoản không tồn tại hoặc chưa được xác thực!")
                        .result(null)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .build());
    }
}
