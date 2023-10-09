package com.trvankiet.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/group-members")
@Slf4j
@RequiredArgsConstructor
public class GroupMemberController {

    @GetMapping
    public String getGroupMembers() {
        return "Hello from GroupMemberController";
    }
}
