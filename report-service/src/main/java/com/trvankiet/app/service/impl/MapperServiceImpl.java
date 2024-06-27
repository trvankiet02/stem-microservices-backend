package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.ReportDto;
import com.trvankiet.app.dto.SimpleUserDto;
import com.trvankiet.app.entity.Report;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.client.UserClientService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapperServiceImpl implements MapperService {

    private final UserClientService userClientService;

    @Override
    public ReportDto mapToReportDto(Report report) {
        return ReportDto.builder()
                .id(report.getId())
                .author(userClientService.getSimpleUserDto(report.getAuthorId()))
                .groupId(report.getGroupId())
                .postId(report.getPostId())
                .content(report.getContent())
                .isProcessed(report.getIsProcessed())
                .isReportToAdmin(report.getIsReportToAdmin())
                .isReportToGroupManager(report.getIsReportToGroupManager())
                .createdAt(report.getCreatedAt() == null ? null : DateUtil.date2String(report.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(report.getUpdatedAt() == null ? null : DateUtil.date2String(report.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }
}
