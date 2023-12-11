package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.constant.Gender;
import com.trvankiet.app.constant.RoleBasedAuthority;
import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.FriendRequestDto;
import com.trvankiet.app.dto.SimpleUserDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.CreateChatUserRequest;
import com.trvankiet.app.dto.request.ProfileRequest;
import com.trvankiet.app.dto.request.UpdateChatUserRequest;
import com.trvankiet.app.dto.request.UserInfoRequest;
import com.trvankiet.app.dto.response.*;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Token;
import com.trvankiet.app.entity.User;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.repository.CredentialRepository;
import com.trvankiet.app.repository.TokenRepository;
import com.trvankiet.app.repository.UserRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.UserService;
import com.trvankiet.app.service.client.ChatUserClientService;
import com.trvankiet.app.service.client.FileClientService;
import com.trvankiet.app.service.client.FriendshipClientService;
import com.trvankiet.app.util.DateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final CredentialRepository credentialRepository;
    private final MapperService mapperService;
    private final FileClientService fileClientService;
    private final FriendshipClientService friendshipClientService;
    private final JwtService jwtService;
    private final ChatUserClientService chatUserClientService;

    @Override
    public <S extends User> S save(S entity) {
        return userRepository.save(entity);
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
        ResponseEntity<String> friendIdsResponse = friendshipClientService.createFriendship(user.getId());
        CreateChatUserRequest createChatUserRequest = CreateChatUserRequest.builder()
                .id(credential.getUser().getId())
                .firstName(credential.getUser().getFirstName())
                .lastName(credential.getUser().getLastName())
                .build();
        ResponseEntity<ChatUser> chatUserResponse = chatUserClientService.createChatUser(createChatUserRequest);

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
        UpdateChatUserRequest updateChatUserRequest = UpdateChatUserRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            updateChatUserRequest.setAvatarUrl(user.getAvatarUrl());
        }
        ResponseEntity<ChatUser> chatUserResponse = chatUserClientService.updateChatUser(userId, updateChatUserRequest);
        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Cập nhật thông tin người dùng thành công!")
                        .result(mapperService.mapToUserDto(user))
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateAvatar(String userId, MultipartFile avatar) throws IOException {
        log.info("UserServiceImpl, ResponseEntity<GenericResponse>, updateAvatar");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại!"));
        String oldAvatar = user.getAvatarUrl();
        String newAvatar = fileClientService.uploadUserAvatar(avatar);
        //upload new avatar
        user.setAvatarUrl(newAvatar);
        user = userRepository.save(user);
        //delete old avatar
        if (oldAvatar != null && !oldAvatar.isEmpty()) {
            fileClientService.deleteUserAvatar(oldAvatar);
        }
        UpdateChatUserRequest updateChatUserRequest = UpdateChatUserRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .build();
        ResponseEntity<ChatUser> chatUserResponse = chatUserClientService.updateChatUser(userId, updateChatUserRequest);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật ảnh đại diện thành công")
                .result(newAvatar)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateCover(String userId, MultipartFile cover) throws IOException {
        log.info("UserServiceImpl, ResponseEntity<GenericResponse>, updateCover");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại!"));
        String oldCover = user.getCoverUrl();
        String newCover = fileClientService.uploadUserCover(cover);
        //upload new cover
        user.setCoverUrl(newCover);
        user = userRepository.save(user);
        //delete old cover
        if (oldCover != null && !oldCover.isEmpty()) {
            fileClientService.deleteUserCover(oldCover);
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật ảnh bìa thành công")
                .result(newCover)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<List<SimpleUserDto>> searchUser(Optional<String> query, Optional<String> role, Optional<String> gender, Optional<String> school, Optional<Integer> grade, Optional<List<String>> subjects) {
        log.info("UserServiceImpl, ResponseEntity<String>, searchUser");

        String queryString = query.orElse("");
        RoleBasedAuthority roleBasedAuthority = role.map(RoleBasedAuthority::valueOf).orElse(null);
        Gender genderString = gender.map(Gender::valueOf).orElse(null);
        String schoolString = school.orElse(null);
        Integer gradeInteger = grade.orElse(null);
        List<String> subjectsList = subjects.orElse(null);

        try {
            List<User> users = userRepository.searchUsers(queryString, roleBasedAuthority, genderString, schoolString, gradeInteger, subjectsList);
            List<SimpleUserDto> userDtos = users.stream().map(mapperService::mapToSimpleUserDto).collect(Collectors.toList());
            return ResponseEntity.ok(userDtos);
        } catch (Exception e) {
            log.error("Error while executing searchUsers query", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<GenericResponse> getFriends(List<String> friendIds) {
        log.info("UserServiceImpl, ResponseEntity<List<UserDto>>, getFriends");
        List<User> users = userRepository.findAllById(friendIds);
        List<SimpleUserDto> userDtos = users.stream().map(mapperService::mapToSimpleUserDto).toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách bạn bè thành công!")
                .result(userDtos)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getFriendRequests(List<FriendRequestDto> friendRequests) {
        log.info("UserServiceImpl, ResponseEntity<List<UserDto>>, getFriendRequests");
        List<FriendRequestResponse> friendRequestResponses = friendRequests.stream()
                .map(friendRequest -> {
                    User user = userRepository.findById(friendRequest.getSenderId()).orElse(null);
                    if (user == null) {
                        return null;
                    }
                    return FriendRequestResponse.builder()
                            .id(friendRequest.getId())
                            .userDto(mapperService.mapToSimpleUserDto(user))
                            .status(friendRequest.getStatus())
                            .createdAt(friendRequest.getCreatedAt() == null ? null : friendRequest.getCreatedAt())
                            .updatedAt(friendRequest.getUpdatedAt() == null ? null : friendRequest.getUpdatedAt())
                            .build();
                }).toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách lời mời kết bạn thành công!")
                .result(friendRequestResponses)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getFriendsOfUser(List<FriendOfUserResponse> body) {
        log.info("UserServiceImpl, ResponseEntity<List<UserDto>>, getFriendsOfUser");
        List<FriendResponse> friendResponse = body.stream().map(friendOfUserResponse -> {
            User user = userRepository.findById(friendOfUserResponse.getUserId()).orElse(null);
            if (user == null) {
                return null;
            }
            return FriendResponse.builder()
                    .userDto(mapperService.mapToSimpleUserDto(user))
                    .isFriend(friendOfUserResponse.getIsFriendOfMe())
                    .build();
        }).toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách bạn bè thành công!")
                .result(friendResponse)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public SimpleUserDto getSimpleUserDto(String uId) {
        User user = userRepository.findById(uId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản!"));
        return mapperService.mapToSimpleUserDto(user);
    }

    @Override
    public ResponseEntity<GenericResponse> getAllUsers(String authorizationHeader, Integer page, Integer size) {
        log.info("UserServiceImpl, ResponseEntity<GenericResponse>, getAllUsers");
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userRepository.findAll(pageable);
        List<SimpleUserDto> userDtos = users.stream().map(mapperService::mapToSimpleUserDto).toList();
        Map<String, Object> result = new HashMap<>();
        result.put("users", userDtos);
        result.put("totalPages", users.getTotalPages());
        result.put("currentPage", users.getNumber());
        result.put("totalElements", users.getTotalElements());
        result.put("currentElements", users.getNumberOfElements());

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách người dùng thành công!")
                .result(result)
                .statusCode(HttpStatus.OK.value())
                .build());
    }


    private Token validateAndRetrieveVerificationToken(String token) {
        Token verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Yêu cầu đã hết hạn hoặc không hợp lệ!"));

        if (!verificationToken.getType().equals(TokenType.VERIFICATION_TOKEN)) {
            throw new BadRequestException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        if (verificationToken.getIsExpired() && verificationToken.getIsRevoked()) {
            throw new BadRequestException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        if (verificationToken.getExpiredAt().before(new Date())) {
            throw new BadRequestException("Yêu cầu đã hết hạn hoặc không hợp lệ!");
        }

        return verificationToken;
    }


}
