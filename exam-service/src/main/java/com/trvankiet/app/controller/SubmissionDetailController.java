package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.DeleteSubmissionDetailRequest;
import com.trvankiet.app.dto.request.SubmissionDetailUpdateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.SubmissionResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.SubmissionDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/submission-details")
@RequiredArgsConstructor
@Slf4j
public class SubmissionDetailController {

    private final SubmissionDetailService submissionDetailService;
    private final JwtService jwtService;

    @PutMapping("/update")
    public ResponseEntity<String> updateSubmissionDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
        , @RequestBody @Valid SubmissionDetailUpdateRequest submissionDetailUpdateRequest) {
        log.info("SubmissionDetailController, updateSubmissionDetail");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return submissionDetailService.updateSubmissionDetail(userId, submissionDetailUpdateRequest);
    }

    @PutMapping("/delete-answer")
    public ResponseEntity<String> deleteAnswer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
        , @RequestBody @Valid DeleteSubmissionDetailRequest deleteSubmissionDetailRequest) {
        log.info("SubmissionDetailController, deleteAnswer");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return submissionDetailService.deleteAnswer(userId, deleteSubmissionDetailRequest);
    }

    @GetMapping("/detail/{submissionId}")
    public ResponseEntity<GenericResponse> getSubmissionDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
        , @PathVariable("submissionId") String submissionId) {
        log.info("SubmissionDetailController, getSubmissionDetail");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return submissionDetailService.getSubmissionDetail(userId, submissionId);
    }


}
