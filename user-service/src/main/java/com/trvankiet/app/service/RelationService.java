package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.CreateRelationRequest;
import com.trvankiet.app.dto.request.UpdateRelationRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface RelationService {
    ResponseEntity<GenericResponse> createRelationRequest(String userId, CreateRelationRequest createRelationRequest);

    ResponseEntity<GenericResponse> getRelationRequest(String userId);

    ResponseEntity<GenericResponse> updateRelationRequest(String userId, String id, UpdateRelationRequest updateRelationRequest);
}
