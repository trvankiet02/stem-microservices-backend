package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.GroupConfigRequest;
import com.trvankiet.app.dto.request.GroupCreateRequest;
import com.trvankiet.app.dto.request.UpdateDetailRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.GroupService;
import com.trvankiet.app.service.client.UserClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/groups")
@Slf4j
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<GenericResponse> createGroup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @RequestBody @Valid GroupCreateRequest groupCreateRequest) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.createGroup(userId, groupCreateRequest);
    }

    @GetMapping
    public ResponseEntity<GenericResponse> getAllGroup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.getGroupsByUserId(userId);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GenericResponse> getGroupById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.getGroupById(userId, groupId);
    }

    @GetMapping("/validate-user-in-group")
    public ResponseEntity<GenericResponse> validateUserInGroup(@RequestParam("userId") String userId
            , @RequestParam("groupId") String groupId) {
        return groupService.getGroupById(userId, groupId);
    }

    @PutMapping("/{groupId}/config")
    public ResponseEntity<GenericResponse> updateGroupConfig(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId
            , @RequestBody GroupConfigRequest groupConfigRequest) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.updateGroupConfig(userId, groupId, groupConfigRequest);
    }

    @PutMapping("/{groupId}/updateDetail")
    public ResponseEntity<GenericResponse> updateGroupDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId
            , @RequestBody UpdateDetailRequest updateDetailRequest) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.updateGroupDetail(userId, groupId, updateDetailRequest);
    }

    @PutMapping(value = "/{groupId}/updateAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> updateGroupAvatar(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId
            , @RequestPart("mediaFile")MultipartFile avatar) throws IOException {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.updateGroupAvatar(userId, groupId, avatar);
    }

    @PutMapping(value = "/{groupId}/updateCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> updateGroupCover(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId
            , @RequestPart("mediaFile")MultipartFile cover) throws IOException {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.updateGroupCover(userId, groupId, cover);
    }

    @DeleteMapping(value = "/{groupId}")
    public ResponseEntity<GenericResponse> deleteGroup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.deleteGroup(userId, groupId);
    }

}
