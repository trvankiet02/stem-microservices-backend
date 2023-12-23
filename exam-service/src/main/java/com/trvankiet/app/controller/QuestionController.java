package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.CreateQuestionRequest;
import com.trvankiet.app.dto.request.UpdateQuestionDetailRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

    private final QuestionService questionService;
    private final JwtService jwtService;

    @GetMapping("/exam")
    public ResponseEntity<GenericResponse> getExamQuestions(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @RequestParam("examId") String examId) {
        log.info("QuestionController, getExamQuestions");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return questionService.getExamQuestions(userId, examId);
    }

    @PostMapping("/update")
    public ResponseEntity<GenericResponse> updateQuestion(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @RequestParam("questionId") String questionId
            , @RequestBody @Valid CreateQuestionRequest createQuestionRequest) {
        log.info("QuestionController, updateQuestion");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return questionService.updateQuestionByQuestionIdAndBody(userId, questionId, createQuestionRequest);
    }

    @PostMapping("/create")
    public ResponseEntity<GenericResponse> createQuestion(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @RequestParam("examId") String examId
            , @RequestBody @Valid CreateQuestionRequest createQuestionRequest) {
        log.info("QuestionController, createQuestion");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return questionService.createQuestion(userId, examId, createQuestionRequest);
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<GenericResponse> getQuestion(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("questionId") String questionId) {
        log.info("QuestionController, getQuestion");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return questionService.getQuestion(userId, questionId);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<GenericResponse> updateQuestionByQuestionId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("questionId") String questionId
            , @RequestBody @Valid UpdateQuestionDetailRequest updateQuestionDetailRequest) {
        log.info("QuestionController, updateQuestionByQuestionId");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return questionService.updateQuestionByQuestionId(userId, questionId, updateQuestionDetailRequest);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<GenericResponse> deleteQuestionByQuestionId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("questionId") String questionId) {
        log.info("QuestionController, deleteQuestionByQuestionId");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return questionService.deleteQuestionByQuestionId(userId, questionId);
    }
}
