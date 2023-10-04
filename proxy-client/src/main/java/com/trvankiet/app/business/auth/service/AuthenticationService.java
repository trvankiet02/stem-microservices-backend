package com.trvankiet.app.business.auth.service;

import com.trvankiet.app.business.auth.model.request.LoginRequest;
import com.trvankiet.app.business.auth.model.request.RegisterRequest;
import com.trvankiet.app.business.user.model.CredentialDto;
import com.trvankiet.app.constant.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<GenericResponse> register(RegisterRequest registerRequest);

    ResponseEntity<GenericResponse> login(LoginRequest loginRequest);

    ResponseEntity<GenericResponse> verify(String token);
}
