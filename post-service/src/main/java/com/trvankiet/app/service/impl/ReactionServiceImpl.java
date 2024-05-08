package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.ReactionTypeEnum;
import com.trvankiet.app.dto.ReactionDto;
import com.trvankiet.app.dto.SimpleUserDto;
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
import com.trvankiet.app.service.client.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactionServiceImpl implements ReactionService {
    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final MapperService mapperService;
    private final UserClientService userClientService;

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
            } else {
                reaction.setType(ReactionTypeEnum.valueOf(reactionRequest.getTypeName()));
                reaction.setUpdatedAt(new Date());
            }
            reactionRepository.save(reaction);
            Map<String, Object> result = Map.of("reaction", mapperService.mapToReactionDto(reaction),
                    "count", reactionRepository.countByPostId(reactionRequest.getPostId()));
            return ResponseEntity.ok().body(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Thành công!")
                    .result(result)
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

    @Override
    public ResponseEntity<GenericResponse> getReactionsByPostIdForHover(String postId) {
        log.info("ReactionServiceImpl, getReactionsByPostIdForHover({})", postId);
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<Reaction> pageReactions = reactionRepository.findAllByPostId(postId, pageable);
        List<String> reactions = pageReactions
                .stream()
                .map(reaction -> {
                    SimpleUserDto simpleUserDto = userClientService.getSimpleUserDto(reaction.getAuthorId());
                    return simpleUserDto.getFirstName() + " " + simpleUserDto.getLastName();
                })
                .toList();
        Map<String, Object> result = Map.of("reactions", reactions, "count", pageReactions.getTotalElements());
        return ResponseEntity.ok().body(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Thành công!")
                .result(result)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> countReactionByGroupId(String authorization, String groupId) {
        log.info("ReactionServiceImpl, countReactionByGroupId({})", groupId);

        long count = postRepository.findAllByGroupId(groupId)
                .stream()
                .mapToLong(post -> reactionRepository.countByPostId(post.getId()))
                .sum();

        return ResponseEntity.ok().body(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Thành công!")
                .result(count)
                .build());

    }
}
