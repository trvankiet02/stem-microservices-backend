package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.NotificationDto;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.entity.Notification;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.ChatUserRepository;
import com.trvankiet.app.repository.NotificationRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
	private final NotificationRepository notificationRepository;
	private final ChatUserRepository chatUserRepository;
	private final MapperService mapperService;

	@Override
	public NotificationDto saveNotificationDto(NotificationDto notificationDto) {
		log.info("NotificationServiceImpl, saveNotificationDto");
		Notification notification = Notification.builder()
				.id(notificationDto.getId() == null ? UUID.randomUUID().toString() : notificationDto.getId())
				.content(notificationDto.getContent()).createdAt(notificationDto.getCreatedAt())
				.isReaded(notificationDto.getIsReaded() != null ? notificationDto.getIsReaded() : false)
				.refUrl(notificationDto.getRefUrl()).avatarUrl(notificationDto.getAvatarUrl()).build();

		Optional<ChatUser> optionalChatUser = chatUserRepository.findById(notificationDto.getReceiverId());
		optionalChatUser.ifPresent(notification::setReceiver);

		if (notificationDto.getPostId() != null) {
			notification.setPostId(notificationDto.getPostId());
		}

		if (notificationDto.getCommentId() != null) {
			notification.setCommentId(notificationDto.getCommentId());
		}

		if (notificationDto.getGroupId() != null) {
			notification.setGroupId(notificationDto.getGroupId());
		}

		if (notificationDto.getChatRoomId() != null) {
			notification.setChatRoomId(notificationDto.getChatRoomId());
		}

		return mapperService.mapToNotificationDto(notificationRepository.save(notification));
	}

	@Override
	public ResponseEntity<GenericResponse> getLastNotifications(String userId, Integer page, Integer size) {
		log.info("NotificationServiceImpl, getLastNotifications");

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		Page<Notification> notificationPage = notificationRepository.findAllByReceiverId(userId, pageable);

		List<NotificationDto> notificationDtos = notificationPage.getContent().stream()
				.map(mapperService::mapToNotificationDto).toList();
		Map<String, Object> result = new HashMap<>();

		result.put("currentPage", notificationPage.getNumber());
		result.put("totalElements", notificationPage.getTotalElements());
		result.put("totalPages", notificationPage.getTotalPages());
		result.put("currentElements", notificationPage.getNumberOfElements());
		result.put("notifications", notificationDtos);

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Get last notifications successfully").result(result).build());

	}

	@Override
	public ResponseEntity<GenericResponse> maskAsReaded(String userId, String id) {
		log.info("NotificationServiceImpl, maskAsReaded");

		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Notification not found"));

		if (!notification.getReceiver().getId().equals(userId)) {
			throw new ForbiddenException("You are not allowed to do this");
		}

		notification.setIsReaded(true);

		return ResponseEntity
				.ok(GenericResponse.builder().success(true).statusCode(200).message("Mask as readed successfully")
						.result(mapperService.mapToNotificationDto(notificationRepository.save(notification))).build());
	}

	@Override
	public ResponseEntity<GenericResponse> maskAllAsReaded(String userId) {
		log.info("NotificationServiceImpl, maskAllAsReaded");

		List<Notification> notifications = notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(userId);

		Date now = new Date();
		List<NotificationDto> notificationDtos = notifications.stream().map(notification -> {
			notification.setIsReaded(true);
			notification.setUpdatedAt(now);
			return mapperService.mapToNotificationDto(notificationRepository.save(notification));
		}).toList();

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Mask all as readed successfully").result(notificationDtos).build());
	}

	@Override
	public ResponseEntity<GenericResponse> deleteNotification(String userId, String id) {
		log.info("NotificationServiceImpl, deleteNotification");

		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Notification not found"));

		if (!notification.getReceiver().getId().equals(userId)) {
			throw new ForbiddenException("You are not allowed to do this");
		}

		notificationRepository.delete(notification);

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Delete notification successfully").result(null).build());
	}

	@Override
	public ResponseEntity<GenericResponse> deleteAllNotifications(String userId) {
		log.info("NotificationServiceImpl, deleteAllNotifications");

		List<Notification> notifications = notificationRepository.findAllByReceiverId(userId);

		notificationRepository.deleteAll(notifications);

		return ResponseEntity.ok(GenericResponse.builder().success(true).statusCode(200)
				.message("Delete all notifications successfully").result(null).build());
	}
}
