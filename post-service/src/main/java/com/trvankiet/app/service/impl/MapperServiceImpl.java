package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.*;
import com.trvankiet.app.dto.response.PostDetailResponse;
import com.trvankiet.app.entity.Comment;
import com.trvankiet.app.entity.Post;
import com.trvankiet.app.entity.Reaction;
import com.trvankiet.app.repository.CommentRepository;
import com.trvankiet.app.repository.ReactionRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.client.UserClientService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapperServiceImpl implements MapperService {

    private final UserClientService userClientService;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;

    @Override
    public PostDto mapToPostDto(Post post) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(0, 5, sort);
        Page<Comment> pageComment = commentRepository.findAllByPostId(post.getId(), pageable);
        List<Comment> comments = pageComment.getContent();
        SimpleUserDto simpleUserDto = userClientService.getSimpleUserDto(post.getAuthorId());

        return PostDto.builder()
                .id(post.getId())
                .groupId(post.getGroupId())
                .authorId(post.getAuthorId())
                .authorFirstName(simpleUserDto.getFirstName())
                .authorLastName(simpleUserDto.getLastName())
                .authorAvatar(simpleUserDto.getAvatarUrl())
                .content(post.getContent())
                .type(post.getType().name())
                .refUrls(post.getRefUrls().isEmpty() ?
                        null : post.getRefUrls())
                .totalReactions(reactionRepository.countByPostId(post.getId()))
                .totalComments(pageComment.getTotalElements())
                .commentDtos(comments.isEmpty() ?
                        null : comments.stream().map(this::mapToCommentDto).toList())
                .createdAt(post.getCreatedAt() == null ?
                        null : DateUtil.date2String(post.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(post.getUpdatedAt() == null ?
                        null : DateUtil.date2String(post.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public PostDetailResponse mapToPostDetailResponse(Post post) {
        //litmit get 5 response lasted
        return null;
    }

    @Override
    public CommentDto mapToCommentDto(Comment comment) {
        SimpleUserDto simpleUserDto = userClientService.getSimpleUserDto(comment.getAuthorId());

        return CommentDto.builder()
                .id(comment.getId())
                .authorId(comment.getAuthorId())
                .authorFirstName(simpleUserDto.getFirstName())
                .authorLastName(simpleUserDto.getLastName())
                .authorAvatar(simpleUserDto.getAvatarUrl())
                .content(comment.getContent())
                .refUrls(comment.getRefUrls().isEmpty() ?
                        null : comment.getRefUrls())
                .subCommentDtos(comment.getSubComments().isEmpty() ?
                        null : comment.getSubComments().stream().map(this::mapToCommentDto).toList())
                .createdAt(comment.getCreatedAt() == null ?
                        null : DateUtil.date2String(comment.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(comment.getUpdatedAt() == null ?
                        null : DateUtil.date2String(comment.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public ReactionDto mapToReactionDto(Reaction reaction) {
        return ReactionDto.builder()
                .id(reaction.getId())
                .userDto(userClientService.getSimpleUserDto(reaction.getAuthorId()))
                .type(reaction.getType().name())
                .createdAt(reaction.getCreatedAt() == null ?
                        null : DateUtil.date2String(reaction.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(reaction.getUpdatedAt() == null ?
                        null : DateUtil.date2String(reaction.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

}
