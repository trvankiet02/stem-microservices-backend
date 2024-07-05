package com.trvankiet.app.controller.admin;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exams/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminExamController {

    private final ExamService examService;

    @GetMapping("/top-5")
    public ResponseEntity<GenericResponse> getTop5Exam(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("AdminExamController, getTop5Exam");
        return examService.getTop5Exam();
    }
}
