package com.akabazan.api.controller;

import com.akabazan.api.dto.NotificationResponse;
import com.akabazan.api.mapper.NotificationResponseMapper;
import com.akabazan.service.NotificationService;
import com.akabazan.service.dto.NotificationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/p2p")
@CrossOrigin(origins = {
    "http://localhost:5500",
    "http://localhost:5174"
})

public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @RequestParam(name = "unreadOnly", defaultValue = "false") boolean unreadOnly) {

        List<NotificationResult> results = notificationService.getCurrentUserNotifications(unreadOnly);
        return ResponseEntity.ok(NotificationResponseMapper.fromList(results));
    }

    @PostMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
