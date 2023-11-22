package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.FriendStateEnum;
import com.trvankiet.app.dto.FriendRequestDto;
import com.trvankiet.app.dto.request.CreateFriendRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.FriendRequest;
import com.trvankiet.app.entity.Friendship;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.repository.FriendRequestRepository;
import com.trvankiet.app.repository.FriendshipRepository;
import com.trvankiet.app.service.FriendRequestService;
import com.trvankiet.app.service.MapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final MapperService mapperService;

    @Override
    public ResponseEntity<List<FriendRequestDto>> getFriendRequests(String userId) {
        log.info("FriendRequestServiceImpl, getFriendRequests");
        List<FriendRequest> friendRequests = friendRequestRepository.findAllByRecipientId(userId);
        List<FriendRequestDto> friendRequestDtos = friendRequests.stream()
                .map(mapperService::mapToFriendRequestDto)
                .toList();
        return ResponseEntity.ok(friendRequestDtos);
    }

    @Override
    public ResponseEntity<GenericResponse> createFriendRequest(String userId, CreateFriendRequest createFriendRequest) {
        log.info("FriendRequestServiceImpl, createFriendRequest");
        Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findByTwoUserId(userId, createFriendRequest.getUserId());
        if (optionalFriendRequest.isPresent()) {
            throw new BadRequestException("Lời mời kết bạn đã được gửi trước đó!");
        }
        if (friendshipRepository.findByAuthorIdAndFriendIdsContaining(userId, createFriendRequest.getUserId()).isPresent()) {
            throw new BadRequestException("Hai người đã là bạn bè!");
        }
        FriendRequest friendRequest = friendRequestRepository.save(FriendRequest.builder()
                .id(UUID.randomUUID().toString())
                .senderId(userId)
                .recipientId(createFriendRequest.getUserId())
                .state(FriendStateEnum.PENDING)
                .createdAt(new Date())
                .build());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Gửi lời mời kết bạn thành công!")
                .result(mapperService.mapToFriendRequestDto(friendRequest))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> acceptFriendRequest(String userId, String friendRequestId) {
        log.info("FriendRequestServiceImpl, acceptFriendRequest");
        Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findById(friendRequestId);
        FriendRequest friendRequest = getFriendRequest(optionalFriendRequest, userId, FriendStateEnum.ACCEPTED);
        friendRequest = friendRequestRepository.save(friendRequest);
        Friendship senderRecipient = friendshipRepository.findByAuthorIdAndFriendIdsContaining(friendRequest.getSenderId(), friendRequest.getRecipientId())
                .orElse(Friendship.builder()
                        .id(UUID.randomUUID().toString())
                        .authorId(friendRequest.getSenderId())
                        .friendIds(List.of(friendRequest.getRecipientId()))
                        .createdAt(new Date())
                        .build());
        Friendship recipientSender = friendshipRepository.findByAuthorIdAndFriendIdsContaining(friendRequest.getRecipientId(), friendRequest.getSenderId())
                .orElse(Friendship.builder()
                        .id(UUID.randomUUID().toString())
                        .authorId(friendRequest.getRecipientId())
                        .friendIds(List.of(friendRequest.getSenderId()))
                        .createdAt(new Date())
                        .build());
        senderRecipient.getFriendIds().add(friendRequest.getRecipientId());
        recipientSender.getFriendIds().add(friendRequest.getSenderId());
        friendshipRepository.save(senderRecipient);
        friendshipRepository.save(recipientSender);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Chấp nhận lời mời kết bạn thành công!")
                .result(mapperService.mapToFriendRequestDto(friendRequest))
                .build());

    }

    @Override
    public ResponseEntity<GenericResponse> rejectFriendRequest(String userId, String friendRequestId) {
        log.info("FriendRequestServiceImpl, rejectFriendRequest");
        Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findById(friendRequestId);
        FriendRequest friendRequest = getFriendRequest(optionalFriendRequest, userId, FriendStateEnum.REJECTED);
        friendRequest = friendRequestRepository.save(friendRequest);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Từ chối lời mời kết bạn thành công!")
                .result(mapperService.mapToFriendRequestDto(friendRequest))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteFriendRequest(String userId, String friendRequestId) {
        log.info("FriendRequestServiceImpl, deleteFriendRequest");
        Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findById(friendRequestId);
        if (optionalFriendRequest.isEmpty()) {
            throw new BadRequestException("Không tìm thấy lời mời kết bạn!");
        }
        FriendRequest friendRequest = optionalFriendRequest.get();
        if (!friendRequest.getSenderId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền thực hiện hành động này!");
        }
        friendRequestRepository.delete(friendRequest);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Xóa lời mời kết bạn thành công!")
                .result(mapperService.mapToFriendRequestDto(friendRequest))
                .build());
    }

    private static FriendRequest getFriendRequest(Optional<FriendRequest> optionalFriendRequest, String userId, FriendStateEnum rejected) {
        if (optionalFriendRequest.isEmpty()) {
            throw new BadRequestException("Không tìm thấy lời mời kết bạn!");
        }
        FriendRequest friendRequest = optionalFriendRequest.get();
        if (!friendRequest.getRecipientId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền thực hiện hành động này!");
        }
        if (friendRequest.getState().equals(FriendStateEnum.ACCEPTED)) {
            throw new BadRequestException("Lời mời kết bạn đã được chấp nhận trước đó!");
        }
        if (friendRequest.getState().equals(FriendStateEnum.REJECTED)) {
            throw new BadRequestException("Lời mời kết bạn đã bị từ chối trước đó!");
        }
        friendRequest.setState(rejected);
        friendRequest.setUpdatedAt(new Date());
        return friendRequest;
    }
}
