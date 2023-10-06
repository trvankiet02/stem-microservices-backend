package com.trvankiet.app.business.user.service;

import com.trvankiet.app.business.user.model.request.TokenRequest;
import com.trvankiet.app.constant.GenericResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", contextId = "tokenClientService", path = "/api/v1/tokens")
public interface TokenClientService {

    @PostMapping("/refresh-access-token")
    ResponseEntity<GenericResponse> refreshAccessToken(@RequestBody @Valid TokenRequest tokenRequest);
}
