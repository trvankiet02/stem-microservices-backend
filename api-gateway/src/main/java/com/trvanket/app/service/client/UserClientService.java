package com.trvanket.app.service.client;

import com.trvanket.app.config.FeignConfig;
import com.trvanket.app.dto.CredentialDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/users", configuration = FeignConfig.class)
public interface UserClientService {

    @GetMapping(value ="/credentials")
    CredentialDto getCredentialDto(@RequestParam String uId);
}
