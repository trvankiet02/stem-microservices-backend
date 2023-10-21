package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.GroupCreateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface GroupService {
    ResponseEntity<GenericResponse> createGroup(String userId, GroupCreateRequest groupCreateRequest);

    ResponseEntity<GenericResponse> getAllGroup();

    ResponseEntity<GenericResponse> getGroupById(String userId, String groupId);

    ResponseEntity<GenericResponse> getGroupsByUserId(String userId);
}
