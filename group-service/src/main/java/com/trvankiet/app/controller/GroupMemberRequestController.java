package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.StateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.GroupMemberRequestService;
import com.trvankiet.app.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/group-member-requests")
@Slf4j
@RequiredArgsConstructor
public class GroupMemberRequestController {
    private final JwtService jwtService;
    private final GroupMemberRequestService groupMemberRequestService;
    private final GroupMemberService groupMemberService;

    @GetMapping
    public ResponseEntity<GenericResponse> getAllGroupMemberRequests(@RequestHeader("Authorization") String authorizationHeader,
                                                                     @RequestParam(value = "groupId") String groupId) {
        log.info("GroupMemberRequestController, ResponseEntity<GenericResponse> getAllGroupMemberRequests");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberRequestService.getAllGroupMemberRequests(userId, groupId);
    }

    @PostMapping("/{gmrId}/response")
    public ResponseEntity<GenericResponse> responseGroupMemberRequest(@RequestHeader("Authorization") String authorizationHeader,
                                                                      @PathVariable("gmrId") String groupMemberRequestId,
                                                                      @RequestBody StateRequest stateRequest) {
        log.info("GroupMemberController, ResponseEntity<GenericResponse> responseGroupMemberRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.responseGroupMemberRequest(userId, groupMemberRequestId, stateRequest);
    }
}
