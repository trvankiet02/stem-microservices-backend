package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.*;
import com.trvankiet.app.entity.*;
import com.trvankiet.app.service.MappingService;
import com.trvankiet.app.service.client.UserClientService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MappingServiceImpl implements MappingService {
    private final UserClientService userClientService;

    @Override
    public GroupDto mapToGroupDto(Group group) {
        return GroupDto.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupDescription(group.getGroupDescription())
                .groupImage(group.getGroupImage())
                .build();
    }

    @Override
    public GroupMemberDto mapToGroupMemberDto(GroupMember groupMember) {
        return GroupMemberDto.builder()
                .groupMemberId(groupMember.getGroupMemberId())
                .groupMemberRole(groupMember.getGroupMemberRole().getRoleName())
                .build();
    }

    @Override
    public GroupMemberInvitationDto mapToGroupMemberInvitationDto(GroupMemberInvitation groupMemberInvitation) {
        return GroupMemberInvitationDto.builder()
                .id(groupMemberInvitation.getGroupMemberInvitationId())
                .groupDto(this.mapToGroupDto(groupMemberInvitation.getGroup()))
                .userDto(this.mapToUserDto(groupMemberInvitation.getFromUserId()))
                .createdDate(DateUtil.date2String(groupMemberInvitation.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public UserDto mapToUserDto(String userId) {
        return userClientService.getUserDtoByUserId(userId);
    }

    @Override
    public GroupMemberRequestDto mapToGroupMemberRequestDto(GroupMemberRequest groupMemberRequest) {
        return GroupMemberRequestDto.builder()
                .groupMemberRequestId(groupMemberRequest.getGroupMemberRequestId())
                .groupDto(this.mapToGroupDto(groupMemberRequest.getGroup()))
                .userDto(this.mapToUserDto(groupMemberRequest.getAuthorId()))
                .createdDate(DateUtil.date2String(groupMemberRequest.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public EventDto mapToEventDto(Event event) {
        return EventDto.builder()
                .eventId(event.getEventId())
                .userDto(this.mapToUserDto(event.getAuthorId()))
                .eventName(event.getEventName())
                .eventDescription(event.getEventDescription())
                .startDate(DateUtil.date2String(event.getStartDate(), AppConstant.LOCAL_DATE_FORMAT))
                .endDate(DateUtil.date2String(event.getEndDate(), AppConstant.LOCAL_DATE_FORMAT))
                .build();
    }
}
