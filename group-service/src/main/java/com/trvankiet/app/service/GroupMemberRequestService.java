package com.trvankiet.app.service;

import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface GroupMemberRequestService {
    ResponseEntity<GenericResponse> getAllGroupMemberRequests(String userId, String groupId, Optional<String> stateCode);
}
