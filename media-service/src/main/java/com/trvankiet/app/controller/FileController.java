package com.trvankiet.app.controller;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.request.DeleteRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final JwtService jwtService;

    @GetMapping
    public String test() {
        return "Hello from FileController";
    }

    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<FileDto> uploadPostFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                         @RequestPart("mediaFiles") List<MultipartFile> mediaFiles) throws IOException {
        log.info("FileController, uploadPostFiles");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.uploadPostFiles(userId, mediaFiles);
    }

    @DeleteMapping("/posts")
    public ResponseEntity<GenericResponse> deletePostFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @RequestBody DeleteRequest deleteRequest) {
        log.info("FileController, deleteFile");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.deletePostFiles(userId, deleteRequest);
    }

    @PostMapping(value = "/comments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<FileDto> uploadCommentFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                            @RequestPart("mediaFiles") List<MultipartFile> mediaFiles) throws IOException {
        log.info("FileController, uploadCommentFiles");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.uploadCommentFiles(userId, mediaFiles);
    }

    @DeleteMapping("/comments")
    public ResponseEntity<GenericResponse> deleteCommentFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @RequestBody DeleteRequest deleteRequest) {
        log.info("FileController, deleteCommentFiles");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.deleteCommentFiles(userId, deleteRequest);
    }

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<FileDto> uploadDocumentFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                             @RequestPart("mediaFiles") List<MultipartFile> mediaFiles) throws IOException {
        log.info("FileController, uploadDocumentFiles");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.uploadDocumentFiles(userId, mediaFiles);
    }

    @DeleteMapping("/documents")
    public ResponseEntity<GenericResponse> deleteDocumentFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @RequestBody DeleteRequest deleteRequest) {
        log.info("FileController, deleteDocumentFiles");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.deleteDocumentFiles(userId, deleteRequest);
    }

    @PostMapping(value = "/uploadUserAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadUserAvatar(@RequestPart("mediaFile") MultipartFile file) throws IOException {
        return fileService.uploadUserAvatar(file);
    }

    @DeleteMapping(value = "/deleteUserAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void deleteUserAvatar(@RequestPart("refUrl") String refUrl) throws IOException {
        fileService.deleteUserAvatar(refUrl);
    }

    @PostMapping(value = "/uploadUserCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadUserCover(@RequestPart("mediaFile") MultipartFile file) throws IOException {
        return fileService.uploadUserCover(file);
    }

    @DeleteMapping(value = "/deleteUserCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void deleteUserCover(@RequestPart("refUrl") String refUrl) throws IOException {
        fileService.deleteUserCover(refUrl);
    }

    @PostMapping(value = "/uploadGroupAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadGroupAvatar(@RequestPart("mediaFile") MultipartFile file) throws IOException {
        return fileService.uploadGroupAvatar(file);
    }

    @DeleteMapping("/deleteGroupAvatar")
    public void deleteGroupAvatar(@RequestPart("refUrl") String refUrl) throws IOException {
        fileService.deleteGroupAvatar(refUrl);
    }

    @PostMapping("/uploadGroupCover")
    public String uploadGroupCover(@RequestPart("mediaFile") MultipartFile file) throws IOException {
        return fileService.uploadGroupCover(file);
    }

    @DeleteMapping("/deleteGroupCover")
    public void deleteGroupCover(@RequestPart("refUrl") String refUrl) throws IOException {
        fileService.deleteGroupCover(refUrl);
    }
}
