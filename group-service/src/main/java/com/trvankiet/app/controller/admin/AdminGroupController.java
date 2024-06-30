package com.trvankiet.app.controller.admin;

import com.trvankiet.app.dto.request.AdminCreateClassRequest;
import com.trvankiet.app.dto.request.AdminCreateGroupRequest;
import com.trvankiet.app.dto.request.AdminUpdateClassRequest;
import com.trvankiet.app.dto.request.AdminUpdateGroupRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminGroupController {

    private final GroupService groupService;

    @GetMapping("/get-all-groups")
    public ResponseEntity<GenericResponse> getAllGroupsForAdmin(@RequestHeader("Authorization") String token,
                                                                @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                @RequestParam(value = "search", required = false) String search) {
        log.info("AdminGroupController, getAllGroupsForAdmin");
        return groupService.getAllGroupsForAdmin(token, page - 1, size, search);
    }

    @GetMapping("/get-all-classes")
    public ResponseEntity<GenericResponse> getAllClassesForAdmin(@RequestHeader("Authorization") String token,
                                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                 @RequestParam(value = "search", required = false) String search) {
        log.info("AdminGroupController, getAllClassesForAdmin");
        return groupService.getAllClassesForAdmin(token, page - 1, size, search);
    }

    @PutMapping("/update-group")
    public ResponseEntity<GenericResponse> updateGroup(@RequestHeader("Authorization") String token,
                                                       @RequestBody AdminUpdateGroupRequest adminUpdateGroupRequest) {
        log.info("AdminGroupController, updateGroup");
        return groupService.adminUpdateGroup(token, adminUpdateGroupRequest);
    }

    @PutMapping("/update-class")
    public ResponseEntity<GenericResponse> updateClass(@RequestHeader("Authorization") String token,
                                                       @RequestBody AdminUpdateClassRequest adminUpdateClassRequest) {
        log.info("AdminGroupController, updateClass");
        return groupService.adminUpdateClass(token, adminUpdateClassRequest);
    }

    @DeleteMapping("/delete-group/{groupId}")
    public ResponseEntity<GenericResponse> deleteGroup(@RequestHeader("Authorization") String token,
                                                       @PathVariable String groupId) {
        log.info("AdminGroupController, deleteGroup");
        return groupService.adminDeleteGroup(token, groupId);
    }

    @PostMapping("/create-group")
    public ResponseEntity<GenericResponse> createGroup(@RequestHeader("Authorization") String token,
                                                       @RequestBody AdminCreateGroupRequest adminCreateGroupRequest) {
        log.info("AdminGroupController, createGroup");
        return groupService.adminCreateGroup(token, adminCreateGroupRequest);
    }

    @PostMapping("/create-class")
    public ResponseEntity<GenericResponse> createClass(@RequestHeader("Authorization") String token,
                                                       @RequestBody AdminCreateClassRequest adminCreateClassRequest) {
        log.info("AdminGroupController, createClass");
        return groupService.adminCreateClass(token, adminCreateClassRequest);
    }
}
