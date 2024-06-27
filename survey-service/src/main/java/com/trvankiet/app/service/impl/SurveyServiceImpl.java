package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.SurveyDto;
import com.trvankiet.app.dto.request.CreateSurveyRequest;
import com.trvankiet.app.dto.request.UpdateSurveyRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Option;
import com.trvankiet.app.entity.Survey;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.repository.OptionRepository;
import com.trvankiet.app.repository.SurveyRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.SurveyService;
import com.trvankiet.app.service.client.GroupClientService;
import com.trvankiet.app.service.client.GroupMemberClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {

    private final GroupClientService groupClientService;
    private final GroupMemberClientService groupMemberClientService;
    private final SurveyRepository surveyRepository;
    private final OptionRepository optionRepository;
    private final MapperService mapperService;

    @Override
    public ResponseEntity<GenericResponse> createSurvey(String userId, CreateSurveyRequest createSurveyRequest) {
        log.info("SurveyServiceImpl, createSurvey()");
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(createSurveyRequest.getGroupId(), userId);
        if (role.equals("NONE"))
            throw new BadRequestException("You are not a member of this group!");
        Date now = new Date();
        Survey survey = surveyRepository.save(Survey.builder()
                .id(UUID.randomUUID().toString())
                .groupId(createSurveyRequest.getGroupId())
                .authorId(userId)
                .content(createSurveyRequest.getContent())
                .isMultipleChoice(createSurveyRequest.getIsMultipleChoice())
                .isAddOtherOption(createSurveyRequest.getIsAddOtherOption())
                .createdAt(now)
                .build());

        createSurveyRequest.getOptions().forEach(option -> {
            // save options
            optionRepository.save(Option.builder()
                    .id(UUID.randomUUID().toString())
                    .survey(survey)
                    .authorId(userId)
                    .content(option)
                    .createdAt(now)
                    .build());
        });

        SurveyDto surveyDto = mapperService.mapToSurveyDto(survey);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Create survey successfully!")
                .result(surveyDto)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateSurvey(String userId, String surveyId, UpdateSurveyRequest updateSurveyRequest) {
        log.info("SurveyServiceImpl, updateSurvey()");

        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new BadRequestException("Survey not found!"));

        if (!survey.getAuthorId().equals(userId)) {
            throw new BadRequestException("You are not the author of this survey!");
        }

        survey.setContent(updateSurveyRequest.getContent());
        survey.setIsMultipleChoice(updateSurveyRequest.getIsMultipleChoice());
        survey.setIsAddOtherOption(updateSurveyRequest.getIsAddOtherOption());

        survey.setUpdatedAt(new Date());

        surveyRepository.save(survey);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Update survey successfully!")
                .result(mapperService.mapToSurveyDto(survey))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteSurvey(String userId, String surveyId) {
        log.info("SurveyServiceImpl, deleteSurvey()");

        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new BadRequestException("Survey not found!"));

        if (!survey.getAuthorId().equals(userId)) {
            throw new BadRequestException("You are not the author of this survey!");
        }

        surveyRepository.delete(survey);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Delete survey successfully!")
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getHomeSurvey(String userId, List<String> body, int page, int size) {
        log.info("SurveyServiceImpl, getHomeSurvey()");

        if (body.isEmpty()) {
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("No group found!")
                    .result(null)
                    .build());
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Survey> surveys = surveyRepository.findByGroupIdIn(body, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("totalPages", surveys.getTotalPages());
        result.put("totalElements", surveys.getTotalElements());
        result.put("currentPage", surveys.getNumber());
        result.put("currentElements", surveys.getNumberOfElements());
        result.put("surveys", surveys.getContent()
                .stream()
                .map(mapperService::mapToSurveyDto)
                .toList());

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Get home survey successfully!")
                .result(result)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getGroupSurvey(String userId, String groupId, int page, int size) {
        log.info("SurveyServiceImpl, getGroupSurvey()");

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Survey> surveys = surveyRepository.findAllByGroupId(groupId, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("totalPages", surveys.getTotalPages());
        result.put("totalElements", surveys.getTotalElements());
        result.put("currentPage", surveys.getNumber());
        result.put("currentElements", surveys.getNumberOfElements());
        result.put("surveys", surveys.getContent()
                .stream()
                .map(mapperService::mapToSurveyDto)
                .toList());

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Get group survey successfully!")
                .result(result)
                .build());
    }
}
