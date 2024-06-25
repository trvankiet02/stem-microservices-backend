package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.CreateOptionRequest;
import com.trvankiet.app.dto.request.UpdateOptionRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.OptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/options")
@RequiredArgsConstructor
@Slf4j
public class OptionController {
    private final OptionService optionService;
    private final JwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<GenericResponse> createOption(@RequestHeader("Authorization") String authorizationHeader,
                                                        @Valid @RequestBody CreateOptionRequest createOptionRequest) {
        log.info("OptionController, createOption()");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return optionService.createOption(userId, createOptionRequest);
    }

    @PostMapping("/vote/{optionId}")
    public ResponseEntity<GenericResponse> voteOption(@RequestHeader("Authorization") String authorizationHeader,
                                                      @PathVariable String optionId) {
        log.info("OptionController, voteOption()");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return optionService.voteOption(userId, optionId);
    }

    @DeleteMapping("/delete/{optionId}")
    public ResponseEntity<GenericResponse> deleteOption(@RequestHeader("Authorization") String authorizationHeader,
                                                        @PathVariable String optionId) {
        log.info("OptionController, deleteOption()");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return optionService.deleteOption(userId, optionId);
    }

    @PostMapping("/unvote/{optionId}")
    public ResponseEntity<GenericResponse> unvoteOption(@RequestHeader("Authorization") String authorizationHeader,
                                                        @PathVariable String optionId) {
        log.info("OptionController, unvoteOption()");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return optionService.unvoteOption(userId, optionId);
    }

    @PutMapping("/update/{optionId}")
    public ResponseEntity<GenericResponse> updateOption(@RequestHeader("Authorization") String authorizationHeader,
                                                        @PathVariable String optionId,
                                                        @Valid @RequestBody UpdateOptionRequest updateOptionRequest) {
        log.info("OptionController, updateOption()");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return optionService.updateOption(userId, optionId, updateOptionRequest);
    }
}
