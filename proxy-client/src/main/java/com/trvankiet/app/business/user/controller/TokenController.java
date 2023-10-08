package com.trvankiet.app.business.user.controller;

import com.trvankiet.app.business.user.model.request.EmailRequest;
import com.trvankiet.app.business.user.service.TokenService;
import com.trvankiet.app.constant.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tokens")
@Slf4j
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponse> resetPassword(@RequestBody final EmailRequest emailRequest) {
        log.info("TokenController, Response<GenericResponse>, resetPassword");
        return tokenService.resetPassword(emailRequest);
    }
}
