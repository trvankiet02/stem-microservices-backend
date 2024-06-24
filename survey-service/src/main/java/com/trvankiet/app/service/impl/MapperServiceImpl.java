package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.OptionDto;
import com.trvankiet.app.dto.SurveyDto;
import com.trvankiet.app.entity.Option;
import com.trvankiet.app.entity.Survey;
import com.trvankiet.app.repository.OptionRepository;
import com.trvankiet.app.repository.SurveyRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.client.UserClientService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapperServiceImpl implements MapperService {

    private final UserClientService userClientService;
    private final OptionRepository optionRepository;
    private final SurveyRepository surveyRepository;

    @Override
    public SurveyDto mapToSurveyDto(Survey survey) {
        List<OptionDto> optionDtos = optionRepository.findAllBySurveyId(survey.getId())
                .stream()
                .map(this::mapToOptionDto)
                .toList();
        return SurveyDto.builder()
                .id(survey.getId())
                .author(userClientService.getSimpleUserDto(survey.getAuthorId()))
                .groupId(survey.getGroupId())
                .content(survey.getContent())
                .isMultipleChoice(survey.getIsMultipleChoice())
                .isAddOtherOption(survey.getIsAddOtherOption())
                .optionDtos(optionDtos)
                .createdAt(survey.getCreatedAt() == null ?
                        null : DateUtil.date2String(survey.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(survey.getUpdatedAt() == null ?
                        null : DateUtil.date2String(survey.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public OptionDto mapToOptionDto(Option option) {

        return OptionDto.builder()
                .id(option.getId())
                .content(option.getContent())
                .voteCount(option.getVoteByUsers() == null ? 0 : option.getVoteByUsers().size())
                .createdAt(option.getCreatedAt() == null ?
                        null : DateUtil.date2String(option.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(option.getUpdatedAt() == null ?
                        null : DateUtil.date2String(option.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }
}
