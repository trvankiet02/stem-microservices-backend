package com.trvankiet.app.business.user.service.client;

import com.trvankiet.app.business.user.model.request.TokenRequest;
import com.trvankiet.app.constant.GenericResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", contextId = "tokenClientService", path = "/api/v1/tokens")
public interface TokenClientService {

    @PostMapping("/refresh-access-token")
    ResponseEntity<GenericResponse> refreshAccessToken(@RequestBody @Valid TokenRequest tokenRequest);

    @PostMapping("reset-password")
    ResponseEntity<GenericResponse> resetPassword(@RequestParam String email);
}
