package com.akabazan.notification.service;
import com.akabazan.notification.enums.NotificationType;

import com.akabazan.notification.dto.NotificationResult;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void notifyUser(UUID userId, NotificationType type, String message);

    void notifyUsers(List<UUID> userIds, NotificationType type , String message);

    List<NotificationResult> getCurrentUserNotifications(boolean unreadOnly);

    void markAsRead(UUID notificationId);
}
