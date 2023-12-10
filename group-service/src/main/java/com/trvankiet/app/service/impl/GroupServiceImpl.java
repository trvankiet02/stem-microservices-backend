package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.GroupAccessType;
import com.trvankiet.app.constant.GroupMemberRoleType;
import com.trvankiet.app.constant.GroupType;
import com.trvankiet.app.dto.GroupDto;
import com.trvankiet.app.dto.SimpleGroupDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.GroupConfigRequest;
import com.trvankiet.app.dto.request.GroupCreateRequest;
import com.trvankiet.app.dto.request.UpdateDetailRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.SuggestGroupResponse;
import com.trvankiet.app.entity.Group;
import com.trvankiet.app.entity.GroupMember;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.GroupMemberRepository;
import com.trvankiet.app.repository.GroupRepository;
import com.trvankiet.app.service.GroupService;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.client.FileClientService;
import com.trvankiet.app.service.client.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private final MapperService mapperService;
    private final FileClientService fileClientService;
    private final MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<GenericResponse> createGroup(String userId, GroupCreateRequest groupCreateRequest) {
        log.info("GroupServiceImpl, createGroup");
        Date now = new Date();

        Group group = Group.builder()
                .id(UUID.randomUUID().toString())
                .name(groupCreateRequest.getName())
                .description(groupCreateRequest.getDescription() == null ?
                        null : groupCreateRequest.getDescription())
                .authorId(userId)
                .isClass(groupCreateRequest.getIsClass())
                .isPublic(groupCreateRequest.getIsPublic())
                .isAcceptAllRequest(groupCreateRequest.getIsAcceptAllRequest())
                .createdAt(now)
                .build();

        if (groupCreateRequest.getIsClass()) {
            UserDto userDto = userClientService.getUserDtoByUserId(userId);
            if (userDto.getRole().equals("TEACHER")) {
                group.setSubject(groupCreateRequest.getSubject());
                group.setGrade(groupCreateRequest.getGrade());
            } else {
                throw new ForbiddenException("Bạn không có quyền tạo lớp học!");
            }
        }

        group = groupRepository.save(group);
        groupMemberRepository.save(GroupMember.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .group(group)
                .role(GroupMemberRoleType.GROUP_OWNER)
                .createdAt(now)
                .build());

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Tạo nhóm thành công!")
                .result(group.getId())
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

        groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .ifPresent(groupMember -> result.put("user", mapperService.mapToGroupMemberDto(groupMember)));

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
        List<GroupMemberRoleType> groupMemberRoles = List.of(GroupMemberRoleType.GROUP_OWNER,
                GroupMemberRoleType.GROUP_ADMIN,
                GroupMemberRoleType.GROUP_MEMBER);
        for (GroupMemberRoleType role : groupMemberRoles) {
            result.put(role.name(), groupMemberRepository.findAllByUserIdAndRole(userId, role).stream()
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
        group.setIsPublic(groupConfigRequest.getIsPublic());
        group.setIsAcceptAllRequest(groupConfigRequest.getIsAcceptAllRequest());
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
        log.info("GroupServiceImpl, deleteGroup");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm!"));
        if (!group.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền xóa nhóm!");
        }
        groupRepository.delete(group);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Xóa nhóm thành công!")
                .result(null)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<List<GroupDto>> searchGroup(
            Optional<String> query, Optional<Boolean> isClass, Optional<Boolean> isPublic,
            Optional<Integer> grade, Optional<String> subject) {
        log.info("GroupServiceImpl, searchGroup");

        String queryValue = query.orElse("");
        Boolean typeValue = isClass.orElse(null);
        Integer gradeValue = grade.orElse(null);
        String subjectValue = subject.orElse(null);

        Query searchQuery = new Query();

        Criteria queryCriteria = new Criteria().orOperator(
                Criteria.where("group_name").regex(queryValue, "i"),
                Criteria.where("group_description").regex(queryValue, "i")
        );

        searchQuery.addCriteria(queryCriteria);

        if (typeValue != null) {
            searchQuery.addCriteria(Criteria.where("isClass").is(typeValue));
        }

        if (gradeValue != null) {
            searchQuery.addCriteria(Criteria.where("grade").is(gradeValue));
        }

        if (subjectValue != null) {
            searchQuery.addCriteria(Criteria.where("subject").regex(subjectValue, "i"));
        }

        List<Group> groups = mongoTemplate.find(searchQuery, Group.class);

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
        List<String> groupIds = new ArrayList<>(groupMembers.stream()
                .map(groupMember -> groupMember.getGroup().getId())
                .toList());
        UserDto userDto = userClientService.getUserDtoByUserId(userId);
        if (userDto.getRole().equals("TEACHER")) {
            userDto.getSubjects().forEach(subject -> {
                List<Group> groupsBySubject = groupRepository.findAllBySubjectAndIsPublic(subject, true);
                groupsBySubject.forEach(group -> {
                    if (!groupIds.contains(group.getId())) {
                        groupIds.add(group.getId());
                    }
                });
            });
        } else if (userDto.getRole().equals("STUDENT")) {
            List<Group> groupsByGrade = groupRepository.findAllByGradeAndIsPublic(userDto.getGrade(), true);
            groupsByGrade.forEach(group -> {
                if (!groupIds.contains(group.getId())) {
                    groupIds.add(group.getId());
                }
            });
        }
        List<Group> suggestGroups = groupRepository.findAllByIsClassAndIsPublic(false, true);
        suggestGroups.forEach(group -> {
            if (!groupIds.contains(group.getId())) {
                groupIds.add(group.getId());
            }
        });
        return ResponseEntity.ok(groupIds);
    }

    @Override
    public ResponseEntity<GenericResponse> suggestGroups(String userId, Integer page, Integer size) {
        log.info("GroupServiceImpl, suggestGroups");
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        List<Group> groups = groupMemberRepository.findAllByUserId(userId).stream()
                .map(GroupMember::getGroup)
                .filter(group -> !group.getIsClass())
                .toList();

        List<Group> suggestGroups = groupRepository.findAllByIsClassAndIsPublic(false, true, pageable).toList();

        List<SuggestGroupResponse> suggestGroupResponses = new ArrayList<>();

        suggestGroups.forEach(group -> {
            if (!groups.contains(group)) {
                suggestGroupResponses.add(SuggestGroupResponse.builder()
                        .groupDto(mapperService.mapToGroupDto(group))
                        .memberCount(groupMemberRepository.countByGroupId(group.getId()))
                        .isMember(false)
                        .build());
            } else {
                suggestGroupResponses.add(SuggestGroupResponse.builder()
                        .groupDto(mapperService.mapToGroupDto(group))
                        .memberCount(groupMemberRepository.countByGroupId(group.getId()))
                        .isMember(true)
                        .build());
            }
        });

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách nhóm thành công!")
                .result(suggestGroupResponses)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> suggestClasses(String userId, Integer page, Integer size) {
        log.info("GroupServiceImpl, suggestClasses");

        List<Group> groups = groupMemberRepository.findAllByUserId(userId).stream()
                .map(GroupMember::getGroup)
                .filter(Group::getIsClass)
                .toList();

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        UserDto userDto = userClientService.getUserDtoByUserId(userId);

        List<Group> suggestGroups = new ArrayList<>();

        if (userDto.getRole().equals("TEACHER")) {
            userDto.getSubjects().forEach(subject -> {
                suggestGroups.addAll(groupRepository.findAllBySubjectAndIsPublic(subject, true, pageable).toList());
            });
        } else if (userDto.getRole().equals("STUDENT")) {
            suggestGroups.addAll(groupRepository.findAllByGradeAndIsPublic(userDto.getGrade(), true, pageable).toList());
        } else if (userDto.getRole().equals("PARENT")) {
            if (userDto.getChildren() != null) {
                userDto.getChildren().forEach(child -> {
                    UserDto childDto = userClientService.getUserDtoByUserId(child.getId());
                    Pageable newPageable = Pageable.ofSize(size).withPage(page / userDto.getChildren().size());
                    if (childDto.getRole().equals("STUDENT")) {
                        suggestGroups.addAll(groupRepository.findAllByGradeAndIsPublic(childDto.getGrade(), true, newPageable).toList());
                    }
                });
            }
        }

        List<SuggestGroupResponse> suggestGroupResponses = new ArrayList<>();

        suggestGroups.forEach(group -> {
            if (!groups.contains(group)) {
                suggestGroupResponses.add(SuggestGroupResponse.builder()
                        .groupDto(mapperService.mapToGroupDto(group))
                        .memberCount(groupMemberRepository.countByGroupId(group.getId()))
                        .isMember(false)
                        .build());
            } else {
                suggestGroupResponses.add(SuggestGroupResponse.builder()
                        .groupDto(mapperService.mapToGroupDto(group))
                        .memberCount(groupMemberRepository.countByGroupId(group.getId()))
                        .isMember(true)
                        .build());
            }
        });

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách lớp học thành công!")
                .result(suggestGroupResponses)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public SimpleGroupDto getSimpleGroupDto(String userId, String groupId) {
        log.info("GroupServiceImpl, getSimpleGroupDto");
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new NotFoundException("Nhóm không tồn tại hoặc bạn không phải thành viên của nhóm!"));

        Group group = groupMember.getGroup();

        return SimpleGroupDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .avatarUrl(group.getAvatarUrl())
                .coverUrl(group.getCoverUrl())
                .isPublic(group.getIsPublic())
                .build();


    }
}
