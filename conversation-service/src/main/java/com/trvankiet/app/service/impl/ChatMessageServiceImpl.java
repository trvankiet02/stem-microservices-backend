package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.ChatMessageDto;
import com.trvankiet.app.dto.ChatMessageResult;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.ChatMessage;
import com.trvankiet.app.entity.ChatRoom;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.repository.ChatMessageRepository;
import com.trvankiet.app.repository.ChatRoomRepository;
import com.trvankiet.app.repository.ChatUserRepository;
import com.trvankiet.app.service.ChatMessageService;
import com.trvankiet.app.service.MapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatUserRepository chatUserRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final MapperService mapperService;

	@Override
	public ChatMessageDto saveChatMessageDto(ChatMessageDto chatMessageDto) {
		log.info("ChatMessageServiceImpl, saveChatMessageDto");
		ChatMessage chatMessage;
		if (chatMessageDto.getId() != null) {
			chatMessage = chatMessageRepository.findById(chatMessageDto.getId()).orElse(ChatMessage.builder()
					.id(UUID.randomUUID().toString()).build());
		} else {
			chatMessage = chatMessageRepository.findBySenderIdAndContentAndCreatedAt(chatMessageDto.getSenderId(),
					chatMessageDto.getContent(), chatMessageDto.getCreatedAt()).orElse(ChatMessage.builder()
							.id(UUID.randomUUID().toString()).build());
		}
		chatMessage.setContent(chatMessageDto.getContent());
		if (chatMessageDto.getSenderId() != null) {
			Optional<ChatUser> optionalSender = chatUserRepository.findById(chatMessageDto.getSenderId());
			optionalSender.ifPresent(chatMessage::setSender);
		}
		if (chatMessageDto.getChatRoomId() != null) {
			Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatMessageDto.getChatRoomId());
			optionalChatRoom.ifPresent(chatMessage::setChatRoom);
		}
		if (chatMessageDto.getReceiverId() != null) {
			Optional<ChatUser> optionalReceiver = chatUserRepository.findById(chatMessageDto.getReceiverId());
			optionalReceiver.ifPresent(chatMessage::setReceiver);
		}
		chatMessage.setRefUrl(chatMessageDto.getRefUrl() != null ? chatMessageDto.getRefUrl() : null);
		chatMessage.setIsDeleted(chatMessageDto.getIsDeleted() != null ? chatMessageDto.getIsDeleted() : false);
		chatMessage.setCreatedAt(chatMessageDto.getCreatedAt() != null ? chatMessageDto.getCreatedAt() : null);
		chatMessage.setUpdatedAt(chatMessageDto.getUpdatedAt() != null ? chatMessageDto.getUpdatedAt() : null);
		return mapperService.mapToChatMessageDto(chatMessageRepository.save(chatMessage));
	}

	@Override
	public ResponseEntity<GenericResponse> getAllUserMessages(String userId) {
		log.info("ChatMessageServiceImpl, getAllUserMessages");

		List<ChatMessageDto> chatMessageDtos = chatMessageRepository.findAllByReceiverId(userId).stream()
				.map(mapperService::mapToChatMessageDto).toList();
		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Get all user messages successfully").result(chatMessageDtos).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getMessageFromUser(String userId, String friendId, Integer page,
			Integer size) {
		log.info("ChatMessageServiceImpl, getMessageFromUser");

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		Page<ChatMessage> chatMessageDtos = chatMessageRepository.findAllByReceiverIdOrSenderId(userId, friendId,
				pageable);

		List<ChatMessageDto> chatMessageDtoList = chatMessageDtos.stream().map(mapperService::mapToChatMessageDto)
				.toList();

		Map<String, Object> result = new HashMap<>();
		result.put("messages", chatMessageDtoList);
		result.put("totalPages", chatMessageDtos.getTotalPages());
		result.put("totalElements", chatMessageDtos.getTotalElements());
		result.put("currentPage", chatMessageDtos.getNumber());
		result.put("currentElements", chatMessageDtos.getNumberOfElements());

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Get message from user successfully").result(result).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getMessageFromGroup(String userId, String groupId, Integer page,
			Integer size) {
		log.info("ChatMessageServiceImpl, getMessageFromGroup");

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		Page<ChatMessage> chatMessageDtos = chatMessageRepository.findAllByChatRoomId(groupId, pageable);

		List<ChatMessageDto> chatMessageDtoList = chatMessageDtos.stream().map(mapperService::mapToChatMessageDto)
				.toList();

		Map<String, Object> result = new HashMap<>();
		result.put("messages", chatMessageDtoList);
		result.put("totalPages", chatMessageDtos.getTotalPages());
		result.put("totalElements", chatMessageDtos.getTotalElements());
		result.put("currentPage", chatMessageDtos.getNumber());
		result.put("currentElements", chatMessageDtos.getNumberOfElements());

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Get message from group successfully").result(result).build());
	}
}
