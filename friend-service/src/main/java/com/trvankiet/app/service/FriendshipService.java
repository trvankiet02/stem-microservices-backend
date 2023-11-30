package com.trvankiet.app.service;

import com.trvankiet.app.dto.response.FriendOfUserResponse;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Friendship;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FriendshipService {
    ResponseEntity<List<String>> getFriendIds(String userId);

    ResponseEntity<GenericResponse> deleteFriend(String userId, String friendId);

    ResponseEntity<String> createFriendship(String userId);

    ResponseEntity<GenericResponse> validateFriendship(String userId, String friendId);

    ResponseEntity<List<FriendOfUserResponse>> getFriendsOfUser(String userId, String friendId);
}
