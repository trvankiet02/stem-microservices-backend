package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.entity.File;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapperServiceImpl implements MapperService {

    @Override
    public FileDto mapToFileDto(File file) {
        return FileDto.builder()
                .authorId(file.getAuthorId())
                .refUrl(file.getRefUrl())
                .type(file.getType().getName())
                .createdAt(DateUtil.date2String(file.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }
}
