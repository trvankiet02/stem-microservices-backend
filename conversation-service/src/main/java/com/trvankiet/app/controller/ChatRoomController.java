package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.CreateChatRoomRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat-rooms")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final JwtService jwtService;
    private final ChatRoomService chatRoomService;
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> createChatRoom(@RequestHeader("Authorization") String authorizationHeader,
                                                          @RequestBody CreateChatRoomRequest createChatRoomRequest) {
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

    @GetMapping(value = "/getMember")
    public ResponseEntity<GenericResponse> getMember(@RequestHeader("Authorization") String authorizationHeader,
                                                     @RequestParam("groupId") String groupId) {
        log.info("ChatRoomController, getMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return chatRoomService.getMember(userId, groupId);
    }


}
