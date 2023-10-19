package com.trvankiet.app.controller;

import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.UserInfoRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"/api/v1/users"})
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/credentials")
    public CredentialDto getCredentialDto (@RequestParam String uId) {
        log.info("UserController Get, CredentialDto, getCredentialDto");
        return userService.getCredentialDto(uId);
    }

    @GetMapping("/{uId}")
    public UserDto getUserDetail (@PathVariable String uId) {
        log.info("UserController Get, UserDto, getUserDto");
        return userService.getUserDetail(uId);
    }

}
