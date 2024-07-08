package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.GroupIdDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final MapperService mapperService;
    private final PostClientService postClientService;
    private final MongoTemplate mongoTemplate;

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
                .createdAt(new Date())
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
        report.setUpdatedAt(new Date());

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
    public ResponseEntity<GenericResponse> getAdminReport(String userId, String groupId, Integer page, Integer size) {
        log.info("ReportServiceImpl, getAdminReport()");

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Report> reports = null;
        if (groupId == null) {
            reports = reportRepository.findAllByIsReportToAdmin(true, pageRequest);
        }
        else {
            reports = reportRepository.findAllByGroupIdAndIsReportToAdmin(groupId, true, pageRequest);
        }

        Map<String, Object> results = Map.of(
                "reports", reports.getContent().stream().map(mapperService::mapToReportDto).toList(),
                "totalPages", reports.getTotalPages(),
                "totalElements", reports.getTotalElements(),
                "currentPage", reports.getNumber() + 1,
                "currentElements", reports.getNumberOfElements()
        );

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Get admin report successfully")
                .result(results)
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

    @Override
    public List<String> getFilteredGroups() {
        log.info("ReportServiceImpl, getFilteredGroups()");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("groupId").first("groupId").as("groupId"),
                Aggregation.project("groupId")
        );

        AggregationResults<GroupIdDTO> result = mongoTemplate.aggregate(aggregation, Report.class, GroupIdDTO.class);
        List<GroupIdDTO> groupIds = result.getMappedResults();

        return groupIds.stream()
                .map(GroupIdDTO::getGroupId)
                .toList();
    }

    @Override
    public ResponseEntity<GenericResponse> markAsProcessed(String userId, String reportId) {
        log.info("ReportServiceImpl, markAsProcessed()");

        Report report = reportRepository.findById(reportId).orElseThrow(() -> new NotFoundException("Report not found"));

        report.setIsProcessed(true);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Marked as processed successfully")
                .result(mapperService.mapToReportDto(reportRepository.save(report)))
                .build());
    }
}
