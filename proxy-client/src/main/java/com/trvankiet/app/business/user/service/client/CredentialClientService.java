package com.trvankiet.app.business.user.service.client;

import com.trvankiet.app.business.auth.model.request.LoginRequest;
import com.trvankiet.app.business.auth.model.request.RegisterRequest;
import com.trvankiet.app.business.auth.model.request.ResetPasswordRequest;
import com.trvankiet.app.business.user.model.CredentialDto;
import com.trvankiet.app.constant.GenericResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", contextId = "credentialClientService", path = "/api/v1/credentials")
public interface CredentialClientService {

    @PostMapping("/register")
    ResponseEntity<GenericResponse> register(@Valid RegisterRequest registerRequest);

    @PostMapping("/login")
    ResponseEntity<GenericResponse> login(@Valid LoginRequest loginRequest);

    @GetMapping("/")
    CredentialDto findByUsername(@RequestParam String username);

    @GetMapping("/{id}")
    CredentialDto findById(@PathVariable String id);

    @GetMapping("/verify")
    ResponseEntity<GenericResponse> verify(@RequestParam String token);

    @GetMapping("/reset-password")
    ResponseEntity<GenericResponse> verifyResetPassword(@RequestParam String token);

    @PostMapping("/reset-password")
    ResponseEntity<GenericResponse> resetPassword(@RequestParam String token,@Valid @RequestBody ResetPasswordRequest resetPasswordRequest);
}
