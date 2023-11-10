package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.*;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.GroupMemberRequest;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/group-members")
@Slf4j
@RequiredArgsConstructor
public class GroupMemberController {
    private final GroupMemberService groupMemberService;
    private final JwtService jwtService;

    @GetMapping
    public String getGroupMembers() {
        return "Hello from GroupMemberController";
    }

    @PostMapping("/invite")
    public ResponseEntity<GenericResponse> inviteGroupMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                          @RequestBody InviteGroupMemberRequest groupMemberRequest) {
        log.info("GroupMemberController, ResponseEntity<GenericResponse> addGroupMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.inivteGroupMember(userId, groupMemberRequest);
    }
    @PostMapping("/request")
    public ResponseEntity<GenericResponse> requestGroupMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                          @RequestBody ToGroupMemberRequest toGroupMemberRequest) {
        log.info("GroupMemberController, ResponseEntity<GenericResponse> addGroupMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.requestGroupMember(userId, toGroupMemberRequest.getGroupId());
    }
    @PostMapping("/add")
    public ResponseEntity<GenericResponse> addGroupMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                          @RequestBody AddGroupMemberRequest addGroupMemberRequest) {
        log.info("GroupMemberController, ResponseEntity<GenericResponse> addGroupMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.addGroupMember(userId, addGroupMemberRequest);
    }

    @PutMapping("/{groupMemberId}/role")
    public ResponseEntity<GenericResponse> changeRole(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @PathVariable("groupMemberId") String groupMemberId,
                                                      @RequestBody MemberRoleRequest memberRoleRequest) {
        log.info("GroupMemberController, ResponseEntity<GenericResponse> changeRole");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.changeRole(userId, groupMemberId, memberRoleRequest.getRole());
    }

    @DeleteMapping("/{groupMemberId}")
    public ResponseEntity<GenericResponse> deleteGroupMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                             @PathVariable("groupMemberId") String groupMemberId) {
        log.info("GroupMemberController, ResponseEntity<GenericResponse> deleteGroupMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.deleteGroupMember(userId, groupMemberId);
    }

}
