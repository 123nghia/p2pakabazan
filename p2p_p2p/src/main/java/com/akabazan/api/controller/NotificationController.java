package com.akabazan.api.controller;

import com.akabazan.api.mapper.NotificationResponseMapper;
import com.akabazan.api.reponse.NotificationResponse;
import com.akabazan.notification.dto.NotificationResult;
import com.akabazan.notification.service.NotificationService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/p2p")

public class NotificationController extends BaseController {

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
    public ResponseEntity<Void> markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
