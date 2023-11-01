package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.InviteGroupMemberRequest;
import com.trvankiet.app.dto.request.StateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface GroupMemberService {
    ResponseEntity<GenericResponse> inivteGroupMember(String userId, InviteGroupMemberRequest groupMemberRequest);

    ResponseEntity<GenericResponse> requestGroupMember(String userId, String groupId);

    ResponseEntity<GenericResponse> responseGroupMemberInvitation(String userId, String groupMemberInvitationId, StateRequest inviteResponseGroupMember);

    ResponseEntity<GenericResponse> responseGroupMemberRequest(String userId, String groupMemberRequestId, StateRequest stateRequest);

    ResponseEntity<GenericResponse> changeRole(String userId, String groupMemberId, String role);

    ResponseEntity<GenericResponse> deleteGroupMember(String userId, String groupMemberId);
}
