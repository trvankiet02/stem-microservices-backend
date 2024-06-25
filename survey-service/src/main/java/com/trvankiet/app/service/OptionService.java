package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.CreateOptionRequest;
import com.trvankiet.app.dto.request.UpdateOptionRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface OptionService {
    ResponseEntity<GenericResponse> createOption(String userId, CreateOptionRequest createOptionRequest);

    ResponseEntity<GenericResponse> voteOption(String userId, String optionId);

    ResponseEntity<GenericResponse> deleteOption(String userId, String optionId);

    ResponseEntity<GenericResponse> unvoteOption(String userId, String optionId);

    ResponseEntity<GenericResponse> updateOption(String userId, String optionId, UpdateOptionRequest updateOptionRequest);
}
