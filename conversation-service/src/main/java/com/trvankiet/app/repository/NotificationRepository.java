package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Notification;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
	
	Page<Notification> findAllByReceiverId(String receiverId, Pageable pageable);

	List<Notification> findAllByReceiverId(String userId);
	
	List<Notification> findAllByReceiverIdOrderByCreatedAtDesc(String userId);
}
