package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.StatusEnum;
import com.trvankiet.app.dto.ChatUserDto;
import com.trvankiet.app.dto.request.StatusRequest;
import com.trvankiet.app.dto.request.UpdateChatUserRequest;
import com.trvankiet.app.dto.request.CreateChatUserRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.ChatMessage;
import com.trvankiet.app.entity.ChatRoom;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.ChatMessageRepository;
import com.trvankiet.app.repository.ChatRoomRepository;
import com.trvankiet.app.repository.ChatUserRepository;
import com.trvankiet.app.service.ChatUserService;
import com.trvankiet.app.service.MapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatUserServiceImpl implements ChatUserService {

    private final ChatUserRepository chatUserRepository;
    private final MongoTemplate mongoTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final MapperService mapperService;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public ResponseEntity<ChatUser> createChatUser(CreateChatUserRequest createChatUserRequest) {
        log.info("ChatUserServiceImpl: createChatUser");
        ChatUser chatUser = chatUserRepository.save(ChatUser.builder()
                .id(createChatUserRequest.getId())
                .firstName(createChatUserRequest.getFirstName())
                .lastName(createChatUserRequest.getLastName())
                .status(StatusEnum.OFFLINE)
                .avatarUrl(createChatUserRequest.getAvatarUrl())
                .build());
        return ResponseEntity.ok(null);
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
            return ResponseEntity.ok(null);
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
            storedChatUser.setLastOnline(new Date());
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

    @Override
    public ResponseEntity<GenericResponse> getOnlineFriends(String userId, List<String> friendIds) {
        log.info("ChatUserServiceImpl: getOnlineFriends");

        List<ChatUser> onlineFriends = chatUserRepository.findAllById(friendIds)
                .stream()
                .filter(chatUser -> chatUser.getStatus().equals(StatusEnum.ONLINE))
                .toList();

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true).statusCode(200)
                .message("Online friends retrieved successfully")
                .result(onlineFriends)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getAllUserMessages(String userId, List<String> friends) {
        log.info("ChatUserServiceImpl: getAllUserMessages");
        return null;
    }

    @Override
    public ResponseEntity<GenericResponse> getLast10UserMessages(String userId, Integer page, Integer size) {
        log.info("ChatUserServiceImpl: getLast10UserMessages");

        List<ChatUser> result = new ArrayList<>();
        while (result.size() < 10) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<ChatMessage> chatMessagePage = chatMessageRepository.findAllByReceiverIdOrSenderId(userId, pageable);
            List<ChatMessage> chatMessages = chatMessagePage.getContent();
            for (ChatMessage chatMessage : chatMessages) {
                // check if sender or receiver is userId
                // and id is not in result
                if (!result.contains(chatMessage.getSender()) && !result.contains(chatMessage.getReceiver()) && chatMessage.getChatRoom() == null) {
                    if (chatMessage.getSender().getId().equals(userId)) {
                        result.add(chatMessage.getReceiver());
                    } else {
                        result.add(chatMessage.getSender());
                    }
                }
            }
            page++;
            if (chatMessagePage.isLast()) {
                break;
            }
        }

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true).statusCode(200)
                .message("Last 10 user messages retrieved successfully")
                .result(result)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getLast10GroupMessages(String userId, Integer page, Integer size) {
        log.info("ChatUserServiceImpl: getLast10GroupMessages");

        Page<ChatRoom> chatRoomPage = chatRoomRepository.findAllByMembersId(userId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));

        List<ChatRoom> chatRooms = chatRoomPage.getContent();

        Map<String, Object> result = new HashMap<>();

        result.put("totalPages", chatRoomPage.getTotalPages());
        result.put("totalElements", chatRoomPage.getTotalElements());
        result.put("currentPage", chatRoomPage.getNumber());
        result.put("currentElements", chatRoomPage.getNumberOfElements());
        result.put("chatRooms", chatRooms);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true).statusCode(200)
                .message("Last 10 group messages retrieved successfully")
                .result(result)
                .build());

    }

    @Override
    public ResponseEntity<GenericResponse> getUserDetails(String id) {
        log.info("ChatUserServiceImpl: getUserDetails");

        Optional<ChatUser> chatUserOptional = chatUserRepository.findById(id);

        if (chatUserOptional.isPresent()) {
            ChatUserDto chatUserDto = mapperService.mapToChatUserDto(chatUserOptional.get());
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true).statusCode(200)
                    .message("User details retrieved successfully")
                    .result(chatUserDto)
                    .build());
        }

        throw new NotFoundException("User not found");
    }
}
