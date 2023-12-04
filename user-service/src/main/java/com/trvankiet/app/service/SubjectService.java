package com.trvankiet.app.service;

import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface SubjectService {
    ResponseEntity<GenericResponse> getAllSubjects();
}
