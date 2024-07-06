package com.trvankiet.app.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "report-service", contextId = "reportClientService", path = "/api/v1/reports")
public interface ReportClientService {

    @GetMapping("/get-filtered-groups")
    List<String> getFilteredGroups();
}
