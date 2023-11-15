package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.CreateQuestionRequest;
import com.trvankiet.app.dto.request.UpdateQuestionDetailRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface QuestionService {
    ResponseEntity<GenericResponse> getExamQuestions(String userId, String examId);

    ResponseEntity<GenericResponse> getQuestion(String userId, String questionId);

    ResponseEntity<GenericResponse> updateQuestionByQuestionId(String userId, String questionId, UpdateQuestionDetailRequest updateQuestionDetailRequest);

    ResponseEntity<GenericResponse> deleteQuestionByQuestionId(String userId, String questionId);

    ResponseEntity<GenericResponse> updateQuestionByQuestionIdAndBody(String userId, String questionId, CreateQuestionRequest createQuestionRequest);
}
