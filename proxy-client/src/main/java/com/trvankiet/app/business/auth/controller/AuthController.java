package com.trvankiet.app.business.auth.controller;

import com.trvankiet.app.business.auth.model.request.LoginRequest;
import com.trvankiet.app.business.auth.model.request.RegisterRequest;
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
    public ResponseEntity<GenericResponse> register(@RequestBody @Valid RegisterRequest registerRequest
    , BindingResult bindingResult) {
        log.info("AuthenticationController, ResponseEntity<GenericResponse>, register");

        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(
                    bindingResult.getFieldError()).getDefaultMessage();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message(errorMessage)
                            .result(null)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build(
                    ));
        }
        return authenticationService.register(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<GenericResponse> login(@RequestBody @Valid LoginRequest loginRequest
    , BindingResult bindingResult){
        log.info("AuthenticationController, ResponseEntity<GenericResponse>, login");
        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(
                    bindingResult.getFieldError()).getDefaultMessage();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(
                            false,
                            errorMessage,
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    ));
        }
        return authenticationService.login(loginRequest);
    }

    @GetMapping("/verify")
    public ResponseEntity<GenericResponse> verify(@RequestParam("token") String token){
        log.info("AuthenticationController, ResponseEntity<GenericResponse>, verify");
        return authenticationService.verify(token);
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<GenericResponse> refreshAccessToken(@RequestBody @Valid TokenRequest tokenRequest) {
        return authenticationService.refreshAccessToken(tokenRequest);
    }
}
