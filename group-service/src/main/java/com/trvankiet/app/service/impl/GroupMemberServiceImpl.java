package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.GroupAccessType;
import com.trvankiet.app.constant.GroupMemberRoleType;
import com.trvankiet.app.constant.StateType;
import com.trvankiet.app.dto.GroupMemberDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.*;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.GroupMemberResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
	private final MapperService mapperService;

	@Override
	public ResponseEntity<GenericResponse> inivteGroupMember(String userId,
			InviteGroupMemberRequest groupMemberRequest) {
		log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> inivteGroupMember");
		GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, groupMemberRequest.getGroupId())
				.orElseThrow(() -> new ForbiddenException("Bạn không có quyền thêm thành viên vào nhóm này"));
		GroupMemberInvitation groupMemberInvitation = groupMemberInvitationRepository
				.save(GroupMemberInvitation.builder().id(UUID.randomUUID().toString()).group(groupMember.getGroup())
						.fromUserId(groupMember.getUserId()).toUserId(groupMemberRequest.getUserId())
						.state(StateType.PENDING).createdAt(new Date()).build());
		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Mời thành viên vào nhóm thành công").result(groupMemberInvitation.getId()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> requestGroupMember(String userId, String groupId) {
		log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> requestGroupMember");
		Group group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Nhóm không tồn tại"));
		Optional<GroupMember> existGroupMember = groupMemberRepository.findByUserIdAndGroupId(userId, group.getId());
		if (existGroupMember.isPresent()) {
			throw new BadRequestException("Bạn đã là thành viên của nhóm này");
		}
		GroupMemberRequest groupMemberRequest = groupMemberRequestRepository
				.save(GroupMemberRequest.builder().id(UUID.randomUUID().toString()).group(group).authorId(userId)
						.state(StateType.PENDING).createdAt(new Date()).build());

		if (group.getIsAcceptAllRequest()) {
			groupMemberRequest.setState(StateType.ACCEPTED);
			groupMemberRequestRepository.save(groupMemberRequest);
			groupMemberRepository.save(GroupMember.builder().id(UUID.randomUUID().toString()).userId(userId)
					.group(group).role(GroupMemberRoleType.GROUP_MEMBER).createdAt(new Date()).build());
			return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(201)
					.message("Đã trở thành thành viên của nhóm!").result(null).build());
		}
		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Yêu cầu tham gia nhóm thành công").result(null).build());
	}

	@Override
	public ResponseEntity<GenericResponse> responseGroupMemberInvitation(String userId, String groupMemberInvitationId,
			StateRequest inviteResponseGroupMember) {
		log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> responseGroupMemberInvitation");
		GroupMemberInvitation groupMemberInvitation = groupMemberInvitationRepository.findById(groupMemberInvitationId)
				.orElseThrow(() -> new NotFoundException("Lời mời không tồn tại"));
		if (!groupMemberInvitation.getToUserId().equals(userId)) {
			throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
		}
		if (inviteResponseGroupMember.getIsAccept()) {
			// chage state to ACCEPT and create GroupMemberRequest
			groupMemberInvitation.setState(StateType.ACCEPTED);
			groupMemberInvitation.setUpdatedAt(new Date());
			groupMemberInvitationRepository.save(groupMemberInvitation);
			// return this.requestGroupMember(userId,
			// groupMemberInvitation.getGroup().getId());
			Group group = groupMemberInvitation.getGroup();
			GroupMemberRequest groupMemberRequest = groupMemberRequestRepository.save(GroupMemberRequest.builder()
					.id(UUID.randomUUID().toString()).group(group).authorId(userId).state(StateType.PENDING)
					.groupMemberInvitation(groupMemberInvitation).createdAt(new Date()).build());

			if (group.getIsAcceptAllRequest()) {
				groupMemberRequest.setState(StateType.ACCEPTED);
				groupMemberRequestRepository.save(groupMemberRequest);

				groupMemberRepository.save(GroupMember.builder().id(UUID.randomUUID().toString()).userId(userId)
						.group(group).role(GroupMemberRoleType.GROUP_MEMBER).createdAt(new Date()).build());
			}
			return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
					.message("Lời mời đã được chấp nhận").result(group.getId()).build());
		} else {
			groupMemberInvitation.setState(StateType.REJECTED);
			groupMemberInvitation.setUpdatedAt(new Date());
			groupMemberInvitationRepository.save(groupMemberInvitation);
			return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
					.message("Lời mời đã bị từ chối").result(null).build());
		}
	}

	@Override
	public ResponseEntity<GenericResponse> responseGroupMemberRequest(String userId, String groupMemberRequestId,
			StateRequest stateRequest) {
		log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> responseGroupMemberRequest");
		GroupMemberRequest groupMemberRequest = groupMemberRequestRepository.findById(groupMemberRequestId)
				.orElseThrow(() -> new NotFoundException("Yêu cầu không tồn tại"));
		Group group = groupMemberRequest.getGroup();
		GroupMember user = groupMemberRepository.findByUserIdAndGroupId(userId, group.getId())
				.orElseThrow(() -> new ForbiddenException("Bạn không có quyền thực hiện hành động này"));
		if (user.getRole().equals(GroupMemberRoleType.GROUP_MEMBER)) {
			throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
		}
		if (stateRequest.getIsAccept()) {
			// change state to ACCEPT and create GroupMember
			groupMemberRequest.setState(StateType.ACCEPTED);
			groupMemberRequest.setUpdatedAt(new Date());
			groupMemberRequestRepository.save(groupMemberRequest);
			groupMemberRepository.save(GroupMember.builder().id(UUID.randomUUID().toString())
					.userId(groupMemberRequest.getAuthorId()).group(group).role(GroupMemberRoleType.GROUP_MEMBER)
					.groupMemberRequest(groupMemberRequest).createdAt(new Date()).build());
			return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
					.message("Yêu cầu đã được chấp nhận").result(null).build());
		} else {
			// change state to REJECT
			groupMemberRequest.setState(StateType.REJECTED);
			groupMemberRequest.setUpdatedAt(new Date());
			groupMemberRequestRepository.save(groupMemberRequest);
			return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
					.message("Yêu cầu đã bị từ chối").result(null).build());
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
		try {
			groupMember.setRole(GroupMemberRoleType.valueOf(role));
			groupMember.setUpdatedAt(new Date());
			groupMemberRepository.save(groupMember);
			return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
					.message("Thay đổi quyền thành viên thành công")
					.result(mapperService.mapToGroupMemberResponse(groupMember)).build());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("Quyền thành viên không hợp lệ");
		} catch (Exception e) {
			throw new BadRequestException(e.getMessage());
		}
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
		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Xóa thành viên thành công").result(null).build());
	}

	@Override
	public ResponseEntity<GenericResponse> addGroupMember(String userId, AddGroupMemberRequest addGroupMemberRequest) {
		log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> addGroupMember");
		Group group = groupRepository.findById(addGroupMemberRequest.getGroupId())
				.orElseThrow(() -> new NotFoundException("Nhóm không tồn tại"));
		Optional<GroupMember> newOptionalGroupMember = groupMemberRepository
				.findByUserIdAndGroupId(addGroupMemberRequest.getUserId(), group.getId());
		if (newOptionalGroupMember.isPresent()) {
			throw new BadRequestException("Thành viên đã tồn tại trong nhóm");
		}
		GroupMember user = groupMemberRepository.findByUserIdAndGroupId(userId, group.getId())
				.orElseThrow(() -> new ForbiddenException("Bạn không có quyền thêm thành viên vào nhóm này"));
		if (user.getRole().equals(GroupMemberRoleType.GROUP_MEMBER)) {
			throw new ForbiddenException("Bạn không có quyền thêm thành viên vào nhóm này");
		}
		try {
			GroupMember groupMember = groupMemberRepository.save(
					GroupMember.builder().id(UUID.randomUUID().toString()).userId(addGroupMemberRequest.getUserId())
							.group(group).role(GroupMemberRoleType.valueOf(addGroupMemberRequest.getRoleCode()))
							.createdAt(new Date()).build());
			return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
					.message("Thêm thành viên vào nhóm thành công")
					.result(mapperService.mapToGroupMemberResponse(groupMember)).build());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("Quyền thành viên không hợp lệ");
		} catch (Exception e) {
			throw new BadRequestException(e.getMessage());
		}
	}

	@Override
	public ResponseEntity<GenericResponse> getGroupMemberByGroupId(String userId, String groupId) {
		log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> getGroupMemberByGroupId");
		List<GroupMemberResponse> groupMembers = groupMemberRepository.findAllByGroupId(groupId).stream()
				.map(mapperService::mapToGroupMemberResponse).toList();
		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Lấy danh sách thành viên thành công!").result(groupMembers).build());
	}

	@Override
	public ResponseEntity<GenericResponse> lockGroupMember(String userId, LockMemberRequest lockMemberRequest) {
		log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> lockGroupMember");
		GroupMember groupMember = groupMemberRepository.findById(lockMemberRequest.getGroupMemberId())
				.orElseThrow(() -> new NotFoundException("Thành viên không tồn tại"));
		Group group = groupMember.getGroup();

		GroupMember user = groupMemberRepository.findByUserIdAndGroupId(userId, group.getId())
				.orElseThrow(() -> new ForbiddenException("Ban không có quyền thực hiện hành động này"));
		if (user.getRole().equals(GroupMemberRoleType.GROUP_MEMBER)) {
			throw new ForbiddenException("Ban không có quyền thực hiện hành động này");
		}
		groupMember.setIsLocked(true);
		groupMember.setLockedAt(new Date());
		groupMember.setLockedReason(lockMemberRequest.getLockedReason());
		groupMemberRepository.save(groupMember);
		return ResponseEntity
				.ok(GenericResponse.builder().success(true).statusCode(200).message("Khóa thành viên thành công")
						.result(mapperService.mapToGroupMemberResponse(groupMember)).build());
	}

	@Override
	public ResponseEntity<GenericResponse> unlockGroupMember(String userId, UnlockMemberRequest unlockMemberRequest) {
		log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> unlockGroupMember");
		GroupMember groupMember = groupMemberRepository.findById(unlockMemberRequest.getGroupMemberId())
				.orElseThrow(() -> new NotFoundException("Thành viên không tồn tại"));
		Group group = groupMember.getGroup();

		GroupMember user = groupMemberRepository.findByUserIdAndGroupId(userId, group.getId())
				.orElseThrow(() -> new ForbiddenException("Ban không có quyền thực hiện hành động này"));
		if (user.getRole().equals(GroupMemberRoleType.GROUP_MEMBER)) {
			throw new ForbiddenException("Ban không có quyền thực hiện hành động này");
		}
		groupMember.setIsLocked(false);
		groupMember.setLockedAt(null);
		groupMember.setLockedReason(null);
		groupMemberRepository.save(groupMember);
		return ResponseEntity
				.ok(GenericResponse.builder().success(true).statusCode(200).message("Mở khóa thành viên thành công")
						.result(mapperService.mapToGroupMemberResponse(groupMember)).build());
	}

	@Override
	public String getGroupMemberRoleByGroupIdAndUserId(String groupId, String userId) {
		log.info("GroupMemberServiceImpl, String getGroupMemberRoleByGroupIdAndUserId");
		GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
				.orElseThrow(() -> new NotFoundException("Thành viên không tồn tại"));
		return groupMember.getRole().name();
	}

	@Override
	public ResponseEntity<GenericResponse> getAllGroupMembers(String authorizationHeader, String groupId, Integer page,
			Integer size) {
		log.info("GroupMemberServiceImpl, ResponseEntity<GenericResponse> getAllGroupMembers");

		if (groupId != null) {
			
			Group group = groupRepository.findById(groupId).orElse(null);

			Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

			Page<GroupMember> groupMembers = groupMemberRepository.findAllByGroupId(group.getId(), pageable);

			List<GroupMemberResponse> groupMemberDtos = groupMembers.stream()
					.map(mapperService::mapToGroupMemberResponse).toList();

			Map<String, Object> result = new HashMap<>();
			result.put("groupMembers", groupMemberDtos);
			result.put("totalPages", groupMembers.getTotalPages());
			result.put("totalElements", groupMembers.getTotalElements());
			result.put("currentPage", groupMembers.getNumber());
			result.put("currentElements", groupMembers.getNumberOfElements());

			return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
					.message("Lấy danh sách thành viên thành công!").result(result).build());
		} else {
			Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

			Page<GroupMember> groupMembers = groupMemberRepository.findAll(pageable);

			List<GroupMemberResponse> groupMemberDtos = groupMembers.stream()
					.map(mapperService::mapToGroupMemberResponse).toList();

			Map<String, Object> result = new HashMap<>();
			result.put("groupMembers", groupMemberDtos);
			result.put("totalPages", groupMembers.getTotalPages());
			result.put("totalElements", groupMembers.getTotalElements());
			result.put("currentPage", groupMembers.getNumber());
			result.put("currentElements", groupMembers.getNumberOfElements());

			return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
					.message("Lấy danh sách thành viên thành công!").result(result).build());
		}
	}
}
