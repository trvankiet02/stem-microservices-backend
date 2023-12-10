package com.trvankiet.app.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "group-service", contextId = "groupMemberClientService", path = "/api/v1/group-members")
public interface GroupMemberClientService {

    @GetMapping("/{groupId}/{userId}/role")
    String getRoleByGroupIdAndUserId(@PathVariable String groupId, @PathVariable String userId);
}
