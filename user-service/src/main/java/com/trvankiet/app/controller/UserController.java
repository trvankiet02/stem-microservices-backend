package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.UserInfoRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"/api/v1/users"})
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/verify")
    public ResponseEntity<GenericResponse> initUserInfo(@RequestParam final String token,
        @RequestBody @Valid final UserInfoRequest userInfoRequest) {
        log.info("UserController Post, ResponseEntity<GenericResponse>, initUserInfo");
        return userService.initCredentialInfo(token, userInfoRequest);
    }

}
