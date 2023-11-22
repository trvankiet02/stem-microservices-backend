package com.trvankiet.app.service;

import com.trvankiet.app.dto.FriendRequestDto;
import com.trvankiet.app.dto.request.CreateFriendRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FriendRequestService {
    ResponseEntity<List<FriendRequestDto>> getFriendRequests(String userId);

    ResponseEntity<GenericResponse> createFriendRequest(String userId, CreateFriendRequest createFriendRequest);

    ResponseEntity<GenericResponse> acceptFriendRequest(String userId, String friendRequestId);

    ResponseEntity<GenericResponse> rejectFriendRequest(String userId, String friendRequestId);

    ResponseEntity<GenericResponse> deleteFriendRequest(String userId, String friendRequestId);
}
