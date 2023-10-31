package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.GroupRole;
import com.trvankiet.app.constant.State;
import com.trvankiet.app.dto.request.InviteGroupMemberRequest;
import com.trvankiet.app.dto.request.StateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Group;
import com.trvankiet.app.entity.GroupMember;
import com.trvankiet.app.entity.GroupMemberInvitation;
import com.trvankiet.app.entity.GroupMemberRequest;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.*;
import com.trvankiet.app.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberInvitationRepository groupMemberInvitationRepository;
    private final GroupMemberRequestRepository groupMemberRequestRepository;
    private final GroupMemberRoleRepository groupMemberRoleRepository;

    @Override
    public ResponseEntity<GenericResponse> inivteGroupMember(String userId, InviteGroupMemberRequest groupMemberRequest) {
        log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> inivteGroupMember");
        Group group = groupRepository.findById(groupMemberRequest.getGroupId())
                .orElseThrow(() -> new NotFoundException("Nhóm không tồn tại"));
        GroupMember user = groupMemberRepository.findByUserIdAndGroupGroupId(userId, group.getGroupId())
                .orElseThrow(() -> new ForbiddenException("Bạn không có quyền thêm thành viên vào nhóm này"));
        GroupMemberInvitation groupMemberInvitation = groupMemberInvitationRepository
                .save(GroupMemberInvitation.builder()
                        .groupMemberInvitationId(UUID.randomUUID().toString())
                        .group(group)
                        .fromUserId(user.getUserId())
                        .toUserId(groupMemberRequest.getUserId())
                        .state(State.PENDING.toString())
                        .createdAt(new Date())
                        .build());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Mời thành viên vào nhóm thành công")
                .result(groupMemberInvitation)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> requestGroupMember(String userId, String groupId) {
        log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> requestGroupMember");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Nhóm không tồn tại"));
        Optional<GroupMember> existGroupMember = groupMemberRepository.findByUserIdAndGroupGroupId(userId, group.getGroupId());
        if (existGroupMember.isPresent()) {
            throw new BadRequestException("Bạn đã là thành viên của nhóm này");
        }
        if (group.getGroupMemberMode().equals("PUBLIC")) {
            GroupMember groupMember = groupMemberRepository.save(
                    GroupMember.builder()
                            .groupMemberId(UUID.randomUUID().toString())
                            .userId(userId)
                            .group(group)
                            .groupMemberRole(groupMemberRoleRepository.findByRoleName(GroupRole.GROUP_MEMBER.toString())
                                    .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền thành viên")))
                            .createdAt(new Date())
                            .build()
            );
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Yêu cầu tham gia nhóm thành công")
                    .result(Map.of("groupId", group.getGroupId()))
                    .build());
        }
        GroupMemberRequest groupMemberRequest = groupMemberRequestRepository
                .save(GroupMemberRequest.builder()
                        .groupMemberRequestId(UUID.randomUUID().toString())
                        .group(group)
                        .authorId(userId)
                        .state(State.PENDING.toString())
                        .createdAt(new Date())
                        .build());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Yêu cầu tham gia nhóm thành công")
                .result(groupMemberRequest)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> responseGroupMemberInvitation(String userId, String groupMemberInvitationId, StateRequest inviteResponseGroupMember) {
        log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> responseGroupMemberInvitation");
        GroupMemberInvitation groupMemberInvitation = groupMemberInvitationRepository.findById(groupMemberInvitationId)
                .orElseThrow(() -> new NotFoundException("Lời mời không tồn tại"));
        if (!groupMemberInvitation.getToUserId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }
        switch (inviteResponseGroupMember.getState()) {
            case "ACCEPT":
                // chage state to ACCEPT and create GroupMemberRequest
                groupMemberInvitation.setState(State.ACCEPTED.toString());
                groupMemberInvitation.setUpdatedAt(new Date());
                groupMemberInvitationRepository.save(groupMemberInvitation);
                return this.requestGroupMember(userId, groupMemberInvitation.getGroup().getGroupId());
            case "REJECT":
                // change state to REJECT
                groupMemberInvitation.setState(State.REJECTED.toString());
                groupMemberInvitation.setUpdatedAt(new Date());
                groupMemberInvitationRepository.save(groupMemberInvitation);
                return ResponseEntity.ok(GenericResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Lời mời đã bị từ chối")
                        .result(null)
                        .build());
            default:
                throw new BadRequestException("Trạng thái không hợp lệ");
        }
    }

    @Override
    public ResponseEntity<GenericResponse> responseGroupMemberRequest(String userId, String groupMemberRequestId, StateRequest stateRequest) {
        log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> responseGroupMemberRequest");
        GroupMemberRequest groupMemberRequest = groupMemberRequestRepository.findById(groupMemberRequestId)
                .orElseThrow(() -> new NotFoundException("Yêu cầu không tồn tại"));
        if (!groupMemberRequest.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }
        switch (stateRequest.getState()) {
            case "ACCEPT":
                // change state to ACCEPT and create GroupMember
                groupMemberRequest.setState(State.ACCEPTED.toString());
                groupMemberRequest.setUpdatedAt(new Date());
                groupMemberRequestRepository.save(groupMemberRequest);
                GroupMember groupMember = groupMemberRepository
                        .save(GroupMember.builder()
                                .groupMemberId(UUID.randomUUID().toString())
                                .userId(userId)
                                .group(groupMemberRequest.getGroup())
                                .groupMemberRole(groupMemberRoleRepository.findByRoleName(GroupRole.GROUP_MEMBER.toString())
                                        .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền thành viên")))
                                .createdAt(new Date())
                                .build());
                Map<String, Object> result = Map.of("groupId", groupMember.getGroup().getGroupId());
                return ResponseEntity.ok(GenericResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Yêu cầu đã được chấp nhận")
                        .result(result)
                        .build());
            case "REJECT":
                // change state to REJECT
                groupMemberRequest.setState(State.REJECTED.toString());
                groupMemberRequest.setUpdatedAt(new Date());
                groupMemberRequestRepository.save(groupMemberRequest);
                return ResponseEntity.ok(GenericResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Yêu cầu đã bị từ chối")
                        .result(null)
                        .build());
            default:
                throw new BadRequestException("Trạng thái không hợp lệ");
        }
    }
}
