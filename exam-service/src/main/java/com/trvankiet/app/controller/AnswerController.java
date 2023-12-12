package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.UpdateAnswerDetailRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/answers")
@RequiredArgsConstructor
@Slf4j
public class AnswerController {

    private final AnswerService answerService;
    private final JwtService jwtService;

    @GetMapping("/question")
    public ResponseEntity<GenericResponse> getAnswersByQuestionId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @RequestParam("questionId") String questionId) {
        log.info("getAnswersByQuestionId: {}", questionId);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return answerService.getAnswersByQuestionId(userId, questionId);
    }

    @GetMapping("/{answerId}")
    public ResponseEntity<GenericResponse> getAnswer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("answerId") String answerId) {
        log.info("getAnswer: {}", answerId);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return answerService.getAnswer(userId, answerId);
    }

    @PutMapping("/{answerId}")
    public ResponseEntity<GenericResponse> updateAnswerByAnswerId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("answerId") String answerId
            , @RequestBody @Valid UpdateAnswerDetailRequest updateAnswerDetailRequest) {
        log.info("updateAnswerByAnswerId: {}", answerId);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return answerService.updateAnswerByAnswerId(userId, answerId, updateAnswerDetailRequest);
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<GenericResponse> deleteAnswerByAnswerId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("answerId") String answerId) {
        log.info("deleteAnswerByAnswerId: {}", answerId);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return answerService.deleteAnswerByAnswerId(userId, answerId);
    }

    @PostMapping("/create")
    public ResponseEntity<GenericResponse> createAnswer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @RequestParam("qId") String qId
            , @RequestBody UpdateAnswerDetailRequest updateAnswerDetailRequest) {
        log.info("createAnswer");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return answerService.createAnswer(userId, qId, updateAnswerDetailRequest);
    }
}
