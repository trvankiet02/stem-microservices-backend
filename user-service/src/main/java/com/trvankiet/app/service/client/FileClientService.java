package com.trvankiet.app.service.client;

import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@FeignClient(name = "media-service", contextId = "fileClientService", path = "/api/v1/files")
public interface FileClientService {

    @PostMapping(value = "/uploadUserAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadUserAvatar(@RequestPart("mediaFile") MultipartFile file);

    @DeleteMapping(value = "/deleteUserAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void deleteUserAvatar(@RequestPart("refUrl") String fileName);

    @PostMapping(value = "/uploadUserCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadUserCover(@RequestPart("mediaFile") MultipartFile file);

    @DeleteMapping(value = "/deleteUserCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void deleteUserCover(@RequestPart("refUrl") String fileName);

}
