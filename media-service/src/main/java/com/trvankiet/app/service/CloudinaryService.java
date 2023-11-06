package com.trvankiet.app.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CloudinaryService {
    public String uploadImage(MultipartFile multipartFile, String name, String folder) throws IOException;
    public String deleteMediaFile(String url, String folder) throws IOException;

    public String uploadMediaFile(MultipartFile mediaFile, String name, String folder) throws IOException;
}
