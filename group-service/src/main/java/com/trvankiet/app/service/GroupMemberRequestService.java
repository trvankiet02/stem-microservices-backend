package com.trvankiet.app.service;

import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface GroupMemberRequestService {
    ResponseEntity<GenericResponse> getAllGroupMemberRequests(String userId, String groupId);
}
