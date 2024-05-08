package com.trvankiet.app.controller;

import com.trvankiet.app.dto.ExamDto;
import com.trvankiet.app.dto.request.CreateExamRequest;
import com.trvankiet.app.dto.request.UpdateExamDetailRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
@Slf4j
public class ExamController {

    private final ExamService examService;
    private final JwtService jwtService;

    @GetMapping("/group/{groupId}")
    public ResponseEntity<GenericResponse> findAllExams(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId) {
        log.info("ExamController, findAllExams");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return examService.findAllExams(userId, groupId);
    }

    @PostMapping
    public ResponseEntity<GenericResponse> createExam(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @RequestBody @Valid CreateExamRequest createExamRequest) throws ParseException {
        log.info("ExamController, createExam");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return examService.createExam(userId, createExamRequest);
    }

    @PostMapping(value = {"/importFromDocx", "/importFromDoc"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreateExamRequest> importFromDocOrDocx(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @RequestPart MultipartFile multipartFile) {
        log.info("ExamController, importFromDocx");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return examService.importFromDocOrDocx(userId, multipartFile);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<GenericResponse> findExamById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable String examId) {
        log.info("ExamController, findExamById");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return examService.findExamById(userId, examId);
    }

    @PutMapping("/{examId}")
    public ResponseEntity<GenericResponse> updateExamDetailByExamId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable String examId
            , @RequestBody @Valid UpdateExamDetailRequest updateExamDetailRequest) throws ParseException {
        log.info("ExamController, updateExamByExamId");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return examService.updateExamDetailByExamId(userId, examId, updateExamDetailRequest);
    }

    @DeleteMapping("/{examId}")
    public ResponseEntity<GenericResponse> deleteExamById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable String examId) {
        log.info("ExamController, deleteExamById");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return examService.deleteExamById(userId, examId);
    }

    @GetMapping("/search")
    public List<ExamDto> searchExam(@RequestParam("query") Optional<String> query
            , @RequestParam("level") Optional<String> level) {
        log.info("ExamController, searchExam");
        return examService.searchExam(query, level);
    }
}
