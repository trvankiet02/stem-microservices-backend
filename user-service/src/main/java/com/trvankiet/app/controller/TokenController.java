package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.EmailRequest;
import com.trvankiet.app.dto.request.TokenRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tokens")
@Slf4j
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/refresh-access-token")
    public ResponseEntity<GenericResponse> refreshAccessToken(@RequestBody @Valid TokenRequest tokenRequest) {
        log.info("TokenController, Response<GenericResponse>, refreshAccessToken");
        return tokenService.refreshAccessToken(tokenRequest);
    }

    @PostMapping("reset-password")
    public ResponseEntity<GenericResponse> resetPassword(@RequestBody final EmailRequest emailRequest) {
        log.info("TokenController, Response<GenericResponse>, resetPassword");
        return tokenService.resetPassword(emailRequest);
    }
}
