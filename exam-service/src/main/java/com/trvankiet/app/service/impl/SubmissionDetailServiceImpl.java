package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.request.DeleteSubmissionDetailRequest;
import com.trvankiet.app.dto.request.SubmissionDetailUpdateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.SubmissionDetailResponse;
import com.trvankiet.app.dto.response.SubmissionResponse;
import com.trvankiet.app.entity.QuestionType;
import com.trvankiet.app.entity.Submission;
import com.trvankiet.app.entity.SubmissionDetail;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.QuestionTypeRepository;
import com.trvankiet.app.repository.SubmissionDetailRepository;
import com.trvankiet.app.repository.SubmissionRepository;
import com.trvankiet.app.service.AnswerService;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.SubmissionDetailService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubmissionDetailServiceImpl implements SubmissionDetailService {
    private final SubmissionDetailRepository submissionDetailRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final SubmissionRepository submissionRepository;
    private final MapperService mapperService;
    private final AnswerService answerService;

    @Override
    public ResponseEntity<String> updateSubmissionDetail(String userId, SubmissionDetailUpdateRequest submissionDetailUpdateRequest) {
        log.info("SubmissionDetailServiceImpl, updateSubmissionDetail");
        SubmissionDetail submissionDetail = submissionDetailRepository.findById(submissionDetailUpdateRequest.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy submission detail!"));
        if (!isValidRequest(submissionDetail, userId)) {
            throw new ForbiddenException("Không có quyền cập nhật submission detail!");
        }
        if (submissionDetailUpdateRequest.getAnswer() == null) {
            submissionDetail.setAnswer(null);
        }
        submissionDetail.setAnswer(submissionDetailUpdateRequest.getAnswer());
        submissionDetail.setUpdatedAt(new Date());
        submissionDetailRepository.save(submissionDetail);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    @Override
    public ResponseEntity<String> deleteAnswer(String userId, DeleteSubmissionDetailRequest deleteSubmissionDetailRequest) {
        log.info("SubmissionDetailServiceImpl, deleteAnswer");
        SubmissionDetail submissionDetail = submissionDetailRepository.findById(deleteSubmissionDetailRequest.getSubmissionDetailId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy submission detail!"));
        if (!isValidRequest(submissionDetail, userId)) {
            throw new ForbiddenException("Không có quyền xóa câu trả lời!");
        }
        submissionDetail.setAnswer(null);
        submissionDetail.setUpdatedAt(new Date());
        submissionDetailRepository.save(submissionDetail);
        return ResponseEntity.ok("Xóa câu trả lời thành công!");
    }

    @Override
    public ResponseEntity<GenericResponse> getSubmissionDetail(String userId, String submissionId) {
        log.info("SubmissionDetailServiceImpl, getSubmissionDetail");
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy submission!"));
        // not need to validate user is author of submission because it is validated in SubmissionController
        List<SubmissionDetail> submissionDetails = submissionDetailRepository.findAllBySubmissionId(submissionId);
        SubmissionResponse submissionResponse = SubmissionResponse.builder()
                .submissionDto(mapperService.mapToSubmissionDto(submission))
                .submissionDetailResponses(submissionDetails.stream()
                        .map(submissionDetail -> SubmissionDetailResponse.builder()
                                .id(submissionDetail.getId())
                                .question(submissionDetail.getQuestion().getContent())
                                .userAnswer(submissionDetail.getAnswer())
                                .correctAnswer(answerService.getCorrectAnswer(submissionDetail.getQuestion().getId()))
                                .createdAt(submissionDetail.getCreatedAt() != null ? DateUtil.date2String(submissionDetail.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT) : null)
                                .updatedAt(submissionDetail.getUpdatedAt() != null ? DateUtil.date2String(submissionDetail.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT) : null)
                                .build())
                        .toList())
                .build();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy submission detail thành công!")
                .result(submissionResponse)
                .build());
    }

    public Boolean isValidRequest(SubmissionDetail submissionDetail, String userId) {
        Date now = new Date();
        if (now.before(submissionDetail.getSubmission().getStartedAt())) {
            return false;
        }
        // check if now if after the submission startedAt + exam duration
        Date submissionEndedAt = new Date(submissionDetail.getSubmission().getStartedAt().getTime() + submissionDetail.getSubmission().getExam().getDuration() * 60 * 1000L);
        if (submissionDetail.getSubmission().getEndedAt() != null || now.after(submissionEndedAt)) {
            return false;
        }
        return submissionDetail.getSubmission().getAuthorId().equals(userId);
    }
}
