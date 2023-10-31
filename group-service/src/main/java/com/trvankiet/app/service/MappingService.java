package com.trvankiet.app.service;

import com.trvankiet.app.dto.*;
import com.trvankiet.app.entity.Group;
import com.trvankiet.app.entity.GroupMember;
import com.trvankiet.app.entity.GroupMemberInvitation;
import com.trvankiet.app.entity.GroupMemberRequest;

public interface MappingService {
    GroupDto mapToGroupDto(Group group);
    GroupMemberDto mapToGroupMemberDto(GroupMember groupMember);
    GroupMemberInvitationDto mapToGroupMemberInvitationDto(GroupMemberInvitation groupMemberInvitation);
    UserDto mapToUserDto(String userId);
    GroupMemberRequestDto mapToGroupMemberRequestDto(GroupMemberRequest groupMemberRequest);
}
