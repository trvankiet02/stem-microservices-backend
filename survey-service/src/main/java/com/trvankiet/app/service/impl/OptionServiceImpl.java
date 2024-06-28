package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.OptionDto;
import com.trvankiet.app.dto.SurveyDto;
import com.trvankiet.app.dto.request.CreateOptionRequest;
import com.trvankiet.app.dto.request.UpdateOptionRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Option;
import com.trvankiet.app.entity.Survey;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.OptionRepository;
import com.trvankiet.app.repository.SurveyRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.OptionService;
import com.trvankiet.app.service.client.GroupMemberClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptionServiceImpl implements OptionService {
    private final OptionRepository optionRepository;
    private final SurveyRepository surveyRepository;
    private final GroupMemberClientService groupMemberClientService;
    private final MapperService mapperService;

    @Override
    public ResponseEntity<GenericResponse> createOption(String userId, CreateOptionRequest createOptionRequest) {
        log.info("OptionServiceImpl, createOption()");

        Survey survey = surveyRepository.findById(createOptionRequest.getSurveyId()).orElseThrow(() -> new NotFoundException("Survey not found"));

        if (groupMemberClientService.getRoleByGroupIdAndUserId(survey.getGroupId(), userId).equals("NONE")) {
            throw new NotFoundException("User is not a member of the group");
        }

        //voteByUser new list of user
        Option option = Option.builder()
                .id(UUID.randomUUID().toString())
                .survey(survey)
                .content(createOptionRequest.getContent())
                .createdAt(new Date())
                .authorId(userId)
                .voteByUsers(List.of(userId))
                .build();

        optionRepository.save(option);

        SurveyDto surveyDto = mapperService.mapToSurveyDto(survey, userId);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Option created successfully")
                .statusCode(HttpStatus.OK.value())
                .result(surveyDto)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> voteOption(String userId, String optionId) {
        log.info("OptionServiceImpl, voteOption()");

        Option option = optionRepository.findById(optionId).orElseThrow(() -> new NotFoundException("Option not found"));

        if (option.getVoteByUsers().contains(userId)) {
            throw new NotFoundException("User has already voted for this option");
        }

        // if survey is single choice, delete first choice and add user to new option of survey

        if (!option.getSurvey().getIsMultipleChoice()) {
            List<Option> options = optionRepository.findAllBySurveyId(option.getSurvey().getId());
            for (Option o : options) {
                if (o.getVoteByUsers().contains(userId)) {
                    o.getVoteByUsers().remove(userId);
                    optionRepository.save(o);
                }
            }
        }

        option.getVoteByUsers().add(userId);

        optionRepository.save(option);

        SurveyDto surveyDto = mapperService.mapToSurveyDto(option.getSurvey(), userId);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Option voted successfully")
                .statusCode(HttpStatus.OK.value())
                .result(surveyDto)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteOption(String userId, String optionId) {
        log.info("OptionServiceImpl, deleteOption()");

        Option option = optionRepository.findById(optionId).orElseThrow(() -> new NotFoundException("Option not found"));

        if (!option.getAuthorId().equals(userId)) {
            throw new NotFoundException("User is not the author of this option");
        }

        optionRepository.delete(option);

        SurveyDto surveyDto = mapperService.mapToSurveyDto(option.getSurvey(), userId);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Option deleted successfully")
                .statusCode(HttpStatus.OK.value())
                .result(surveyDto)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> unvoteOption(String userId, String optionId) {
        log.info("OptionServiceImpl, unvoteOption()");

        Option option = optionRepository.findById(optionId).orElseThrow(() -> new NotFoundException("Option not found"));

        if (!option.getVoteByUsers().contains(userId)) {
            throw new NotFoundException("User has not voted for this option");
        }

        option.getVoteByUsers().remove(userId);

        optionRepository.save(option);

        SurveyDto surveyDto = mapperService.mapToSurveyDto(option.getSurvey(), userId);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Option unvoted successfully")
                .statusCode(HttpStatus.OK.value())
                .result(surveyDto)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateOption(String userId, String optionId, UpdateOptionRequest updateOptionRequest) {
        log.info("OptionServiceImpl, updateOption()");

        Option option = optionRepository.findById(optionId).orElseThrow(() -> new NotFoundException("Option not found"));

        if (!option.getAuthorId().equals(userId)) {
            throw new NotFoundException("User is not the author of this option");
        }

        option.setContent(updateOptionRequest.getContent());

        optionRepository.save(option);

        SurveyDto surveyDto = mapperService.mapToSurveyDto(option.getSurvey(), userId);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Option updated successfully")
                .statusCode(HttpStatus.OK.value())
                .result(surveyDto)
                .build());
    }
}
