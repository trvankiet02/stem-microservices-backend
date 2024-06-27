package com.trvankiet.app.service;

import com.trvankiet.app.dto.ReportDto;
import com.trvankiet.app.entity.Report;

public interface MapperService {
    ReportDto mapToReportDto(Report report);
}
