package com.trvankiet.app.service.client;

import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/users")
public interface UserClientService {

    @GetMapping("/userDto/{uId}")
    UserDto getUserDtoByUserId(@PathVariable String uId);

    @GetMapping("/credentials")
    CredentialDto getCredentialDtoByUserId(@RequestParam String uId);
}
