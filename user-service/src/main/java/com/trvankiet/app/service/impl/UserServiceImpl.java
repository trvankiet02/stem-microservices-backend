package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.Gender;
import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.dto.request.UserInfoRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Token;
import com.trvankiet.app.entity.User;
import com.trvankiet.app.exception.wrapper.TokenException;
import com.trvankiet.app.exception.wrapper.UserException;
import com.trvankiet.app.repository.CredentialRepository;
import com.trvankiet.app.repository.TokenRepository;
import com.trvankiet.app.repository.UserRepository;
import com.trvankiet.app.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

        Token verificationToken = validateAndRetrieveVerificationToken(token);

        User user = verificationToken.getCredential().getUser();
        user.setFirstName(userInfoRequest.getFirstName());
        user.setLastName(userInfoRequest.getLastName());
        user.setDob(userInfoRequest.getDob());
        user.setPhone(userInfoRequest.getPhone());

        try {
            user.setGender(Gender.valueOf(userInfoRequest.getGender()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Giới tính không hợp lệ!");
        }

        userRepository.save(user);

        Credential credential = verificationToken.getCredential();
        credential.setIsEnabled(true);
        credentialRepository.save(credential);

        verificationToken.setRevoked(true);
        verificationToken.setExpired(true);
        tokenRepository.save(verificationToken);

        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Thông tin tài khoản đã được khởi tạo thành công!")
                        .result(null)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    private Token validateAndRetrieveVerificationToken(String token) {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            throw new TokenException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        Token verificationToken = optionalToken.get();

        if (!verificationToken.getType().equals(TokenType.VERIFICATION_TOKEN)) {
            throw new TokenException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        if (verificationToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new TokenException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        return verificationToken;
    }

}
