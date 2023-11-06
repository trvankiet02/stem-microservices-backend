package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.*;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.CredentialService;
import com.trvankiet.app.service.TokenService;
import com.trvankiet.app.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CredentialService credentialService;
    private final TokenService tokenService;

    @PostMapping("/register?role=teacher")
    public ResponseEntity<GenericResponse> registerForTeacher(@RequestBody final TeacherRegisterRequest teacherRegisterRequest) {
        log.info("CredentialController Post, ResponseEntity<CredentialDto>, register");
        return credentialService.registerForTeacher(teacherRegisterRequest);
    }

    @PostMapping("/register?role=parent")
    public ResponseEntity<GenericResponse> registerForParent(@RequestBody final ParentRegisterRequest parentRegisterRequest) {
        log.info("CredentialController Post, ResponseEntity<CredentialDto>, register");
        return credentialService.registerForParent(parentRegisterRequest);
    }

    @PostMapping("/register?role=student")
    public ResponseEntity<GenericResponse> registerForStudent(@RequestBody final StudentAndParentRequest studentAndParentRequest) {
        log.info("CredentialController Post, ResponseEntity<CredentialDto>, register");
        return credentialService.registerForStudent(studentAndParentRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<GenericResponse> login(@RequestBody final LoginRequest loginRequest) {
        log.info("CredentialController Post, ResponseEntity<CredentialDto>, login");
        return credentialService.login(loginRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<GenericResponse> logout(@RequestHeader("Authorization") final String authorizationHeader) {
        log.info("CredentialController Post, ResponseEntity<CredentialDto>, logout");
        return credentialService.logout(authorizationHeader);
    }

    @GetMapping("/verify")
    public ResponseEntity<GenericResponse> verify(@RequestParam final @Valid @NotBlank String token) {
        log.info("CredentialController Get, ResponseEntity<CredentialDto>, verify");
        return credentialService.verify(token);
    }

    @PostMapping("/verify")
    public ResponseEntity<GenericResponse> initUserInfo(@RequestParam final String token,
                                                        @RequestBody @Valid final UserInfoRequest userInfoRequest) {
        log.info("UserController Post, ResponseEntity<GenericResponse>, initUserInfo");
        return userService.initCredentialInfo(token, userInfoRequest);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<GenericResponse> verifyResetPassword(@RequestParam @Valid @NotBlank final String token) {
        log.info("CredentialController Get, ResponseEntity<CredentialDto>, resetPassword");
        return credentialService.verifyResetPassword(token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponse> resetPassword(@RequestParam final String token
            , @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        log.info("CredentialController Post, ResponseEntity<CredentialDto>, resetPassword");
        return credentialService.resetPassword(token, resetPasswordRequest);
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<GenericResponse> refreshAccessToken(@RequestBody @Valid TokenRequest tokenRequest) {
        log.info("TokenController, Response<GenericResponse>, refreshAccessToken");
        return tokenService.refreshAccessToken(tokenRequest);
    }
}
