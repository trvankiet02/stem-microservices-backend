package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.ReportPostRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;
    private final JwtService jwtService;

    @PostMapping("/reportPost")
    public ResponseEntity<GenericResponse> reportPost(@RequestHeader("Authorization") String authorizationHeader,
                                                      @Valid @RequestBody ReportPostRequest reportPostRequest) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return reportService.reportPost(userId, reportPostRequest);
    }

    @PutMapping("/processReport/{reportId}")
    public ResponseEntity<GenericResponse> processReport(@RequestHeader("Authorization") String authorizationHeader,
                                                         @PathVariable String reportId) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return reportService.processReport(userId, reportId);
    }

    @GetMapping("/groupReport/{groupId}")
    public ResponseEntity<GenericResponse> getGroupReport(@RequestHeader("Authorization") String authorizationHeader,
                                                          @PathVariable String groupId) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return reportService.getGroupReport(userId, groupId);
    }

    @GetMapping("/adminReport/{groupId}")
    public ResponseEntity<GenericResponse> getAdminReport(@RequestHeader("Authorization") String authorizationHeader,
                                                          @PathVariable String groupId) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return reportService.getAdminReport(userId, groupId);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<GenericResponse> getReport(@RequestHeader("Authorization") String authorizationHeader,
                                                     @PathVariable String reportId) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return reportService.getReport(userId, reportId);
    }




}
