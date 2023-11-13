package com.trvankiet.app.service;

import com.trvankiet.app.dto.*;
import com.trvankiet.app.entity.*;

public interface MapperService {
    AnswerDto mapToAnswerDto(Answer answer);
    ExamDto mapToExamDto(Exam exam);
    QuestionDto mapToQuestionDto(Question question);
    SubmissionDto mapToSubmissionDto(Submission submission);
    SubmissionDetailDto mapToSubmissionDetailDto(SubmissionDetail submissionDetail);

}
