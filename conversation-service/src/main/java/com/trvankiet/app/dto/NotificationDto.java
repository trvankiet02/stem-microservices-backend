package com.trvankiet.app.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDto {
	
	private String id;
	private String receiverId;
	private String avatarUrl;
	
	private String postId;
	private String commentId;
	private String groupId;
	private String chatRoomId;
	
	private String content;
	private String refUrl;
	private Boolean isReaded;
	
	private Date createdAt;
	private Date updatedAt;

}
