package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.FriendRequestDto;
import com.trvankiet.app.dto.FriendshipDto;
import com.trvankiet.app.entity.FriendRequest;
import com.trvankiet.app.entity.Friendship;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.util.DateUtil;
import org.springframework.stereotype.Service;

@Service
public class MapperServiceImpl implements MapperService {
    @Override
    public FriendRequestDto mapToFriendRequestDto(FriendRequest friendRequest) {
        return FriendRequestDto.builder()
                .id(friendRequest.getId())
                .senderId(friendRequest.getSenderId())
                .recipientId(friendRequest.getRecipientId())
                .status(friendRequest.getState().toString())
                .createdAt(friendRequest.getCreatedAt() == null ?
                        null : DateUtil.date2String(friendRequest.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(friendRequest.getUpdatedAt() == null ?
                        null : DateUtil.date2String(friendRequest.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public FriendshipDto mapToFriendshipDto(Friendship friendship) {
        return FriendshipDto.builder()
                .id(friendship.getId())
                .authorId(friendship.getAuthorId())
                .friendIds(friendship.getFriendIds())
                .createdAt(friendship.getCreatedAt() == null ?
                        null : DateUtil.date2String(friendship.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(friendship.getUpdatedAt() == null ?
                        null : DateUtil.date2String(friendship.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }
}
