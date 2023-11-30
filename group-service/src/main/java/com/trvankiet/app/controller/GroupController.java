package com.trvankiet.app.controller;

import com.trvankiet.app.dto.GroupDto;
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
import java.util.List;
import java.util.Optional;

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
        log.info("GroupController, createGroup");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.createGroup(userId, groupCreateRequest);
    }

    @GetMapping
    public ResponseEntity<GenericResponse> getAllGroup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("GroupController, getAllGroup");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.getGroupsByUserId(userId);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GenericResponse> getGroupById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId) {
        log.info("GroupController, getGroupById");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.getGroupById(userId, groupId);
    }

    @GetMapping("/validate-user-in-group")
    public ResponseEntity<GenericResponse> validateUserInGroup(@RequestParam("userId") String userId
            , @RequestParam("groupId") String groupId) {
        log.info("GroupController, validateUserInGroup");
        return groupService.valiadateUserInGroup(userId, groupId);
    }

    @GetMapping("/get-group-by-user")
    public ResponseEntity<List<String>> getGroupByUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("GroupController, getGroupByUserId");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.getGroupByUserId(userId);
    }

    @PutMapping("/{groupId}/config")
    public ResponseEntity<GenericResponse> updateGroupConfig(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId
            , @RequestBody GroupConfigRequest groupConfigRequest) {
        log.info("GroupController, updateGroupConfig");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.updateGroupConfig(userId, groupId, groupConfigRequest);
    }

    @PutMapping("/{groupId}/updateDetail")
    public ResponseEntity<GenericResponse> updateGroupDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId
            , @RequestBody UpdateDetailRequest updateDetailRequest) {
        log.info("GroupController, updateGroupDetail");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.updateGroupDetail(userId, groupId, updateDetailRequest);
    }

    @PutMapping(value = "/{groupId}/updateAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> updateGroupAvatar(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId
            , @RequestPart("mediaFile")MultipartFile avatar) throws IOException {
        log.info("GroupController, updateGroupAvatar");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.updateGroupAvatar(userId, groupId, avatar);
    }

    @PutMapping(value = "/{groupId}/updateCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> updateGroupCover(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId
            , @RequestPart("mediaFile")MultipartFile cover) throws IOException {
        log.info("GroupController, updateGroupCover");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.updateGroupCover(userId, groupId, cover);
    }

    @DeleteMapping(value = "/{groupId}")
    public ResponseEntity<GenericResponse> deleteGroup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId) {
        log.info("GroupController, deleteGroup");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.deleteGroup(userId, groupId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GroupDto>> searchGroup(@RequestParam("query") Optional<String> query
                                                , @RequestParam("type") Optional<String> type
                                                , @RequestParam("accessibility") Optional<String> accessibility
                                                , @RequestParam("grade") Optional<Integer> grade
                                                , @RequestParam("subject") Optional<String> subject) {
        log.info("GroupController, searchGroup");
        return groupService.searchGroup(query, type, accessibility, grade, subject);
    }

}
