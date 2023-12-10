package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.ReactionTypeEnum;
import com.trvankiet.app.dto.ReactionDto;
import com.trvankiet.app.dto.request.ReactionRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Post;
import com.trvankiet.app.entity.Reaction;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.PostRepository;
import com.trvankiet.app.repository.ReactionRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.ReactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactionServiceImpl implements ReactionService {
    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final MapperService mapperService;

    @Override
    public ResponseEntity<GenericResponse> reactPost(String userId, ReactionRequest reactionRequest) {
        log.info("ReactionServiceImpl, reactPost({})", reactionRequest);
        try {
            Post post = postRepository.findById(reactionRequest.getPostId())
                    .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại!"));
            Reaction reaction = reactionRepository.findByAuthorIdAndPostId(userId, reactionRequest.getPostId())
                    .orElse(null);
            if (reaction == null) {
                reaction = Reaction.builder()
                        .id(UUID.randomUUID().toString())
                        .post(post)
                        .authorId(userId)
                        .type(ReactionTypeEnum.valueOf(reactionRequest.getTypeName()))
                        .createdAt(new Date())
                        .build();
                postRepository.save(post);
            } else {
                reaction.setType(ReactionTypeEnum.valueOf(reactionRequest.getTypeName()));
                reaction.setUpdatedAt(new Date());
            }
            reactionRepository.save(reaction);
            return ResponseEntity.ok().body(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Thành công!")
                    .result(mapperService.mapToReactionDto(reaction))
                    .build());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Loại reaction không tồn tại!");
        } catch (Exception e) {
            throw new BadRequestException("Bài viết không tồn tại!");
        }
    }

    @Override
    public ResponseEntity<GenericResponse> deleteReaction(String userId, String reactionId) {
        log.info("ReactionServiceImpl, deleteReaction({})", reactionId);
        Reaction reaction = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new NotFoundException("Reaction không tồn tại!"));
        if (reaction.getAuthorId().equals(userId)) {
            reactionRepository.delete(reaction);
            return ResponseEntity.ok().body(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Thành công!")
                    .result(null)
                    .build());
        }
        throw new ForbiddenException("Bạn không có quyền xóa reaction này!");
    }

    @Override
    public ResponseEntity<GenericResponse> getReactionsByPostId(String postId) {
        log.info("ReactionServiceImpl, getReactionsByPostId({})", postId);
        List<ReactionDto> reactions = reactionRepository.findAllByPostId(postId)
                .stream()
                .map(mapperService::mapToReactionDto)
                .toList();
        return ResponseEntity.ok().body(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Thành công!")
                .result(reactions)
                .build());
    }
}
