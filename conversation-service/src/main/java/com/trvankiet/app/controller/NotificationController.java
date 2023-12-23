package com.trvankiet.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

	private final NotificationService notificationService;
	private final JwtService jwtService;

	@GetMapping("/get-last-notifications")
	public ResponseEntity<GenericResponse> getLastNotifications(
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "10") Integer size) {
		log.info("NotificationController, getLastNotifications");
		String accessToken = authorizationHeader.substring(7);
		String userId = jwtService.extractUserId(accessToken);

		return notificationService.getLastNotifications(userId, page, size);
	}

	@PostMapping("/mask-as-readed/{id}")
	public ResponseEntity<GenericResponse> maskAsReaded(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("id") String id) {
		String accessToken = authorizationHeader.substring(7);
		String userId = jwtService.extractUserId(accessToken);
		log.info("NotificationController, maskAsReaded");
		return notificationService.maskAsReaded(userId, id);
	}
	
	@PostMapping("/mask-all-as-readed")
	public ResponseEntity<GenericResponse> maskAllAsReaded(@RequestHeader("Authorization") String authorizationHeader) {
		String accessToken = authorizationHeader.substring(7);
		String userId = jwtService.extractUserId(accessToken);
		log.info("NotificationController, maskAllAsReaded");
		return notificationService.maskAllAsReaded(userId);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<GenericResponse> deleteNotification(
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable("id") String id) {
		String accessToken = authorizationHeader.substring(7);
		String userId = jwtService.extractUserId(accessToken);
		log.info("NotificationController, deleteNotification");
		return notificationService.deleteNotification(userId, id);
	}
	
	@DeleteMapping("/delete-all")
	public ResponseEntity<GenericResponse> deleteAllNotifications(
			@RequestHeader("Authorization") String authorizationHeader) {
		String accessToken = authorizationHeader.substring(7);
		String userId = jwtService.extractUserId(accessToken);
		log.info("NotificationController, deleteAllNotifications");
		return notificationService.deleteAllNotifications(userId);
	}

}
