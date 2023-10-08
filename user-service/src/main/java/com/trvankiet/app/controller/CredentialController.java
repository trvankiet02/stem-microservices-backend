package com.trvankiet.app.controller;

import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.request.LoginRequest;
import com.trvankiet.app.dto.request.RegisterRequest;
import com.trvankiet.app.dto.request.ResetPasswordRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.repository.service.CredentialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/credentials")
@Slf4j
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;

    @GetMapping("/")
    public CredentialDto findByUsername(@RequestParam final String username) {
        log.info("CredentialController Get, CredentialDto, findByUsername");
        return credentialService.findByUsernameDto(username);
    }

    @GetMapping("/{id}")
    public CredentialDto findById(@PathVariable final String id) {
        log.info("CredentialController Get, CredentialDto, findById");
        return credentialService.findByIdDto(id);
    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponse> register(@RequestBody final RegisterRequest registerRequest) {
        log.info("CredentialController Post, ResponseEntity<CredentialDto>, register");
        return credentialService.register(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<GenericResponse> login(@RequestBody final LoginRequest loginRequest) {
        log.info("CredentialController Post, ResponseEntity<CredentialDto>, login");
        return credentialService.login(loginRequest);
    }

    @GetMapping("/verify")
    public ResponseEntity<GenericResponse> verify(@RequestParam final String token) {
        log.info("CredentialController Get, ResponseEntity<CredentialDto>, verify");
        return credentialService.verify(token);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<GenericResponse> verifyResetPassword(@RequestParam final String token) {
        log.info("CredentialController Get, ResponseEntity<CredentialDto>, resetPassword");
        return credentialService.verifyResetPassword(token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponse> resetPassword(@RequestParam final String token
            , @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        log.info("CredentialController Post, ResponseEntity<CredentialDto>, resetPassword");
        return credentialService.resetPassword(token, resetPasswordRequest);
    }

}
