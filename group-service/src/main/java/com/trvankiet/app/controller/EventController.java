package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.CreateEventRequest;
import com.trvankiet.app.dto.request.ModifyEventRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/events")
@Slf4j
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<GenericResponse> getEvents(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                     @RequestParam String groupId) {
        log.info("EventController, getEvents");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return eventService.getEvents(userId, groupId);
    }

    @PostMapping
    public ResponseEntity<GenericResponse> createEvent(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                       @RequestBody CreateEventRequest createEventRequest) throws ParseException {
        log.info("EventController, createEvent");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return eventService.createEvent(userId, createEventRequest);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<GenericResponse> updateEvent(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                       @PathVariable("eventId") String eventId,
                                                       @RequestBody ModifyEventRequest modifyEventRequest) throws ParseException {
        log.info("EventController, updateEvent");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return eventService.updateEvent(userId, eventId, modifyEventRequest);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<GenericResponse> deleteEvent(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                       @PathVariable("eventId") String eventId) {
        log.info("EventController, deleteEvent");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return eventService.deleteEvent(userId, eventId);
    }

    @GetMapping("/home-events")
    public ResponseEntity<GenericResponse> getHomeEvents(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("EventController, getHomeEvents");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return eventService.getHomeEvents(userId);
    }

}
