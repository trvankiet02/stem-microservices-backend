package com.trvankiet.app.controller;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat-messages")
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final JwtService jwtService;

    @GetMapping("/get-user-messages")
    public ResponseEntity<GenericResponse> getAllUserMessages(@RequestHeader("Authorization") String token) {
        String accessToken = token.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return chatMessageService.getAllUserMessages(userId);
    }

    @GetMapping("/get-message-from-user")
    public ResponseEntity<GenericResponse> getMessageFromUser(@RequestHeader("Authorization") String token,
                                                              @RequestParam String friendId,
                                                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                              @RequestParam(value = "size", defaultValue = "20") Integer size) {
        String accessToken = token.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return chatMessageService.getMessageFromUser(userId, friendId, page, size);
    }

    @GetMapping("/get-message-from-group")
    public ResponseEntity<GenericResponse> getMessageFromGroup(@RequestHeader("Authorization") String token,
                                                              @RequestParam String groupId,
                                                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                              @RequestParam(value = "size", defaultValue = "20") Integer size) {
        String accessToken = token.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return chatMessageService.getMessageFromGroup(userId, groupId, page, size);
    }

}
