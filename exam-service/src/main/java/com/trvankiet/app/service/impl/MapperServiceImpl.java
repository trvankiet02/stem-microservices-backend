package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.*;
import com.trvankiet.app.entity.*;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MapperServiceImpl implements MapperService {

    @Override
    public AnswerDto mapToAnswerDto(Answer answer) {
        log.info("Mapping Answer to AnswerDto");
        return AnswerDto.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .isCorrect(answer.isCorrect())
                .questionDto(mapToQuestionDto(answer.getQuestion()))
                .createdAt(answer.getCreatedAt() == null
                        ? null : DateUtil.date2String(answer.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(answer.getUpdatedAt() == null
                        ? null : DateUtil.date2String(answer.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public ExamDto mapToExamDto(Exam exam) {
        log.info("Mapping Exam to ExamDto");
        return ExamDto.builder()
                .id(exam.getId())
                .groupId(exam.getGroupId())
                .name(exam.getName())
                .description(exam.getDescription())
                .duration(exam.getDuration())
                .staredAt(DateUtil.date2String(exam.getStaredAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .endedAt(DateUtil.date2String(exam.getEndedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .isEnabled(exam.getIsEnabled())
                .level(exam.getLevel())
                .maxScore(exam.getMaxScore())
                .createdAt(exam.getCreatedAt() == null
                        ? null : DateUtil.date2String(exam.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(exam.getUpdatedAt() == null
                        ? null : DateUtil.date2String(exam.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public QuestionDto mapToQuestionDto(Question question) {
        log.info("Mapping Question to QuestionDto");
        return QuestionDto.builder()
                .id(question.getId())
                .content(question.getContent())
                .examDto(mapToExamDto(question.getExam()))
                .level(question.getLevel())
                .type(question.getType().getName())
                .createdAt(question.getCreatedAt() == null
                        ? null : DateUtil.date2String(question.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(question.getUpdatedAt() == null
                        ? null : DateUtil.date2String(question.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public SubmissionDto mapToSubmissionDto(Submission submission) {
        log.info("Mapping Submission to SubmissionDto");
        return SubmissionDto.builder()
                .id(submission.getId())
                .authorId(submission.getAuthorId())
                .score(submission.getScore())
                .examDto(mapToExamDto(submission.getExam()))
                .startedAt(DateUtil.date2String(submission.getStartedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .endedAt(DateUtil.date2String(submission.getEndedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .createdAt(submission.getCreatedAt() == null
                        ? null : DateUtil.date2String(submission.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(submission.getUpdatedAt() == null
                        ? null : DateUtil.date2String(submission.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public SubmissionDetailDto mapToSubmissionDetailDto(SubmissionDetail submissionDetail) {
        log.info("Mapping SubmissionDetail to SubmissionDetailDto");
        return SubmissionDetailDto.builder()
                .id(submissionDetail.getId())
                .answer(submissionDetail.getAnswer())
                .score(submissionDetail.getScore())
                .questionDto(mapToQuestionDto(submissionDetail.getQuestion()))
                .submissionDto(mapToSubmissionDto(submissionDetail.getSubmission()))
                .createdAt(submissionDetail.getCreatedAt() == null
                        ? null : DateUtil.date2String(submissionDetail.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(submissionDetail.getUpdatedAt() == null
                        ? null : DateUtil.date2String(submissionDetail.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }
}
