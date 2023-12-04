package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.SubjectResponse;
import com.trvankiet.app.repository.SubjectRepository;
import com.trvankiet.app.service.SubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    @Override
    public ResponseEntity<GenericResponse> getAllSubjects() {
        log.info("SubjectServiceImpl, getAllSubjects");
        List<SubjectResponse> subjectResponses = subjectRepository.findAll()
                .stream()
                .map(subject -> SubjectResponse.builder()
                        .id(subject.getId())
                        .name(subject.getName())
                        .build())
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Get all subjects successfully")
                .result(subjectResponses)
                .build());
    }
}
