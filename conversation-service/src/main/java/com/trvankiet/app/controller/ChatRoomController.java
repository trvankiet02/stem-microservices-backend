package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.*;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/chat-rooms")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final JwtService jwtService;
    private final ChatRoomService chatRoomService;
    @PostMapping(value = "/create")
    public ResponseEntity<GenericResponse> createChatRoom(@RequestHeader("Authorization") String authorizationHeader,
                                                          @RequestBody @Valid CreateChatRoomRequest createChatRoomRequest) {
        log.info("ChatRoomController, createChatRoom");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return chatRoomService.createChatRoom(userId, createChatRoomRequest);
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<GenericResponse> getChatRoom(@RequestHeader("Authorization") String authorizationHeader,
                                                       @PathVariable("id") String id) {
        log.info("ChatRoomController, getChatRoom");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return chatRoomService.getChatRoom(userId, id);
    }

    @GetMapping(value = "/get-members")
    public ResponseEntity<GenericResponse> getMember(@RequestHeader("Authorization") String authorizationHeader,
                                                     @RequestParam("groupId") String groupId) {
        log.info("ChatRoomController, getMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return chatRoomService.getMember(userId, groupId);
    }

    @PutMapping(value = "/change-name")
    public ResponseEntity<GenericResponse> changeName(@RequestHeader("Authorization") String authorizationHeader,
                                                      @RequestBody @Valid ChangeNameRequest changeNameRequest) {
        log.info("ChatRoomController, changeName");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return chatRoomService.changeName(userId, changeNameRequest);
    }

    @PutMapping(value = "/change-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> changeAvatar(@RequestHeader("Authorization") String authorizationHeader,
    		@RequestPart(name = "groupId") String groupId,
    		@RequestPart(name = "avatar") MultipartFile avatar) throws IOException {
        log.info("ChatRoomController, changeAvatar");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return chatRoomService.changeAvatar(userId, groupId, avatar);
    }

    @PutMapping(value = "/change-accept-all-request")
    public ResponseEntity<GenericResponse> changeAcceptAllRequest(@RequestHeader("Authorization") String authorizationHeader,
                                                                  @RequestBody @Valid ChangeIsAcceptAllRequest isAcceptAllRequest) {
        log.info("ChatRoomController, changeAcceptAllRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return chatRoomService.changeAcceptAllRequest(userId, isAcceptAllRequest);
    }

    @PostMapping(value = "/add-member")
    public ResponseEntity<GenericResponse> addMember(@RequestHeader("Authorization") String authorizationHeader,
                                                     @RequestBody @Valid AddChatMemberRequest addChatMemberRequest) {
        log.info("ChatRoomController, addMember");
        String accessToken = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(accessToken);
        return chatRoomService.addMember(currentUserId, addChatMemberRequest);
    }
    
    @PostMapping(value = "/add-members")
    public ResponseEntity<GenericResponse> addMembers(@RequestHeader("Authorization") String authorizationHeader,
                                                     @RequestBody @Valid AddChatMembersRequest addChatMembersRequest) {
        log.info("ChatRoomController, addMember");
        String accessToken = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(accessToken);
        return chatRoomService.addMembers(currentUserId, addChatMembersRequest);
    }

    @PostMapping(value = "/remove-member")
    public ResponseEntity<GenericResponse> removeMember(@RequestHeader("Authorization") String authorizationHeader,
                                                        @RequestBody @Valid RemoveChatMemberRequest removeChatMemberRequest) {
        log.info("ChatRoomController, removeMember");
        String accessToken = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(accessToken);
        return chatRoomService.removeMember(currentUserId, removeChatMemberRequest);
    }
    
    @PostMapping(value = "/leave-group")
	public ResponseEntity<GenericResponse> leaveGroup(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody @Valid LeaveChatRoomRequest leaveChatRoomRequest) {
		log.info("ChatRoomController, leaveGroup");
		String accessToken = authorizationHeader.substring(7);
		String currentUserId = jwtService.extractUserId(accessToken);
		return chatRoomService.leaveGroup(currentUserId, leaveChatRoomRequest);
	}

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<GenericResponse> deleteChatRoom(@RequestHeader("Authorization") String authorizationHeader,
                                                          @PathVariable("id") String id) {
        log.info("ChatRoomController, deleteChatRoom");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return chatRoomService.deleteChatRoom(userId, id);
    }
    
    @PostMapping(value = "/give-admin")
	public ResponseEntity<GenericResponse> giveAdmin(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody @Valid GiveAdminRequest giveAdminRequest) {
		log.info("ChatRoomController, giveAdmin");
		String accessToken = authorizationHeader.substring(7);
		String currentUserId = jwtService.extractUserId(accessToken);
		return chatRoomService.giveAdmin(currentUserId, giveAdminRequest);
	}
}
