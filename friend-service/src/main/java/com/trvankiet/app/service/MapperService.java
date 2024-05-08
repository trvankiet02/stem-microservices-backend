package com.trvankiet.app.service;

import com.trvankiet.app.dto.FriendRequestDto;
import com.trvankiet.app.dto.FriendshipDto;
import com.trvankiet.app.entity.FriendRequest;
import com.trvankiet.app.entity.Friendship;

public interface MapperService {

    FriendRequestDto mapToFriendRequestDto(FriendRequest friendRequest);
    FriendshipDto mapToFriendshipDto(Friendship friendship);

}
