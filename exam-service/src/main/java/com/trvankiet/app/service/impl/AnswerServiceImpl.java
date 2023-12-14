package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.AnswerDto;
import com.trvankiet.app.dto.request.UpdateAnswerDetailRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Answer;
import com.trvankiet.app.entity.Question;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.repository.AnswerRepository;
import com.trvankiet.app.repository.QuestionRepository;
import com.trvankiet.app.service.AnswerService;
import com.trvankiet.app.service.MapperService;
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
public class AnswerServiceImpl implements AnswerService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final GroupMemberClientService groupMemberClientService;
    private final MapperService mapperService;

    @Override
    public ResponseEntity<GenericResponse> getAnswersByQuestionId(String userId, String questionId) {
        log.info("AnswerServiceImpl, getAnswersByQuestionId, ResponseEntity<GenericResponse>");
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Câu hỏi không tồn tại!"));
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(question.getExam().getGroupId(), userId);
        if (!role.equals("GROUP_OWNER"))
            throw new ForbiddenException("Bạn không có quyền truy cập!");
        List<Answer> answers = answerRepository.findAllByQuestionId(questionId);
        List<AnswerDto> answerDtos = answers.stream().map(mapperService::mapToAnswerDto).toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách câu trả lời thành công!")
                .result(answerDtos)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getAnswer(String userId, String answerId) {
        log.info("AnswerServiceImpl, getAnswer, ResponseEntity<GenericResponse>");
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Câu trả lời không tồn tại!"));
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(answer.getQuestion().getExam().getGroupId(), userId);
        if (!role.equals("GROUP_OWNER"))
            throw new ForbiddenException("Bạn không có quyền truy cập!");
        AnswerDto answerDto = mapperService.mapToAnswerDto(answer);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy câu trả lời thành công!")
                .result(answerDto)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateAnswerByAnswerId(String userId, String answerId, UpdateAnswerDetailRequest updateAnswerDetailRequest) {
        log.info("AnswerServiceImpl, updateAnswerByAnswerId, ResponseEntity<GenericResponse>");
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Câu trả lời không tồn tại!"));
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(answer.getQuestion().getExam().getGroupId(), userId);
        if (!role.equals("GROUP_OWNER"))
            throw new ForbiddenException("Bạn không có quyền truy cập!");
        answer.setContent(updateAnswerDetailRequest.getContent());
        answer.setIsCorrect(updateAnswerDetailRequest.getIsCorrect());
        answer.setUpdatedAt(new Date());
        answer = answerRepository.save(answer);
        AnswerDto answerDto = mapperService.mapToAnswerDto(answer);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật câu trả lời thành công!")
                .result(answerDto)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteAnswerByAnswerId(String userId, String answerId) {
        log.info("AnswerServiceImpl, deleteAnswerByAnswerId, ResponseEntity<GenericResponse>");
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Câu trả lời không tồn tại!"));
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(answer.getQuestion().getExam().getGroupId(), userId);
        if (!role.equals("GROUP_OWNER"))
            throw new ForbiddenException("Bạn không có quyền truy cập!");
        answerRepository.delete(answer);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Xóa câu trả lời thành công!")
                .build());
    }

    @Override
    public String getCorrectAnswer(String questionId) {
        log.info("AnswerServiceImpl, getCorrectAnswer, String");
        Answer answer = answerRepository.findByQuestionIdAndIsCorrectTrue(questionId)
                .orElse(null);
        if (answer == null) {
            return "***";
        }
        return answer.getContent();
    }

    @Override
    public ResponseEntity<GenericResponse> createAnswer(String userId, String qId, UpdateAnswerDetailRequest updateAnswerDetailRequest) {
        log.info("AnswerServiceImpl, createAnswer, ResponseEntity<GenericResponse>");
        Question question = questionRepository.findById(qId)
                .orElseThrow(() -> new RuntimeException("Câu hỏi không tồn tại!"));
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(question.getExam().getGroupId(), userId);
        if (!role.equals("GROUP_OWNER"))
            throw new ForbiddenException("Bạn không có quyền truy cập!");
        Answer answer = answerRepository.save(Answer.builder()
                .id(UUID.randomUUID().toString())
                .content(updateAnswerDetailRequest.getContent())
                .isCorrect(updateAnswerDetailRequest.getIsCorrect())
                .question(question)
                .createdAt(new Date())
                .build());

        AnswerDto answerDto = mapperService.mapToAnswerDto(answer);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Tạo câu trả lời thành công!")
                .result(answerDto)
                .build());
    }
}
