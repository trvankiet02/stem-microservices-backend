package com.trvankiet.app.service;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    List<FileDto> uploadPostFiles(String userId, List<MultipartFile> mediaFiles) throws IOException;

    List<FileDto> uploadCommentFiles(String userId, List<MultipartFile> mediaFiles) throws IOException;
}
