package com.trvankiet.app.service;

import org.springframework.http.ResponseEntity;

import com.trvankiet.app.dto.NotificationDto;
import com.trvankiet.app.dto.response.GenericResponse;

public interface NotificationService {
	
	NotificationDto saveNotificationDto(NotificationDto notificationDto);

	ResponseEntity<GenericResponse> getLastNotifications(String userId, Integer page, Integer size);

	ResponseEntity<GenericResponse> maskAsReaded(String userId, String id);

	ResponseEntity<GenericResponse> maskAllAsReaded(String userId);

	ResponseEntity<GenericResponse> deleteNotification(String userId, String id);

	ResponseEntity<GenericResponse> deleteAllNotifications(String userId);
	
	
}
