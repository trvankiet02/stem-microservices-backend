package com.trvankiet.app.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.trvankiet.app.exception.wrapper.UnsupportedMediaTypeException;
import com.trvankiet.app.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private static final String ROOT_FOLDER = "stem-microservice-backend";

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile multipartFile, String name, String folder) throws IOException {
        log.info("CloudinaryServiceImpl, uploadImage");
        if (multipartFile.isEmpty()) {
            throw new UnsupportedMediaTypeException("File is null. Please upload a valid file.");
        }
        if (!multipartFile.getContentType().startsWith("image/")) {
            throw new UnsupportedMediaTypeException("Only image files are allowed.");
        }
        var params = ObjectUtils.asMap(
                "folder", ROOT_FOLDER + folder,
                "public_id", name,
                "resource_type", "image");
        var uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(), params);
        log.info("CloudinaryServiceImpl, uploadImage, uploadResult: {}", uploadResult.get("secure_url").toString());
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public void deleteUserImage(String url, String folder) throws IOException {
        var params = ObjectUtils.asMap(
                "folder", ROOT_FOLDER + folder,
                "resource_type", "image");
        var result = cloudinary.uploader().destroy(getPublicIdFromUrl(url, folder), params);
        log.info("CloudinaryServiceImpl, deleteUserImage, result: {}", result.get("result").toString());
    }

    public String getPublicIdFromUrl(String imageUrl, String folder)  {
        String publicId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
        return ROOT_FOLDER + folder + "/" + publicId;
    }
}
