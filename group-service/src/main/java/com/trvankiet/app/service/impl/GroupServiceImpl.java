package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.GroupRole;
import com.trvankiet.app.constant.RoleBasedAuthority;
import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.GroupDto;
import com.trvankiet.app.dto.GroupMemberDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.GroupCreateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.GroupOfUserResponse;
import com.trvankiet.app.entity.Group;
import com.trvankiet.app.entity.GroupMember;
import com.trvankiet.app.entity.GroupMemberRole;
import com.trvankiet.app.exception.wrapper.GroupException;
import com.trvankiet.app.repository.GroupMemberRepository;
import com.trvankiet.app.repository.GroupMemberRoleRepository;
import com.trvankiet.app.repository.GroupRepository;
import com.trvankiet.app.service.GroupService;
import com.trvankiet.app.service.client.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    @Override
    public ResponseEntity<GenericResponse> createGroup(String userId, GroupCreateRequest groupCreateRequest) {
        UserDto userDto = userClientService.getUserDtoByUserId(userId);
        if (!userDto.getCredential().getRole().equals(RoleBasedAuthority.TEACHER.toString()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Bạn không có quyền để thực hiện hành động này!")
                            .result(null)
                            .statusCode(HttpStatus.FORBIDDEN.value())
                            .build());
        Date now = new Date();

        GroupMemberRole groupAdminRole = groupMemberRoleRepository.findByRoleName(GroupRole.GROUP_ADMIN.toString())
                .orElseThrow(() -> new GroupException("Không tìm thấy quyền!"));

        GroupMember groupMember = groupMemberRepository
                .save(GroupMember.builder()
                        .groupMemberId(UUID.randomUUID().toString())
                        .userId(userDto.getUserId())
                        .groupMemberRole(groupAdminRole)
                        .createdAt(now)
                        .build());

        Group group = groupRepository.save(Group.builder()
                .groupId(UUID.randomUUID().toString())
                .groupName(groupCreateRequest.getGroupName())
                .groupDescription(groupCreateRequest.getGroupDescription())
                .groupType(groupCreateRequest.getGroupType())
                .groupMembers(List.of(groupMember))
                .createdAt(now)
                .build());

        Map<String, String> result = Map.of("groupId", group.getGroupId());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Tạo nhóm thành công!")
                .result(result)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getAllGroup() {
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách nhóm thành công!")
                .result(groupRepository.findAll())
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getGroupById(String userId, String groupId) {
        Group group = groupRepository.findByGroupId(groupId).orElseThrow(() -> new GroupException("Không tìm thấy nhóm!"));
        GroupDto groupDto = GroupDto.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupDescription(group.getGroupDescription())
                .groupImage(group.getGroupImage())
                .groupType(group.getGroupType())
                .build();

        Map<String, Object> result = new HashMap<>();
        result.put("group", groupDto);

        GroupMember groupMember = group.getGroupMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        if (groupMember != null) {
            GroupMemberDto groupMemberDto = GroupMemberDto.builder()
                    .groupMemberId(groupMember.getGroupMemberId())
                    .groupMemberRole(groupMember.getGroupMemberRole().getRoleName())
                    .build();
            result.put("user", groupMemberDto);
        }

        if ("PUBLIC".equals(group.getGroupType())) {
            //result.put("post", postClientService.getPostByGroupId(groupId));
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
        Map<String, List<GroupOfUserResponse>> result = new HashMap<>();
        List<Group> groups = groupRepository.findAll().stream()
                .filter(group -> group.getGroupMembers().stream()
                        .anyMatch(groupMember -> groupMember.getUserId().equals(userId)
                )).collect(Collectors.toList());
        List<GroupMemberRole> groupMemberRoles = groupMemberRoleRepository.findAll();
        for (GroupMemberRole role: groupMemberRoles) {
            result.put(role.getRoleName(), getGroupsByUserRole(groups, userId, role.getRoleName()));
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách nhóm thành công!")
                .result(result)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    private List<GroupOfUserResponse> getGroupsByUserRole(List<Group> groups, String userId, String roleName) {
        return groups.stream()
                .filter(group -> isUserInRole(group, userId, roleName))
                .map(group -> GroupOfUserResponse.builder()
                        .groupId(group.getGroupId())
                        .groupName(group.getGroupName())
                        .groupDescription(group.getGroupDescription())
                        .groupImage(group.getGroupImage())
                        .groupType(group.getGroupType())
                        .build())
                .collect(Collectors.toList());
    }

    private boolean isUserInRole(Group group, String userId, String roleName) {
        return group.getGroupMembers().stream()
                .anyMatch(member -> userId.equals(member.getUserId()) &&
                        member.getGroupMemberRole().getRoleName().equals(roleName));
    }
}
