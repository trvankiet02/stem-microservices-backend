package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.GroupConfigRequest;
import com.trvankiet.app.dto.request.GroupCreateRequest;
import com.trvankiet.app.dto.request.UpdateDetailRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GroupService {
    ResponseEntity<GenericResponse> createGroup(String userId, GroupCreateRequest groupCreateRequest);

    ResponseEntity<GenericResponse> getAllGroup();

    ResponseEntity<GenericResponse> getGroupById(String userId, String groupId);

    ResponseEntity<GenericResponse> getGroupsByUserId(String userId);

    ResponseEntity<GenericResponse> updateGroupConfig(String userId, String groupId, GroupConfigRequest groupConfigRequest);

    ResponseEntity<GenericResponse> updateGroupDetail(String userId, String groupId, UpdateDetailRequest updateDetailRequest);

    ResponseEntity<GenericResponse> updateGroupAvatar(String userId, String groupId, MultipartFile avatar) throws IOException;

    ResponseEntity<GenericResponse> updateGroupCover(String userId, String groupId, MultipartFile cover) throws IOException;

    ResponseEntity<GenericResponse> deleteGroup(String userId, String groupId);
}
