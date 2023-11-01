package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.constant.GroupRole;
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
import com.trvankiet.app.service.MappingService;
import com.trvankiet.app.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Nodes.collect;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final EventRepository eventRepository;
    private final MappingService mappingService;
    @Override
    public ResponseEntity<GenericResponse> createEvent(String userId, CreateEventRequest createEventRequest) throws ParseException {
        log.info("EventServiceImpl, createEvent");
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupGroupId(userId, createEventRequest.getGroupId())
                .orElseThrow(() -> new ForbiddenException("Bạn không có quyền tạo sự kiện cho nhóm này"));
        Event event = eventRepository.save(Event.builder()
                .eventId(UUID.randomUUID().toString())
                .group(groupMember.getGroup())
                .authorId(userId)
                .eventName(createEventRequest.getEventName())
                .eventDescription(createEventRequest.getEventDescription())
                .startDate(DateUtil.string2Date(createEventRequest.getStartDate(), AppConstant.LOCAL_DATE_FORMAT))
                .endDate(DateUtil.string2Date(createEventRequest.getEndDate(), AppConstant.LOCAL_DATE_FORMAT))
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
        List<EventDto> events = eventRepository.findAllByGroupGroupId(groupId)
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
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("Sự kiện không tồn tại"));
        if (!event.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa sự kiện này");
        }
        event.setEventName(modifyEventRequest.getEventName());
        event.setEventDescription(modifyEventRequest.getEventDescription());
        event.setStartDate(DateUtil.string2Date(modifyEventRequest.getStartDate(), AppConstant.LOCAL_DATE_FORMAT));
        event.setEndDate(DateUtil.string2Date(modifyEventRequest.getEndDate(), AppConstant.LOCAL_DATE_FORMAT));
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
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("Sự kiện không tồn tại"));
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupGroupId(userId, event.getGroup().getGroupId())
                .orElseThrow(() -> new ForbiddenException("Bạn không có quyền xóa sự kiện này"));
        if (groupMember.getGroupMemberRole().getRoleName().equals(GroupRole.GROUP_MEMBER.toString())) {
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
