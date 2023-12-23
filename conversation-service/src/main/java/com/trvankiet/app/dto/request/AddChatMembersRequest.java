package com.trvankiet.app.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class AddChatMembersRequest {
	
	private String groupId;
	private List<String> userIds;

}
