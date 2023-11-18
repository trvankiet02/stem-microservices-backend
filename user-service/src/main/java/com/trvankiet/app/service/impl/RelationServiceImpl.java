package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.RelationshipDto;
import com.trvankiet.app.dto.request.CreateRelationRequest;
import com.trvankiet.app.dto.request.UpdateRelationRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Relationship;
import com.trvankiet.app.entity.User;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.RelationRepository;
import com.trvankiet.app.repository.UserRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.RelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RelationServiceImpl implements RelationService {

    private final RelationRepository relationRepository;
    private final UserRepository userRepository;
    private final MapperService mapperService;

    @Override
    public ResponseEntity<GenericResponse> createRelationRequest(String userId, CreateRelationRequest createRelationRequest) {
        log.info("RelationServiceImpl, createRelationRequest");
        String studentId = createRelationRequest.getStudentId();
        User parent = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phụ huynh với id: " + userId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy học sinh với id: " + studentId));
        Relationship relationship = relationRepository.save(Relationship.builder()
                .parent(parent)
                .child(student)
                .isAccepted(false)
                .build());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Tạo yêu cầu thành công!")
                .result(mapperService.mapToRelationDto(relationship))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getRelationRequest(String userId) {
        log.info("RelationServiceImpl, getRelationRequest");
        List<RelationshipDto> relationships = relationRepository.findAllByChildId(userId)
                .stream()
                .map(mapperService::mapToRelationDto)
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách yêu cầu thành công!")
                .result(relationships)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateRelationRequest(String userId, String id, UpdateRelationRequest updateRelationRequest) {
        log.info("RelationServiceImpl, updateRelationRequest");
        Relationship relationship = relationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy yêu cầu với id: " + id));
        if (!relationship.getChild().getId().equals(userId)) {
            throw new NotFoundException("Không tìm thấy yêu cầu với id: " + id);
        }
        relationship.setIsAccepted(updateRelationRequest.getIsAccepted());
        if (updateRelationRequest.getIsAccepted()) {
            User student = relationship.getChild();
            User parent = relationship.getParent();
            student.getParents().add(parent);
            parent.getStudents().add(student);
            userRepository.save(student);
        }
        relationship = relationRepository.save(relationship);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật yêu cầu thành công!")
                .result(mapperService.mapToRelationDto(relationship))
                .build());
    }
}
