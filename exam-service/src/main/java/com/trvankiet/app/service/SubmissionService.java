package com.trvankiet.app.service;

import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface SubmissionService {
    ResponseEntity<GenericResponse> createSubmission(String userId, String examId);

    ResponseEntity<GenericResponse> submitSubmission(String userId, String submissionId);
}
