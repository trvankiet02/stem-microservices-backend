package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.SubjectRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface SubjectService {
    ResponseEntity<GenericResponse> getAllSubjects();

    ResponseEntity<GenericResponse> getAllSubjectsForAdmin(Integer page, Integer size);

    ResponseEntity<GenericResponse> addSubject(String authorizationHeader, SubjectRequest subjectRequest);

    ResponseEntity<GenericResponse> updateSubject(String authorizationHeader, Integer id, SubjectRequest subjectRequest);

    ResponseEntity<GenericResponse> deleteSubject(String authorizationHeader, Integer id);
}
