package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.constant.Gender;
import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.ProfileRequest;
import com.trvankiet.app.dto.request.UserInfoRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.ProfileResponse;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Token;
import com.trvankiet.app.entity.User;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.CredentialRepository;
import com.trvankiet.app.repository.TokenRepository;
import com.trvankiet.app.repository.UserRepository;
import com.trvankiet.app.service.CloudinaryService;
import com.trvankiet.app.service.UserService;
import com.trvankiet.app.util.DateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
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
    private final CloudinaryService cloudinaryService;

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
            throw new BadRequestException("Giới tính không hợp lệ!");
        }

        userRepository.save(user);

        Credential credential = verificationToken.getCredential();
        credential.setIsEnabled(true);
        credentialRepository.save(credential);

        verificationToken.setRevoked(true);
        verificationToken.setExpired(true);
        tokenRepository.save(verificationToken);

        return ResponseEntity.ok().body(
                GenericResponse.builder()
                        .success(true)
                        .message("Thông tin tài khoản đã được khởi tạo thành công!")
                        .result(null)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @Override
    public CredentialDto getCredentialDto(String uId) {
        User user = userRepository.findById(uId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản!"));
        Credential credential = user.getCredential();
        return CredentialDto.builder()
                .credentialId(credential.getCredentialId())
                .provider(credential.getProvider())
                .username(credential.getUsername())
                .password(credential.getPassword())
                .role(credential.getRole().getRoleName())
                .isEnabled(credential.getIsEnabled())
                .isAccountNonExpired(credential.getIsAccountNonExpired())
                .isAccountNonLocked(credential.getIsAccountNonLocked())
                .isCredentialsNonExpired(credential.getIsCredentialsNonExpired())
                .lockedAt(credential.getLockedAt())
                .lockedReason(credential.getLockedReason())
                .build();
    }

    @Override
    public UserDto getUserDetail(String uId) {
        User user = userRepository.findById(uId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản!"));
        CredentialDto credentialDto = CredentialDto.builder()
                .credentialId(user.getCredential().getCredentialId())
                .provider(user.getCredential().getProvider())
                .username(user.getCredential().getUsername())
                .password(user.getCredential().getPassword())
                .role(user.getCredential().getRole().getRoleName())
                .isEnabled(user.getCredential().getIsEnabled())
                .isAccountNonExpired(user.getCredential().getIsAccountNonExpired())
                .isAccountNonLocked(user.getCredential().getIsAccountNonLocked())
                .isCredentialsNonExpired(user.getCredential().getIsCredentialsNonExpired())
                .lockedAt(user.getCredential().getLockedAt())
                .lockedReason(user.getCredential().getLockedReason())
                .build();

        return UserDto.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profileImageUrl(user.getProfileImageUrl())
                .coverImageUrl(user.getCoverImageUrl())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dob(user.getDob())
                .gender(user.getGender())
                .about(user.getAbout())
                .address(user.getAddress())
                .credential(credentialDto)
                .build();
    }

    @Override
    public ResponseEntity<GenericResponse> getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại!"));
        ProfileResponse profileResponse = generateProfileResponse(user);
        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Lấy thông tin người dùng thành công!")
                        .result(profileResponse)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateProfile(String userId, ProfileRequest postProfileRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại!"));
        try {
            user.setFirstName(postProfileRequest.getFirstName());
            user.setLastName(postProfileRequest.getLastName());
            user.setPhone(postProfileRequest.getPhone());
            user.setDob(DateUtil.string2Date(postProfileRequest.getDob(), AppConstant.LOCAL_DATE_FORMAT));
            user.setGender(Gender.valueOf(postProfileRequest.getGender()));
            user.setAbout(postProfileRequest.getAbout());
            user.setWorkedAt(postProfileRequest.getWorkedAt());
            user.setAddress(postProfileRequest.getAddress());
            user = userRepository.save(user);
        } catch (ParseException e) {
            log.error("ParseException: {}", e.getMessage());
            throw new BadRequestException("Ngày sinh không hợp lệ!");
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException: {}", e.getMessage());
            throw new BadRequestException("Giới tính không hợp lệ!");
        }
        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Cập nhật thông tin người dùng thành công!")
                        .result(generateProfileResponse(user))
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateAvatar(String userId, MultipartFile avatar) throws IOException{
        String folderName = "/user/avatar";
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại!"));
        String oldAvatar = user.getProfileImageUrl();
        String fileName = user.getFirstName() + "_" + user.getLastName()
                + "_" + DateUtil.date2String(new Date(), AppConstant.FULL_DATE_FORMAT);

        //upload new avatar
        user.setProfileImageUrl(cloudinaryService.uploadImage(avatar, fileName, folderName));
        user = userRepository.save(user);
        //delete old avatar
        if (oldAvatar != null) {
            cloudinaryService.deleteUserImage(oldAvatar, folderName);
        }

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật ảnh đại diện thành công")
                .result(generateProfileResponse(user))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateCover(String userId, MultipartFile cover) throws IOException {
        String folderName = "/user/cover";
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại!"));
        String oldCover = user.getCoverImageUrl();
        String fileName = user.getFirstName() + "_" + user.getLastName()
                + "_" + DateUtil.date2String(new Date(), AppConstant.FULL_DATE_FORMAT);

        //upload new cover
        user.setCoverImageUrl(cloudinaryService.uploadImage(cover, fileName, folderName));
        user = userRepository.save(user);
        //delete old cover
        if (oldCover != null) {
            cloudinaryService.deleteUserImage(oldCover, folderName);
        }

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật ảnh bìa thành công")
                .result(generateProfileResponse(user))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    private Token validateAndRetrieveVerificationToken(String token) {
        Token verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Yêu cầu đã hết hạn hoặc không hợp lệ!"));

        if (!verificationToken.getType().equals(TokenType.VERIFICATION_TOKEN)) {
            throw new BadRequestException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        if (verificationToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        return verificationToken;
    }

    private ProfileResponse generateProfileResponse(User user) {
        return ProfileResponse.builder()
                .firstName(user.getFirstName() == null ? "" : user.getFirstName())
                .lastName(user.getLastName() == null ? "" : user.getLastName())
                .profileImageUrl(user.getProfileImageUrl() == null ? "" : user.getProfileImageUrl())
                .coverImageUrl(user.getCoverImageUrl() == null ? "" : user.getCoverImageUrl())
                .email(user.getEmail() == null ? "" : user.getEmail())
                .phone(user.getPhone() == null ? "" : user.getPhone())
                .dob(user.getDob() == null ? "" : DateUtil.date2String(user.getDob(), AppConstant.LOCAL_DATE_FORMAT))
                .gender(user.getGender() == null ? "" : user.getGender().toString())
                .about(user.getAbout() == null ? "" : user.getAbout())
                .workedAt(user.getWorkedAt() == null ? "" : user.getWorkedAt())
                .address(user.getAddress() == null ? "" : user.getAddress())
                .build();
    }

}
