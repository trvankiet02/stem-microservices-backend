package com.trvankiet.app.service.client;

import com.trvankiet.app.dto.FileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "media-service", contextId = "fileClientService", path = "/api/v1/files")
public interface FileClientService {

    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    List<FileDto> uploadPostFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                              @RequestPart("mediaFiles") List<MultipartFile> mediaFiles);

    @PostMapping(value = "/comments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    List<FileDto> uploadCommentFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                     @RequestPart("mediaFiles") List<MultipartFile> mediaFiles);

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    List<FileDto> uploadDocumentFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                     @RequestPart("mediaFiles") List<MultipartFile> mediaFiles);
}
