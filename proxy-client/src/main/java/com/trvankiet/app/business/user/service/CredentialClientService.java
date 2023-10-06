package com.trvankiet.app.business.user.service;

import com.trvankiet.app.business.auth.model.request.LoginRequest;
import com.trvankiet.app.business.auth.model.request.RegisterRequest;
import com.trvankiet.app.business.user.model.CredentialDto;
import com.trvankiet.app.constant.GenericResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
