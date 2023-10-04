package com.trvankiet.app.business.user.service;

import com.trvankiet.app.business.user.model.request.UserInfoRequest;
import com.trvankiet.app.constant.GenericResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/users")
public interface UserClientService {
    @PostMapping("/verify")
    ResponseEntity<GenericResponse> initUserInfo(@RequestParam final String token,
                                                 @RequestBody final UserInfoRequest userInfoRequest);
}
