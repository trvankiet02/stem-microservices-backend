package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.ReportDto;
import com.trvankiet.app.dto.request.ReportPostRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Report;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.ReportRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.ReportService;
import com.trvankiet.app.service.client.PostClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final MapperService mapperService;
    private final PostClientService postClientService;

    @Override
    public ResponseEntity<GenericResponse> reportPost(String userId, ReportPostRequest reportPostRequest) {
        log.info("ReportServiceImpl, reportPost()");

        ResponseEntity<String> groupIdResponse = postClientService.getGroupId(reportPostRequest.getPostId());
        if (groupIdResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(false)
                    .statusCode(groupIdResponse.getStatusCode().value())
                    .message("Post not found")
                    .build());
        }

        Report report = reportRepository.save(Report.builder()
                .id(UUID.randomUUID().toString())
                .authorId(userId)
                .postId(reportPostRequest.getPostId())
                .groupId(groupIdResponse.getBody())
                .content(reportPostRequest.getReason())
                .isProcessed(false)
                .isReportToAdmin(reportPostRequest.getIsReportToAdmin())
                .isReportToGroupManager(reportPostRequest.getIsReportToGroupManager())
                .build());

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Reported successfully")
                .result(report.getId())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> processReport(String userId, String reportId) {
        log.info("ReportServiceImpl, processReport()");

        Report report = reportRepository.findById(reportId).orElseThrow(() -> new NotFoundException("Report not found"));

        report.setIsProcessed(true);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Processed successfully")
                .result(mapperService.mapToReportDto(reportRepository.save(report)))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getGroupReport(String userId, String groupId) {
        log.info("ReportServiceImpl, getGroupReport()");

        List<Report> reports = reportRepository.findAllByGroupIdAndIsReportToGroupManager(groupId, true);

        List<ReportDto> reportDtos = reports.stream()
                .map(mapperService::mapToReportDto)
                .toList();

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Get group report successfully")
                .result(reportDtos)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getAdminReport(String userId, String groupId) {
        log.info("ReportServiceImpl, getAdminReport()");

        List<Report> reports = reportRepository.findAllByGroupIdAndIsReportToAdmin(groupId, true);


        List<ReportDto> reportDtos = reports.stream()
                .map(mapperService::mapToReportDto)
                .toList();

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Get admin report successfully")
                .result(reportDtos)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getReport(String userId, String reportId) {
        log.info("ReportServiceImpl, getReport()");

        Report report = reportRepository.findById(reportId).orElseThrow(() -> new NotFoundException("Report not found"));

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Get report successfully")
                .result(mapperService.mapToReportDto(report))
                .build());
    }
}
