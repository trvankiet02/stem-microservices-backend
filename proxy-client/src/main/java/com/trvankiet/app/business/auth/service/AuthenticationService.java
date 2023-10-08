package com.trvankiet.app.business.auth.service;

import com.trvankiet.app.business.auth.model.request.LoginRequest;
import com.trvankiet.app.business.auth.model.request.RegisterRequest;
import com.trvankiet.app.business.auth.model.request.ResetPasswordRequest;
import com.trvankiet.app.business.user.model.CredentialDto;
import com.trvankiet.app.business.user.model.request.TokenRequest;
import com.trvankiet.app.constant.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<GenericResponse> register(RegisterRequest registerRequest);

    ResponseEntity<GenericResponse> login(LoginRequest loginRequest);

    ResponseEntity<GenericResponse> verify(String token);

    ResponseEntity<GenericResponse> refreshAccessToken(TokenRequest tokenRequest);

    ResponseEntity<GenericResponse> verifyResetPassword(String token);

    ResponseEntity<GenericResponse> resetPassword(String token, ResetPasswordRequest resetPasswordRequest);
}
