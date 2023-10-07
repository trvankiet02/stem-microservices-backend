package com.trvankiet.app.business.auth.controller;

import com.trvankiet.app.business.auth.model.request.LoginRequest;
import com.trvankiet.app.business.auth.model.request.RegisterRequest;
import com.trvankiet.app.business.auth.model.request.ResetPasswordRequest;
import com.trvankiet.app.business.auth.service.AuthenticationService;
import com.trvankiet.app.business.user.model.CredentialDto;
import com.trvankiet.app.business.user.model.request.TokenRequest;
import com.trvankiet.app.constant.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<GenericResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        log.info("AuthenticationController, ResponseEntity<GenericResponse>, register");
        return authenticationService.register(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<GenericResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        log.info("AuthenticationController, ResponseEntity<GenericResponse>, login");
        return authenticationService.login(loginRequest);
    }

    @GetMapping("/verify")
    public ResponseEntity<GenericResponse> verify(@RequestParam("token") String token){
        log.info("AuthenticationController, ResponseEntity<GenericResponse>, verify");
        return authenticationService.verify(token);
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<GenericResponse> refreshAccessToken(@RequestBody @Valid TokenRequest tokenRequest) {
        log.info("AuthenticationController, ResponseEntity<GenericResponse>, refreshAccessToken");
        return authenticationService.refreshAccessToken(tokenRequest);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<GenericResponse> verifyResetPassword(@RequestParam final String token) {
        log.info("AuthenticationController, ResponseEntity<GenericResponse>, getResetPassword");
        return authenticationService.verifyResetPassword(token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponse> resetPassword(@RequestParam final String token
            , @RequestParam final ResetPasswordRequest resetPasswordRequest) {
        log.info("AuthenticationController, ResponseEntity<GenericResponse>, resetPassword");
        return authenticationService.resetPassword(token, resetPasswordRequest);
    }
}
