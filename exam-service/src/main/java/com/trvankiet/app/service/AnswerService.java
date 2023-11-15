package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.UpdateAnswerDetailRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface AnswerService {
    ResponseEntity<GenericResponse> getAnswersByQuestionId(String userId, String questionId);

    ResponseEntity<GenericResponse> getAnswer(String userId, String answerId);

    ResponseEntity<GenericResponse> updateAnswerByAnswerId(String userId, String answerId, UpdateAnswerDetailRequest updateAnswerDetailRequest);

    ResponseEntity<GenericResponse> deleteAnswerByAnswerId(String userId, String answerId);
}
