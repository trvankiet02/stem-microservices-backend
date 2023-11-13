package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.CommentDto;
import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.PostDto;
import com.trvankiet.app.dto.ReactionDto;
import com.trvankiet.app.entity.Comment;
import com.trvankiet.app.entity.Post;
import com.trvankiet.app.entity.Reaction;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.client.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapperServiceImpl implements MapperService {

    private final UserClientService userClientService;

    @Override
    public PostDto mapToPostDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .groupId(post.getGroupId())
                .userDto(userClientService.getUserDtoByUserId(post.getAuthorId()))
                .content(post.getContent())
                .type(post.getType().getName())
                .refUrls(post.getRefUrls().isEmpty() ?
                        null : post.getRefUrls())
                .reactionDtos(post.getReactions().isEmpty() ?
                        null : post.getReactions().stream().map(this::mapToReactionDto).toList())
                .commentDtos(post.getComments().isEmpty() ?
                        null : post.getComments().stream().map(this::mapToCommentDto).toList())
                .createdAt(post.getCreatedAt() == null ?
                        null : post.getCreatedAt().toString())
                .updatedAt(post.getUpdatedAt() == null ?
                        null : post.getUpdatedAt().toString())
                .build();
    }

    @Override
    public CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userDto(userClientService.getUserDtoByUserId(comment.getAuthorId()))
                .content(comment.getContent())
                .refUrls(comment.getRefUrls().isEmpty() ?
                        null : comment.getRefUrls())
                .reactionDtos(comment.getReactions().isEmpty() ?
                        null : comment.getReactions().stream().map(this::mapToReactionDto).toList())
                .subCommentDtos(comment.getSubComments().isEmpty() ?
                        null : comment.getSubComments().stream().map(this::mapToCommentDto).toList())
                .createdAt(comment.getCreatedAt() == null ?
                        null : comment.getCreatedAt().toString())
                .updatedAt(comment.getUpdatedAt() == null ?
                        null : comment.getUpdatedAt().toString())
                .build();
    }

    @Override
    public ReactionDto mapToReactionDto(Reaction reaction) {
        return ReactionDto.builder()
                .id(reaction.getId())
                .userDto(userClientService.getUserDtoByUserId(reaction.getAuthorId()))
                .type(reaction.getType().getName())
                .createdAt(reaction.getCreatedAt() == null ?
                        null : reaction.getCreatedAt().toString())
                .updatedAt(reaction.getUpdatedAt() == null ?
                        null : reaction.getUpdatedAt().toString())
                .build();
    }
}
