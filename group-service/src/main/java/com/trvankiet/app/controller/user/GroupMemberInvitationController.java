package com.trvankiet.app.controller.user;

import com.trvankiet.app.dto.request.StateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.GroupMemberInvitationService;
import com.trvankiet.app.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/group-member-invitations")
@Slf4j
@RequiredArgsConstructor
public class GroupMemberInvitationController {
    private final JwtService jwtService;
    private final GroupMemberInvitationService groupMemberInvitationService;
    private final GroupMemberService groupMemberService;
    @GetMapping
    public ResponseEntity<GenericResponse> getAllGroupMemberInvitations(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("GroupMemberInvitationController, ResponseEntity<GenericResponse> getAllGroupMemberInvitations");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberInvitationService.getAllGroupMemberInvitations(userId);
    }
    @PostMapping("/{gmiId}/response")
    public ResponseEntity<GenericResponse> responseGroupMemberInvitation(@RequestHeader("Authorization") String authorizationHeader,
                                                                         @PathVariable("gmiId") String groupMemberInvitationId,
                                                                         @RequestBody StateRequest inviteResponseGroupMember) {
        log.info("AdminGroupMemberController, ResponseEntity<GenericResponse> responseGroupMemberInvitation");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.responseGroupMemberInvitation(userId, groupMemberInvitationId, inviteResponseGroupMember);
    }
}
