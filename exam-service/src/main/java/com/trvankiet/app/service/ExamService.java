package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.CreateExamRequest;
import com.trvankiet.app.dto.request.UpdateExamDetailRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;

public interface ExamService {
    ResponseEntity<GenericResponse> findAllExams();

    ResponseEntity<GenericResponse> createExam(String userId, CreateExamRequest createExamRequest) throws ParseException;

    ResponseEntity<CreateExamRequest> importFromDocOrDocx(String userId, MultipartFile multipartFile);

    ResponseEntity<GenericResponse> findExamById(String examId);

    ResponseEntity<GenericResponse> updateExamDetailByExamId(String userId, String examId, UpdateExamDetailRequest updateExamDetailRequest) throws ParseException;

    ResponseEntity<GenericResponse> deleteExamById(String userId, String examId);
}
