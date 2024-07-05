package com.trvankiet.app.service.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.constant.QuestionTypeEnum;
import com.trvankiet.app.dto.ExamCountDTO;
import com.trvankiet.app.dto.ExamDto;
import com.trvankiet.app.dto.SubmissionDto;
import com.trvankiet.app.dto.request.*;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Answer;
import com.trvankiet.app.entity.Exam;
import com.trvankiet.app.entity.Question;
import com.trvankiet.app.entity.Submission;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.*;
import com.trvankiet.app.service.ExamService;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.client.GroupMemberClientService;
import com.trvankiet.app.util.DateUtil;
import com.trvankiet.app.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

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
    private final SubmissionRepository submissionRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<GenericResponse> findAllExams() {
        log.info("ExamServiceImpl, findAllExams");
        return ResponseEntity.ok().body(GenericResponse.builder()
                .result(true)
                .statusCode(200)
                .message("Lấy danh sách đề thi thành công!")
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
                            .startedAt(DateUtil.string2Date(createExamRequest.getStartedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                            .endedAt(DateUtil.string2Date(createExamRequest.getEndedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                            .isEnabled(createExamRequest.getIsEnabled())
                            .numberOfQuestion(createExamRequest.getNumberOfQuestion())
                            .isAutoMark(createExamRequest.getIsAutoMark())
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
                                .score(questionRequest.getScore())
                                .type(questionTypeRepository.findByCode(questionRequest.getTypeCode())
                                        .orElseThrow(() -> new NotFoundException("Loại câu hỏi không tồn tại!")))
                                .createdAt(new Date())
                                .build());
                questionRequest.getAnswers().forEach(answerRequest -> {
                    answerRepository.save(Answer.builder()
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
                            .success(true)
                            .statusCode(200)
                            .message("Success")
                            .result(mapperService.mapToExamDto(exam))
                            .build()
            );
        }
        throw new ForbiddenException("Bạn không có quyền tạo đề thi cho nhóm này!");
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
                    .startedAt(DateUtil.date2String(new Date(), AppConstant.LOCAL_DATE_TIME_FORMAT))
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
    public ResponseEntity<GenericResponse> findExamById(String userId, String examId) {
        log.info("ExamServiceImpl, findExamById");
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Exam not found"));
        Submission submission = submissionRepository.findByExamIdAndAuthorId(examId, userId)
                .orElse(null);
        SubmissionDto submissionDto = submission == null ?
                null : mapperService.mapToSubmissionDto(submission);
        Map<String, Object> result = new HashMap<>();
        result.put("exam", mapperService.mapToExamDto(exam));
        result.put("submission", submissionDto);
        return ResponseEntity.ok().body(
                GenericResponse.builder()
                        .result(true)
                        .statusCode(200)
                        .message("Success")
                        .result(result)
                        .build()
        );
    }

    @Override
    public ResponseEntity<GenericResponse> updateExamDetailByExamId(String userId, String examId, UpdateExamDetailRequest updateExamDetailRequest) throws ParseException {
        log.info("ExamServiceImpl, updateExamDetailByExamId");
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Exam not found"));
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(exam.getGroupId(), userId);
        if (role.equals("GROUP_OWNER")) {
            exam.setName(updateExamDetailRequest.getName());
            exam.setDescription(updateExamDetailRequest.getDescription());
            exam.setDuration(updateExamDetailRequest.getDuration());
            exam.setStartedAt(DateUtil.string2Date(updateExamDetailRequest.getStartedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT));
            exam.setEndedAt(DateUtil.string2Date(updateExamDetailRequest.getEndedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT));
            exam.setIsEnabled(updateExamDetailRequest.getIsEnabled());
            exam.setNumberOfQuestion(updateExamDetailRequest.getNumberOfQuestion());
            exam.setIsAutoMark(updateExamDetailRequest.getIsAutoMark());
            exam.setLevel(updateExamDetailRequest.getLevel());
            exam.setMaxScore(updateExamDetailRequest.getMaxScore());
            exam.setUpdatedAt(new Date());
            examRepository.save(exam);
            return ResponseEntity.ok().body(
                    GenericResponse.builder()
                            .success(true)
                            .statusCode(200)
                            .message("Success")
                            .result(mapperService.mapToExamDto(exam))
                            .build()
            );
        }
        throw new ForbiddenException("You are not allowed to update exam");

    }

    @Override
    public ResponseEntity<GenericResponse> deleteExamById(String userId, String examId) {
        log.info("ExamServiceImpl, deleteExamById");
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Exam not found"));
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(exam.getGroupId(), userId);
        if (role.equals("GROUP_OWNER")) {
            examRepository.deleteById(examId);
            return ResponseEntity.ok().body(
                    GenericResponse.builder()
                            .result(true)
                            .statusCode(200)
                            .message("Xoá đề thi thành công!")
                            .build()
            );
        }
        throw new ForbiddenException("Bạn không có quyền xoá đề thi này!");
    }

    @Override
    public List<ExamDto> searchExam(Optional<String> query, Optional<String> level) {
        log.info("ExamServiceImpl, searchExam");
        if (query.isPresent() && level.isPresent()) {
            return examRepository.searchExam(query.get()).stream()
                    .filter(exam -> exam.getLevel().equals(level.get()))
                    .map(mapperService::mapToExamDto)
                    .toList();
        } else {
            return query.map(s -> examRepository.searchExam(s).stream()
                            .map(mapperService::mapToExamDto)
                            .toList())
                    .orElseGet(() -> level.map(s -> examRepository.findAll().stream()
                                    .filter(exam -> exam.getLevel().equals(s))
                                    .map(mapperService::mapToExamDto)
                                    .toList())
                            .orElseGet(() -> examRepository.findAll().stream()
                                    .map(mapperService::mapToExamDto)
                                    .toList()));

        }
    }

    @Override
    public ResponseEntity<GenericResponse> findAllExams(String userId, String groupId) {
        log.info("ExamServiceImpl, findAllExams");
        String role = groupMemberClientService.getRoleByGroupIdAndUserId(groupId, userId);
        if (role.isEmpty())
            throw new NotFoundException("Group not found");
        List<Exam> exams = examRepository.findAllByGroupId(groupId);
        if (exams.isEmpty())
            return ResponseEntity.ok().body(
                    GenericResponse.builder()
                            .result(true)
                            .statusCode(200)
                            .message("Lấy danh sách đề thi thành công!")
                            .result(List.of())
                            .build()
            );
        List<Map<String, Object>> result = exams.stream()
                .map(exam -> {
                    Submission submission = submissionRepository.findByExamIdAndAuthorId(exam.getId(), userId)
                            .orElse(null);
                    SubmissionDto submissionDto = submission == null ?
                            null : mapperService.mapToSubmissionDto(submission);
                    Map<String, Object> map = new HashMap<>();
                    map.put("exam", mapperService.mapToExamDto(exam));
                    map.put("submission", submissionDto);
                    return map;
                })
                .toList();

        return ResponseEntity.ok().body(
                GenericResponse.builder()
                        .result(true)
                        .statusCode(200)
                        .message("Lấy danh sách đề thi thành công!")
                        .result(result)
                        .build()
        );
    }

    @Override
    public ResponseEntity<GenericResponse> importFromExcelOrXlsx(String userId, CreateExamByExcelRequest createExamByExcelRequest) {
        log.info("ExamServiceImpl, importFromExcelOrXlsx");

        Workbook workbook = null;
        MultipartFile multipartFile = createExamByExcelRequest.getMultipartFile();
        try {
            if (multipartFile.isEmpty()) {
                throw new BadRequestException("File is empty");
            }
            // check file is excel or xlsx
            if (multipartFile.getOriginalFilename().endsWith(".xls")) {
                workbook = new HSSFWorkbook(multipartFile.getInputStream());
            } else if (multipartFile.getOriginalFilename().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(multipartFile.getInputStream());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (workbook == null) {
            throw new BadRequestException("File is not excel or xlsx");
        }

        Sheet sheet = workbook.getSheetAt(0);

        List<CreateQuestionRequest> questionRequests = new ArrayList<>();

        int maxScore = 0;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row currentRow = sheet.getRow(i);
            CreateQuestionRequest questionRequest = new CreateQuestionRequest();
            questionRequest.setContent(currentRow.getCell(0).getStringCellValue());
            questionRequest.setLevel(currentRow.getCell(1).getStringCellValue());
            questionRequest.setScore((int) currentRow.getCell(2).getNumericCellValue());

            List<CreateAnswerRequest> answerRequests = new ArrayList<>();
            int correctAnswerCount = 0;
            for (int j = 3; j < currentRow.getLastCellNum(); j++) {
                String content = ExcelUtil.getCellValueAsString(currentRow.getCell(j));
                // if content has Bold style then set isCorrect = true
                boolean isCorrect = ExcelUtil.isCellBold(currentRow.getCell(j).getCellStyle(), workbook);
                if (isCorrect) {
                    correctAnswerCount++;
                }
                CreateAnswerRequest answerRequest = new CreateAnswerRequest();
                answerRequest.setContent(content);
                answerRequest.setIsCorrect(isCorrect);
                answerRequests.add(answerRequest);
            }
            questionRequest.setAnswers(answerRequests);
            if (correctAnswerCount == 0) {
                questionRequest.setTypeCode(QuestionTypeEnum.ESSAY.getCode());
            } else if (correctAnswerCount == 1) {
                questionRequest.setTypeCode(QuestionTypeEnum.MULTIPLE_CHOICE.getCode());
            } else {
                questionRequest.setTypeCode(QuestionTypeEnum.SINGLE_CHOICE.getCode());
            }
            questionRequests.add(questionRequest);
            maxScore += questionRequest.getScore();
        }
        CreateExamRequest createExamRequest = CreateExamRequest.builder()
                .groupId(createExamByExcelRequest.getGroupId())
                .name(createExamByExcelRequest.getName())
                .description(createExamByExcelRequest.getDescription())
                .duration(createExamByExcelRequest.getDuration())
                .startedAt(createExamByExcelRequest.getStartedAt())
                .endedAt(createExamByExcelRequest.getEndedAt())
                .isEnabled(createExamByExcelRequest.getIsEnabled())
                .numberOfQuestion(questionRequests.size())
                .isAutoMark(createExamByExcelRequest.getIsAutoMark())
                .level(createExamByExcelRequest.getLevel())
                .maxScore(maxScore)
                .questions(questionRequests)
                .build();

        try {
            return this.createExam(userId, createExamRequest);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<GenericResponse> getTop5Exam() {
        log.info("ExamServiceImpl, getTop5Exam");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("exam_id").count().as("count"),
                Aggregation.sort(Sort.by(Sort.Order.desc("count"))),
                Aggregation.limit(5),
                Aggregation.project("count").and("_id").as("examId")
        );

        AggregationResults<ExamCountDTO> result = mongoTemplate.aggregate(aggregation, Submission.class, ExamCountDTO.class);
        List<ExamCountDTO> top5Exams = result.getMappedResults();

        List<Map<String, Object>> examResult = new ArrayList<>();

        for (ExamCountDTO examCountDTO : top5Exams) {
            Exam exam = examRepository.findById(examCountDTO.getExamId())
                            .orElse(null);
            if (exam != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("exam", mapperService.mapToExamDto(exam));
                map.put("count", examCountDTO.getCount());
                examResult.add(map);
            }
        }
        return ResponseEntity.ok().body(
                GenericResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Success")
                        .result(examResult)
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
