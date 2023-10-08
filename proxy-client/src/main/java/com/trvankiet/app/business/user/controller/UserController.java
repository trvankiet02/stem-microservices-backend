package com.trvankiet.app.business.user.controller;

import com.trvankiet.app.business.user.model.request.UserInfoRequest;
import com.trvankiet.app.business.user.service.client.UserClientService;
import com.trvankiet.app.constant.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserClientService userClientService;

    @PostMapping("/verify")
    public ResponseEntity<GenericResponse> initUserInfo(@RequestParam final String token,
                                                         @Valid @RequestBody final UserInfoRequest userInfoRequest) {
        log.info("UserController Post, ResponseEntity<GenericResponse>, initUserInfo");
        return userClientService.initUserInfo(token, userInfoRequest);
    }
}
