package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.CreateEventRequest;
import com.trvankiet.app.dto.request.ModifyEventRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

public interface EventService {
    ResponseEntity<GenericResponse> createEvent(String userId, CreateEventRequest createEventRequest) throws ParseException;

    ResponseEntity<GenericResponse> getEvents(String userId, String groupId);

    ResponseEntity<GenericResponse> updateEvent(String userId, String eventId, ModifyEventRequest modifyEventRequest) throws ParseException;

    ResponseEntity<GenericResponse> deleteEvent(String userId, String eventId);
}
