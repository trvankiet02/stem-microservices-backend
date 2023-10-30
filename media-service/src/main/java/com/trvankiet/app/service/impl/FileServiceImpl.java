package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.entity.File;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.FileRepository;
import com.trvankiet.app.repository.FileTypeRepository;
import com.trvankiet.app.service.CloudinaryService;
import com.trvankiet.app.service.FileService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final FileTypeRepository fileTypeRepository;

    @Override
    public List<FileDto> uploadPostFiles(String userId, List<MultipartFile> mediaFiles) throws IOException {
        List<FileDto> fileDtos = new ArrayList<>();
        for (MultipartFile mediaFile : mediaFiles) {
            if (mediaFile.getOriginalFilename() != null) {
                String fileName = mediaFile.getOriginalFilename();
                Date now = new Date();
                String name = DateUtil.date2String(now, AppConstant.FULL_DATE_FORMAT) + "_" + fileName;
                File file = fileRepository.save(File.builder()
                        .fileId(UUID.randomUUID().toString())
                        .authorId(userId)
                        .fileLink(cloudinaryService.uploadMediaFiles(mediaFile, name, "/posts"))
                        .fileType(fileTypeRepository.findByFileTypeExtensionContaining(getFileExtension(fileName))
                                .orElseThrow(() -> new NotFoundException("File type not found")))
                        .createdAt(now)
                        .build());
                FileDto fileDto = this.mapToFileDto(file);
                fileDtos.add(fileDto);
            }
        }
        return fileDtos;
    }

    @Override
    public List<FileDto> uploadCommentFiles(String userId, List<MultipartFile> mediaFiles) throws IOException {
        List<FileDto> fileDtos = new ArrayList<>();
        for (MultipartFile mediaFile : mediaFiles) {
            if (mediaFile.getOriginalFilename() != null) {
                String fileName = mediaFile.getOriginalFilename();
                Date now = new Date();
                String name = DateUtil.date2String(now, AppConstant.FULL_DATE_FORMAT) + "_" + fileName;
                File file = fileRepository.save(File.builder()
                        .fileId(UUID.randomUUID().toString())
                        .authorId(userId)
                        .fileLink(cloudinaryService.uploadMediaFiles(mediaFile, name, "/comments"))
                        .fileType(fileTypeRepository.findByFileTypeExtensionContaining(getFileExtension(fileName))
                                .orElseThrow(() -> new NotFoundException("File type not found")))
                        .createdAt(now)
                        .build());
                FileDto fileDto = this.mapToFileDto(file);
                fileDtos.add(fileDto);
            }
        }
        return fileDtos;
    }

    public FileDto mapToFileDto(File file) {
        return FileDto.builder()
                .fileId(file.getFileId())
                .authorId(file.getAuthorId())
                .fileLink(file.getFileLink())
                .fileType(file.getFileType().getFileTypeName())
                .build();
    }

    public String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
