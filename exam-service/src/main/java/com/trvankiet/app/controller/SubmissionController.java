package com.trvankiet.app.controller;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
@Slf4j
public class SubmissionController {

    private final JwtService jwtService;
    private final SubmissionService submissionService;

    @PostMapping("/create")
    public ResponseEntity<GenericResponse> createSubmission(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken
            , @RequestParam("examId") String examId) {
        log.info("SubmissionController, createSubmission");
        String accessToken = authorizationToken.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return submissionService.createSubmission(userId, examId);
    }

    @PostMapping("/submit")
    public ResponseEntity<GenericResponse> submitSubmission(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken
            , @RequestParam("submissionId") String submissionId) {
        log.info("SubmissionController, submitSubmission");
        String accessToken = authorizationToken.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return submissionService.submitSubmission(userId, submissionId);
    }

    @GetMapping("/continue/{submissionId}")
    public ResponseEntity<GenericResponse> continueSubmissionById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken
            , @PathVariable("submissionId") String submissionId) {
        log.info("SubmissionController, getSubmission");
        String accessToken = authorizationToken.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return submissionService.continueSubmissionById(userId, submissionId);
    }

    // Get list of submissions of class for teacher
    // Get result of a submission for student and parent of student
    // Get list exam and submission of student for student and parent of student
    
}
