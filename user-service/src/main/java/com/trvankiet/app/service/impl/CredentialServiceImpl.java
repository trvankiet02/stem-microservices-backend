package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.Provider;
import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.request.LoginRequest;
import com.trvankiet.app.dto.request.RegisterRequest;
import com.trvankiet.app.dto.request.ResetPasswordRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Role;
import com.trvankiet.app.entity.Token;
import com.trvankiet.app.entity.User;
import com.trvankiet.app.exception.wrapper.PasswordException;
import com.trvankiet.app.exception.wrapper.TokenException;
import com.trvankiet.app.exception.wrapper.UserException;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.repository.CredentialRepository;
import com.trvankiet.app.repository.RoleRepository;
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
    private final RoleRepository roleRepository;

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

        Role role = roleRepository.findByRoleName(registerRequest.getRole()).orElseThrow(() -> new UserException("Role không tồn tại!"));

        Credential credential = Credential.builder()
                .username(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .provider(Provider.LOCAL)
                .role(role)
                .isEnabled(false)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .user(user)
                .build();

        credential = credentialRepository.save(credential);

        emailService.sendVerificationEmail(credential);

        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.builder()
                        .success(true)
                        .message("Tài khoản đã được tạo thành công! Vui lòng kiểm tra email để xác thực tài khoản!")
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
            if (!passwordEncoder.matches(loginRequest.getPassword(), optionalCredential.get().getPassword()))
                throw new UserException("Tài khoản hoặc mật khẩu không đúng!");

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
                    return ResponseEntity.ok().body(GenericResponse.builder()
                            .success(true)
                            .message("Xác thực thành công! Mời bạn nhập mật khẩu mới!")
                            .result(null)
                            .statusCode(HttpStatus.OK.value())
                            .build());
                }
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GenericResponse.builder()
                        .success(false)
                        .message("Yêu cầu đã hết hạn hoặc không hợp lệ!")
                        .result(null)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build());
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
        String userId = jwtService.extractUserId(accessToken);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserException("Không tìm thấy người dùng!");
        }
        Optional<Credential> optionalCredential = Optional.ofNullable(optionalUser.get().getCredential());
        if (optionalCredential.isPresent()) {
            tokenService.revokeRefreshToken(optionalCredential.get().getCredentialId());
            return ResponseEntity.ok().body(GenericResponse.builder()
                    .success(true)
                    .message("Đăng xuất thành công!")
                    .result(null)
                    .statusCode(HttpStatus.OK.value())
                    .build());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(GenericResponse.builder()
                        .success(false)
                        .message("Đăng xuất thất bại! Vui lòng đăng nhập trước khi thực hiện hành động này!")
                        .result(null)
                        .statusCode(HttpStatus.FORBIDDEN.value())
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
                .role(optionalCredential.get().getRole().getRoleName())
                .isEnabled(optionalCredential.get().getIsEnabled())
                .isAccountNonExpired(optionalCredential.get().getIsAccountNonExpired())
                .isAccountNonLocked(optionalCredential.get().getIsAccountNonLocked())
                .isCredentialsNonExpired(optionalCredential.get().getIsCredentialsNonExpired())
                .build();
    }

}

