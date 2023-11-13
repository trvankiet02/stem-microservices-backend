package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.constant.GroupMemberRoleType;
import com.trvankiet.app.dto.EventDto;
import com.trvankiet.app.dto.request.CreateEventRequest;
import com.trvankiet.app.dto.request.ModifyEventRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Event;
import com.trvankiet.app.entity.GroupMember;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.EventRepository;
import com.trvankiet.app.repository.GroupMemberRepository;
import com.trvankiet.app.repository.GroupRepository;
import com.trvankiet.app.service.EventService;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final EventRepository eventRepository;
    private final MapperService mappingService;
    @Override
    public ResponseEntity<GenericResponse> createEvent(String userId, CreateEventRequest createEventRequest) throws ParseException {
        log.info("EventServiceImpl, createEvent");
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, createEventRequest.getGroupId())
                .orElseThrow(() -> new ForbiddenException("Bạn không có quyền tạo sự kiện cho nhóm này"));
        Event event = eventRepository.save(Event.builder()
                .id(UUID.randomUUID().toString())
                .group(groupMember.getGroup())
                .authorId(userId)
                .name(createEventRequest.getEventName())
                .description(createEventRequest.getEventDescription())
                .startedAt(DateUtil.string2Date(createEventRequest.getStartDate(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .endedAt(DateUtil.string2Date(createEventRequest.getEndDate(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .createdAt(new Date())
                .build());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Tạo sự kiện thành công")
                .result(mappingService.mapToEventDto(event))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getEvents(String userId, String groupId) {
        log.info("EventServiceImpl, getEvents");
        List<EventDto> events = eventRepository.findAllByGroupId(groupId)
                .stream()
                .map(mappingService::mapToEventDto)
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách sự kiện thành công")
                .result(events)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateEvent(String userId, String eventId, ModifyEventRequest modifyEventRequest) throws ParseException {
        log.info("EventServiceImpl, updateEvent");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Sự kiện không tồn tại"));
        if (!event.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa sự kiện này");
        }
        event.setName(modifyEventRequest.getEventName());
        event.setDescription(modifyEventRequest.getEventDescription());
        event.setStartedAt(DateUtil.string2Date(modifyEventRequest.getStartDate(), AppConstant.LOCAL_DATE_TIME_FORMAT));
        event.setEndedAt(DateUtil.string2Date(modifyEventRequest.getEndDate(), AppConstant.LOCAL_DATE_TIME_FORMAT));
        eventRepository.save(event);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật sự kiện thành công")
                .result(mappingService.mapToEventDto(event))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteEvent(String userId, String eventId) {
        log.info("EventServiceImpl, deleteEvent");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Sự kiện không tồn tại"));
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, event.getGroup().getId())
                .orElseThrow(() -> new ForbiddenException("Bạn không có quyền xóa sự kiện này"));
        if (groupMember.getGroupMemberRole().getName().equals(GroupMemberRoleType.GROUP_MEMBER.toString())) {
            throw new ForbiddenException("Bạn không có quyền xóa sự kiện này");
        }
        eventRepository.delete(event);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Xóa sự kiện thành công")
                .result(null)
                .build());
    }
}
