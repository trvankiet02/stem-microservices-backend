package com.trvankiet.app.service;

import com.trvankiet.app.dto.*;
import com.trvankiet.app.entity.*;

public interface MappingService {
    GroupDto mapToGroupDto(Group group);
    GroupMemberDto mapToGroupMemberDto(GroupMember groupMember);
    GroupMemberInvitationDto mapToGroupMemberInvitationDto(GroupMemberInvitation groupMemberInvitation);
    UserDto mapToUserDto(String userId);
    GroupMemberRequestDto mapToGroupMemberRequestDto(GroupMemberRequest groupMemberRequest);
    EventDto mapToEventDto(Event event);
}
