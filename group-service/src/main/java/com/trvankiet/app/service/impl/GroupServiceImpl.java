package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.RoleBasedAuthority;
import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.GroupCreateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Group;
import com.trvankiet.app.entity.GroupMember;
import com.trvankiet.app.repository.GroupRepository;
import com.trvankiet.app.service.GroupService;
import com.trvankiet.app.service.client.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final UserClientService userClientService;
    private final GroupRepository groupRepository;
    @Override
    public ResponseEntity<GenericResponse> createGroup(String userId, GroupCreateRequest groupCreateRequest) {
//        UserDto userDto = userClientService.getUserDtoByUserId(userId);
//        if (!userDto.getCredential().getRoleBasedAuthority().equals(RoleBasedAuthority.TEACHER.toString()))
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(GenericResponse.builder()
//                            .success(false)
//                            .message("Bạn không có quyền để thực hiện hành động này!")
//                            .result(null)
//                            .statusCode(HttpStatus.FORBIDDEN.value())
//                            .build());
//        Date now = new Date();
//        Group group = groupRepository.save(Group.builder()
//                .groupId(UUID.randomUUID().toString())
//                .groupName(groupCreateRequest.getGroupName())
//                .groupDescription(groupCreateRequest.getGroupDescription())
//                .groupType(groupCreateRequest.getGroupType())
//                .createdAt(now)
//                .build());
//
//        GroupMember groupMember = GroupMember.builder()
//                .groupMemberId(UUID.randomUUID().toString())
//                .userId(cre)
        return null;
    }
}
