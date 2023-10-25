package com.trvankiet.app.service;

import com.trvankiet.app.dto.CommentDto;
import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.PostDto;
import com.trvankiet.app.dto.ReactionDto;
import com.trvankiet.app.entity.Comment;
import com.trvankiet.app.entity.Post;
import com.trvankiet.app.entity.Reaction;

import java.util.List;

public interface MapperService {
    PostDto mapToPostDto(Post post);
    CommentDto mapToCommentDto(Comment comment);
    ReactionDto mapToReactionDto(Reaction reaction);
}
