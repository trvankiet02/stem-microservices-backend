package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.GroupRole;
import com.trvankiet.app.constant.RoleBasedAuthority;
import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.GroupCreateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Group;
import com.trvankiet.app.entity.GroupMember;
import com.trvankiet.app.repository.GroupMemberRepository;
import com.trvankiet.app.repository.GroupRepository;
import com.trvankiet.app.service.GroupService;
import com.trvankiet.app.service.client.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final UserClientService userClientService;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public ResponseEntity<GenericResponse> createGroup(String userId, GroupCreateRequest groupCreateRequest) {
        UserDto userDto = userClientService.getUserDtoByUserId(userId);
        if (!userDto.getCredential().getRoleBasedAuthority().equals(RoleBasedAuthority.TEACHER.toString()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Bạn không có quyền để thực hiện hành động này!")
                            .result(null)
                            .statusCode(HttpStatus.FORBIDDEN.value())
                            .build());
        Date now = new Date();

        GroupMember groupMember = groupMemberRepository
                .save(GroupMember.builder()
                        .groupMemberId(UUID.randomUUID().toString())
                        .userId(userDto.getUserId())
                        .groupMemberRole(GroupRole.GROUP_ADMIN.toString())
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
    }
}
