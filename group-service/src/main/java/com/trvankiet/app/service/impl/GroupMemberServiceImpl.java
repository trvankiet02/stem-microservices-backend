package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.GroupAccessType;
import com.trvankiet.app.constant.GroupMemberRoleType;
import com.trvankiet.app.constant.StateType;
import com.trvankiet.app.dto.GroupMemberDto;
import com.trvankiet.app.dto.request.AddGroupMemberRequest;
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
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.client.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {
    private final UserClientService userClientService;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberInvitationRepository groupMemberInvitationRepository;
    private final GroupMemberRequestRepository groupMemberRequestRepository;
    private final GroupMemberRoleRepository groupMemberRoleRepository;
    private final StateRepository stateRepository;
    private final MapperService mapperService;

    @Override
    public ResponseEntity<GenericResponse> inivteGroupMember(String userId, InviteGroupMemberRequest groupMemberRequest) {
        log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> inivteGroupMember");
        Group group = groupRepository.findById(groupMemberRequest.getGroupId())
                .orElseThrow(() -> new NotFoundException("Nhóm không tồn tại"));
        GroupMember user = groupMemberRepository.findByUserIdAndGroupId(userId, group.getId())
                .orElseThrow(() -> new ForbiddenException("Bạn không có quyền thêm thành viên vào nhóm này"));
        GroupMemberInvitation groupMemberInvitation = groupMemberInvitationRepository
                .save(GroupMemberInvitation.builder()
                        .id(UUID.randomUUID().toString())
                        .group(group)
                        .fromUserId(user.getUserId())
                        .toUserId(groupMemberRequest.getUserId())
                        .state(stateRepository.findByCode(StateType.PENDING.getCode())
                                .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái")))
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
        Optional<GroupMember> existGroupMember = groupMemberRepository.findByUserIdAndGroupId(userId, group.getId());
        if (existGroupMember.isPresent()) {
            throw new BadRequestException("Bạn đã là thành viên của nhóm này");
        }

        GroupMemberRequest groupMemberRequest = groupMemberRequestRepository
                .save(GroupMemberRequest.builder()
                        .id(UUID.randomUUID().toString())
                        .group(group)
                        .authorId(userId)
                        .state(stateRepository.findByCode(StateType.PENDING.getCode())
                                .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái")))
                        .createdAt(new Date())
                        .build());

        if (group.getConfig().getMemberMode().equals(GroupAccessType.PUBLIC.toString())) {
            groupMemberRequest.setState(stateRepository.findByCode(StateType.ACCEPTED.getCode())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái")));
            groupMemberRequestRepository.save(groupMemberRequest);

            GroupMember groupMember = groupMemberRepository.save(
                    GroupMember.builder()
                            .id(UUID.randomUUID().toString())
                            .userId(userId)
                            .group(group)
                            .groupMemberRole(groupMemberRoleRepository.findByCode(GroupMemberRoleType.GROUP_MEMBER.getCode())
                                    .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền thành viên")))
                            .createdAt(new Date())
                            .build()
            );
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Yêu cầu tham gia nhóm thành công")
                .result(mapperService.mapToGroupDto(groupMemberRequest.getGroup()))
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
                groupMemberInvitation.setState(stateRepository.findByCode(StateType.ACCEPTED.getCode())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái")));
                groupMemberInvitation.setUpdatedAt(new Date());
                groupMemberInvitationRepository.save(groupMemberInvitation);
                //return this.requestGroupMember(userId, groupMemberInvitation.getGroup().getId());
                Group group = groupMemberInvitation.getGroup();
                GroupMemberRequest groupMemberRequest = groupMemberRequestRepository
                        .save(GroupMemberRequest.builder()
                                .id(UUID.randomUUID().toString())
                                .group(group)
                                .authorId(userId)
                                .state(stateRepository.findByCode(StateType.PENDING.getCode())
                                        .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái")))
                                .groupMemberInvitation(groupMemberInvitation)
                                .createdAt(new Date())
                                .build());

                if (group.getConfig().getMemberMode().equals(GroupAccessType.PUBLIC.toString())) {
                    groupMemberRequest.setState(stateRepository.findByCode(StateType.ACCEPTED.getCode())
                            .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái")));
                    groupMemberRequestRepository.save(groupMemberRequest);

                    GroupMember groupMember = groupMemberRepository.save(
                            GroupMember.builder()
                                    .id(UUID.randomUUID().toString())
                                    .userId(userId)
                                    .group(group)
                                    .groupMemberRole(groupMemberRoleRepository.findByCode(GroupMemberRoleType.GROUP_MEMBER.getCode())
                                            .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền thành viên")))
                                    .createdAt(new Date())
                                    .build()
                    );
                }
                return ResponseEntity.ok(GenericResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Lời mời đã được chấp nhận")
                        .result(mapperService.mapToGroupDto(groupMemberRequest.getGroup()))
                        .build());
            case "REJECT":
                // change state to REJECT
                groupMemberInvitation.setState(stateRepository.findByCode(StateType.REJECTED.getCode())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái")));
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
        Group group = groupMemberRequest.getGroup();
        GroupMember user = groupMemberRepository.findByUserIdAndGroupId(userId, group.getId())
                .orElseThrow(() -> new ForbiddenException("Bạn không có quyền thực hiện hành động này"));
        if (user.getGroupMemberRole().getCode().equals(GroupMemberRoleType.GROUP_MEMBER.getCode())) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }
        switch (stateRequest.getState()) {
            case "ACCEPT":
                // change state to ACCEPT and create GroupMember
                groupMemberRequest.setState(stateRepository.findByCode(StateType.ACCEPTED.getCode())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái")));
                groupMemberRequest.setUpdatedAt(new Date());
                groupMemberRequestRepository.save(groupMemberRequest);
                GroupMember groupMember = groupMemberRepository
                        .save(GroupMember.builder()
                                .id(UUID.randomUUID().toString())
                                .userId(userId)
                                .group(group)
                                .groupMemberRole(groupMemberRoleRepository.findByCode(GroupMemberRoleType.GROUP_MEMBER.getCode())
                                        .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền thành viên")))
                                .groupMemberRequest(groupMemberRequest)
                                .createdAt(new Date())
                                .build());
                return ResponseEntity.ok(GenericResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Yêu cầu đã được chấp nhận")
                        .result(mapperService.mapToGroupDto(groupMember.getGroup()))
                        .build());
            case "REJECT":
                // change state to REJECT
                groupMemberRequest.setState(stateRepository.findByCode(StateType.REJECTED.getCode())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái")));
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

    @Override
    public ResponseEntity<GenericResponse> changeRole(String userId, String groupMemberId, String role) {
        log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> changeRole");
        GroupMember groupMember = groupMemberRepository.findById(groupMemberId)
                .orElseThrow(() -> new NotFoundException("Thành viên không tồn tại"));
        Group group = groupMember.getGroup();

        if (!userId.equals(group.getAuthorId())) {
            throw new ForbiddenException("Bạn không có quyền thay đổi quyền thành viên");
        }
        groupMember.setGroupMemberRole(groupMemberRoleRepository.findByCode(role)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền thành viên")));
        groupMember.setUpdatedAt(new Date());
        groupMemberRepository.save(groupMember);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Thay đổi quyền thành viên thành công")
                .result(null)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteGroupMember(String userId, String groupMemberId) {
        log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> deleteGroupMember");
        GroupMember groupMember = groupMemberRepository.findById(groupMemberId)
                .orElseThrow(() -> new NotFoundException("Thành viên không tồn tại"));
        Group group = groupMember.getGroup();
        if (!userId.equals(group.getAuthorId())) {
            throw new ForbiddenException("Bạn không có quyền xóa thành viên");
        }
        groupMemberRepository.delete(groupMember);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Xóa thành viên thành công")
                .result(null)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> addGroupMember(String userId, AddGroupMemberRequest addGroupMemberRequest) {
        log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> addGroupMember");
        Group group = groupRepository.findById(addGroupMemberRequest.getGroupId())
                .orElseThrow(() -> new NotFoundException("Nhóm không tồn tại"));
        Optional<GroupMember> newOptionalGroupMember = groupMemberRepository.findByUserIdAndGroupId(addGroupMemberRequest.getUserId(), group.getId());
        if (newOptionalGroupMember.isPresent()) {
            throw new BadRequestException("Thành viên đã tồn tại trong nhóm");
        }
        GroupMember user = groupMemberRepository.findByUserIdAndGroupId(userId, group.getId())
                .orElseThrow(() -> new ForbiddenException("Bạn không có quyền thêm thành viên vào nhóm này"));
        if (user.getGroupMemberRole().getCode().equals(GroupMemberRoleType.GROUP_MEMBER.getCode())) {
            throw new ForbiddenException("Bạn không có quyền thêm thành viên vào nhóm này");
        }
        GroupMember groupMember = groupMemberRepository
                .save(GroupMember.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(addGroupMemberRequest.getUserId())
                        .group(group)
                        .groupMemberRole(groupMemberRoleRepository.findByCode(GroupMemberRoleType.GROUP_MEMBER.getCode())
                                .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền thành viên")))
                        .createdAt(new Date())
                        .build());
        List<GroupMemberDto> groupMembers = groupMemberRepository.findByGroupId(group.getId())
                .stream()
                .map(mapperService::mapToGroupMemberDto)
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Thêm thành viên vào nhóm thành công")
                .result(groupMembers)
                .build());
    }
}
