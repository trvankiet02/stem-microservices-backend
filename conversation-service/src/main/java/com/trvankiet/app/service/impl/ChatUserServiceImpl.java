package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.UpdateUserRequest;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.repository.ChatUserRepository;
import com.trvankiet.app.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatUserServiceImpl implements ChatUserService {

    private final ChatUserRepository chatUserRepository;

    @Override
    public ResponseEntity<ChatUser> createChatUser(UserDto userDto) {
        log.info("ChatUserServiceImpl: createChatUser");
        ChatUser chatUser = chatUserRepository.save(ChatUser.builder()
                        .id(userDto.getId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .avatarUrl(userDto.getAvatarUrl())
                .build());
        return ResponseEntity.ok(chatUser);
    }

    @Override
    public ResponseEntity<ChatUser> updateChatUser(String id, UpdateUserRequest updateUserRequest) {
        log.info("ChatUserServiceImpl: updateChatUser");
        Optional<ChatUser> chatUserOptional = chatUserRepository.findById(id);
        if (chatUserOptional.isPresent()) {
            ChatUser chatUser = chatUserOptional.get();
            chatUser.setFirstName(updateUserRequest.getFirstName());
            chatUser.setLastName(updateUserRequest.getLastName());
            chatUser.setAvatarUrl(updateUserRequest.getAvatarUrl());
            chatUserRepository.save(chatUser);
            return ResponseEntity.ok(chatUser);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<ChatUser> deleteChatUser(String id) {
        log.info("ChatUserServiceImpl: deleteChatUser");
        Optional<ChatUser> chatUserOptional = chatUserRepository.findById(id);
        if (chatUserOptional.isPresent()) {
            ChatUser chatUser = chatUserOptional.get();
            chatUserRepository.delete(chatUser);
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.notFound().build();
    }
}
