package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.GroupAccessType;
import com.trvankiet.app.constant.GroupMemberRoleType;
import com.trvankiet.app.constant.GroupType;
import com.trvankiet.app.dto.GroupDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.GroupConfigRequest;
import com.trvankiet.app.dto.request.GroupCreateRequest;
import com.trvankiet.app.dto.request.UpdateDetailRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Group;
import com.trvankiet.app.entity.GroupConfig;
import com.trvankiet.app.entity.GroupMember;
import com.trvankiet.app.entity.GroupMemberRole;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.GroupConfigRepository;
import com.trvankiet.app.repository.GroupMemberRepository;
import com.trvankiet.app.repository.GroupMemberRoleRepository;
import com.trvankiet.app.repository.GroupRepository;
import com.trvankiet.app.service.GroupService;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.client.FileClientService;
import com.trvankiet.app.service.client.UserClientService;
import com.trvankiet.app.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final UserClientService userClientService;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberRoleRepository groupMemberRoleRepository;
    private final GroupConfigRepository groupConfigRepository;
    private final MapperService mapperService;
    private final FileClientService fileClientService;

    @Override
    public ResponseEntity<GenericResponse> createGroup(String userId, GroupCreateRequest groupCreateRequest) {
        log.info("GroupServiceImpl, createGroup");
        UserDto userDto = userClientService.getUserDtoByUserId(userId);
        Date now = new Date();

        GroupConfig groupConfig = groupConfigRepository.findByTypeAndAccessibilityAndMemberMode(
                        groupCreateRequest.getTypeName(),
                        groupCreateRequest.getAccessibilityName(),
                        groupCreateRequest.getMemberModeName())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy cấu hình nhóm!"));

        Group group = Group.builder()
                .id(UUID.randomUUID().toString())
                .name(groupCreateRequest.getName())
                .description(groupCreateRequest.getDescription())
                .authorId(userId)
                .config(groupConfig)
                .createdAt(now)
                .build();

        if (groupConfig.getType().equals(GroupType.CLASS.toString())) {
            if (userDto.getRole().equals("TEACHER")) {
                group.setSubject(groupCreateRequest.getSubject());
                group.setGrade(groupCreateRequest.getGrade());
            } else {
                throw new ForbiddenException("Bạn không có quyền tạo lớp học!");
            }
        }

        group = groupRepository.save(group);

        GroupMemberRole groupOwnerRole = groupMemberRoleRepository.findByCode(GroupMemberRoleType.GROUP_OWNER.getCode())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền thành viên nhóm!"));

        GroupMember groupMember = groupMemberRepository
                .save(GroupMember.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(userId)
                        .group(group)
                        .groupMemberRole(groupOwnerRole)
                        .createdAt(now)
                        .build());

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Tạo nhóm thành công!")
                .result(mapperService.mapToGroupDto(group))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getAllGroup() {
        log.info("GroupServiceImpl, getAllGroup");
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách nhóm thành công!")
                .result(groupRepository.findAll())
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getGroupById(String userId, String groupId) {
        log.info("GroupServiceImpl, getGroupById");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm!"));
        GroupDto groupDto = mapperService.mapToGroupDto(group);

        Map<String, Object> result = new HashMap<>();
        result.put("group", groupDto);

        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, groupId).orElse(null);

        if (group.getConfig().getAccessibility().equals(GroupAccessType.PUBLIC.toString())) {
            if (groupMember != null) {
                result.put("user", mapperService.mapToGroupMemberDto(groupMember));
            }
            // result.put("posts", List<Post> getPostsByGroupId(groupId));
            if (group.getConfig().getType().equals(GroupType.CLASS.toString())) {
                // result.put("events", List<Event> getEventsByGroupId(groupId));
                // result.put("exams", List<Exam> getExamsByGroupId(groupId));
            }
        } else {
            if (groupMember != null) {
                result.put("user", mapperService.mapToGroupMemberDto(groupMember));
                // result.put("posts", List<Post> getPostsByGroupId(groupId));
                if (group.getConfig().getType().equals(GroupType.CLASS.toString())) {
                    // result.put("events", List<Event> getEventsByGroupId(groupId));
                    // result.put("exams", List<Exam> getExamsByGroupId(groupId));
                }
            }
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Truy cập nhóm thành công!")
                .result(result)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getGroupsByUserId(String userId) {
        log.info("GroupServiceImpl, getGroupsByUserId");
        Map<String, List<GroupDto>> result = new HashMap<>();
        List<com.trvankiet.app.entity.GroupMemberRole> groupMemberRoles = groupMemberRoleRepository.findAll();
        for (com.trvankiet.app.entity.GroupMemberRole role : groupMemberRoles) {
            result.put(role.getName(), groupMemberRepository.findByUserIdAndGroupMemberRole(userId, role).stream()
                    .map(groupMember -> mapperService.mapToGroupDto(groupMember.getGroup()))
                    .collect(Collectors.toList()));
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách nhóm thành công!")
                .result(result)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateGroupConfig(String userId, String groupId, GroupConfigRequest groupConfigRequest) {
        log.info("GroupServiceImpl, updateGroupConfig");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm!"));
        if (!group.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền thay đổi cấu hình nhóm!");
        }
        GroupConfig groupConfig = groupConfigRepository.findByTypeAndAccessibilityAndMemberMode(
                        groupConfigRequest.getTypeCode(),
                        groupConfigRequest.getAccessibilityCode(),
                        groupConfigRequest.getMemberModeCode())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy cấu hình nhóm!"));
        group.setConfig(groupConfig);
        groupRepository.save(group);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật cấu hình nhóm thành công!")
                .result(mapperService.mapToGroupDto(group))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateGroupDetail(String userId, String groupId, UpdateDetailRequest updateDetailRequest) {
        log.info("GroupServiceImpl, updateGroupDetail");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm!"));
        if (!group.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền thay đổi thông tin nhóm!");
        }
        group.setName(updateDetailRequest.getName());
        group.setDescription(updateDetailRequest.getDescription());
        groupRepository.save(group);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật thông tin nhóm thành công!")
                .result(mapperService.mapToGroupDto(group))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateGroupAvatar(String userId, String groupId, MultipartFile avatar) throws IOException {
        log.info("GroupServiceImpl, updateGroupAvatar");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm!"));
        if (!group.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền thay đổi ảnh đại diện nhóm!");
        }
        String oldAvatar = group.getAvatarUrl();
        String newAvatar = fileClientService.uploadGroupAvatar(avatar);
        group.setAvatarUrl(newAvatar);
        groupRepository.save(group);
        if (oldAvatar != null) {
            fileClientService.deleteGroupAvatar(oldAvatar);
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật ảnh đại diện nhóm thành công!")
                .result(mapperService.mapToGroupDto(group))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateGroupCover(String userId, String groupId, MultipartFile cover) throws IOException {
        log.info("GroupServiceImpl, updateGroupCover");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm!"));
        if (!group.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền thay đổi ảnh bìa nhóm!");
        }
        String oldCover = group.getCoverUrl();
        String newCover = fileClientService.uploadGroupCover(cover);
        group.setCoverUrl(newCover);
        groupRepository.save(group);
        if (oldCover != null) {
            fileClientService.deleteGroupCover(oldCover);
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật ảnh bìa nhóm thành công!")
                .result(mapperService.mapToGroupDto(group))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteGroup(String userId, String groupId) {
        return null;
    }

    @Override
    public ResponseEntity<List<GroupDto>> searchGroup(
            Optional<String> query, Optional<String> type, Optional<String> accessibility,
            Optional<Integer> grade, Optional<String> subject) {
        log.info("GroupServiceImpl, searchGroup");

        List<Group> groups = groupRepository.searchGroupByQuery(query.orElse(""));

        type.ifPresent(typeValue -> groups.removeIf(group -> !group.getConfig().getType().equals(typeValue)));
        accessibility.ifPresent(accessibilityValue -> groups.removeIf(group -> !group.getConfig().getAccessibility().equals(accessibilityValue)));
        grade.ifPresent(gradeValue -> groups.removeIf(group -> group.getGrade() == null || !group.getGrade().equals(gradeValue)));
        subject.ifPresent(subjectValue -> groups.removeIf(group -> group.getSubject() == null || !group.getSubject().equals(subjectValue)));

        List<GroupDto> groupDtos = groups.stream()
                .map(mapperService::mapToGroupDto)
                .toList();

        return ResponseEntity.ok(groupDtos);
    }

    @Override
    public ResponseEntity<GenericResponse> valiadateUserInGroup(String userId, String groupId) {
        log.info("GroupServiceImpl, valiadateUserInGroup");
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElse(null);
        if (groupMember == null) {
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .message("Bạn không có quyền truy cập nhóm!")
                    .statusCode(HttpStatus.OK.value())
                    .result(false)
                    .build());
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Bạn có quyền truy cập nhóm!")
                .statusCode(HttpStatus.OK.value())
                .result(true)
                .build());
    }

    @Override
    public ResponseEntity<List<String>> getGroupByUserId(String userId) {
        log.info("GroupServiceImpl, getGroupByUserId");
        List<GroupMember> groupMembers = groupMemberRepository.findAllByUserId(userId);
        return ResponseEntity.ok(groupMembers.stream()
                .map(groupMember -> groupMember.getGroup().getId())
                .toList());
    }
}
