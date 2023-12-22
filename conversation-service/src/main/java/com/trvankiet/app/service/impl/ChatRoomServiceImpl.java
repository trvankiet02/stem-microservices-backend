package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.ChatRoomDto;
import com.trvankiet.app.dto.ChatUserDto;
import com.trvankiet.app.dto.request.*;
import com.trvankiet.app.dto.response.ChatMemberResponse;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.ChatRoom;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.repository.ChatRoomRepository;
import com.trvankiet.app.repository.ChatUserRepository;
import com.trvankiet.app.service.ChatRoomService;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.client.FileClientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;
	private final ChatUserRepository chatUserRepository;
	private final MapperService mapperService;
	private final FileClientService fileClientService;

	@Override
	public ResponseEntity<GenericResponse> createChatRoom(String userId, CreateChatRoomRequest createChatRoomRequest) {
		log.info("ChatRoomServiceImpl, createChatRoom");

		ChatUser chatUser = chatUserRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		List<ChatUser> chatUsers = chatUserRepository.findAllById(createChatRoomRequest.getMemberIds());

		chatUsers.add(chatUser);

		ChatRoom chatRoom = ChatRoom.builder().id(UUID.randomUUID().toString()).name(createChatRoomRequest.getName())
				.authorId(userId).isAcceptAllRequest(createChatRoomRequest.getIsAcceptAllRequest())
				.createdAt(new Date()).members(chatUsers).build();

		ChatRoomDto chatRoomDto = mapperService.mapToChatRoomDto(chatRoomRepository.save(chatRoom));

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Create chat room successfully").result(chatRoomDto).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getChatRoom(String userId, String id) {
		log.info("ChatRoomServiceImpl, getChatRoom");

		ChatRoom chatRoom = chatRoomRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Chat room not found"));

		if (chatRoom.getMembers().stream().noneMatch(chatUser -> chatUser.getId().equals(userId))) {
			throw new RuntimeException("You are not a member of this chat room");
		}

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Get chat room successfully").result(chatRoom).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getMember(String userId, String groupId) {
		log.info("ChatRoomServiceImpl, getMember");

		ChatRoom chatRoom = chatRoomRepository.findById(groupId)
				.orElseThrow(() -> new RuntimeException("Chat room not found"));

		if (chatRoom.getMembers().stream().noneMatch(chatUser -> chatUser.getId().equals(userId))) {
			throw new RuntimeException("You are not a member of this chat room");
		}

		List<ChatMemberResponse> chatUsers = chatRoom.getMembers().stream()
				.map(chatUser -> ChatMemberResponse.builder().chatUserDto(mapperService.mapToChatUserDto(chatUser))
						.isOwner(chatUser.getId().equals(chatRoom.getAuthorId())).build())
				.toList();

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Get member successfully").result(chatUsers).build());
	}

	@Override
	public ResponseEntity<GenericResponse> changeName(String userId, ChangeNameRequest changeNameRequest) {
		log.info("ChatRoomServiceImpl, changeName");

		ChatRoom chatRoom = chatRoomRepository.findById(changeNameRequest.getGroupId())
				.orElseThrow(() -> new RuntimeException("Chat room not found"));

		if (!chatRoom.getAuthorId().equals(userId)) {
			throw new RuntimeException("You are not the owner of this chat room");
		}

		chatRoom.setName(changeNameRequest.getName());
		chatRoom.setUpdatedAt(new Date());

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Change name successfully").result(chatRoomRepository.save(chatRoom)).build());
	}

	@Override
	public ResponseEntity<GenericResponse> changeAvatar(String userId, ChangeAvatarRequest changeAvatarRequest)
			throws IOException {
		log.info("ChatRoomServiceImpl, changeAvatar");

		ChatRoom chatRoom = chatRoomRepository.findById(changeAvatarRequest.getGroupId())
				.orElseThrow(() -> new RuntimeException("Chat room not found"));

		if (!chatRoom.getAuthorId().equals(userId)) {
			throw new RuntimeException("You are not the owner of this chat room");
		}

		String avatarUrl = fileClientService.uploadGroupAvatar(changeAvatarRequest.getAvatar());

		if (chatRoom.getAvatarUrl() != null) {
			fileClientService.deleteGroupAvatar(chatRoom.getAvatarUrl());
		}

		chatRoom.setAvatarUrl(avatarUrl);
		chatRoom.setUpdatedAt(new Date());

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Change avatar successfully").result(chatRoomRepository.save(chatRoom)).build());
	}

	@Override
	public ResponseEntity<GenericResponse> changeAcceptAllRequest(String userId,
			ChangeIsAcceptAllRequest isAcceptAllRequest) {
		log.info("ChatRoomServiceImpl, changeAcceptAllRequest");

		ChatRoom chatRoom = chatRoomRepository.findById(isAcceptAllRequest.getGroupId())
				.orElseThrow(() -> new RuntimeException("Chat room not found"));

		if (!chatRoom.getAuthorId().equals(userId)) {
			throw new RuntimeException("You are not the owner of this chat room");
		}

		chatRoom.setIsAcceptAllRequest(isAcceptAllRequest.getIsAcceptAllRequest());
		chatRoom.setUpdatedAt(new Date());

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Change isAcceptAllRequest successfully")
				.result(chatRoomRepository.save(chatRoomRepository.save(chatRoom))).build());
	}

	@Override
	public ResponseEntity<GenericResponse> addMember(String currentUserId, AddChatMemberRequest addChatMemberRequest) {
		log.info("ChatRoomServiceImpl, addMember");

		ChatRoom chatRoom = chatRoomRepository.findById(addChatMemberRequest.getGroupId())
				.orElseThrow(() -> new RuntimeException("Chat room not found"));

		// check if isAcceptAllRequest is true, user can add member else checkuser is
		// owner
		if (chatRoom.getIsAcceptAllRequest()) {
			ChatUser chatUser = chatUserRepository.findById(addChatMemberRequest.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found"));

			chatRoom.getMembers().add(chatUser);
		} else {
			if (!chatRoom.getAuthorId().equals(currentUserId)) {
				throw new RuntimeException("You are not the owner of this chat room");
			}

			ChatUser chatUser = chatUserRepository.findById(addChatMemberRequest.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found"));

			chatRoom.getMembers().add(chatUser);
		}

		chatRoom.setUpdatedAt(new Date());

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Add member successfully").result(chatRoomRepository.save(chatRoom)).build());
	}

	@Override
	public ResponseEntity<GenericResponse> removeMember(String currentUserId,
			RemoveChatMemberRequest removeChatMemberRequest) {
		log.info("ChatRoomServiceImpl, removeMember");

		ChatRoom chatRoom = chatRoomRepository.findById(removeChatMemberRequest.getGroupId())
				.orElseThrow(() -> new RuntimeException("Chat room not found"));

		if (!chatRoom.getAuthorId().equals(currentUserId)) {
			throw new RuntimeException("You are not the owner of this chat room");
		}

		ChatUser chatUser = chatUserRepository.findById(removeChatMemberRequest.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		chatRoom.getMembers().remove(chatUser);

		chatRoom.setUpdatedAt(new Date());

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Remove member successfully").result(chatRoomRepository.save(chatRoom)).build());
	}

	@Override
	public ResponseEntity<GenericResponse> deleteChatRoom(String userId, String id) {
		log.info("ChatRoomServiceImpl, deleteChatRoom");

		ChatRoom chatRoom = chatRoomRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Chat room not found"));

		if (!chatRoom.getAuthorId().equals(userId)) {
			throw new RuntimeException("You are not the owner of this chat room");
		}

		chatRoomRepository.delete(chatRoom);

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Delete chat room successfully").build());
	}

	@Override
	public ResponseEntity<GenericResponse> addMembers(String currentUserId,
			AddChatMembersRequest addChatMembersRequest) {
		log.info("ChatRoomServiceImpl, addMember");

		ChatRoom chatRoom = chatRoomRepository.findById(addChatMembersRequest.getGroupId())
				.orElseThrow(() -> new RuntimeException("Chat room not found"));

		// check if isAcceptAllRequest is true, user can add member else checkuser is
		// owner
		if (chatRoom.getIsAcceptAllRequest()) {
			List<ChatUser> chatUsers = chatUserRepository.findAllById(addChatMembersRequest.getUserIds());

			chatUsers.forEach(chatUser -> {
				if (chatRoom.getMembers().stream().noneMatch(member -> member.getId().equals(chatUser.getId()))) {
					chatRoom.getMembers().add(chatUser);
				}
			});
		} else {
			if (!chatRoom.getAuthorId().equals(currentUserId)) {
				throw new RuntimeException("You are not the owner of this chat room");
			}

			List<ChatUser> chatUsers = chatUserRepository.findAllById(addChatMembersRequest.getUserIds());

			chatUsers.forEach(chatUser -> {
				if (chatRoom.getMembers().stream().noneMatch(member -> member.getId().equals(chatUser.getId()))) {
					chatRoom.getMembers().add(chatUser);
				}
			});
		}

		chatRoom.setUpdatedAt(new Date());

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Add member successfully").result(chatRoomRepository.save(chatRoom)).build());
	}

	@Override
	public ResponseEntity<GenericResponse> leaveGroup(String currentUserId,
			@Valid LeaveChatRoomRequest leaveChatRoomRequest) {
		log.info("ChatRoomServiceImpl, leaveGroup");
		
		ChatRoom chatRoom = chatRoomRepository.findById(leaveChatRoomRequest.getGroupId())
				.orElseThrow(() -> new RuntimeException("Chat room not found"));
		
		ChatUser chatUser = chatUserRepository.findById(currentUserId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		if (chatRoom.getAuthorId().equals(currentUserId)) {
			throw new RuntimeException("You are the owner of this chat room");
        }
		
		chatRoom.getMembers().remove(chatUser);
		chatRoom.setUpdatedAt(new Date());
		
		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
                .message("Leave group successfully").result(chatRoomRepository.save(chatRoom)).build());
		}

	@Override
	public ResponseEntity<GenericResponse> giveAdmin(String currentUserId, @Valid GiveAdminRequest giveAdminRequest) {
		log.info("ChatRoomServiceImpl, giveAdmin");
		
		ChatRoom chatRoom = chatRoomRepository.findById(giveAdminRequest.getGroupId())
				.orElseThrow(() -> new RuntimeException("Chat room not found"));
		
		if (!chatRoom.getAuthorId().equals(currentUserId)) {
			throw new RuntimeException("You are not the owner of this chat room");
        }
		
		ChatUser chatUser = chatUserRepository.findById(giveAdminRequest.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		if (chatRoom.getMembers().stream().noneMatch(chatUser1 -> chatUser1.getId().equals(chatUser.getId()))) {
			throw new RuntimeException("User is not a member of this chat room");
		}
		
		chatRoom.setAuthorId(chatUser.getId());
		chatRoom.setUpdatedAt(new Date());
		
		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Give admin successfully").result(chatRoomRepository.save(chatRoom)).build());
	}
}
