package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.CreateRelationRequest;
import com.trvankiet.app.dto.request.UpdateRelationRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.RelationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/relationships")
@Slf4j
@RequiredArgsConstructor
public class RelationshipController {

    private final JwtService jwtService;
    private final RelationService relationService;

    @PostMapping
    public ResponseEntity<GenericResponse> createRelationRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @RequestBody @Valid CreateRelationRequest createRelationRequest) {
        log.info("RelationshipController, createRelationRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return relationService.createRelationRequest(userId, createRelationRequest);
    }

    @GetMapping
    public ResponseEntity<GenericResponse> getRelationRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("RelationshipController, getRelationRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return relationService.getRelationRequest(userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse> updateRelationRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("id") String id, @RequestBody @Valid UpdateRelationRequest updateRelationRequest) {
        log.info("RelationshipController, updateRelationRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return relationService.updateRelationRequest(userId, id, updateRelationRequest);
    }

}