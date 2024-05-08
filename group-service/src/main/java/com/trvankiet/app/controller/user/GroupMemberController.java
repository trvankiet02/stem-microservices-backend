package com.trvankiet.app.controller.user;

import com.trvankiet.app.dto.request.*;
import com.trvankiet.app.dto.response.GenericResponse;
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

    @PostMapping("/invite")
    public ResponseEntity<GenericResponse> inviteGroupMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                          @RequestBody InviteGroupMemberRequest groupMemberRequest) {
        log.info("AdminGroupMemberController, ResponseEntity<GenericResponse> addGroupMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.inivteGroupMember(userId, groupMemberRequest);
    }
    @PostMapping("/request")
    public ResponseEntity<GenericResponse> requestGroupMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                          @RequestBody ToGroupMemberRequest toGroupMemberRequest) {
        log.info("AdminGroupMemberController, ResponseEntity<GenericResponse> addGroupMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.requestGroupMember(userId, toGroupMemberRequest.getGroupId());
    }
    @PostMapping("/add")
    public ResponseEntity<GenericResponse> addGroupMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                          @RequestBody AddGroupMemberRequest addGroupMemberRequest) {
        log.info("AdminGroupMemberController, ResponseEntity<GenericResponse> addGroupMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.addGroupMember(userId, addGroupMemberRequest);
    }

    @PutMapping("/{groupMemberId}/role")
    public ResponseEntity<GenericResponse> changeRole(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @PathVariable("groupMemberId") String groupMemberId,
                                                      @RequestBody MemberRoleRequest memberRoleRequest) {
        log.info("AdminGroupMemberController, ResponseEntity<GenericResponse> changeRole");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.changeRole(userId, groupMemberId, memberRoleRequest.getRoleCode());
    }

    @GetMapping("/{groupId}/{userId}/role")
    public String getGroupMember(@PathVariable("userId") String userId, @PathVariable("groupId") String groupId) {
        log.info("AdminGroupMemberController, ResponseEntity<GenericResponse> getGroupMember");
        return groupMemberService.getGroupMemberRoleByGroupIdAndUserId(groupId, userId);
    }

    @DeleteMapping("/{groupMemberId}")
    public ResponseEntity<GenericResponse> deleteGroupMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                             @PathVariable("groupMemberId") String groupMemberId) {
        log.info("AdminGroupMemberController, ResponseEntity<GenericResponse> deleteGroupMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.deleteGroupMember(userId, groupMemberId);
    }
    @GetMapping
    public ResponseEntity<GenericResponse> getGroupMemberByGroupId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                           @RequestParam("groupId") String groupId) {
        log.info("AdminGroupMemberController, ResponseEntity<GenericResponse> getGroupMembers");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.getGroupMemberByGroupId(userId, groupId);
    }
    // blockMember
    @PutMapping("/lock")
    public ResponseEntity<GenericResponse> lockGroupMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                            @RequestBody LockMemberRequest lockMemberRequest) {
        log.info("AdminGroupMemberController, ResponseEntity<GenericResponse> lockGroupMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.lockGroupMember(userId, lockMemberRequest);
    }

    @PutMapping("/unlock")
    public ResponseEntity<GenericResponse> unlockGroupMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                              @RequestBody UnlockMemberRequest unlockMemberRequest) {
        log.info("AdminGroupMemberController, ResponseEntity<GenericResponse> unlockGroupMember");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.unlockGroupMember(userId, unlockMemberRequest);
    }

}
