package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.GroupMemberRoleType;
import com.trvankiet.app.constant.StateType;
import com.trvankiet.app.dto.GroupMemberRequestDto;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Group;
import com.trvankiet.app.entity.GroupMember;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.GroupMemberRepository;
import com.trvankiet.app.repository.GroupMemberRequestRepository;
import com.trvankiet.app.repository.GroupMemberRoleRepository;
import com.trvankiet.app.repository.GroupRepository;
import com.trvankiet.app.service.GroupMemberRequestService;
import com.trvankiet.app.service.MapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupMemberRequestServiceImpl implements GroupMemberRequestService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberRequestRepository groupMemberRequestRepository;
    private final GroupMemberRoleRepository groupMemberRoleRepository;
    private final MapperService mappingService;

    @Override
    public ResponseEntity<GenericResponse> getAllGroupMemberRequests(String userId, String groupId) {
        log.info("GroupMemberRequestServiceImpl, ResponseEntity<GenericResponse> getAllGroupMemberRequests");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Nhóm không tồn tại"));
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, group.getId())
                .orElseThrow(() -> new NotFoundException("Bạn không thuộc nhóm này"));
        if (groupMember.getGroupMemberRole()
                .equals(groupMemberRoleRepository.findByCode(GroupMemberRoleType.GROUP_MEMBER.getCode())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền thành viên nhóm")))) {
            throw new ForbiddenException("Bạn không có quyền truy cập");
        }
        List<GroupMemberRequestDto> groupMemberRequestDtos = groupMemberRequestRepository
                .findAllByGroupIdAndStateCode(groupId, StateType.PENDING.getCode())
                .stream()
                .map(mappingService::mapToGroupMemberRequestDto)
                .toList();
        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Lấy danh sách yêu cầu thành công")
                        .result(groupMemberRequestDtos)
                        .build()
        );
    }
}
