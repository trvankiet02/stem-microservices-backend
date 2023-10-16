package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.GroupCreateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface GroupService {
    ResponseEntity<GenericResponse> createGroup(String userId, GroupCreateRequest groupCreateRequest);
}
