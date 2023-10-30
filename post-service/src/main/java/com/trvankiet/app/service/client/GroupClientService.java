package com.trvankiet.app.service.client;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "group-service", contextId = "groupClientService", path = "/api/v1/groups")
public interface GroupClientService {
    @GetMapping("/validate-user-in-group")
    ResponseEntity<GenericResponse> validateUserInGroup(
            @RequestParam("userId") String userId,
            @RequestParam("groupId") String groupId);
}
