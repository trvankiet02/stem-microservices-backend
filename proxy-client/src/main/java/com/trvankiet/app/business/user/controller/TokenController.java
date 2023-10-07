package com.trvankiet.app.business.user.controller;

import com.trvankiet.app.business.user.service.TokenService;
import com.trvankiet.app.constant.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tokens")
@Slf4j
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    public ResponseEntity<GenericResponse> resetPassword(@RequestParam final String email) {
        log.info("TokenController, Response<GenericResponse>, resetPassword");
        return tokenService.resetPassword(email);
    }
}
