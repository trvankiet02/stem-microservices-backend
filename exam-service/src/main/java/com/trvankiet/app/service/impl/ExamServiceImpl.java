package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.constant.QuestionTypeEnum;
import com.trvankiet.app.dto.request.CreateAnswerRequest;
import com.trvankiet.app.dto.request.CreateExamRequest;
import com.trvankiet.app.dto.request.CreateQuestionRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Exam;
import com.trvankiet.app.entity.Question;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.AnswerRepository;
import com.trvankiet.app.repository.ExamRepository;
import com.trvankiet.app.repository.QuestionRepository;
import com.trvankiet.app.repository.QuestionTypeRepository;
import com.trvankiet.app.service.ExamService;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.client.GroupMemberClientService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.wp.usermodel.Paragraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final MapperService mapperService;
    private final GroupMemberClientService groupMemberClientService;

    @Override
    public ResponseEntity<GenericResponse> findAllExams() {
        log.info("ExamServiceImpl, findAllExams");
        return ResponseEntity.ok().body(GenericResponse.builder()
                .result(true)
                .statusCode(200)
                .message("Success")
                .result(examRepository.findAll().stream().map(mapperService::mapToExamDto).toList())
                .build()
        );
    }

    @Override
    public ResponseEntity<GenericResponse> createExam(String userId, CreateExamRequest createExamRequest) throws ParseException {
        log.info("ExamServiceImpl, createExam");
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(createExamRequest.getGroupId(), userId);
        if (role.equals("GROUP_OWNER")) {
            Exam exam = examRepository.save(
                    Exam.builder()
                            .id(UUID.randomUUID().toString())
                            .groupId(createExamRequest.getGroupId())
                            .name(createExamRequest.getName())
                            .description(createExamRequest.getDescription())
                            .duration(createExamRequest.getDuration())
                            .staredAt(DateUtil.string2Date(createExamRequest.getStaredAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                            .endedAt(DateUtil.string2Date(createExamRequest.getEndedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                            .isEnabled(createExamRequest.getIsEnabled())
                            .level(createExamRequest.getLevel())
                            .maxScore(createExamRequest.getMaxScore())
                            .createdAt(new Date())
                            .build());
            createExamRequest.getQuestions().forEach(questionRequest -> {
                Question question = questionRepository.save(
                        Question.builder()
                                .id(UUID.randomUUID().toString())
                                .content(questionRequest.getContent())
                                .level(questionRequest.getLevel())
                                .exam(exam)
                                .type(questionTypeRepository.findByCode(questionRequest.getTypeCode())
                                        .orElseThrow(() -> new NotFoundException("Question type not found")))
                                .createdAt(new Date())
                                .build());
                questionRequest.getAnswers().forEach(answerRequest -> {
                    answerRepository.save(
                            com.trvankiet.app.entity.Answer.builder()
                                    .id(UUID.randomUUID().toString())
                                    .content(answerRequest.getContent())
                                    .isCorrect(answerRequest.getIsCorrect())
                                    .question(question)
                                    .createdAt(new Date())
                                    .build());
                });
            });
            return ResponseEntity.ok().body(
                    GenericResponse.builder()
                            .result(true)
                            .statusCode(200)
                            .message("Success")
                            .result(mapperService.mapToExamDto(exam))
                            .build()
            );
        }
        throw new ForbiddenException("You are not allowed to create exam");
    }

    @Override
    public ResponseEntity<CreateExamRequest> importFromDocOrDocx(String userId, MultipartFile multipartFile) {
        log.info("ExamServiceImpl, importFromDocOrDocx");
        if (multipartFile.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        // check file is doc or docx
        if (!multipartFile.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                && !multipartFile.getContentType().equals("application/msword")) {
            throw new BadRequestException("File is not doc or docx");
        }

        List<CreateQuestionRequest> questionRequests = new ArrayList<>();

        try (InputStream fileInputStream = multipartFile.getInputStream()) {
            XWPFDocument document = new XWPFDocument(fileInputStream);

            CreateQuestionRequest currentQuestion = null;
            List<CreateAnswerRequest> currentAnswers = new ArrayList<>();

            // Lặp qua các đoạn văn bản trong tài liệu Word
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();

                // Kiểm tra và trích xuất thông tin câu hỏi
                if (text.matches("Câu \\d+: .*")) {
                    // Nếu đã có một câu hỏi trước đó, thêm nó vào danh sách câu hỏi
                    if (currentQuestion != null) {
                        currentQuestion.setAnswers(currentAnswers);
                        // check if list answer has two or more true answer set typeCode multiple_choice
                        if (currentAnswers.stream().filter(CreateAnswerRequest::getIsCorrect).count() > 1) {
                            currentQuestion.setTypeCode(QuestionTypeEnum.MULTIPLE_CHOICE.getCode());
                        } else {
                            currentQuestion.setTypeCode(QuestionTypeEnum.SINGLE_CHOICE.getCode());
                        }
                        questionRequests.add(currentQuestion);
                    }

                    // Tạo câu hỏi mới
                    currentQuestion = new CreateQuestionRequest();
                    currentQuestion.setContent(text.substring(text.indexOf(":") + 2).trim());

                    // Khởi tạo danh sách câu trả lời mới cho câu hỏi mới
                    currentAnswers = new ArrayList<>();
                }
                // Kiểm tra và trích xuất thông tin câu trả lời
                else {
                    CreateAnswerRequest answerRequest = new CreateAnswerRequest();
                    answerRequest.setContent(text.trim()); // Lấy phần nội dung của câu trả lời

                    // Kiểm tra xem câu trả lời có được in đậm không và đặt giá trị isCorrect tương ứng
                    answerRequest.setIsCorrect(isCorrectAnswer(paragraph));

                    // Thêm câu trả lời vào danh sách câu trả lời hiện tại
                    currentAnswers.add(answerRequest);
                }
            }

            // Thêm câu hỏi cuối cùng vào danh sách câu hỏi
            if (currentQuestion != null) {
                currentQuestion.setAnswers(currentAnswers);
                questionRequests.add(currentQuestion);
            }
            return ResponseEntity.ok().body(CreateExamRequest.builder()
                    .name("Exam from doc or docx")
                    .description("Exam from doc or docx")
                    .duration(60)
                    .staredAt(DateUtil.date2String(new Date(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                    .endedAt(DateUtil.date2String(new Date(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                    .isEnabled(true)
                    .level("Medium")
                    .maxScore(10)
                    .questions(questionRequests)
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<GenericResponse> findExamById(String examId) {
        log.info("ExamServiceImpl, findExamById");
        return ResponseEntity.ok().body(
                GenericResponse.builder()
                        .result(true)
                        .statusCode(200)
                        .message("Success")
                        .result(mapperService.mapToExamDto(examRepository.findById(examId)
                                .orElseThrow(() -> new NotFoundException("Exam not found"))))
                        .build()
        );
    }

    private static boolean isCorrectAnswer(XWPFParagraph paragraph) {
        // Kiểm tra xem đoạn văn bản có được in đậm hay không
        for (XWPFRun run : paragraph.getRuns()) {
            if (run.isBold()) {
                return true;
            }
        }
        return false;
    }
}
