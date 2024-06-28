package com.trvankiet.app.service;

import com.trvankiet.app.dto.OptionDto;
import com.trvankiet.app.dto.SurveyDto;
import com.trvankiet.app.entity.Option;
import com.trvankiet.app.entity.Survey;

public interface MapperService {
    SurveyDto mapToSurveyDto(Survey survey, String userId);
    OptionDto mapToOptionDto(Option option, String userId);
}
