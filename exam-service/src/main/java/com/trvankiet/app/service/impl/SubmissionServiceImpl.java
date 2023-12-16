package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.QuestionTypeEnum;
import com.trvankiet.app.dto.*;
import com.trvankiet.app.dto.response.AnswerSubmissionResponse;
import com.trvankiet.app.dto.response.ExamSubmissionResponse;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.QuestionSubmissionResponse;
import com.trvankiet.app.entity.*;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.*;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.SubmissionService;
import com.trvankiet.app.service.client.GroupMemberClientService;
import com.trvankiet.app.service.client.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserClientService userClientService;


    @Override
    public ResponseEntity<GenericResponse> createSubmission(String userId, String examId) {
        log.info("SubmissionServiceImpl, createSubmission");

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài thi!"));

        Optional<Submission> existSubmission = submissionRepository.findByExamIdAndAuthorId(examId, userId);
        if (existSubmission.isPresent()) {
            return continueSubmissionById(userId, existSubmission.get().getId());
        }

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
                .createdAt(now)
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
            List<AnswerSubmissionResponse> answers = answerRepository.findAllByQuestionId(question.getId())
                    .stream()
                    .map(answer -> AnswerSubmissionResponse.builder()
                            .answer(answer.getContent())
                            .isChecked(false)
                            .build())
                    .toList();
            QuestionSubmissionResponse questionSubmissionResponse = QuestionSubmissionResponse.builder()
                    .submissionDetailId(submissionDetail.getId())
                    .content(submissionDetail.getQuestion().getContent())
                    .level(submissionDetail.getQuestion().getLevel())
                    .typeCode(submissionDetail.getQuestion().getType().getCode())
                    .answers(answers)
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
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này!");
        }

        Date now = new Date();
        submission.setEndedAt(now);

        Set<String> correctAnswerContents = new HashSet<>();
        List<SubmissionDetail> submissionDetails = submissionDetailRepository.findAllBySubmissionId(submissionId);
        int score = 0;

        for (SubmissionDetail submissionDetail : submissionDetails) {
            // Skip question if user not answer
            if (submissionDetail.getAnswer() == null) continue;

            Question question = submissionDetail.getQuestion();
            correctAnswerContents.clear();

            if (question.getType().getCode().equals(QuestionTypeEnum.SINGLE_CHOICE.getCode())) {
                Answer correctAnswer = findCorrectAnswer(question.getId());
                correctAnswerContents.add(correctAnswer.getContent());
            } else if (question.getType().getCode().equals(QuestionTypeEnum.MULTIPLE_CHOICE.getCode())) {
                List<Answer> correctAnswers = findCorrectAnswers(question.getId());
                correctAnswerContents.addAll(correctAnswers.stream().map(Answer::getContent).collect(Collectors.toSet()));
            } else if (question.getType().getCode().equals(QuestionTypeEnum.ESSAY.getCode())) {
                Answer correctAnswer = findCorrectAnswer(question.getId());
                correctAnswerContents.add(correctAnswer.getContent());
            }

            boolean checkedValue = correctAnswerContents.equals(new HashSet<>(Arrays.asList(submissionDetail.getAnswer().split(","))));
            score = updateScoreAndSubmissionDetail(score, checkedValue, submissionDetail);
            submissionDetailRepository.save(submissionDetail);
        }

        submission.setScore((float) score / submissionDetails.size() * submission.getExam().getMaxScore());
        submission.setUpdatedAt(now);
        submissionRepository.save(submission);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Nộp bài thi thành công!")
                .result(mapperService.mapToSubmissionDto(submission))
                .build());
    }

    private Answer findCorrectAnswer(String questionId) {
        return answerRepository.findAllByQuestionId(questionId).stream()
                .filter(Answer::getIsCorrect)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đáp án đúng!"));
    }

    private List<Answer> findCorrectAnswers(String questionId) {
        return answerRepository.findAllByQuestionId(questionId).stream()
                .filter(Answer::getIsCorrect)
                .collect(Collectors.toList());
    }


    private int updateScoreAndSubmissionDetail(int score, boolean checkedValue, SubmissionDetail submissionDetail){
        if (checkedValue) {
            score += 1;
        }
        submissionDetail.setScore(checkedValue ? 1 : 0);
        return score;
    }

    @Override
    public ResponseEntity<GenericResponse> continueSubmissionById(String userId, String submissionId) {
        log.info("SubmissionServiceImpl, getSubmissionById");
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài thi!"));
        if (!isValidContinueRequest(submission)) {
            throw new BadRequestException("Không thể tiếp tục bài thi!");
        }
        if (!submission.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này!");
        }
        List<SubmissionDetail> submissionDetails = submissionDetailRepository.findAllBySubmissionId(submissionId);
        // FOR EACH DETAIL, CHECK THAT IF DETAIL.ANSWER ==  ANY ANSWER SET BOOLEAN TRUE
        // if question tupe is single choice ex answer: 1
        // if question type is multiple choice ex answer: 1,2,3
        // if use contain maybe has an exception that user answer is not in answer list
        ExamSubmissionResponse examSubmissionResponse = ExamSubmissionResponse.builder()
                .submissionId(submission.getId())
                .questionSubmissionResponses(new ArrayList<>())
                .build();
        submissionDetails.forEach(submissionDetail -> {
            List<AnswerSubmissionResponse> answers = answerRepository.findAllByQuestionId(submissionDetail.getQuestion().getId())
                    .stream()
                    .map(answer -> AnswerSubmissionResponse.builder()
                            .answer(answer.getContent())
                            .isChecked(submissionDetail.getAnswer() != null && submissionDetail.getAnswer().contains(answer.getContent()))
                            .build())
                    .toList();
            QuestionSubmissionResponse questionSubmissionResponse = QuestionSubmissionResponse.builder()
                    .submissionDetailId(submissionDetail.getId())
                    .content(submissionDetail.getQuestion().getContent())
                    .level(submissionDetail.getQuestion().getLevel())
                    .typeCode(submissionDetail.getQuestion().getType().getCode())
                    .answers(answers)
                    .build();
            examSubmissionResponse.getQuestionSubmissionResponses().add(questionSubmissionResponse);
        });
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Mời bạn tiếp tục bài thi!")
                .result(examSubmissionResponse)
                .build());
    }

    public Boolean isValidContinueRequest(Submission submission) {
        // check that request is valid between startedAt and endedAt
        // check that submission endedAt is null
        Date now = new Date();
        if (submission.getEndedAt() != null) {
            return false;
        }
        return !now.before(submission.getStartedAt()) && !now.after(submission.getExam().getEndedAt());
    }

    @Transactional
    @Override
    public ResponseEntity<GenericResponse> createSubmissionV2(String userId, String examId) {
        log.info("SubmissionServiceImpl, createSubmission");

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài thi!"));

        Optional<Submission> existingSubmission = submissionRepository.findByExamIdAndAuthorId(examId, userId);
        if (existingSubmission.isPresent()) {
            return continueSubmissionById(userId, existingSubmission.get().getId());
        }

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

        // Fetch a randomized subset of questions
        List<Question> examQuestions = questionRepository.findAllByExamId(exam.getId());
        Collections.shuffle(examQuestions);
        examQuestions = examQuestions.stream()
                .limit(exam.getNumberOfQuestion())
                .toList();

        // Create submission object
        Submission submission = submissionRepository.save(Submission.builder()
                .id(UUID.randomUUID().toString())
                .authorId(userId)
                .startedAt(now)
                .exam(exam)
                .createdAt(now)
                .build());

        // Create SubmissionDetail and AnswerSubmissionResponse objects in batches
        List<SubmissionDetail> submissionDetails = new ArrayList<>();
        List<AnswerSubmissionResponse> answerSubmissionResponses = new ArrayList<>();

        for (Question question : examQuestions) {
            SubmissionDetail submissionDetail = SubmissionDetail.builder()
                    .id(UUID.randomUUID().toString())
                    .question(question)
                    .submission(submission)
                    .createdAt(now)
                    .build();
            submissionDetails.add(submissionDetail);

            List<Answer> answers = answerRepository.findAllByQuestionId(question.getId());
            for (Answer answer : answers) {
                answerSubmissionResponses.add(AnswerSubmissionResponse.builder()
                        .answer(answer.getContent())
                        .isChecked(false)
                        .build());
            }
        }

        // Save SubmissionDetail objects in batches
        submissionDetailRepository.saveAll(submissionDetails);

        // Create ExamSubmissionResponse object
        ExamSubmissionResponse examSubmissionResponse = ExamSubmissionResponse.builder()
                .submissionId(submission.getId())
                .questionSubmissionResponses(new ArrayList<>())
                .build();

        // Add QuestionSubmissionResponse objects to ExamSubmissionResponse
        submissionDetails.forEach(submissionDetail -> {
            List<AnswerSubmissionResponse> answers = answerSubmissionResponses.subList(0, exam.getNumberOfQuestion() - 1);
            answerSubmissionResponses.subList(0, exam.getNumberOfQuestion() - 1).clear();

            QuestionSubmissionResponse questionSubmissionResponse = QuestionSubmissionResponse.builder()
                    .submissionDetailId(submissionDetail.getId())
                    .content(submissionDetail.getQuestion().getContent())
                    .level(submissionDetail.getQuestion().getLevel())
                    .typeCode(submissionDetail.getQuestion().getType().getCode())
                    .answers(answers)
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
    public ResponseEntity<GenericResponse> getListSubmissionByExamId(String userId, String examId) {
        log.info("SubmissionServiceImpl, getListSubmissionByExamId");
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài thi!"));
        List<SubmissionDto> submissions = submissionRepository.findAllByExamId(exam.getId())
                .stream()
                .map(mapperService::mapToSubmissionDto)
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách bài thi thành công!")
                .result(submissions)
                .build());

    }

    @Override
    public ResponseEntity<GenericResponse> getListSubmissionByExamIdForParent(String userId, String examId) {
        log.info("SubmissionServiceImpl, getListSubmissionByExamIdForParent");
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài thi!"));
        UserDto userDto = userClientService.getUserDtoByUserId(userId);
        if (!userDto.getRole().equals("PARENT")) {
            throw new ForbiddenException("Bạn không có quyền truy cập!");
        }
        List<Submission> result = new ArrayList<>();
        if (userDto.getChildren().isEmpty())
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Lấy danh sách bài thi thành công!")
                    .result(result)
                    .build());
        userDto.getChildren().forEach(child -> {
            Optional<Submission> optionalSubmission = submissionRepository.findAllByExamIdAndAuthorId(exam.getId(), child.getId());
            optionalSubmission.ifPresent(result::add);
        });
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách bài thi thành công!")
                .result(result)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getListSubmissionByForParent(String userId) {
        log.info("SubmissionServiceImpl, getListSubmissionByForParent");
        UserDto userDto = userClientService.getUserDtoByUserId(userId);
        if (!userDto.getRole().equals("PARENT")) {
            throw new ForbiddenException("Bạn không có quyền truy cập!");
        }

        if (userDto.getChildren().isEmpty())
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Lấy danh sách bài thi thành công!")
                    .result(new ArrayList<>())
                    .build());
        List<Map<String, Object>> result = new ArrayList<>();
        userDto.getChildren().forEach(child -> {
            Map<String, Object> map = new HashMap<>();
            SimpleUserDto simpleUserDto = userClientService.getSimpleUserDtoByUserId(child.getId());
            map.put("user", simpleUserDto);
            List<SubmissionDto> submissions = submissionRepository.findAllByAuthorId(child.getId())
                    .stream()
                    .map(mapperService::mapToSubmissionDto)
                    .toList();
            map.put("submissions", submissions);
            result.add(map);
        });

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách bài thi thành công!")
                .result(result)
                .build());
    }
}
