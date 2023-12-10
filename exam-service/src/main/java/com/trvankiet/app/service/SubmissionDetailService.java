package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.DeleteSubmissionDetailRequest;
import com.trvankiet.app.dto.request.SubmissionDetailUpdateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface SubmissionDetailService {
    ResponseEntity<String> updateSubmissionDetail(String userId, SubmissionDetailUpdateRequest submissionDetailUpdateRequest);

    ResponseEntity<String> deleteAnswer(String userId, DeleteSubmissionDetailRequest deleteSubmissionDetailRequest);

    ResponseEntity<GenericResponse> getSubmissionDetail(String userId, String submissionId);
}
