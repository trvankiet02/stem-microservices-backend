package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.request.ReactionRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Post;
import com.trvankiet.app.entity.Reaction;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.PostRepository;
import com.trvankiet.app.repository.ReactionRepository;
import com.trvankiet.app.repository.ReactionTypeRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.ReactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactionServiceImpl implements ReactionService {
    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final ReactionTypeRepository reactionTypeRepository;
    private final MapperService mapperService;

    @Override
    public ResponseEntity<GenericResponse> reactPost(String userId, ReactionRequest reactionRequest) {
        log.info("ReactionServiceImpl, reactPost({})", reactionRequest);
        Post post = postRepository.findById(reactionRequest.getPostId())
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại!"));
        Reaction reaction = post.getReactions().stream()
                .filter(r -> r.getAuthorId().equals(userId))
                .findFirst()
                .orElse(null);
        if (reaction == null) {
            reaction = Reaction.builder()
                    .reactionId(UUID.randomUUID().toString())
                    .authorId(userId)
                    .reactionType(reactionTypeRepository.findByReactionTypeName(reactionRequest.getReactionType())
                            .orElseThrow(() -> new NotFoundException("Loại reaction không tồn tại!")))
                    .createdAt(new Date())
                    .build();
            post.getReactions().add(reaction);
            postRepository.save(post);
        } else {
            reaction.setReactionType(reactionTypeRepository.findByReactionTypeName(reactionRequest.getReactionType())
                    .orElseThrow(() -> new NotFoundException("Loại reaction không tồn tại!")));
            reaction.setUpdatedAt(new Date());
        }
        reactionRepository.save(reaction);
        return ResponseEntity.ok().body(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Thành công!")
                .result(mapperService.mapToReactionDto(reaction))
                .build());
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
}
