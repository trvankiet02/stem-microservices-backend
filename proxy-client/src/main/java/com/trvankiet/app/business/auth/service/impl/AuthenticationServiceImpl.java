package com.trvankiet.app.business.auth.service.impl;

import com.trvankiet.app.business.auth.model.request.LoginRequest;
import com.trvankiet.app.business.auth.model.request.RegisterRequest;
import com.trvankiet.app.business.auth.service.AuthenticationService;
import com.trvankiet.app.business.user.model.CredentialDto;
import com.trvankiet.app.business.user.model.UserDetailsImpl;
import com.trvankiet.app.business.user.service.CredentialClientService;
import com.trvankiet.app.constant.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final CredentialClientService credentialClientService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<GenericResponse> register(RegisterRequest registerRequest) {
        log.info("AuthenticationServiceImpl, ResponseEntity<GenericResponse>, register");
        ResponseEntity<GenericResponse> response = credentialClientService.register(registerRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GenericResponse.builder()
                        .success(true)
                        .message("User registered successfully")
                        .result(Objects.requireNonNull(response.getBody()).getResult())
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @Override
    public ResponseEntity<GenericResponse> login(LoginRequest loginRequest) {
        log.info("AuthenticationServiceImpl, ResponseEntity<GenericResponse>, login");
        CredentialDto credentialDto = credentialClientService.findByUsername(loginRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentialDto.getUsername(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return credentialClientService.login(loginRequest);
    }

    @Override
    public ResponseEntity<GenericResponse> verify(String token) {
        log.info("AuthenticationServiceImpl, ResponseEntity<GenericResponse>, verify");
        return credentialClientService.verify(token);
    }
}
