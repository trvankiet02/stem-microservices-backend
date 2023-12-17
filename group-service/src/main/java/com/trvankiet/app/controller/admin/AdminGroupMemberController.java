package com.trvankiet.app.controller.admin;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/group-members/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminGroupMemberController {

    private final GroupMemberService groupMemberService;

    @GetMapping("/get-group-members")
    public ResponseEntity<GenericResponse> getAllGroupMembers(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                              @RequestHeader("groupId") String groupId,
                                                              @RequestHeader(value = "page", defaultValue = "0") Integer page,
                                                              @RequestHeader(value = "size", defaultValue = "10") Integer size) {
        log.info("AdminGroupMemberController, getAllGroupMembers");
        return groupMemberService.getAllGroupMembers(authorizationHeader, groupId, page, size);
    }
}
