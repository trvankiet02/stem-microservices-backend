package com.trvankiet.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/v1/albums")
@Slf4j
@RequiredArgsConstructor
public class AlbumController {

    @GetMapping
    public String test() {
        return "Hello from AlbumController";
    }
}
