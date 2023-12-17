package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.ChatRoomDto;
import com.trvankiet.app.dto.ChatUserDto;
import com.trvankiet.app.dto.request.CreateChatRoomRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.ChatRoom;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.repository.ChatRoomRepository;
import com.trvankiet.app.repository.ChatUserRepository;
import com.trvankiet.app.service.ChatRoomService;
import com.trvankiet.app.service.MapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    @Override
    public ResponseEntity<GenericResponse> createChatRoom(String userId, CreateChatRoomRequest createChatRoomRequest) {
        log.info("ChatRoomServiceImpl, createChatRoom");

        ChatUser chatUser = chatUserRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        createChatRoomRequest.getMemberIds();

        List<ChatUser> chatUsers = chatUserRepository.findAllById(createChatRoomRequest.getMemberIds());

        chatUsers.add(chatUser);

        ChatRoom chatRoom = ChatRoom.builder()
                .id(UUID.randomUUID().toString())
                .name(createChatRoomRequest.getName())
                .authorId(userId)
                .createdAt(new Date())
                .members(chatUsers)
                .build();

        ChatRoomDto chatRoomDto = mapperService.mapToChatRoomDto(chatRoomRepository.save(chatRoom));

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Create chat room successfully")
                .result(chatRoomDto)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getChatRoom(String userId, String id) {
        log.info("ChatRoomServiceImpl, getChatRoom");

        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Chat room not found")
        );

        if (chatRoom.getMembers().stream().noneMatch(chatUser -> chatUser.getId().equals(userId))) {
            throw new RuntimeException("You are not a member of this chat room");
        }

        ChatRoomDto chatRoomDto = mapperService.mapToChatRoomDto(chatRoom);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Get chat room successfully")
                .result(chatRoomDto)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getMember(String userId, String groupId) {
        log.info("ChatRoomServiceImpl, getMember");

        ChatRoom chatRoom = chatRoomRepository.findById(groupId).orElseThrow(
                () -> new RuntimeException("Chat room not found")
        );

        if (chatRoom.getMembers().stream().noneMatch(chatUser -> chatUser.getId().equals(userId))) {
            throw new RuntimeException("You are not a member of this chat room");
        }

        List<ChatUserDto> chatUsers = chatRoom.getMembers().stream()
                .map(mapperService::mapToChatUserDto)
                .toList();

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Get member successfully")
                .result(chatUsers)
                .build());
    }
}
