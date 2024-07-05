package com.trvankiet.app.controller.admin;

import com.trvankiet.app.dto.request.AddGroupMemberRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/group-members/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminGroupMemberController {

    private final GroupMemberService groupMemberService;

    @GetMapping("/get-group-members")
    public ResponseEntity<GenericResponse> getAllGroupMembers(@RequestHeader("Authorization") String authorizationHeader,
                                                              @RequestParam(value = "groupId", required = false) String groupId,
                                                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                              @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("AdminGroupMemberController, getAllGroupMembers");
        return groupMemberService.getAllGroupMembers(authorizationHeader, groupId, page - 1, size);
    }

    @DeleteMapping("/delete-group-member/{groupMemberId}")
    public ResponseEntity<GenericResponse> deleteGroupMember(@RequestHeader("Authorization") String authorizationHeader,
                                                             @PathVariable String groupMemberId) {
        log.info("AdminGroupMemberController, deleteGroupMember");
        return groupMemberService.deleteGroupMemberByAdmin(authorizationHeader, groupMemberId);
    }

    @PostMapping("/add-group-member")
    public ResponseEntity<GenericResponse> addGroupMember(@RequestHeader("Authorization") String authorizationHeader,
                                                          @RequestBody AddGroupMemberRequest addGroupMemberRequest) {
        log.info("AdminGroupMemberController, addGroupMember");
        return groupMemberService.addGroupMemberByAdmin(authorizationHeader, addGroupMemberRequest);
    }

}
