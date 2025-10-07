package com.akabazan.notification.service;
import com.akabazan.notification.enums.NotificationType;

import com.akabazan.notification.dto.NotificationResult;

import java.util.List;

public interface NotificationService {

    void notifyUser(Long userId,NotificationType type, String message);

    void notifyUsers(List<Long> userIds,NotificationType type , String message);

    List<NotificationResult> getCurrentUserNotifications(boolean unreadOnly);



    void markAsRead(Long notificationId);
}
