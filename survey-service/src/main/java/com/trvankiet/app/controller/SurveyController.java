package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.CreateSurveyRequest;
import com.trvankiet.app.dto.request.UpdateSurveyRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.SurveyService;
import com.trvankiet.app.service.client.GroupClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/surveys")
@Slf4j
@RequiredArgsConstructor
public class SurveyController {

    private final GroupClientService groupClientService;
    private final SurveyService surveyService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<GenericResponse> createSurvey(@RequestHeader("Authorization") String authorizationHeader,
                                                        @RequestBody @Valid CreateSurveyRequest createSurveyRequest) {
        log.info("SurveyController, createSurvey()");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return surveyService.createSurvey(userId, createSurveyRequest);
    }

    @PutMapping("/{surveyId}")
    public ResponseEntity<GenericResponse> updateSurvey(@RequestHeader("Authorization") String authorizationHeader,
                                                        @PathVariable String surveyId,
                                                        @RequestBody @Valid UpdateSurveyRequest updateSurveyRequest) {
        log.info("SurveyController, updateSurvey()");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return surveyService.updateSurvey(userId, surveyId, updateSurveyRequest);
    }

    @DeleteMapping("/{surveyId}")
    public ResponseEntity<GenericResponse> deleteSurvey(@RequestHeader("Authorization") String authorizationHeader,
                                                        @PathVariable String surveyId) {
        log.info("SurveyController, deleteSurvey()");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return surveyService.deleteSurvey(userId, surveyId);
    }

    @GetMapping("/getHomeSurvey")
    public ResponseEntity<GenericResponse> getHomeSurvey(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("PostController, getHomePost");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        ResponseEntity<List<String>> groupIds = groupClientService.getGroupByUserId(authorizationHeader);
        return surveyService.getHomeSurvey(userId, groupIds.getBody(), page, size);
    }
}
