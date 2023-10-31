package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.InviteGroupMemberRequest;
import com.trvankiet.app.dto.request.StateRequest;
import com.trvankiet.app.dto.request.ToGroupMemberRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.GroupMemberRequest;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<GenericResponse> inviteGroupMember(@RequestHeader("Authorization") String authorizationHeader,
                                                          @RequestBody InviteGroupMemberRequest groupMemberRequest) {
        log.info("GroupMemberController, ResponseEntity<GenericResponse> addGroupMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.inivteGroupMember(userId, groupMemberRequest);
    }
    @PostMapping("/request")
    public ResponseEntity<GenericResponse> requestGroupMember(@RequestHeader("Authorization") String authorizationHeader,
                                                          @RequestBody ToGroupMemberRequest toGroupMemberRequest) {
        log.info("GroupMemberController, ResponseEntity<GenericResponse> addGroupMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.requestGroupMember(userId, toGroupMemberRequest.getGroupId());
    }

}
