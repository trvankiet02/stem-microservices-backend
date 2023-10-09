package com.trvankiet.app.controller;

import com.trvankiet.app.config.client.UserServiceClient;
import com.trvankiet.app.entity.Friendship;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/api/v1/friendships")
@RequiredArgsConstructor
public class FriendshipController {
    public final UserServiceClient userServiceClient;
    public final RestTemplate restTemplate;

    @GetMapping
    public String test() {
        log.info("*** Test ***");
        //log.info("####1 {} 1####", userServiceClient.test());
        //log.info("####2 {} 2####", restTemplate.getForObject("http://user-service/api/v1/user", String.class));
        return "userServiceClient.test()";
    }
}
