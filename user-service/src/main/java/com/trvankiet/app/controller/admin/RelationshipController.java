package com.trvankiet.app.controller.admin;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.RelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/relationships/admin")
@RequiredArgsConstructor
@Slf4j
public class RelationshipController {

    private final RelationService relationService;

    @GetMapping("/handleHover/relationships")
    public ResponseEntity<GenericResponse> handleHoverRelationships(@RequestHeader("Authorization") final String token,
                                                                    @RequestParam("userId") String userId) {
        log.info("RelationshipController, handleHoverRelationships");
        return relationService.handleHoverRelationships(token, userId);
    }
}
