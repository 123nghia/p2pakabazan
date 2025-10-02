package com.akabazan.service;

import com.akabazan.service.dto.NotificationResult;

import java.util.List;

public interface NotificationService {

    void notifyUser(Long userId, String message);

    void notifyUsers(List<Long> userIds, String message);

    List<NotificationResult> getCurrentUserNotifications(boolean unreadOnly);

    void markAsRead(Long notificationId);
}
