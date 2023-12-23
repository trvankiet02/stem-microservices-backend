package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.dto.*;
import com.trvankiet.app.dto.response.GroupMemberResponse;
import com.trvankiet.app.entity.*;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.client.UserClientService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapperServiceImpl implements MapperService {
    private final UserClientService userClientService;

    @Override
    public GroupDto mapToGroupDto(Group group) {
        return GroupDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription() == null ?
                        null : group.getDescription())
                .isClass(group.getIsClass() != null ? group.getIsClass() : false)
                .isPublic(group.getIsPublic() != null ? group.getIsPublic() : true)
                .isAcceptAllRequest(group.getIsAcceptAllRequest() != null ? group.getIsAcceptAllRequest() : true)
                .userDto(this.mapToSimpleUserDto(group.getAuthorId()))
                .avatarUrl(group.getAvatarUrl())
                .coverUrl(group.getCoverUrl())
                .subject(group.getSubject() == null ?
                        null : group.getSubject())
                .grade(group.getGrade() == null ?
                        null : group.getGrade())
                .createdAt(group.getCreatedAt() == null ?
                        null : DateUtil.date2String(group.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(group.getUpdatedAt() == null ?
                        null : DateUtil.date2String(group.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public GroupMemberDto mapToGroupMemberDto(GroupMember groupMember) {
        return GroupMemberDto.builder()
                .id(groupMember.getId())
                .userDto(this.mapToSimpleUserDto(groupMember.getUserId()))
                .isLocked(groupMember.getIsLocked())
                .lockedAt(groupMember.getLockedAt() == null ?
                        null : DateUtil.date2String(groupMember.getLockedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .lockedReason(groupMember.getLockedReason() == null ?
                        null : groupMember.getLockedReason())
                .groupDto(this.mapToGroupDto(groupMember.getGroup()))
                .role(groupMember.getRole().name())
                .groupMemberRequestDto(groupMember.getGroupMemberRequest() == null ?
                        null : this.mapToGroupMemberRequestDto(groupMember.getGroupMemberRequest()))
                .createdAt(groupMember.getCreatedAt() == null ?
                        null : DateUtil.date2String(groupMember.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(groupMember.getUpdatedAt() == null ?
                        null : DateUtil.date2String(groupMember.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public GroupMemberInvitationDto mapToGroupMemberInvitationDto(GroupMemberInvitation groupMemberInvitation) {
        return GroupMemberInvitationDto.builder()
                .id(groupMemberInvitation.getId())
                .inviterDto(this.mapToSimpleUserDto(groupMemberInvitation.getFromUserId()))
                .receiverDto(this.mapToSimpleUserDto(groupMemberInvitation.getToUserId()))
                .groupDto(this.mapToSimpleGroupDto(groupMemberInvitation.getGroup()))
                .state(groupMemberInvitation.getState().name())
                .createdAt(groupMemberInvitation.getCreatedAt() == null ?
                        null : DateUtil.date2String(groupMemberInvitation.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(groupMemberInvitation.getUpdatedAt() == null ?
                        null : DateUtil.date2String(groupMemberInvitation.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public UserDto mapToUserDto(String userId) {
        return userClientService.getUserDtoByUserId(userId);
    }

    @Override
    public GroupMemberRequestDto mapToGroupMemberRequestDto(GroupMemberRequest groupMemberRequest) {
        return GroupMemberRequestDto.builder()
                .id(groupMemberRequest.getId())
                .userDto(this.mapToSimpleUserDto(groupMemberRequest.getAuthorId()))
                .state(groupMemberRequest.getState().name())
                .invitationDto(groupMemberRequest.getGroupMemberInvitation() == null ?
                        null : this.mapToGroupMemberInvitationDto(groupMemberRequest.getGroupMemberInvitation()))
                .createdAt(groupMemberRequest.getCreatedAt() == null ?
                        null : DateUtil.date2String(groupMemberRequest.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(groupMemberRequest.getUpdatedAt() == null ?
                        null : DateUtil.date2String(groupMemberRequest.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();

    }

    @Override
    public EventDto mapToEventDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .groupDto(this.mapToGroupDto(event.getGroup()))
                .userDto(this.mapToSimpleUserDto(event.getAuthorId()))
                .name(event.getName())
                .description(event.getDescription() == null ?
                        null : event.getDescription())
                .startedAt(event.getStartedAt() == null ?
                        null : DateUtil.date2String(event.getStartedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .endedAt(event.getEndedAt() == null ?
                        null : DateUtil.date2String(event.getEndedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .createdAt(event.getCreatedAt() == null ?
                        null : DateUtil.date2String(event.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(event.getUpdatedAt() == null ?
                        null : DateUtil.date2String(event.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public SimpleUserDto mapToSimpleUserDto(String userId) {
        return userClientService.getSimpleUserDtoByUserId(userId);
    }

    @Override
    public GroupMemberResponse mapToGroupMemberResponse(GroupMember groupMember) {
        return GroupMemberResponse.builder()
                .id(groupMember.getId())
                .userDto(this.mapToSimpleUserDto(groupMember.getUserId()))
                .isLocked(groupMember.getIsLocked())
                .lockedAt(groupMember.getLockedAt() == null ?
                        null : DateUtil.date2String(groupMember.getLockedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .lockedReason(groupMember.getLockedReason() == null ?
                        null : groupMember.getLockedReason())
                .role(groupMember.getRole().name())
                .createdAt(groupMember.getCreatedAt() == null ?
                        null : DateUtil.date2String(groupMember.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(groupMember.getUpdatedAt() == null ?
                        null : DateUtil.date2String(groupMember.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public SimpleGroupDto mapToSimpleGroupDto(Group group) {
        return SimpleGroupDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription() == null ?
                        null : group.getDescription())
                .avatarUrl(group.getAvatarUrl())
                .coverUrl(group.getCoverUrl())
                .isPublic(group.getIsPublic())
                .build();
    }

}
