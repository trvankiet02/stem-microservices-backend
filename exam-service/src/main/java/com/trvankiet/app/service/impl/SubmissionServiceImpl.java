package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.QuestionTypeEnum;
import com.trvankiet.app.dto.QuestionDto;
import com.trvankiet.app.dto.response.ExamSubmissionResponse;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.QuestionSubmissionResponse;
import com.trvankiet.app.entity.*;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.*;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.SubmissionService;
import com.trvankiet.app.service.client.GroupMemberClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionDetailRepository submissionDetailRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final MapperService mapperService;
    private final GroupMemberClientService groupMemberClientService;
    private final AnswerRepository answerRepository;

    @Override
    public ResponseEntity<GenericResponse> createSubmission(String userId, String examId) {
        log.info("SubmissionServiceImpl, createSubmission");
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài thi!"));
        try {
            String role = groupMemberClientService.getRoleByGroupIdAndUserId(exam.getGroupId(), userId);
        } catch (Exception e) {
            throw new BadRequestException("Bạn không có quyền thực hiện hành động này!");
        }
        Date now = new Date();
        if (!exam.getIsEnabled()) {
            throw new BadRequestException("Bài thi đã bị vô hiệu hóa!");
        }
        if (now.before(exam.getStartedAt()) || now.after(exam.getEndedAt())) {
            throw new BadRequestException("Bài thi chưa bắt đầu hoặc đã kết thúc!");
        }
        Submission submission = submissionRepository.save(Submission.builder()
                .id(UUID.randomUUID().toString())
                .authorId(userId)
                .startedAt(now)
                .exam(exam)
                .build()
        );
        List<Question> examQuestions = questionRepository.findAllByExamId(exam.getId());
        Collections.shuffle(examQuestions);  // Xáo trộn danh sách câu hỏi
        List<Question> submissionQuestions = examQuestions.stream()
                .limit(exam.getNumberOfQuestion())
                .toList();
        ExamSubmissionResponse examSubmissionResponse = ExamSubmissionResponse.builder()
                .submissionId(submission.getId())
                .questionSubmissionResponses(new ArrayList<>())
                .build();

        submissionQuestions.forEach(question -> {
            SubmissionDetail submissionDetail = submissionDetailRepository
                    .save(SubmissionDetail.builder()
                            .id(UUID.randomUUID().toString())
                            .question(question)
                            .submission(submission)
                            .createdAt(now)
                            .build());
            QuestionSubmissionResponse questionSubmissionResponse = QuestionSubmissionResponse.builder()
                    .submissionDetailId(submissionDetail.getId())
                    .content(submissionDetail.getQuestion().getContent())
                    .level(submissionDetail.getQuestion().getLevel())
                    .typeCode(submissionDetail.getQuestion().getType().getCode())
                    .answers(answerRepository.findAllByQuestionId(submissionDetail.getQuestion().getId())
                            .stream()
                            .map(Answer::getContent)
                            .toList())
                    .build();
            examSubmissionResponse.getQuestionSubmissionResponses().add(questionSubmissionResponse);
        });

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Tạo bài thi thành công!")
                .result(examSubmissionResponse)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> submitSubmission(String userId, String submissionId) {
        log.info("SubmissionServiceImpl, submitSubmission");
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài thi!"));
        if (!submission.getAuthorId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền thực hiện hành động này!");
        }
        submission.setEndedAt(new Date());
        List<SubmissionDetail> submissionDetails = submissionDetailRepository.findAllBySubmissionId(submissionId);
        int score = 0;
        // 3 type of question: single_choice, multiple_choice, essay
        for (SubmissionDetail submissionDetail : submissionDetails) {
            Question question = submissionDetail.getQuestion();
            if (question.getType().getCode().equals(QuestionTypeEnum.SINGLE_CHOICE.getCode())) {
                List<Answer> answers = answerRepository.findAllByQuestionId(question.getId());
                Answer correctAnswer = answers.stream()
                        .filter(Answer::getIsCorrect)
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy đáp án đúng!"));
                if (correctAnswer.getContent().equals(submissionDetail.getAnswer())) {
                    score += 1;
                }
            } else if (question.getType().getCode().equals(QuestionTypeEnum.MULTIPLE_CHOICE.getCode())) {
                List<Answer> answers = answerRepository.findAllByQuestionId(question.getId());
                List<Answer> correctAnswers = answers.stream()
                        .filter(Answer::getIsCorrect)
                        .toList();
                Set<String> correctAnswerContents = correctAnswers.stream()
                        .map(Answer::getContent)
                        .collect(Collectors.toSet());

                Set<String> submissionAnswerContents = new HashSet<>(Arrays.asList(submissionDetail.getAnswer().split(",")));

                if (correctAnswerContents.equals(submissionAnswerContents)) {
                    score += 1;
                }

            } else if (question.getType().getCode().equals(QuestionTypeEnum.ESSAY.getCode())) {
                List<Answer> answers = answerRepository.findAllByQuestionId(question.getId());
                Answer correctAnswer = answers.stream()
                        .filter(Answer::getIsCorrect)
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy đáp án đúng!"));
                if (correctAnswer.getContent().equals(submissionDetail.getAnswer())) {
                    score += 1;
                }
            }
        }
        submission.setScore((float) score / submissionDetails.size() * 10);
        submissionRepository.save(submission);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Nộp bài thi thành công!")
                .result(mapperService.mapToSubmissionDto(submission))
                .build());
    }
}
