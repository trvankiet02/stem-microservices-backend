package com.trvankiet.app.controller;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.GroupCreateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.GroupService;
import com.trvankiet.app.service.client.UserClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups")
@Slf4j
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final JwtService jwtService;
    private final UserClientService userClientService;

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
    public ResponseEntity<GenericResponse> validateUserInGroup(@RequestParam("userId") String userId,
            @RequestParam("groupId") String groupId) {
        return groupService.getGroupById(userId, groupId);
    }


}
