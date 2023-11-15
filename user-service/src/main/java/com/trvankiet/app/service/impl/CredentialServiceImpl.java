package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.*;
import com.trvankiet.app.dto.request.*;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.*;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.repository.CredentialRepository;
import com.trvankiet.app.repository.ProviderRepository;
import com.trvankiet.app.repository.RoleRepository;
import com.trvankiet.app.repository.UserRepository;
import com.trvankiet.app.service.CredentialService;
import com.trvankiet.app.service.EmailService;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.TokenService;
import com.trvankiet.app.util.DateUtil;
import com.trvankiet.app.util.TokenUtil;
import io.swagger.v3.oas.models.examples.Example;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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
    private final ProviderRepository providerRepository;
    private final MapperService mapperService;

    @Override
    public <S extends Credential> S save(S entity) {
        return credentialRepository.save(entity);
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
    public ResponseEntity<GenericResponse> registerForTeacher(TeacherRegisterRequest teacherRegisterRequest) {
        log.info("CredentialServiceImpl, ResponseEntity<GenericResponse>, register");
        Optional<Credential> optionalCredential = credentialRepository.findByUsername(teacherRegisterRequest.getEmail());

        if (optionalCredential.isPresent())
            throw new BadRequestException("Tài khoản đã tồn tại!");

        if (teacherRegisterRequest.getPassword().length() < 8 || teacherRegisterRequest.getPassword().length() > 32)
            throw new BadRequestException("Mật khẩu phải có độ dài từ 8 đến 32 ký tự!");

        User user = userRepository.save(User.builder()
                .email(teacherRegisterRequest.getEmail())
                .role(RoleBasedAuthority.TEACHER)
                .build());

        Role role = roleRepository.findByCode(RoleType.ROLE_USER.getCode())
                .orElseThrow(() -> new NotFoundException("Role không tồn tại!"));

        Provider provider = providerRepository.findByCode(ProviderType.PROVIDER_LOCAL.getCode())
                .orElseThrow(() -> new NotFoundException("Provider không tồn tại!"));

        Credential credential = Credential.builder()
                .username(teacherRegisterRequest.getEmail())
                .password(passwordEncoder.encode(teacherRegisterRequest.getPassword()))
                .isEnabled(false)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .lockedAt(null)
                .lockedReason(null)
                .user(user)
                .role(role)
                .provider(provider)
                .build();

        credential = credentialRepository.save(credential);

        emailService.sendVerificationEmail(credential);

        return ResponseEntity.ok().body(
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
            throw new BadRequestException("Tài khoản hoặc mật khẩu không đúng!");
        else {
            if (!passwordEncoder.matches(loginRequest.getPassword(), optionalCredential.get().getPassword()))
                throw new BadRequestException("Tài khoản hoặc mật khẩu không đúng!");

            String accessToken = jwtService.generateAccessToken(optionalCredential.get());
            String refreshToken = jwtService.generateRefreshToken(optionalCredential.get());

            //30 - Refresh token expired time
            Token token = Token.builder()
                    .token(refreshToken)
                    .isExpired(false)
                    .isRevoked(false)
                    .type(TokenType.REFRESH_ACCESS_TOKEN)
                    .expiredAt(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000))
                    .credential(optionalCredential.get())
                    .build();

            tokenService.revokeRefreshToken(optionalCredential.get().getId());
            tokenService.save(token);

            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("accessToken", accessToken);
            tokenMap.put("refreshToken", refreshToken);

            return ResponseEntity.ok().body(
                    GenericResponse.builder()
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
        Token verificationToken = tokenService.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Yêu cầu xác thực đã hết hạn hoặc không hợp lệ!"));
        if (verificationToken.getType().equals(TokenType.VERIFICATION_TOKEN)
                && TokenUtil.tokenIsNotExpiredAndRevoked(verificationToken)
                && !verificationToken.getExpiredAt().before(new Date())) {
            return ResponseEntity.ok().body(
                    GenericResponse.builder()
                            .success(true)
                            .message("Xác thực thành công! Mời bạn điền thông tin cần thiết để hoàn tất đăng ký!")
                            .result(null)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        }
        throw new BadRequestException("Yêu cầu xác thực đã hết hạn hoặc không hợp lệ!");
    }

    @Override
    public ResponseEntity<GenericResponse> verifyResetPassword(String token) {
        log.info("CredentialServiceImpl, ResponseEntity<GenericResponse>, verifyResetPassword");
        Token resetPasswordToken = tokenService.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Yêu cầu xác thực đã hết hạn hoặc không hợp lệ!"));
        if (resetPasswordToken.getType().equals(TokenType.RESET_PASSWORD_TOKEN)
                && TokenUtil.tokenIsNotExpiredAndRevoked(resetPasswordToken)
                && !resetPasswordToken.getExpiredAt().before(new Date())) {
            return ResponseEntity.ok().body(
                    GenericResponse.builder()
                            .success(true)
                            .message("Xác thực thành công! Mời bạn nhập mật khẩu mới!")
                            .result(null)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        }
        throw new BadRequestException("Yêu cầu xác thực đã hết hạn hoặc không hợp lệ!");
    }

    @Override
    public ResponseEntity<GenericResponse> resetPassword(String token, ResetPasswordRequest resetPasswordRequest) {
        log.info("CredentialServiceImpl, ResponseEntity<GenericResponse>, resetPassword");
        if (!resetPasswordRequest.getPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp!");
        }

        Token resetPasswordToken = tokenService.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Yêu cầu đã hết hạn hoặc không hợp lệ!"));

        if (!resetPasswordToken.getType().equals(TokenType.RESET_PASSWORD_TOKEN)) {
            throw new BadRequestException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        if (resetPasswordToken.getExpiredAt().before(new Date())
                && TokenUtil.tokenIsNotExpiredAndRevoked(resetPasswordToken)) {
            throw new BadRequestException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        Credential credential = resetPasswordToken.getCredential();
        if (!credential.getIsEnabled()) {
            throw new BadRequestException("Tài khoản chưa được xác thực!");
        }

        if (resetPasswordRequest.getPassword().length() < 8 || resetPasswordRequest.getPassword().length() > 32) {
            throw new BadRequestException("Mật khẩu phải có độ dài từ 8 đến 32 ký tự!");
        }

        credential.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        credentialRepository.save(credential);

        resetPasswordToken.setIsExpired(true);
        resetPasswordToken.setIsRevoked(true);
        tokenService.save(resetPasswordToken);

        return ResponseEntity.ok().body(
                GenericResponse.builder()
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng!"));

        Optional<Credential> optionalCredential = Optional.ofNullable(user.getCredential());
        if (optionalCredential.isPresent()) {
            tokenService.revokeRefreshToken(optionalCredential.get().getId());
            return ResponseEntity.ok().body(
                    GenericResponse.builder()
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

    @Override
    public ResponseEntity<GenericResponse> registerForParent(ParentRegisterRequest parentRegisterRequest) {
        log.info("CredentialServiceImpl, ResponseEntity<GenericResponse>, registerForParent");
        validateEmailAndPassword(parentRegisterRequest.getEmail(), parentRegisterRequest.getPassword());
        User user = null;
        try {
            user = userRepository.save(User.builder()
                    .email(parentRegisterRequest.getEmail())
                    .role(RoleBasedAuthority.PARENT)
                    .firstName(parentRegisterRequest.getFirstName())
                    .lastName(parentRegisterRequest.getLastName())
                    .gender(Gender.valueOf(parentRegisterRequest.getGender()))
                    .phone(parentRegisterRequest.getPhone())
                    .dob(DateUtil.string2Date(parentRegisterRequest.getDob(), AppConstant.LOCAL_DATE_FORMAT))
                    .students(new ArrayList<>())
                    .build());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Giới tính không hợp lệ!");
        } catch (ParseException e) {
            throw new BadRequestException("Ngày sinh không hợp lệ!");
        }

        Credential credential = saveCredential(parentRegisterRequest.getEmail(), parentRegisterRequest.getPassword(), user);

        return ResponseEntity.ok().body(
                GenericResponse.builder()
                        .success(true)
                        .message("Tài khoản đã được tạo thành công! Đăng nhập để sử dụng ứng dụng!")
                        .result(mapperService.mapToCredentialDto(credential))
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    private Credential saveCredential(String email, String password, User user) {
        return credentialRepository.save(Credential.builder()
                .username(email)
                .password(passwordEncoder.encode(password))
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .lockedAt(null)
                .lockedReason(null)
                .user(user)
                .role(roleRepository.findByCode(RoleType.ROLE_USER.getCode())
                        .orElseThrow(() -> new NotFoundException("Role không tồn tại!")))
                .provider(providerRepository.findByCode(ProviderType.PROVIDER_LOCAL.getCode())
                        .orElseThrow(() -> new NotFoundException("Provider không tồn tại!")))
                .build());
    }

    private void validateEmailAndPassword(String email, String password) {
        Optional<Credential> optionalCredential = credentialRepository.findByUsername(email);

        if (optionalCredential.isPresent())
            throw new BadRequestException("Tài khoản đã tồn tại!");

        if (password.length() < 8 || password.length() > 32)
            throw new BadRequestException("Mật khẩu phải có độ dài từ 8 đến 32 ký tự!");
        User user = null;
    }

    @Override
    public ResponseEntity<GenericResponse> registerForStudent(StudentAndParentRequest studentAndParentRequest) {
        log.info("CredentialServiceImpl, ResponseEntity<GenericResponse>, registerForStudent");
        Map<String, CredentialDto> result = new HashMap<>();
        User student = null, parent = null;
        if (studentAndParentRequest.getStudent().getEmail() != null) {
            StudentRegisterRequest studentRegisterRequest = studentAndParentRequest.getStudent();
            validateEmailAndPassword(studentRegisterRequest.getEmail(), studentRegisterRequest.getPassword());
            try {
                student = userRepository.save(User.builder()
                        .email(studentRegisterRequest.getEmail())
                        .role(RoleBasedAuthority.STUDENT)
                        .firstName(studentRegisterRequest.getFirstName())
                        .lastName(studentRegisterRequest.getLastName())
                        .gender(Gender.valueOf(studentRegisterRequest.getGender()))
                        .phone(studentRegisterRequest.getPhone())
                        .dob(DateUtil.string2Date(studentRegisterRequest.getDob(), AppConstant.LOCAL_DATE_FORMAT))
                        .province(studentRegisterRequest.getProvince())
                        .district(studentRegisterRequest.getDistrict())
                        .school(studentRegisterRequest.getSchool())
                        .grade(studentRegisterRequest.getGrade())
                        .build());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Giới tính không hợp lệ!");
            } catch (ParseException e) {
                throw new BadRequestException("Ngày sinh không hợp lệ!");
            }
            Credential studentCredential = saveCredential(studentRegisterRequest.getEmail(), studentRegisterRequest.getPassword(), student);
            result.put("student", mapperService.mapToCredentialDto(studentCredential));
        }
        if (studentAndParentRequest.getParent().getEmail() != null) {
            ParentRegisterRequest parentRegisterRequest = studentAndParentRequest.getParent();
            validateEmailAndPassword(parentRegisterRequest.getEmail(), parentRegisterRequest.getPassword());

            try {
                parent = userRepository.save(User.builder()
                        .email(parentRegisterRequest.getEmail())
                        .role(RoleBasedAuthority.PARENT)
                        .firstName(parentRegisterRequest.getFirstName())
                        .lastName(parentRegisterRequest.getLastName())
                        .gender(Gender.valueOf(parentRegisterRequest.getGender()))
                        .phone(parentRegisterRequest.getPhone())
                        .dob(DateUtil.string2Date(parentRegisterRequest.getDob(), AppConstant.LOCAL_DATE_FORMAT))
                        .students(new ArrayList<>())
                        .build());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Giới tính không hợp lệ!");
            } catch (ParseException e) {
                throw new BadRequestException("Ngày sinh không hợp lệ!");
            }
            Credential parentCredential = saveCredential(parentRegisterRequest.getEmail(), parentRegisterRequest.getPassword(), parent);
            result.put("parent", mapperService.mapToCredentialDto(parentCredential));
        }

        if (student != null && parent != null) {
            parent.getStudents().add(student);
            userRepository.save(parent);
        }
        return ResponseEntity.ok().body(
                GenericResponse.builder()
                        .success(true)
                        .message("Tài khoản đã được tạo thành công! Đăng nhập để sử dụng ứng dụng!")
                        .result(result)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    private CredentialDto getCredentialDto(Optional<Credential> optionalCredential) {
        if (optionalCredential.isEmpty()) {
            throw new NotFoundException("Tài khoản không tồn tại!");
        }
        if (!optionalCredential.get().getIsEnabled())
            throw new BadRequestException("Tài khoản chưa được xác thực!");
        return mapperService.mapToCredentialDto(optionalCredential.get());
    }
}

