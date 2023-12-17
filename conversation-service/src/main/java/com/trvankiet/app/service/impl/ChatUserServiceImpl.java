package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.StatusEnum;
import com.trvankiet.app.dto.request.StatusRequest;
import com.trvankiet.app.dto.request.UpdateChatUserRequest;
import com.trvankiet.app.dto.request.CreateChatUserRequest;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.repository.ChatUserRepository;
import com.trvankiet.app.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatUserServiceImpl implements ChatUserService {

    private final ChatUserRepository chatUserRepository;

    @Override
    public ResponseEntity<ChatUser> createChatUser(CreateChatUserRequest createChatUserRequest) {
        log.info("ChatUserServiceImpl: createChatUser");
        ChatUser chatUser = chatUserRepository.save(ChatUser.builder()
                        .id(createChatUserRequest.getId())
                .firstName(createChatUserRequest.getFirstName())
                .lastName(createChatUserRequest.getLastName())
                .avatarUrl(createChatUserRequest.getAvatarUrl())
                .build());
        return ResponseEntity.ok(chatUser);
    }

    @Override
    public ResponseEntity<ChatUser> updateChatUser(String id, UpdateChatUserRequest updateChatUserRequest) {
        log.info("ChatUserServiceImpl: updateChatUser");
        Optional<ChatUser> chatUserOptional = chatUserRepository.findById(id);
        if (chatUserOptional.isPresent()) {
            ChatUser chatUser = chatUserOptional.get();
            chatUser.setFirstName(updateChatUserRequest.getFirstName());
            chatUser.setLastName(updateChatUserRequest.getLastName());
            chatUser.setAvatarUrl(updateChatUserRequest.getAvatarUrl());
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

    @Override
    public ChatUser saveChatUser(StatusRequest statusRequest) {
        log.info("ChatUserServiceImpl: saveChatUser");
        ChatUser storedChatUser = chatUserRepository.findById(statusRequest.getUserId())
                .orElse(null);

        if (storedChatUser != null) {
            storedChatUser.setStatus(StatusEnum.ONLINE);
            chatUserRepository.save(storedChatUser);
        } else {
            throw new BadRequestException("User not found");
        }

        return storedChatUser;
    }

    @Override
    public ChatUser disconnectChatUser(StatusRequest statusRequest) {
        log.info("ChatUserServiceImpl: disconnectChatUser");
        ChatUser storedChatUser = chatUserRepository.findById(statusRequest.getUserId())
                .orElse(null);

        if (storedChatUser != null) {
            storedChatUser.setStatus(StatusEnum.OFFLINE);
            chatUserRepository.save(storedChatUser);
        } else {
            throw new BadRequestException("User not found");

        }
        return storedChatUser;
    }

    @Override
    public List<ChatUser> findOnlineChatUsers(List<String> userIds) {
        log.info("ChatUserServiceImpl: findOnlineChatUsers");
        return chatUserRepository.findAllById(userIds)
                .stream()
                .filter(chatUser -> chatUser.getStatus().equals(StatusEnum.ONLINE))
                .toList();
    }
}
