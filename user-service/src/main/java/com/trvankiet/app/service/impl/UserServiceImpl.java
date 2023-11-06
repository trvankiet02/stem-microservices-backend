package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.constant.Gender;
import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.ProfileRequest;
import com.trvankiet.app.dto.request.UserInfoRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Token;
import com.trvankiet.app.entity.User;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.CredentialRepository;
import com.trvankiet.app.repository.TokenRepository;
import com.trvankiet.app.repository.UserRepository;
import com.trvankiet.app.service.CloudinaryService;
import com.trvankiet.app.service.MapperService;
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
    private final MapperService mapperService;

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

        try {
            user.setFirstName(userInfoRequest.getFirstName());
            user.setLastName(userInfoRequest.getLastName());
            user.setDob(DateUtil.string2Date(userInfoRequest.getDob(), AppConstant.LOCAL_DATE_FORMAT));
            user.setPhone(userInfoRequest.getPhone());
            user.setGender(Gender.valueOf(userInfoRequest.getGender()));
            user.setProvince(userInfoRequest.getProvince());
            user.setDistrict(userInfoRequest.getDistrict());
            user.setSchool(userInfoRequest.getSchool());
            user.setSubjects(userInfoRequest.getSubjects());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Giới tính không hợp lệ!");
        } catch (ParseException e) {
            throw new BadRequestException("Ngày sinh không hợp lệ!");
        }

        userRepository.save(user);

        Credential credential = verificationToken.getCredential();
        credential.setIsEnabled(true);
        credentialRepository.save(credential);

        verificationToken.setIsExpired(true);
        verificationToken.setIsRevoked(true);
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
        return mapperService.mapToCredentialDto(credential);
    }

    @Override
    public UserDto getUserDetail(String uId) {
        User user = userRepository.findById(uId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản!"));

        return mapperService.mapToUserDto(user);
    }

    @Override
    public ResponseEntity<GenericResponse> getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại!"));
        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Lấy thông tin người dùng thành công!")
                        .result(mapperService.mapToUserDto(user))
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
                        .result(mapperService.mapToUserDto(user))
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateAvatar(String userId, MultipartFile avatar) throws IOException{
        String folderName = "/user/avatar";
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại!"));
        String oldAvatar = user.getAvatarUrl();
        String fileName = user.getFirstName() + "_" + user.getLastName()
                + "_" + DateUtil.date2String(new Date(), AppConstant.FULL_DATE_TIME_FORMAT);

        //upload new avatar
        user.setAvatarUrl(cloudinaryService.uploadImage(avatar, fileName, folderName));
        user = userRepository.save(user);
        //delete old avatar
        if (oldAvatar != null) {
            cloudinaryService.deleteUserImage(oldAvatar, folderName);
        }

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật ảnh đại diện thành công")
                .result(mapperService.mapToUserDto(user))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateCover(String userId, MultipartFile cover) throws IOException {
        String folderName = "/user/cover";
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại!"));
        String oldCover = user.getCoverUrl();
        String fileName = user.getFirstName() + "_" + user.getLastName()
                + "_" + DateUtil.date2String(new Date(), AppConstant.FULL_DATE_TIME_FORMAT);

        //upload new cover
        user.setCoverUrl(cloudinaryService.uploadImage(cover, fileName, folderName));
        user = userRepository.save(user);
        //delete old cover
        if (oldCover != null) {
            cloudinaryService.deleteUserImage(oldCover, folderName);
        }

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật ảnh bìa thành công")
                .result(mapperService.mapToUserDto(user))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    private Token validateAndRetrieveVerificationToken(String token) {
        Token verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Yêu cầu đã hết hạn hoặc không hợp lệ!"));

        if (!verificationToken.getType().equals(TokenType.VERIFICATION_TOKEN)) {
            throw new BadRequestException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        if (verificationToken.getExpiredAt().before(new Date())) {
            throw new BadRequestException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        return verificationToken;
    }

}
