package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.AnswerDto;
import com.trvankiet.app.dto.QuestionDto;
import com.trvankiet.app.dto.request.CreateAnswerRequest;
import com.trvankiet.app.dto.request.CreateQuestionRequest;
import com.trvankiet.app.dto.request.UpdateQuestionDetailRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.QuestionDetailResponse;
import com.trvankiet.app.entity.Answer;
import com.trvankiet.app.entity.Exam;
import com.trvankiet.app.entity.Question;
import com.trvankiet.app.entity.QuestionType;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.AnswerRepository;
import com.trvankiet.app.repository.ExamRepository;
import com.trvankiet.app.repository.QuestionRepository;
import com.trvankiet.app.repository.QuestionTypeRepository;
import com.trvankiet.app.service.AnswerService;
import com.trvankiet.app.service.ExamService;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.QuestionService;
import com.trvankiet.app.service.client.GroupMemberClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final MapperService mapperService;
    private final GroupMemberClientService groupMemberClientService;
    private final QuestionTypeRepository questionTypeRepository;

    @Override
    public ResponseEntity<GenericResponse> getExamQuestions(String userId, String examId) {
        log.info("QuestionServiceImpl, getExamQuestions, ResponseEntity<GenericResponse>");
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đề thi với id: " + examId));
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(exam.getGroupId(), userId);
        if (!role.equals("GROUP_OWNER"))
            throw new ForbiddenException("Bạn không có quyền truy cập vào đề thi này!");
        List<QuestionDto> questionDtos = questionRepository.findAllByExamId(examId)
                .stream()
                .map(mapperService::mapToQuestionDto)
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách câu hỏi thành công!")
                .result(questionDtos)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getQuestion(String userId, String questionId) {
        log.info("QuestionServiceImpl, getQuestion, ResponseEntity<GenericResponse>");
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu hỏi với id: " + questionId));
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(question.getExam().getGroupId(), userId);
        if (!role.equals("GROUP_OWNER"))
            throw new ForbiddenException("Bạn không có quyền truy cập vào câu hỏi này!");
        QuestionDto questionDto = mapperService.mapToQuestionDto(question);
        List<Answer> answers = answerRepository.findAllByQuestionId(questionId);
        List<AnswerDto> answerDtos = answers.stream()
                .map(mapperService::mapToAnswerDto)
                .toList();
        QuestionDetailResponse questionDetailResponse = QuestionDetailResponse.builder()
                .questionDto(questionDto)
                .answerDtos(answerDtos)
                .build();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy câu hỏi thành công!")
                .result(questionDetailResponse)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateQuestionByQuestionId(String userId, String questionId, UpdateQuestionDetailRequest updateQuestionDetailRequest) {
        log.info("QuestionServiceImpl, updateQuestionByQuestionId, ResponseEntity<GenericResponse>");
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu hỏi với id: " + questionId));
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(question.getExam().getGroupId(), userId);
        if (!role.equals("GROUP_OWNER"))
            throw new ForbiddenException("Bạn không có quyền truy cập vào câu hỏi này!");
        QuestionType questionType = questionTypeRepository.findByCode(updateQuestionDetailRequest.getTypeCode())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại câu hỏi với code: " + updateQuestionDetailRequest.getTypeCode()));
        question.setContent(updateQuestionDetailRequest.getContent());
        question.setLevel(updateQuestionDetailRequest.getLevel());
        question.setType(questionType);
        question.setUpdatedAt(new Date());
        question = questionRepository.save(question);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật câu hỏi thành công!")
                .result(mapperService.mapToQuestionDto(question))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteQuestionByQuestionId(String userId, String questionId) {
        log.info("QuestionServiceImpl, deleteQuestionByQuestionId, ResponseEntity<GenericResponse>");
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu hỏi với id: " + questionId));
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(question.getExam().getGroupId(), userId);
        if (!role.equals("GROUP_OWNER"))
            throw new ForbiddenException("Bạn không có quyền truy cập vào câu hỏi này!");
        List<Answer> answers = answerRepository.findAllByQuestionId(questionId);
        answerRepository.deleteAll(answers);
        questionRepository.delete(question);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Xóa câu hỏi thành công!")
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateQuestionByQuestionIdAndBody(String userId, String questionId, CreateQuestionRequest createQuestionRequest) {
        log.info("QuestionServiceImpl, updateQuestionByQuestionIdAndBody, ResponseEntity<GenericResponse>");
        Question oldQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu hỏi với id: " + questionId));
        List<Answer> oldAnswers = answerRepository.findAllByQuestionId(questionId);
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(oldQuestion.getExam().getGroupId(), userId);
        if (!role.equals("GROUP_OWNER"))
            throw new ForbiddenException("Bạn không có quyền truy cập vào câu hỏi này!");
        QuestionType questionType = questionTypeRepository.findByCode(createQuestionRequest.getTypeCode())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại câu hỏi với code: " + createQuestionRequest.getTypeCode()));

        try {
            Question newQuestion = questionRepository.save(Question.builder()
                    .id(UUID.randomUUID().toString())
                    .content(createQuestionRequest.getContent())
                    .level(createQuestionRequest.getLevel())
                    .exam(oldQuestion.getExam())
                    .type(questionType)
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .build());
            for (CreateAnswerRequest answerRequest: createQuestionRequest.getAnswers()) {
                answerRepository.save(Answer.builder()
                        .id(UUID.randomUUID().toString())
                        .content(answerRequest.getContent())
                        .isCorrect(answerRequest.getIsCorrect())
                        .question(newQuestion)
                        .createdAt(new Date())
                        .updatedAt(new Date())
                        .build());
            }
            answerRepository.deleteAll(oldAnswers);
            questionRepository.delete(oldQuestion);
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Cập nhật câu hỏi thành công!")
                    .result(mapperService.mapToQuestionDto(newQuestion))
                    .build());
        } catch (Exception e) {
            log.error("QuestionServiceImpl, updateQuestionByQuestionIdAndBody, Exception: " + e.getMessage());
            throw new BadRequestException("Cập nhật câu hỏi thất bại!");
        }
    }

    @Override
    public ResponseEntity<GenericResponse> createQuestion(String userId, String examId, CreateQuestionRequest createQuestionRequest) {
        log.info("QuestionServiceImpl, createQuestion, ResponseEntity<GenericResponse>");
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đề thi với id: " + examId));
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(exam.getGroupId(), userId);
        if (!role.equals("GROUP_OWNER"))
            throw new ForbiddenException("Bạn không có quyền truy cập vào đề thi này!");
        QuestionType questionType = questionTypeRepository.findByCode(createQuestionRequest.getTypeCode())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại câu hỏi với code: " + createQuestionRequest.getTypeCode()));
        try {
            Question question = questionRepository.save(Question.builder()
                    .id(UUID.randomUUID().toString())
                    .content(createQuestionRequest.getContent())
                    .level(createQuestionRequest.getLevel())
                    .exam(exam)
                    .type(questionType)
                    .createdAt(new Date())
                    .build());
            for (CreateAnswerRequest answerRequest: createQuestionRequest.getAnswers()) {
                answerRepository.save(Answer.builder()
                        .id(UUID.randomUUID().toString())
                        .content(answerRequest.getContent())
                        .isCorrect(answerRequest.getIsCorrect())
                        .question(question)
                        .createdAt(new Date())
                        .build());
            }
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Tạo câu hỏi thành công!")
                    .result(mapperService.mapToQuestionDto(question))
                    .build());
        } catch (Exception e) {
            log.error("QuestionServiceImpl, createQuestion, Exception: " + e.getMessage());
            throw new BadRequestException("Tạo câu hỏi thất bại!");
        }

    }
}
