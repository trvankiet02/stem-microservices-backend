package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.DeleteSubmissionDetailRequest;
import com.trvankiet.app.dto.request.SubmissionDetailUpdateRequest;
import org.springframework.http.ResponseEntity;

public interface SubmissionDetailService {
    ResponseEntity<String> updateSubmissionDetail(String userId, SubmissionDetailUpdateRequest submissionDetailUpdateRequest);

    ResponseEntity<String> deleteAnswer(String userId, DeleteSubmissionDetailRequest deleteSubmissionDetailRequest);

}
