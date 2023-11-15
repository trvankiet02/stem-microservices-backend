package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.QuestionDto;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Exam;
import com.trvankiet.app.entity.Question;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.ExamRepository;
import com.trvankiet.app.repository.QuestionRepository;
import com.trvankiet.app.repository.SubmissionDetailRepository;
import com.trvankiet.app.repository.SubmissionRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionDetailRepository submissionDetailRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final MapperService mapperService;

    @Override
    public ResponseEntity<GenericResponse> createSubmission(String userId, String examId) {
        log.info("SubmissionServiceImpl, createSubmission");
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài thi!"));
        Date now = new Date();
        if (!exam.getIsEnabled()) {
            throw new BadRequestException("Bài thi đã bị vô hiệu hóa!");
        }
        if (now.before(exam.getStartedAt()) || now.after(exam.getEndedAt())) {
            throw new BadRequestException("Bài thi chưa bắt đầu hoặc đã kết thúc!");
        }

//        Submission submission = submissionRepository.save(Submission.builder()
//                .id(UUID.randomUUID().toString())
//                .authorId(userId)
//                .startedAt(now)
//                .exam(exam)
//                .build()
//        );
        List<Question> examQuestions = questionRepository.findAllByExamId(exam.getId());
        List<Question> submissionQuestions = new ArrayList<>();
        examQuestions.parallelStream()
                .unordered()
                .limit(exam.getNumberOfQuestion())
                .forEach(question -> {
                    synchronized (submissionQuestions) {
                        submissionQuestions.add(question);
                    }
                });
        List<QuestionDto> questionDtos = submissionQuestions.stream()
                .map(mapperService::mapToQuestionDto)
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Tạo bài thi thành công!")
                .result(questionDtos)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> submitSubmission(String userId, String submissionId) {
        return null;
    }
}
