package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.Provider;
import com.trvankiet.app.constant.RoleBasedAuthority;
import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.LoginRequest;
import com.trvankiet.app.dto.request.RegisterRequest;
import com.trvankiet.app.dto.request.ResetPasswordRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Token;
import com.trvankiet.app.entity.User;
import com.trvankiet.app.exception.wrapper.PasswordStrongException;
import com.trvankiet.app.exception.wrapper.TokenException;
import com.trvankiet.app.exception.wrapper.UserNotEnabledException;
import com.trvankiet.app.exception.wrapper.UserNotFoundException;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.repository.CredentialRepository;
import com.trvankiet.app.repository.UserRepository;
import com.trvankiet.app.service.CredentialService;
import com.trvankiet.app.service.EmailService;
import com.trvankiet.app.service.TokenService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        Optional<Credential> optionalCredential = credentialRepository.findById(id);
        return getCredentialDto(optionalCredential);
    }

    @Override
    public Optional<Credential> findByUsername(String username) {
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
                            .message("Account already exists")
                            .result(null)
                            .statusCode(HttpStatus.CONFLICT.value())
                            .build());

        if (registerRequest.getPassword().length() < 8 || registerRequest.getPassword().length() > 32)
            throw new PasswordStrongException("Password must be between 8 and 32 characters long");

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
                        .message("Account created successfully")
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
            throw new UserNotFoundException("Account does not exist");
        else {
            String accessToken = jwtService.generateAccessToken(optionalCredential.get());
            String refreshToken = jwtService.generateRefreshToken(optionalCredential.get());

            Token token = Token.builder()
                    .token(refreshToken)
                    .revoked(false)
                    .expired(false)
                    .type(TokenType.REFRESH_TOKEN)
                    .credential(optionalCredential.get())
                    .build();

            tokenService.revokeRefreshToken(optionalCredential.get().getCredentialId());
            tokenService.save(token);

            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("accessToken", accessToken);
            tokenMap.put("refreshToken", refreshToken);

            return ResponseEntity.ok().body(GenericResponse.builder()
                    .success(true)
                    .message("Login successfully!")
                    .result(tokenMap)
                    .statusCode(HttpStatus.OK.value())
                    .build());
        }
    }
    @Override
    public ResponseEntity<GenericResponse> verify(String token) {
        log.info("CredentialServiceImpl, ResponseEntity<GenericResponse>, verify");
        if (token == null ||  token.isBlank()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GenericResponse.builder()
                    .success(false)
                    .message("Token is required")
                    .result(null)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .build());
        } else {
            Optional<Token> optionalToken = tokenService.findByToken(token);
            if (optionalToken.isEmpty()){
                throw new TokenException("Token is invalid");
            } else {
                if (!optionalToken.get().getType().equals(TokenType.VERIFICATION_TOKEN)){
                    throw new TokenException("Token is invalid");
                } else {
                    if (optionalToken.get().getExpiredAt().isBefore(LocalDateTime.now())){
                        throw new TokenException("Token is expired");
                    } else {
                        Token verificationToken = optionalToken.get();
                        verificationToken.setExpired(true);
                        verificationToken.setRevoked(true);
                        tokenService.save(verificationToken);

                        Credential credential = verificationToken.getCredential();
                        credential.setIsEnabled(true);
                        credentialRepository.save(credential);

                        return ResponseEntity.ok().body(GenericResponse.builder()
                                .success(true)
                                .message("Account verified successfully")
                                .result(null)
                                .statusCode(HttpStatus.OK.value())
                                .build());
                    }
                }
            }
        }
    }

    @Override
    public ResponseEntity<GenericResponse> verifyResetPassword(String token) {
        log.info("CredentialServiceImpl, ResponseEntity<GenericResponse>, verifyResetPassword");
        Optional<Token> optionalToken = tokenService.findByToken(token);
        if (optionalToken.isEmpty()){
            throw new TokenException("Token is invalid");
        } else {
            if (!optionalToken.get().getType().equals(TokenType.RESET_PASSWORD_TOKEN)){
                throw new TokenException("Token is invalid");
            } else {
                if (optionalToken.get().getExpiredAt().isBefore(LocalDateTime.now())){
                    throw new TokenException("Token is expired");
                } else {
                    if (!optionalToken.get().getCredential().getIsEnabled())
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(GenericResponse.builder()
                                .success(false)
                                .message("Account is not verified")
                                .result(null)
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .build());
                    User user = optionalToken.get().getCredential().getUser();
                    UserDto userDto = UserDto.builder()
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .profileImageUrl(user.getProfileImageUrl())
                            .email(user.getEmail())
                            .build();
                    return ResponseEntity.ok()
                            .body(GenericResponse.builder()
                            .success(true)
                            .message("Verify token successfully")
                            .result(userDto)
                            .statusCode(HttpStatus.OK.value())
                            .build());
                }
            }
        }
    }

    @Override
    public ResponseEntity<GenericResponse> resetPassword(String token, ResetPasswordRequest resetPasswordRequest) {
        log.info("CredentialServiceImpl, ResponseEntity<GenericResponse>, resetPassword");
        if (!resetPasswordRequest.getPassword().equals(resetPasswordRequest.getConfirmPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GenericResponse.builder()
                    .success(false)
                    .message("Password and confirm password must be the same")
                    .result(null)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .build());
        }
        Optional<Token> optionalToken = tokenService.findByToken(token);
        if (optionalToken.isEmpty()){
            throw new TokenException("Token is invalid");
        } else {
            if (!optionalToken.get().getType().equals(TokenType.RESET_PASSWORD_TOKEN)){
                throw new TokenException("Token is invalid");
            } else {
                if (optionalToken.get().getExpiredAt().isBefore(LocalDateTime.now())){
                    throw new TokenException("Token is expired");
                } else {
                    if (!optionalToken.get().getCredential().getIsEnabled())
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(GenericResponse.builder()
                                        .success(false)
                                        .message("Account is not verified")
                                        .result(null)
                                        .statusCode(HttpStatus.BAD_REQUEST.value())
                                        .build());
                    if (resetPasswordRequest.getPassword().length() < 8 || resetPasswordRequest.getPassword().length() > 32)
                        throw new PasswordStrongException("Password must be between 8 and 32 characters long");

                    Credential credential = optionalToken.get().getCredential();
                    credential.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
                    credentialRepository.save(credential);

                    Token resetPasswordToken = optionalToken.get();
                    resetPasswordToken.setExpired(true);
                    resetPasswordToken.setRevoked(true);
                    tokenService.save(resetPasswordToken);

                    return ResponseEntity.ok()
                            .body(GenericResponse.builder()
                            .success(true)
                            .message("Reset password successfully")
                            .result(null)
                            .statusCode(HttpStatus.OK.value())
                            .build());
                }
            }
        }
    }

    private CredentialDto getCredentialDto(Optional<Credential> optionalCredential) {
        if (optionalCredential.isEmpty()){
            throw new UserNotFoundException("Account does not exist");
        } else {
            if (optionalCredential.get().getIsEnabled()){
                return CredentialDto.builder()
                        .username(optionalCredential.get().getUsername())
                        .password(optionalCredential.get().getPassword())
                        .roleBasedAuthority(optionalCredential.get().getRoleBasedAuthority())
                        .isEnabled(optionalCredential.get().getIsEnabled())
                        .isAccountNonExpired(optionalCredential.get().getIsAccountNonExpired())
                        .isAccountNonLocked(optionalCredential.get().getIsAccountNonLocked())
                        .isCredentialsNonExpired(optionalCredential.get().getIsCredentialsNonExpired())
                        .build();
            } else {
                throw new UserNotEnabledException("Account is not verified");
            }
        }
    }

}
