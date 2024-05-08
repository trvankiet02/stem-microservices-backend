package com.trvankiet.app.service;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.entity.File;

public interface MapperService {
    FileDto mapToFileDto(File file);
}
