package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.StateType;
import com.trvankiet.app.dto.GroupMemberInvitationDto;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.repository.GroupMemberInvitationRepository;
import com.trvankiet.app.service.GroupMemberInvitationService;
import com.trvankiet.app.service.MapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupMemberInvitationServiceImpl implements GroupMemberInvitationService {
    private final GroupMemberInvitationRepository groupMemberInvitationRepository;
    private final MapperService mappingService;
    @Override
    public ResponseEntity<GenericResponse> getAllGroupMemberInvitations(String userId) {
        log.info("GroupMemberInvitationServiceImpl, ResponseEntity<GenericResponse> getAllGroupMemberInvitations");
        List<GroupMemberInvitationDto> groupMemberInvitations = groupMemberInvitationRepository
                .findAllByToUserIdAndState(userId, StateType.PENDING)
                .stream()
                .map(mappingService::mapToGroupMemberInvitationDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách lời mời vào nhóm thành công")
                .result(groupMemberInvitations)
                .build());
    }
}
