package com.trvankiet.app.controller.admin;

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
                                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("AdminGroupController, getAllGroupsForAdmin");
        return groupService.getAllGroupsForAdmin(token, page, size);
    }

    @GetMapping("/get-all-classes")
    public ResponseEntity<GenericResponse> getAllClassesForAdmin(@RequestHeader("Authorization") String token,
                                                                 @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                 @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("AdminGroupController, getAllClassesForAdmin");
        return groupService.getAllClassesForAdmin(token, page, size);
    }
}
