package com.trvankiet.app.business.auth.service.impl;

import com.trvankiet.app.business.auth.model.request.LoginRequest;
import com.trvankiet.app.business.auth.model.request.RegisterRequest;
import com.trvankiet.app.business.auth.model.request.ResetPasswordRequest;
import com.trvankiet.app.business.auth.service.AuthenticationService;
import com.trvankiet.app.business.user.model.CredentialDto;
import com.trvankiet.app.business.user.model.request.TokenRequest;
import com.trvankiet.app.business.user.service.client.CredentialClientService;
import com.trvankiet.app.business.user.service.client.TokenClientService;
import com.trvankiet.app.constant.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final CredentialClientService credentialClientService;
    private final AuthenticationManager authenticationManager;
    private final TokenClientService tokenClientService;

    @Override
    public ResponseEntity<GenericResponse> register(RegisterRequest registerRequest) {
        log.info("AuthenticationServiceImpl, ResponseEntity<GenericResponse>, register");
        return credentialClientService.register(registerRequest);
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

    @Override
    public ResponseEntity<GenericResponse> refreshAccessToken(TokenRequest tokenRequest) {
        log.info("AuthenticationServiceImpl, ResponseEntity<GenericResponse>, refreshAccessToken");
        return tokenClientService.refreshAccessToken(tokenRequest);
    }

    @Override
    public ResponseEntity<GenericResponse> verifyResetPassword(String token) {
        log.info("AuthenticationServiceImpl, ResponseEntity<GenericResponse>, verifyResetPassword");
        return credentialClientService.verifyResetPassword(token);
    }

    @Override
    public ResponseEntity<GenericResponse> resetPassword(String token, ResetPasswordRequest resetPasswordRequest) {
        log.info("AuthenticationServiceImpl, ResponseEntity<GenericResponse>, resetPassword");
        return credentialClientService.resetPassword(token, resetPasswordRequest);
    }
}
