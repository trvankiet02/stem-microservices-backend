package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.*;
import com.trvankiet.app.dto.response.GenericResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ChatRoomService {
    ResponseEntity<GenericResponse> createChatRoom(String userId, CreateChatRoomRequest createChatRoomRequest);

    ResponseEntity<GenericResponse> getChatRoom(String userId, String id);

    ResponseEntity<GenericResponse> getMember(String userId, String groupId);

    ResponseEntity<GenericResponse> changeName(String userId, ChangeNameRequest changeNameRequest);

    ResponseEntity<GenericResponse> changeAvatar(String userId, String groupId, MultipartFile avatar) throws IOException;

    ResponseEntity<GenericResponse> changeAcceptAllRequest(String userId, ChangeIsAcceptAllRequest isAcceptAllRequest);

    ResponseEntity<GenericResponse> addMember(String currentUserId, AddChatMemberRequest addChatMemberRequest);

    ResponseEntity<GenericResponse> removeMember(String currentUserId, RemoveChatMemberRequest removeChatMemberRequest);

    ResponseEntity<GenericResponse> deleteChatRoom(String userId, String id);

	ResponseEntity<GenericResponse> addMembers(String currentUserId,
			 AddChatMembersRequest addChatMembersRequest);

	ResponseEntity<GenericResponse> leaveGroup(String currentUserId, @Valid LeaveChatRoomRequest leaveChatRoomRequest);

	ResponseEntity<GenericResponse> giveAdmin(String currentUserId, @Valid GiveAdminRequest giveAdminRequest);
}
