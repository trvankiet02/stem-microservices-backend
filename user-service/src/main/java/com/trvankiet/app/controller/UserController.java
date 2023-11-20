package com.trvankiet.app.controller;

import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.ProfileRequest;
import com.trvankiet.app.dto.request.UserInfoRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.User;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.UserService;
import jakarta.validation.Valid;
import jakarta.ws.rs.PUT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = {"/api/v1/users"})
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/credentials")
    public CredentialDto getCredentialDto(@RequestParam String uId) {
        log.info("UserController Get, CredentialDto, getCredentialDto");
        return userService.getCredentialDto(uId);
    }

    @GetMapping("/{uId}")
    public UserDto getUserDetail(@PathVariable String uId) {
        log.info("UserController Get, UserDto, getUserDto");
        return userService.getUserDetail(uId);
    }

    @GetMapping("/profile")
    public ResponseEntity<GenericResponse> getUserProfile(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("UserController Get, GenericResponse, getUserProfile");
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return userService.getUserProfile(userId);
    }

    @PutMapping("/profile")
    public ResponseEntity<GenericResponse> updateProfile(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody ProfileRequest postProfileRequest) {
        log.info("UserController Post, GenericResponse, updateProfile");
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return userService.updateProfile(userId, postProfileRequest);
    }

    @PutMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> updateAvatar(@RequestHeader("Authorization") String authorizationHeader, @RequestPart("avatar") MultipartFile avatar) throws IOException {
        log.info("UserController Post, GenericResponse, updateAvatar");
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return userService.updateAvatar(userId, avatar);
    }

    @PutMapping(value = "/profile/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> updateCover(@RequestHeader("Authorization") String authorizationHeader, @RequestPart("cover") MultipartFile cover) throws IOException {
        log.info("UserController Post, GenericResponse, updateCover");
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return userService.updateCover(userId, cover);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUser(@RequestParam Optional<String> query
            , @RequestParam Optional<String> role
            , @RequestParam Optional<String> gender
            , @RequestParam Optional<String> school
            , @RequestParam Optional<Integer> grade
            , @RequestParam Optional<List<String>> subjects) {
        log.info("UserController Get, UserDto, searchUser");
        return userService.searchUser(query, role, gender, school, grade, subjects);
    }

}
