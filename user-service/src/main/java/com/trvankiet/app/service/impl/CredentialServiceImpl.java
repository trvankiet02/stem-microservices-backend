package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.Provider;
import com.trvankiet.app.constant.RoleBasedAuthority;
import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.request.LoginRequest;
import com.trvankiet.app.dto.request.RegisterRequest;
import com.trvankiet.app.dto.request.ResetPasswordRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Token;
import com.trvankiet.app.entity.User;
import com.trvankiet.app.exception.wrapper.PasswordException;
import com.trvankiet.app.exception.wrapper.TokenException;
import com.trvankiet.app.exception.wrapper.UserException;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.repository.CredentialRepository;
import com.trvankiet.app.repository.UserRepository;
import com.trvankiet.app.service.CredentialService;
import com.trvankiet.app.service.EmailService;
import com.trvankiet.app.service.TokenService;
import com.trvankiet.app.util.TokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {

    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final EmailService emailService;

    @Override
    public <S extends Credential> S save(S entity) {
        return credentialRepository.save(entity);
    }

    @Override
    public <S extends Credential> Optional<S> findOne(Example<S> example) {
        return credentialRepository.findOne(example);
    }

    @Override
    public List<Credential> findAll(Sort sort) {
        return credentialRepository.findAll(sort);
    }

    @Override
    public List<Credential> findAll() {
        return credentialRepository.findAll();
    }

    @Override
    public Optional<Credential> findById(String id) {
        return credentialRepository.findById(id);
    }

    @Override
    public CredentialDto findByIdDto(String id) {
        log.info("CredentialServiceImpl, CredentialDto, findByIdDto");
        Optional<Credential> optionalCredential = credentialRepository.findById(id);
        return getCredentialDto(optionalCredential);
    }

    @Override
    public Optional<Credential> findByUsername(String username) {
        log.info("CredentialServiceImpl, Optional<Credential>, findByUsername");
        return credentialRepository.findByUsername(username);
    }

    @Override
    public boolean existsById(String id) {
        return credentialRepository.existsById(id);
    }

    @Override
    public <S extends Credential> long count(Example<S> example) {
        return credentialRepository.count(example);
    }

    @Override
    public <S extends Credential> boolean exists(Example<S> example) {
        return credentialRepository.exists(example);
    }

    @Override
    public long count() {
        return credentialRepository.count();
    }

    @Override
    public void deleteById(String id) {
        credentialRepository.deleteById(id);
    }

    @Override
    public void delete(Credential entity) {
        credentialRepository.delete(entity);
    }

    @Override
    public ResponseEntity<GenericResponse> register(RegisterRequest registerRequest) {
        log.info("CredentialServiceImpl, ResponseEntity<GenericResponse>, register");
        Optional<Credential> optionalCredential = credentialRepository.findByUsername(registerRequest.getEmail());

        if (optionalCredential.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT.value())
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Tài khoản đã tồn tại trong hệ thống!")
                            .result(null)
                            .statusCode(HttpStatus.CONFLICT.value())
                            .build());

        if (registerRequest.getPassword().length() < 8 || registerRequest.getPassword().length() > 32)
            throw new PasswordException("Mật khẩu phải có độ dài từ 8 đến 32 ký tự!");

        User user = userRepository.save(User.builder()
                .email(registerRequest.getEmail())
                .build());

        Credential credential = Credential.builder()
                .username(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .provider(Provider.LOCAL)
                .isEnabled(false)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .user(user)
                .build();

        switch (registerRequest.getRole()) {
            case "ADMIN":
                credential.setRoleBasedAuthority(RoleBasedAuthority.ADMIN);
                break;
            case "PARENT":
                credential.setRoleBasedAuthority(RoleBasedAuthority.PARENT);
                break;
            case "TEACHER":
                credential.setRoleBasedAuthority(RoleBasedAuthority.TEACHER);
                break;
            case "STUDENT":
                credential.setRoleBasedAuthority(RoleBasedAuthority.STUDENT);
        }

        credential = credentialRepository.save(credential);

        emailService.sendVerificationEmail(credential);

        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.builder()
                        .success(true)
                        .message("Tài khoản đã được tạo thành công!")
                        .result(null)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @Override
    public CredentialDto findByUsernameDto(String username) {
        log.info("CredentialServiceImpl, CredentialDto, findByUsernameDto");
        Optional<Credential> optionalCredential = credentialRepository.findByUsername(username);
        return getCredentialDto(optionalCredential);
    }

    @Override
    public ResponseEntity<GenericResponse> login(LoginRequest loginRequest) {
        log.info("CredentialServiceImpl, ResponseEntity<GenericResponse>, login");
        Optional<Credential> optionalCredential = credentialRepository.findByUsername(loginRequest.getEmail());

        if (optionalCredential.isEmpty())
            throw new UserException("Tài khoản hoặc mật khẩu không đúng!");
        else {
            String accessToken = jwtService.generateAccessToken(optionalCredential.get());
            String refreshToken = jwtService.generateRefreshToken(optionalCredential.get());

            //30 - Refresh token expired time
            Token token = Token.builder()
                    .token(refreshToken)
                    .revoked(false)
                    .expired(false)
                    .type(TokenType.REFRESH_TOKEN)
                    .expiredAt(LocalDateTime.now().plusDays(30))
                    .credential(optionalCredential.get())
                    .build();

            tokenService.revokeRefreshToken(optionalCredential.get().getCredentialId());
            tokenService.save(token);

            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("accessToken", accessToken);
            tokenMap.put("refreshToken", refreshToken);

            return ResponseEntity.ok().body(GenericResponse.builder()
                    .success(true)
                    .message("Đăng nhập thành công!")
                    .result(tokenMap)
                    .statusCode(HttpStatus.OK.value())
                    .build());
        }
    }

    @Override
    public ResponseEntity<GenericResponse> verify(String token) {
        log.info("CredentialServiceImpl, ResponseEntity<GenericResponse>, verify");
        if (!token.isBlank()) {
            Optional<Token> optionalToken = tokenService.findByToken(token);
            if (optionalToken.isPresent()) {
                Token verificationToken = optionalToken.get();
                if (verificationToken.getType().equals(TokenType.VERIFICATION_TOKEN)
                        && TokenUtil.tokenIsNotExpiredAndRevoked(verificationToken)
                        && !verificationToken.getExpiredAt().isBefore(LocalDateTime.now())) {
                    return ResponseEntity.ok().body(GenericResponse.builder()
                            .success(true)
                            .message("Xác thực thành công! Mời bạn điền thông tin cần thiết để hoàn tất đăng ký!")
                            .result(null)
                            .statusCode(HttpStatus.OK.value())
                            .build());
                }
            }
        }
        throw new TokenException("Yêu cầu xác thực đã hết hạn hoặc không hợp lệ!");
    }

    @Override
    public ResponseEntity<GenericResponse> verifyResetPassword(String token) {
        log.info("CredentialServiceImpl, ResponseEntity<GenericResponse>, verifyResetPassword");
        if (!token.isBlank()) {
            Optional<Token> optionalToken = tokenService.findByToken(token);
            if (optionalToken.isPresent()) {
                Token resetPasswordToken = optionalToken.get();
                if (resetPasswordToken.getType().equals(TokenType.RESET_PASSWORD_TOKEN)
                        && TokenUtil.tokenIsNotExpiredAndRevoked(resetPasswordToken)
                        && !resetPasswordToken.getExpiredAt().isBefore(LocalDateTime.now())) {
                    resetPasswordToken.setExpired(true);
                    resetPasswordToken.setRevoked(true);
                    tokenService.save(resetPasswordToken);

                    return ResponseEntity.ok().body(GenericResponse.builder()
                            .success(true)
                            .message("Xác thực thành công! Mời bạn nhập mật khẩu mới!")
                            .result(null)
                            .statusCode(HttpStatus.OK.value())
                            .build());
                }
            }
        }
        throw new TokenException("Yêu cầu xác thực cho hành động quên mật khẩu đã hết hạn hoặc không hợp lệ!");
    }

    @Override
    public ResponseEntity<GenericResponse> resetPassword(String token, ResetPasswordRequest resetPasswordRequest) {
        log.info("CredentialServiceImpl, ResponseEntity<GenericResponse>, resetPassword");
        if (!resetPasswordRequest.getPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Mật khẩu và mật khẩu xác nhận không khớp! Vui lòng nhập lại!")
                            .result(null)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        }
        Optional<Token> optionalToken = tokenService.findByToken(token);
        if (optionalToken.isEmpty()) {
            throw new TokenException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        Token resetPasswordToken = optionalToken.get();

        if (!resetPasswordToken.getType().equals(TokenType.RESET_PASSWORD_TOKEN)) {
            throw new TokenException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        if (resetPasswordToken.getExpiredAt().isBefore(LocalDateTime.now()) && TokenUtil.tokenIsNotExpiredAndRevoked(resetPasswordToken)) {
            throw new TokenException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        Credential credential = resetPasswordToken.getCredential();
        if (!credential.getIsEnabled()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Tài khoản chưa được xác thực! Vui lòng xác thực tài khoản trước khi thực hiện hành động này!")
                            .result(null)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        }

        if (resetPasswordRequest.getPassword().length() < 8 || resetPasswordRequest.getPassword().length() > 32) {
            throw new PasswordException("Mật khẩu phải có độ dài từ 8 đến 32 ký tự!");
        }

        credential.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        credentialRepository.save(credential);

        resetPasswordToken.setExpired(true);
        resetPasswordToken.setRevoked(true);
        tokenService.save(resetPasswordToken);

        return ResponseEntity.ok()
                .body(GenericResponse.builder()
                        .success(true)
                        .message("Đặt lại mật khẩu thành công!")
                        .result(null)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @Override
    public ResponseEntity<GenericResponse> logout(String authorizationHeader) {
        String accessToken = authorizationHeader.substring(7);
        String credentialId = jwtService.extractCredentialId(accessToken);
        Optional<Credential> optionalCredential = credentialRepository.findById(credentialId);
        if (optionalCredential.isPresent()) {
            tokenService.revokeRefreshToken(credentialId);
            return ResponseEntity.ok().body(GenericResponse.builder()
                    .success(true)
                    .message("Đăng xuất thành công!")
                    .result(null)
                    .statusCode(HttpStatus.OK.value())
                    .build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GenericResponse.builder()
                        .success(false)
                        .message("Đăng xuất thất bại! Vui lòng đăng nhập trước khi thực hiện hành động này!")
                        .result(null)
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .build());
    }

    private CredentialDto getCredentialDto(Optional<Credential> optionalCredential) {
        if (optionalCredential.isEmpty()) {
            throw new UserException("Tài khoản không tồn tại!");
        }
        if (!optionalCredential.get().getIsEnabled())
            throw new UserException("Tài khoản chưa được xác thực!");
        return CredentialDto.builder()
                .username(optionalCredential.get().getUsername())
                .password(optionalCredential.get().getPassword())
                .roleBasedAuthority(optionalCredential.get().getRoleBasedAuthority())
                .isEnabled(optionalCredential.get().getIsEnabled())
                .isAccountNonExpired(optionalCredential.get().getIsAccountNonExpired())
                .isAccountNonLocked(optionalCredential.get().getIsAccountNonLocked())
                .isCredentialsNonExpired(optionalCredential.get().getIsCredentialsNonExpired())
                .build();
    }

}

