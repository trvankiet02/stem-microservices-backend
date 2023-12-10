package com.trvankiet.app.service;

import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface SubmissionService {
    ResponseEntity<GenericResponse> createSubmission(String userId, String examId);

    ResponseEntity<GenericResponse> submitSubmission(String userId, String submissionId);

    ResponseEntity<GenericResponse> continueSubmissionById(String userId, String submissionId);

    ResponseEntity<GenericResponse> createSubmissionV2(String userId, String examId);

    ResponseEntity<GenericResponse> getListSubmissionByExamId(String userId, String examId);

    ResponseEntity<GenericResponse> getListSubmissionByExamIdForParent(String userId, String examId);
}
