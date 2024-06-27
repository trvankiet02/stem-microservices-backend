package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.CreateSurveyRequest;
import com.trvankiet.app.dto.request.UpdateSurveyRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SurveyService {
    ResponseEntity<GenericResponse> createSurvey(String userId, CreateSurveyRequest createSurveyRequest);

    ResponseEntity<GenericResponse> updateSurvey(String userId, String surveyId, UpdateSurveyRequest updateSurveyRequest);

    ResponseEntity<GenericResponse> deleteSurvey(String userId, String surveyId);

    ResponseEntity<GenericResponse> getHomeSurvey(String userId, List<String> body, int page, int size);

    ResponseEntity<GenericResponse> getGroupSurvey(String userId, String groupId, int page, int size);
}
