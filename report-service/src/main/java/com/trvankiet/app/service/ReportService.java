package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.ReportPostRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReportService {
    ResponseEntity<GenericResponse> reportPost(String userId, ReportPostRequest reportPostRequest);

    ResponseEntity<GenericResponse> processReport(String userId, String reportId);

    ResponseEntity<GenericResponse> getGroupReport(String userId, String groupId);

    ResponseEntity<GenericResponse> getAdminReport(String userId, String groupId, Integer page, Integer size);

    ResponseEntity<GenericResponse> getReport(String userId, String reportId);

    List<String> getFilteredGroups();

    ResponseEntity<GenericResponse> markAsProcessed(String userId, String reportId);
}
