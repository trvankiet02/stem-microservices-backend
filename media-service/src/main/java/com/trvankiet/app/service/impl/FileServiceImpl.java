package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.request.DeleteRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.File;
import com.trvankiet.app.entity.FileType;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.exception.wrapper.UnsupportedMediaTypeException;
import com.trvankiet.app.repository.FileRepository;
import com.trvankiet.app.repository.FileTypeRepository;
import com.trvankiet.app.service.CloudinaryService;
import com.trvankiet.app.service.FileService;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final FileTypeRepository fileTypeRepository;
    private final MapperService mapperService;

    @Override
    public List<FileDto> uploadPostFiles(String userId, List<MultipartFile> mediaFiles) throws IOException {
        return this.uploadMediaFile(userId, mediaFiles, "/posts");
    }

    @Override
    public List<FileDto> uploadCommentFiles(String userId, List<MultipartFile> mediaFiles) throws IOException {
        return this.uploadMediaFile(userId, mediaFiles, "/comments");
    }

    @Override
    public List<FileDto> uploadDocumentFiles(String userId, List<MultipartFile> mediaFiles) throws IOException {
        return this.uploadMediaFile(userId, mediaFiles, "/documents");
    }

    @Override
    public ResponseEntity<GenericResponse> deletePostFiles(String userId, DeleteRequest deleteRequest) {
        return this.deleteMediaFiles(userId, deleteRequest, "/posts");
    }

    @Override
    public ResponseEntity<GenericResponse> deleteCommentFiles(String userId, DeleteRequest deleteRequest) {
        return this.deleteMediaFiles(userId, deleteRequest, "/comments");
    }

    @Override
    public ResponseEntity<GenericResponse> deleteDocumentFiles(String userId, DeleteRequest deleteRequest) {
        return this.deleteMediaFiles(userId, deleteRequest, "/documents");
    }

    @Override
    public String uploadUserAvatar(MultipartFile file) throws IOException {
        return this.uploadImage(file, "/users/avatars");
    }

    @Override
    public String uploadUserCover(MultipartFile file) throws IOException {
        return this.uploadImage(file, "/users/covers");
    }

    @Override
    public String uploadGroupAvatar(MultipartFile file) throws IOException {
        return this.uploadImage(file, "/groups/avatars");
    }

    @Override
    public String uploadGroupCover(MultipartFile file) throws IOException {
        return this.uploadImage(file, "/groups/covers");
    }

    @Override
    public void deleteUserAvatar(String refUrl) throws IOException {
        cloudinaryService.deleteImage(refUrl, "/users/avatars");
    }

    @Override
    public void deleteUserCover(String refUrl) throws IOException {
        cloudinaryService.deleteImage(refUrl, "/users/covers");
    }

    @Override
    public void deleteGroupAvatar(String refUrl) throws IOException {
        cloudinaryService.deleteImage(refUrl, "/groups/avatars");
    }

    @Override
    public void deleteGroupCover(String refUrl) throws IOException {
        cloudinaryService.deleteImage(refUrl, "/groups/covers");
    }

    public String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public String getFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * @param userId     String
     * @param mediaFiles MultiparFile
     * @param folder     String "/folder"
     * @return
     * @throws IOException
     */
    public List<FileDto> uploadMediaFile(String userId, List<MultipartFile> mediaFiles, String folder) throws IOException {
        List<FileDto> fileDtos = new ArrayList<>();
        for (MultipartFile mediaFile : mediaFiles) {
            if (mediaFile.getOriginalFilename() != null) {
                String fileName = mediaFile.getOriginalFilename();
                Date now = new Date();
                String name = DateUtil.date2String(now, AppConstant.FULL_DATE_TIME_FORMAT) + "_" + getFileName(fileName);
                File file = fileRepository.save(File.builder()
                        .id(UUID.randomUUID().toString())
                        .authorId(userId)
                        .refUrl(cloudinaryService.uploadMediaFile(mediaFile, name, folder))
                        .type(fileTypeRepository.findByExtensionContaining(getFileExtension(fileName))
                                .orElseThrow(() -> new NotFoundException("File type not found")))
                        .createdAt(now)
                        .build());
                FileDto fileDto = mapperService.mapToFileDto(file);
                fileDtos.add(fileDto);
            }
        }
        return fileDtos;
    }
    public String uploadImage(MultipartFile mediaFile, String folder) throws IOException {
        if (mediaFile.isEmpty() || mediaFile.getOriginalFilename() == null) {
            throw new UnsupportedMediaTypeException("File is null. Please upload a valid file.");
        }
        String fileName = mediaFile.getOriginalFilename();
        Date now = new Date();
        String name = DateUtil.date2String(now, AppConstant.FULL_DATE_TIME_FORMAT) + "_" + getFileName(fileName);
        return cloudinaryService.uploadImage(mediaFile, name, folder);
    }

    public ResponseEntity<GenericResponse> deleteMediaFiles(String userId, DeleteRequest deleteRequest, String folder) {
        try {
            for (String url : deleteRequest.getRefUrls()) {
                File file = fileRepository.findByRefUrl(url)
                        .orElseThrow(() -> new NotFoundException("File not found"));
                if (!file.getAuthorId().equals(userId)) {
                    throw new BadRequestException("You are not the owner of this file");
                }
                cloudinaryService.deleteMediaFile(url, folder);
            }
            return ResponseEntity.ok().body(GenericResponse.builder()
                    .success(true)
                    .message("Delete file successfully")
                    .result(null)
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GenericResponse.builder()
                    .success(false)
                    .message("Delete file failed")
                    .result(e.getMessage())
                    .statusCode(400)
                    .build());
        }
    }
}
