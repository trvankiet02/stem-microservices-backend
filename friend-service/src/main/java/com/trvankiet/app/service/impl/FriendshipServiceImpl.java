package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.FriendRequest;
import com.trvankiet.app.entity.Friendship;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.repository.FriendRequestRepository;
import com.trvankiet.app.repository.FriendshipRepository;
import com.trvankiet.app.service.FriendshipService;
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
public class FriendshipServiceImpl implements FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final FriendRequestRepository friendRequestRepository;

    @Override
    public ResponseEntity<List<String>> getFriendIds(String userId) {
        log.info("FriendshipServiceImpl, getFriendIds");
        Optional<Friendship> friendship = friendshipRepository.findByAuthorId(userId);
        return friendship.map(value -> ResponseEntity.ok(value.getFriendIds()))
                .orElseGet(() -> ResponseEntity.ok(List.of()));
    }

    @Override
    public ResponseEntity<GenericResponse> deleteFriend(String userId, String friendId) {
        log.info("FriendshipServiceImpl, deleteFriend");
        Optional<Friendship> optionalSenderRecipient = friendshipRepository.findByAuthorId(userId);
        Optional<Friendship> optionalRecipientSender = friendshipRepository.findByAuthorId(friendId);
        if (optionalSenderRecipient.isPresent() && optionalRecipientSender.isPresent()) {
            Friendship senderRecipient = optionalSenderRecipient.get();
            Friendship recipientSender = optionalRecipientSender.get();
            senderRecipient.getFriendIds().remove(friendId);
            recipientSender.getFriendIds().remove(userId);
            friendshipRepository.saveAll(List.of(senderRecipient, recipientSender));
            Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findByTwoUserId(userId, friendId);
            optionalFriendRequest.ifPresent(friendRequestRepository::delete);
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Xoá bạn thành công!")
                    .result(null)
                    .build());
        }
        throw new BadRequestException("Bạn không có quyền xoá bạn bè này!");
    }

    @Override
    public ResponseEntity<String> createFriendship(String userId) {
        log.info("FriendshipServiceImpl, createFriendship");
        Optional<Friendship> optionalFriendship = friendshipRepository.findByAuthorId(userId);
        if (optionalFriendship.isPresent()) {
            return ResponseEntity.ok("Đã tồn tại mối quan hệ bạn bè!");
        }
        Friendship friendship = Friendship.builder()
                .id(UUID.randomUUID().toString())
                .authorId(userId)
                .friendIds(List.of())
                .createdAt(new Date())
                .build();
        friendshipRepository.save(friendship);
        return ResponseEntity.ok("Tạo mối quan hệ bạn bè thành công!");
    }

    @Override
    public ResponseEntity<GenericResponse> validateFriendship(String userId, String friendId) {
        log.info("FriendshipServiceImpl, validateFriendship");
        Friendship senderRecipient = friendshipRepository.findByAuthorId(userId)
                .orElseThrow(() -> new BadRequestException("Bạn không có quyền thực hiện hành động này!"));
        if (senderRecipient.getFriendIds().contains(friendId)) {
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Bạn bè")
                    .result(true)
                    .build());
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Không phải bạn bè")
                .result(false)
                .build());
    }
}
