package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.File;
import com.trvankiet.app.repository.FileRepository;
import com.trvankiet.app.service.CloudinaryService;
import com.trvankiet.app.service.FileService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final CloudinaryService cloudinaryService;
    private final FileRepository fileRepository;
    @Override
    public List<FileDto> uploadFiles(String userId, List<MultipartFile> mediaFiles) throws IOException {
        List<FileDto> fileDtos = new ArrayList<>();
        if (!mediaFiles.isEmpty()) {
            for (MultipartFile mediaFile : mediaFiles) {
                Date now = new Date();
                String name = DateUtil.date2String(now, AppConstant.FULL_DATE_FORMAT) + "_" + mediaFile.getOriginalFilename();
                File file = fileRepository.save(File.builder()
                        .fileId(UUID.randomUUID().toString())
                        .authorId(userId)
                        .fileLink(cloudinaryService.uploadMediaFiles(mediaFile, name, "/media"))
                        .createdAt(now)
                        .build());
                FileDto fileDto = FileDto.builder()
                        .fileId(file.getFileId())
                        .authorId(file.getAuthorId())
                        .fileLink(file.getFileLink())
                        .build();
                fileDtos.add(fileDto);
            }
        }
        return fileDtos;
    }
}
