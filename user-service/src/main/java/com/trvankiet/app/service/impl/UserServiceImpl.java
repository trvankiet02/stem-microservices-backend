package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.Gender;
import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.dto.request.UserInfoRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Token;
import com.trvankiet.app.entity.User;
import com.trvankiet.app.exception.wrapper.TokenException;
import com.trvankiet.app.exception.wrapper.UserNotFoundException;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.repository.CredentialRepository;
import com.trvankiet.app.repository.TokenRepository;
import com.trvankiet.app.repository.UserRepository;
import com.trvankiet.app.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final CredentialRepository credentialRepository;

    @Override
    public <S extends User> S save(S entity) {
        return userRepository.save(entity);
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return userRepository.findOne(example);
    }

    @Override
    public List<User> findAll(Sort sort) {
        return userRepository.findAll(sort);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        return userRepository.count(example);
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        return userRepository.exists(example);
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public void delete(User entity) {
        userRepository.delete(entity);
    }

    @Override
    public ResponseEntity<GenericResponse> initCredentialInfo(String token, UserInfoRequest userInfoRequest) {
        log.info("UserServiceImpl, ResponseEntity<GenericResponse>, initCredentialInfo");
        Optional<Token> optionalToken = tokenRepository.findByToken(token);
        if (userInfoRequest.getDob().after(new Date()))
            throw new IllegalArgumentException("Date of birth is invalid");
        if (optionalToken.isEmpty())
            throw new TokenException("Token is invalid");
        else {
            if (!optionalToken.get().getType().equals(TokenType.VERIFICATION_TOKEN)){
                throw new TokenException("Token is not verification token");
            } else {
                if (optionalToken.get().getExpiredAt().isBefore(LocalDateTime.now()))
                    throw new TokenException("Token is expired");
                else {
                    Token verificationToken = tokenRepository.findByToken(token).orElseThrow(() -> new TokenException("Token is invalid"));
                    Credential credential = credentialRepository.findById(verificationToken.getCredential().getCredentialId()).orElseThrow(() -> new UserNotFoundException("User is not found"));
                    try {
                        User user = credential.getUser();
                        user.setFirstName(userInfoRequest.getFirstName());
                        user.setLastName(userInfoRequest.getLastName());
                        user.setDob(userInfoRequest.getDob());
                        user.setPhone(userInfoRequest.getPhone());
                        user.setGender(Gender.valueOf(userInfoRequest.getGender()));
                        userRepository.save(user);

                        credential.setIsEnabled(true);
                        credentialRepository.save(credential);

                        verificationToken.setRevoked(true);
                        verificationToken.setExpired(true);
                        tokenRepository.save(verificationToken);

                        return ResponseEntity.ok(
                                GenericResponse.builder()
                                .success(true)
                                .message("Init credential info successfully")
                                .result(null)
                                .statusCode(200)
                                .build());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid Gender");
                    }
                }
            }
        }
    }
}
