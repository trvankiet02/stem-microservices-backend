package com.trvankiet.app.service.client;

import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "media-service", contextId = "fileClientService", path = "/api/v1/files")
public interface FileClientService {

    @PostMapping
    ResponseEntity<GenericResponse> uploadFiles(@RequestParam("mediaFiles") List<MultipartFile> mediaFiles);
}
